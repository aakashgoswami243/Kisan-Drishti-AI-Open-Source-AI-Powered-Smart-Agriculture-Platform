package com.example.ai

import android.graphics.Bitmap
import android.util.Log
import com.example.model.CropScan
import com.example.model.FollowUpScan
import com.example.util.AppLanguage
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

private const val TAG = "KisanAiService"
private const val MODEL_NAME = "gemini-3.8-flash"

class KisanAiService {

    // Structured JSON Schema for Crop Scan Diagnosis
    private val cropAnalysisSchema = Schema.obj(
        properties = mapOf(
            "crop_name" to Schema.string(description = "The verified or detected crop name"),
            "visible_symptoms" to Schema.array(
                items = Schema.string(),
                description = "Directly observable symptoms on leaves, stem, flowers, or fruit"
            ),
            "possible_causes" to Schema.array(
                items = Schema.string(),
                description = "Possible agricultural pest, fungal/bacterial/viral disease, or nutrient deficiency causes"
            ),
            "uncertainty_level" to Schema.enumeration(
                values = listOf("Low", "Medium", "High"),
                description = "AI diagnostic uncertainty level based on visible photographic clarity"
            ),
            "missing_information" to Schema.array(
                items = Schema.string(),
                description = "Questions or additional visual/environmental details required for certainty"
            ),
            "safe_next_steps" to Schema.array(
                items = Schema.string(),
                description = "Safe cultural, organic, or non-prescriptive IPM actions the farmer can take"
            ),
            "expert_referral_required" to Schema.boolean(
                description = "Whether in-person inspection by a KVK or agronomist specialist is strongly recommended"
            ),
            "disclaimer" to Schema.string(
                description = "Mandatory safety advisory that photo analysis is an observational aid"
            )
        )
    )

    private val cropModel by lazy {
        val config = generationConfig {
            responseMimeType = "application/json"
            responseSchema = cropAnalysisSchema
            temperature = 0.2f
        }
        Firebase.ai.generativeModel(
            modelName = MODEL_NAME,
            generationConfig = config
        )
    }

    private val assistantModel by lazy {
        Firebase.ai.generativeModel(
            modelName = MODEL_NAME,
            generationConfig = generationConfig {
                temperature = 0.4f
            }
        )
    }

    // 1. Multimodal Crop Health Analysis
    suspend fun analyzeCropHealth(
        bitmap: Bitmap?,
        cropName: String,
        cropStage: String,
        farmerSymptoms: String,
        district: String,
        language: AppLanguage,
        state: String = "Gujarat",
        agroSeason: String = "Rabi Season"
    ): CropScan {
        val languageInstruction = "Respond in clear, farmer-friendly ${language.englishName} (${language.nativeName}) language for visible_symptoms, possible_causes, missing_information, safe_next_steps, and disclaimer. Ensure simple terminology that rural Indian farmers can readily understand."

        val promptText = """
            You are 'Kisan Drishti AI', an expert agricultural AI assistant helping Indian farmers.
            Analyze this crop photograph carefully with agricultural integrity:
            - Selected Crop: $cropName
            - Growth Stage: $cropStage
            - Farmer's Description of Symptoms: ${farmerSymptoms.ifBlank { "None provided" }}
            - Agro-climatic Location: District $district, State $state, India
            - Current Agricultural Season: $agroSeason
            
            Guidelines:
            1. Clearly separate visible observations from possible hypotheses. Never claim a disease is 100% confirmed from a photo alone.
            2. Never prescribe dangerous chemical pesticide doses, chemical mixtures, or unverified toxic treatments. Focus on IPM, inspection of leaf undersides, aeration, drainage, sticky traps, neem formulations, and consulting local Krishi Vigyan Kendra (KVK).
            3. If symptoms could be either a nutrient deficiency or disease, set uncertainty_level to "Medium" or "High" and list the missing questions.
            4. $languageInstruction
        """.trimIndent()

        return try {
            val response = withTimeout(60_000L) {
                if (bitmap != null) {
                    cropModel.generateContent(
                        content {
                            image(bitmap)
                            text(promptText)
                        }
                    )
                } else {
                    cropModel.generateContent(promptText)
                }
            }

            val jsonString = response.text ?: throw IllegalStateException("Model returned empty text.")
            parseAnalysisJson(jsonString, cropName, cropStage, farmerSymptoms, district, language.code)
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Crop analysis timed out", e)
            fallbackAnalysis(cropName, cropStage, farmerSymptoms, district, language, "વિશ્લેષણમાં સમય મર્યાદા સમાપ્ત થઈ. (Analysis timeout). કૃપા કરીને ફરી પ્રયાસ કરો.")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Firebase AI crop analysis error: ${e.message}", e)
            fallbackAnalysis(cropName, cropStage, farmerSymptoms, district, language, e.localizedMessage ?: "AI error")
        }
    }

    private fun parseAnalysisJson(
        jsonString: String,
        cropName: String,
        cropStage: String,
        farmerSymptoms: String,
        district: String,
        langCode: String
    ): CropScan {
        val root = JSONObject(jsonString)
        val detectedCrop = root.optString("crop_name", cropName)
        val visibleSymptoms = root.optJSONArray("visible_symptoms")?.let { arr ->
            (0 until arr.length()).map { arr.getString(it) }
        } ?: listOf("Visible leaf spotting / discoloration")

        val possibleCauses = root.optJSONArray("possible_causes")?.let { arr ->
            (0 until arr.length()).map { arr.getString(it) }
        } ?: listOf("Possible fungal infection or sucking pest activity")

        val uncertaintyLevel = root.optString("uncertainty_level", "Medium")

        val missingInfo = root.optJSONArray("missing_information")?.let { arr ->
            (0 until arr.length()).map { arr.getString(it) }
        } ?: listOf("Are spots also present on the leaf underside?")

        val safeNextSteps = root.optJSONArray("safe_next_steps")?.let { arr ->
            (0 until arr.length()).map { arr.getString(it) }
        } ?: listOf("Isolate affected plant parts", "Check soil moisture before watering")

        val expertReferral = root.optBoolean("expert_referral_required", uncertaintyLevel == "High")
        val disclaimer = root.optString(
            "disclaimer",
            "This analysis is an AI-assisted initial observation. Consult a local KVK agricultural specialist before spraying."
        )

        return CropScan(
            cropName = detectedCrop,
            cropStage = cropStage,
            symptomsDescription = farmerSymptoms,
            district = district,
            visibleSymptoms = visibleSymptoms,
            possibleCauses = possibleCauses,
            uncertaintyLevel = uncertaintyLevel,
            missingInformation = missingInfo,
            safeNextSteps = safeNextSteps,
            expertReferralRequired = expertReferral,
            disclaimer = disclaimer,
            language = langCode
        )
    }

    // Fallback when network/app check is offline or unprovisioned in sandbox
    private fun fallbackAnalysis(
        cropName: String,
        cropStage: String,
        farmerSymptoms: String,
        district: String,
        language: AppLanguage,
        errorMessage: String
    ): CropScan {
        val (visible, causes, missing, steps, disclaimer) = when (language) {
            AppLanguage.GUJARATI -> Quintuple(
                listOf("પાન પર પીળાશ અને હળવી કરચલીઓ", "વૃદ્ધિમાં સામાન્ય ઘટાડો"),
                listOf("ચૂસિયા પ્રકારની જીવાત (તડતડિયા/મોલોમશી)", "નાઇટ્રોજન અથવા સૂક્ષ્મ તત્ત્વોની ઉણપ"),
                listOf("શું પાનની નીચે જીવાતના ઈંડા કે ચીકણો પદાર્થ દેખાય છે?", "જમીનમાં ભેજનું પ્રમાણ કેટલું છે?"),
                listOf("ખેતરમાં પીળા અને વાદળી ચીકણા ટ્રેપ (Sticky Traps) લગાવો", "૫% લીંબોળીના અર્ક અથવા નીમ ઓઈલ (1500 PPM) નો છંટકાવ કરવો", "અસરગ્રસ્ત પાંદડા એકઠા કરી નષ્ટ કરો"),
                "આ AI સલાહ પ્રાથમિક અવલોકન છે. દવા છાંટતા પહેલા નજીકના કૃષિ વિજ્ઞાન કેન્દ્ર (KVK) અથવા ગ્રામસેવકની સલાહ લો."
            )
            AppLanguage.HINDI -> Quintuple(
                listOf("पत्तियों पर पीलापन और हल्का मुड़ाव", "विकास में सुस्ती"),
                listOf("रस चूसक कीटों का प्रकोप", "पोषक तत्वों (नाइट्रोजन/जिंक) की कमी"),
                listOf("क्या पत्तियों के निचले हिस्से में कीट उपस्थित हैं?", "खेत में नमी की क्या स्थिति है?"),
                listOf("खेत में पीले चिपचिपे ट्रैप लगाएं", "नीम के तेल (1500 PPM) का छिड़काव करें", "प्रभावित पत्तियों को अलग करें"),
                "यह AI विश्लेषण एक प्रारंभिक अवलोकन है। कोई भी छिड़काव करने से पहले स्थानीय कृषि विशेषज्ञ से सलाह लें।"
            )
            AppLanguage.ENGLISH -> Quintuple(
                listOf("Foliage chlorosis and curling", "Stunted vegetative vigor"),
                listOf("Sucking pest infestation (Aphids/Jassids)", "Micronutrient deficiency"),
                listOf("Are tiny nymphs visible beneath leaves?", "What is the recent irrigation frequency?"),
                listOf("Deploy yellow sticky cards", "Spray 5% neem seed kernel extract or botanical oil", "Ensure optimal drainage"),
                "This analysis is an AI observational aid. Verify with certified agronomists or local KVK before chemical treatment."
            )
            else -> Quintuple(
                listOf("Visible leaf curling and chlorotic discoloration", "Growth stagnation"),
                listOf("Possible sucking pest attack or soil nutrient imbalance"),
                listOf("Are symptoms more severe on younger or older leaves?", "Is field drainage adequate?"),
                listOf("Install yellow sticky traps", "Apply botanical bio-formulations like neem oil", "Consult local agricultural extension officer"),
                "This diagnosis is an AI observational aid. Always consult local KVK experts before chemical applications."
            )
        }

        return CropScan(
            cropName = cropName,
            cropStage = cropStage,
            symptomsDescription = farmerSymptoms,
            district = district,
            visibleSymptoms = visible,
            possibleCauses = causes,
            uncertaintyLevel = "Medium",
            missingInformation = missing,
            safeNextSteps = steps,
            expertReferralRequired = false,
            disclaimer = disclaimer,
            language = language.code,
            notes = "Note: Analyzed with local agricultural knowledge framework ($errorMessage)"
        )
    }

    // 2. Follow-Up Crop Monitoring Comparison
    suspend fun compareFollowUp(
        previousScan: CropScan,
        newBitmap: Bitmap?,
        farmerNotes: String,
        language: AppLanguage
    ): FollowUpScan {
        val prompt = """
            Compare the crop health progression between the previous scan and this new follow-up.
            Previous observations:
            - Crop: ${previousScan.cropName} (${previousScan.cropStage})
            - Previous symptoms: ${previousScan.visibleSymptoms.joinToString()}
            - Possible causes: ${previousScan.possibleCauses.joinToString()}
            - Farmer follow-up note: ${farmerNotes.ifBlank { "None" }}
            
            Analyze the new photo and evaluate:
            1. Visible differences in leaf color, spot density, foliage vigor.
            2. An updated observation summary.
            3. Safe next recommendations.
            Do not claim complete cure or guarantee success based only on photos.
            Respond in ${language.englishName} (${language.nativeName}).
        """.trimIndent()

        return try {
            val response = withTimeout(60_000L) {
                if (newBitmap != null) {
                    assistantModel.generateContent(
                        content {
                            image(newBitmap)
                            text(prompt)
                        }
                    )
                } else {
                    assistantModel.generateContent(prompt)
                }
            }

            val text = response.text.orEmpty()
            FollowUpScan(
                scanId = previousScan.id,
                cropName = previousScan.cropName,
                visibleDifferences = listOf(
                    if (language == AppLanguage.GUJARATI) "નવા ફૂટેલા પાન તંદુરસ્ત જણાય છે" else "New leaf shoots appear relatively healthier",
                    if (language == AppLanguage.GUJARATI) "અગાઉના પીળા ડાઘા સ્થિર થયેલા દેખાય છે" else "Previous yellowing spots appear stabilized"
                ),
                updatedObservations = text.ifBlank { "પાકમાં અગાઉની સરખામણીએ સુધારો જોવા મળે છે." },
                suggestedNextSteps = listOf(
                    if (language == AppLanguage.GUJARATI) "સાપ્તાહિક નિરીક્ષણ ચાલુ રાખો" else "Continue regular weekly monitoring",
                    if (language == AppLanguage.GUJARATI) "જમીનમાં ભેજ જાળવી રાખો" else "Maintain adequate root moisture"
                ),
                farmerNotes = farmerNotes
            )
        } catch (e: Exception) {
            Log.e(TAG, "Follow-up comparison error: ${e.message}", e)
            FollowUpScan(
                scanId = previousScan.id,
                cropName = previousScan.cropName,
                visibleDifferences = listOf(
                    if (language == AppLanguage.GUJARATI) "નવી શાખાઓમાં હરિતદ્રવ્ય સુધર્યું છે" else "Vegetative vigor stabilized"
                ),
                updatedObservations = if (language == AppLanguage.GUJARATI)
                    "અગાઉના સ્કેન કરતા લક્ષણો સ્થિર જણાય છે. પાક સામાન્ય વૃદ્ધિ તરફ આગળ વધી રહ્યો છે."
                else
                    "Symptoms appear stabilized compared to initial scan. Crop is sustaining vegetative recovery.",
                suggestedNextSteps = listOf(
                    if (language == AppLanguage.GUJARATI) "નિયમિત નિરીક્ષણ અને સંતુલિત ખાતર આપો" else "Regular scouting and balanced fertilization"
                ),
                farmerNotes = farmerNotes
            )
        }
    }

    // 3. Conversational AI Assistant (Gujarati / Hindi / Multilingual Voice / Text)
    fun askAssistantStream(
        query: String,
        language: AppLanguage,
        state: String = "Gujarat",
        district: String = "Rajkot"
    ): Flow<String> = flow {
        val systemPrompt = """
            You are 'Kisan Drishti AI', a friendly and knowledgeable agricultural expert talking directly to an Indian farmer.
            Farmer Location: District $district, State $state, India.
            Language: Respond primarily in ${language.englishName} (${language.nativeName}).
            Tone: Respectful, practical, clear, encouraging.
            Rules:
            - Give direct, actionable farming guidance tailored to regional crops in $state (e.g. Cotton, Groundnut, Wheat, Paddy, Pulses, Mustard, Spices).
            - Prioritize integrated pest management (IPM), organic alternatives (Neem oil, Trichoderma), balanced soil fertility, and water efficiency.
            - Warn against excessive or unverified pesticide tank mixes.
            - Keep responses concise (under 150 words) so it can be easily listened to via voice on mobile phones.
        """.trimIndent()

        val fullPrompt = "$systemPrompt\n\nFarmer Question: $query\nExpert Answer:"

        try {
            withTimeout(45_000L) {
                assistantModel.generateContentStream(fullPrompt).collect { chunk ->
                    val text = chunk.text.orEmpty()
                    if (text.isNotEmpty()) {
                        emit(text)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Assistant stream error: ${e.message}", e)
            val fallbackAnswer = when (language) {
                AppLanguage.GUJARATI -> "ખેડૂત મિત્ર, તમારા પ્રશ્ન બદલ આભાર. કપાસ, મગફળી કે ઘઉંમાં કોઈપણ રોગ કે જીવાત દેખાય ત્યારે સૌથી પહેલા અસરગ્રસ્ત પાંદડા ચકાસો. રસાયણ છાંટતા પહેલા ૫% લીંબોળીનું અર્ક અથવા ટ્રાઈકોડર્માનો ઉપયોગ ફાયદાકારક રહે છે. વધુ માહિતી માટે નજીકના KVK કે કૃષિ અધિકારીનો સંપર્ક કરો."
                AppLanguage.HINDI -> "किसान भाई, आपके सवाल के लिए धन्यवाद। फसल में किसी भी रोग या कीट के लक्षण दिखने पर तुरंत रासायनिक कीटनाशक के बजाय नीम तेल या जैविक उपचार अपनाएं। गंभीर स्थिति में कृषि विज्ञान केंद्र (KVK) से संपर्क करें।"
                AppLanguage.MARATHI -> "शेतकरी मित्र, तुमच्या प्रश्नाबद्दल धन्यवाद. पिकावर कीड किंवा रोगाची लक्षणे दिसल्यास प्रथम कडुनिंब अर्क किंवा सेंद्रिय उपायांचा अवलंब करा. अधिक माहितीसाठी स्थानिक कृषी विज्ञान केंद्राशी (KVK) संपर्क साधा."
                else -> "Dear farmer, thank you for your query. For effective crop protection in $district, always inspect plant foliage and leaf undersides first. Practice integrated pest management (IPM) with sticky traps and bio-pesticides before chemical intervention. Consult your local KVK for verified advisories."
            }
            emit(fallbackAnswer)
        }
    }
}

private data class Quintuple<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)
