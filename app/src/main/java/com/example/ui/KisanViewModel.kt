package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.KisanAiService
import com.example.data.KisanRepository
import com.example.model.ChatMessage
import com.example.model.CropScan
import com.example.model.FollowUpScan
import com.example.model.SafetyCheckItem
import com.example.model.SupportedCrop
import com.example.model.UserProfile
import com.example.util.AgroSeason
import com.example.util.AppLanguage
import com.example.util.GeoLocationResult
import com.example.util.IndiaGeoData
import com.example.util.LanguageMode
import com.example.util.LocationHelper
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID

enum class AppScreen {
    WELCOME,
    DASHBOARD,
    CROP_SCANNER,
    SCAN_RESULT,
    CROP_PASSPORT,
    FOLLOW_UP_COMPARISON,
    SAFETY_CHECK,
    VOICE_ASSISTANT,
    ABOUT_APP
}

class KisanViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = KisanRepository(application)
    private val aiService = KisanAiService()

    // Navigation State
    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Language State (Gujarati default for initial release, all 22 Indian languages supported)
    private val _language = MutableStateFlow(AppLanguage.GUJARATI)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _languageMode = MutableStateFlow(LanguageMode.AUTO)
    val languageMode: StateFlow<LanguageMode> = _languageMode.asStateFlow()

    private val _suggestedRegionalLanguage = MutableStateFlow<AppLanguage?>(null)
    val suggestedRegionalLanguage: StateFlow<AppLanguage?> = _suggestedRegionalLanguage.asStateFlow()

    // Location State (Default: Gujarat, Rajkot)
    private val _selectedState = MutableStateFlow(IndiaGeoData.DEFAULT_STATE)
    val selectedState: StateFlow<String> = _selectedState.asStateFlow()

    private val _selectedDistrict = MutableStateFlow(IndiaGeoData.DEFAULT_DISTRICT)
    val selectedDistrict: StateFlow<String> = _selectedDistrict.asStateFlow()

    private val _locationConsent = MutableStateFlow(true)
    val locationConsent: StateFlow<Boolean> = _locationConsent.asStateFlow()

    private val _isLocating = MutableStateFlow(false)
    val isLocating: StateFlow<Boolean> = _isLocating.asStateFlow()

    private val _isLangLocationSheetOpen = MutableStateFlow(false)
    val isLangLocationSheetOpen: StateFlow<Boolean> = _isLangLocationSheetOpen.asStateFlow()

    // Auth & Farmer Profile State
    private val _isGuestMode = MutableStateFlow(false)
    val isGuestMode: StateFlow<Boolean> = _isGuestMode.asStateFlow()

    private val _farmerProfile = MutableStateFlow(
        UserProfile(
            displayName = "પ્રવીણભાઈ પટેલ (Pravinbhai)",
            preferredLanguage = "gu",
            languageMode = "AUTO",
            state = "Gujarat",
            district = "Rajkot",
            locationConsent = true,
            primaryCrops = listOf("Cotton", "Groundnut", "Wheat")
        )
    )
    val farmerProfile: StateFlow<UserProfile> = _farmerProfile.asStateFlow()

    // Scans State
    private val _scansList = MutableStateFlow<List<CropScan>>(emptyList())
    val scansList: StateFlow<List<CropScan>> = _scansList.asStateFlow()

    private val _followUpsList = MutableStateFlow<List<FollowUpScan>>(emptyList())
    val followUpsList: StateFlow<List<FollowUpScan>> = _followUpsList.asStateFlow()

    private val _currentScanResult = MutableStateFlow<CropScan?>(null)
    val currentScanResult: StateFlow<CropScan?> = _currentScanResult.asStateFlow()

    private val _selectedScanForFollowUp = MutableStateFlow<CropScan?>(null)
    val selectedScanForFollowUp: StateFlow<CropScan?> = _selectedScanForFollowUp.asStateFlow()

    // Scanner Form State
    private val _selectedCrop = MutableStateFlow("Cotton")
    val selectedCrop: StateFlow<String> = _selectedCrop.asStateFlow()

    private val _selectedStage = MutableStateFlow("Flowering")
    val selectedStage: StateFlow<String> = _selectedStage.asStateFlow()

    private val _capturedBitmap = MutableStateFlow<Bitmap?>(null)
    val capturedBitmap: StateFlow<Bitmap?> = _capturedBitmap.asStateFlow()

    private val _symptomInput = MutableStateFlow("")
    val symptomInput: StateFlow<String> = _symptomInput.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    // Follow-up comparison state
    private val _followUpBitmap = MutableStateFlow<Bitmap?>(null)
    val followUpBitmap: StateFlow<Bitmap?> = _followUpBitmap.asStateFlow()

    private val _followUpNotes = MutableStateFlow("")
    val followUpNotes: StateFlow<String> = _followUpNotes.asStateFlow()

    private val _latestComparisonResult = MutableStateFlow<FollowUpScan?>(null)
    val latestComparisonResult: StateFlow<FollowUpScan?> = _latestComparisonResult.asStateFlow()

    private val _isComparing = MutableStateFlow(false)
    val isComparing: StateFlow<Boolean> = _isComparing.asStateFlow()

    // Safety Checklist State
    private val _safetyChecks = MutableStateFlow(
        listOf(
            SafetyCheckItem("weather", "safety_rule_1", "વરસાદ કે વધુ પવનમાં છંટકાવ કરવાથી દવા ધોવાઈ જાય છે અથવા ઉડી જાય છે."),
            SafetyCheckItem("pollinators", "safety_rule_2", "મધમાખી અને પરોપજીવી કીટકો કુદરતી પરાગનયન અને જીવાત નિયંત્રણમાં મદદ કરે છે."),
            SafetyCheckItem("ppe", "safety_rule_3", "ત્વચા અને શ્વાસ દ્વારા ઝેરી રસાયણોના શોષણથી બચવા સુરક્ષા સાધનો અનિવાર્ય છે."),
            SafetyCheckItem("tank_mix", "safety_rule_4", "અયોગ્ય મિશ્રણથી પાન બળી શકે છે (ફાઈટોટોક્સિસિટી) અથવા અસર ઘટતી હોય છે.")
        )
    )
    val safetyChecks: StateFlow<List<SafetyCheckItem>> = _safetyChecks.asStateFlow()

    // AI Voice & Text Assistant State
    private val _chatMessages = MutableStateFlow(
        listOf(
            ChatMessage(
                id = "welcome",
                isUser = false,
                text = "નમસ્તે ખેડૂત મિત્ર! હું તમારો કિસાન દ્રષ્ટિ AI સલાહકાર છું. કપાસ, મગફળી, ઘઉં કે અન્ય પાક વિશે કોઈ પણ પ્રશ્ન પૂછો અથવા બોલો."
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _assistantStreamingText = MutableStateFlow("")
    val assistantStreamingText: StateFlow<String> = _assistantStreamingText.asStateFlow()

    private val _isAssistantResponding = MutableStateFlow(false)
    val isAssistantResponding: StateFlow<Boolean> = _isAssistantResponding.asStateFlow()

    // Android Text-to-Speech
    private var tts: TextToSpeech? = null
    private val _isTtsReady = MutableStateFlow(false)
    val isTtsReady: StateFlow<Boolean> = _isTtsReady.asStateFlow()

    val supportedCropsList = listOf(
        SupportedCrop(
            id = "cotton",
            nameEn = "Cotton",
            nameGu = "કપાસ",
            nameHi = "कपास",
            season = "Kharif",
            commonPests = listOf("Pink Bollworm (ગુલાબી ઈયળ)", "Whitefly (સફેદ માખી)", "Jassids (તડતડિયા)", "Bacterial Blight"),
            defaultStages = listOf("Germination", "Vegetative", "Flowering", "Boll Formation", "Maturity")
        ),
        SupportedCrop(
            id = "groundnut",
            nameEn = "Groundnut",
            nameGu = "મગફળી",
            nameHi = "मूंगफली",
            season = "Kharif / Summer",
            commonPests = listOf("Tikka Disease (ટિક્કા રોગ)", "White Grub (ધેણ)", "Collar Rot (થડનો કોહવારો)", "Stem Rot"),
            defaultStages = listOf("Germination", "Vegetative", "Flowering", "Pegging / Pod Formation", "Maturity")
        ),
        SupportedCrop(
            id = "wheat",
            nameEn = "Wheat",
            nameGu = "ઘઉં",
            nameHi = "गेहूं",
            season = "Rabi",
            commonPests = listOf("Yellow/Brown Rust (ગેરુ રોગ)", "Termites (ઉધઈ)", "Aphids (મોલો)", "Loose Smut"),
            defaultStages = listOf("Crown Root Initiation", "Tillering", "Jointing", "Heading / Milking", "Dough / Maturity")
        )
    )

    val gujaratDistricts = listOf(
        "Rajkot (રાજકોટ)", "Amreli (અમરેલી)", "Junagadh (જૂનાગઢ)", "Bhavnagar (ભાવનગર)",
        "Jamnagar (જામનગર)", "Surendranagar (સુરેન્દ્રનગર)", "Mehsana (મહેસાણા)", "Patan (પાટણ)",
        "Banaskantha (બનાસકાંઠા)", "Sabarkantha (સાબરકાંઠા)", "Ahmedabad (અમદાવાદ)",
        "Vadodara (વડોદરા)", "Kutch (કચ્છ)", "Kheda (ખેડા)", "Surat (સૂરત)"
    )

    init {
        initTts(application)
        loadInitialData()
    }

    private fun initTts(context: Application) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val guLocale = Locale("gu", "IN")
                val result = tts?.setLanguage(guLocale)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // Fallback to Hindi or default locale
                    tts?.setLanguage(Locale("hi", "IN"))
                }
                _isTtsReady.value = true
            }
        }
    }

    private fun loadInitialData() {
        if (Firebase.auth.currentUser != null) {
            observeFirestoreData()
        } else {
            // Seed sample demonstrative scans for initial experience
            seedSampleScans()
        }
    }

    fun onAuthSuccess() {
        _isGuestMode.value = false
        observeFirestoreData()
    }

    fun enableGuestMode() {
        _isGuestMode.value = true
        seedSampleScans()
        _currentScreen.value = AppScreen.DASHBOARD
    }

    private fun observeFirestoreData() {
        if (Firebase.auth.currentUser == null) return
        viewModelScope.launch {
            repository.observeScans()
                .catch { e -> Log.e("KisanVM", "Error observing scans: ${e.message}") }
                .collect { list ->
                    if (list.isNotEmpty()) {
                        _scansList.value = list
                    } else if (_scansList.value.isEmpty()) {
                        seedSampleScans()
                    }
                }
        }
        viewModelScope.launch {
            repository.observeFollowUps()
                .catch { e -> Log.e("KisanVM", "Error observing follow-ups: ${e.message}") }
                .collect { list ->
                    _followUpsList.value = list
                }
        }
        viewModelScope.launch {
            val profile = repository.getUserProfile()
            if (profile != null) {
                _farmerProfile.value = profile
                val mode = if (profile.languageMode.equals("MANUAL", ignoreCase = true)) LanguageMode.MANUAL else LanguageMode.AUTO
                _languageMode.value = mode
                if (profile.state.isNotBlank()) _selectedState.value = profile.state
                if (profile.district.isNotBlank()) _selectedDistrict.value = profile.district
                _locationConsent.value = profile.locationConsent

                val resolved = AppLanguage.resolveSmartLanguage(
                    savedPref = profile.preferredLanguage,
                    mode = mode,
                    detectedState = profile.state
                )
                _language.value = resolved
                updateSuggestedRegionalLanguage(profile.state, resolved)
            }
        }
    }

    private fun seedSampleScans() {
        val sample1 = CropScan(
            id = "demo_cotton_01",
            cropName = "Cotton",
            cropStage = "Flowering",
            symptomsDescription = "પાનની કિનારીઓ પીળી પડી રહી છે અને પાન ઉપર તરફ વળે છે",
            district = "Rajkot",
            visibleSymptoms = listOf("Leaf margin yellowing (હરિતદ્રવ્ય ઘટાડો)", "Upward leaf cupping (પાનનો વળાંક)"),
            possibleCauses = listOf("Jassids / Sucking pest attack (તડતડિયા)", "Potassium / Nitrogen imbalance (પોટાશ ઉણપ)"),
            uncertaintyLevel = "Low",
            missingInformation = listOf("શું પાનની નીચે લીલા કલરની નાની જીવાત દેખાય છે?"),
            safeNextSteps = listOf(
                "ખેતરમાં પીળા ચીકણા ટ્રેપ (Yellow Sticky Traps) એકરે ૬ થી ૮ લગાવો",
                "૫% લીંબોળીનું તેલ અથવા નીમ ઓઈલ ૧૫૦૦ PPM નો છંટકાવ કરો",
                "પાકને તાત્કાલિક ભારે ઝેરી દવાઓ છાંટવાનું ટાળો"
            ),
            expertReferralRequired = false,
            disclaimer = "આ AI વિશ્લેષણ પ્રાથમિક સલાહ છે. કોઈપણ દવાનો ઉપયોગ કરતા પહેલા કૃષિ નિષ્ણાતનો સંપર્ક કરો.",
            language = "gu"
        )
        val sample2 = CropScan(
            id = "demo_groundnut_02",
            cropName = "Groundnut",
            cropStage = "Pod Formation",
            symptomsDescription = "નીચલા પાન પર ગોળ ઘેરા બદામી ટપકાં દેખાય છે",
            district = "Junagadh",
            visibleSymptoms = listOf("Dark circular leaf spots with yellow halo (ટિક્કા રોગના ટપકાં)"),
            possibleCauses = listOf("Early Tikka Leaf Spot (સર્કોસ્પોરા ફૂગ રોગ)"),
            uncertaintyLevel = "Low",
            missingInformation = listOf("શું સમગ્ર ખેતરમાં છોડ પીળા પડેલા છે કે ચોક્કસ ભાગમાં?"),
            safeNextSteps = listOf(
                "ટ્રાઇકોડર્મા હરઝીયાનમ અથવા સ્યુડોમોનાસ નો ઉપયોગ કરવો",
                "છોડ વચ્ચે પૂરતો સૂર્યપ્રકાશ અને હવા ઉજાસ રહે તેવો નિકાલ રાખો"
            ),
            expertReferralRequired = false,
            disclaimer = "સલામત ઓર્ગેનિક અને સંકલિત જીવાત વ્યવસ્થાપન પદ્ધતિઓ અપનાવો.",
            language = "gu"
        )
        _scansList.value = listOf(sample1, sample2)
    }

    fun setLanguage(lang: AppLanguage, mode: LanguageMode = LanguageMode.MANUAL) {
        _language.value = lang
        _languageMode.value = mode
        updateSuggestedRegionalLanguage(_selectedState.value, lang)

        viewModelScope.launch {
            if (repository.isUserSignedIn()) {
                val updated = _farmerProfile.value.copy(
                    preferredLanguage = lang.code,
                    languageMode = mode.name
                )
                repository.saveUserProfile(updated)
            }
        }
        val locale = lang.getLocale()
        val result = tts?.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            tts?.setLanguage(Locale("hi", "IN"))
        }
    }

    fun setLanguageMode(mode: LanguageMode) {
        _languageMode.value = mode
        if (mode == LanguageMode.AUTO) {
            val autoResolved = AppLanguage.resolveSmartLanguage(
                savedPref = null,
                mode = LanguageMode.AUTO,
                detectedState = _selectedState.value
            )
            _language.value = autoResolved
            updateSuggestedRegionalLanguage(_selectedState.value, autoResolved)
        }
        viewModelScope.launch {
            if (repository.isUserSignedIn()) {
                val updated = _farmerProfile.value.copy(languageMode = mode.name)
                repository.saveUserProfile(updated)
            }
        }
    }

    fun updateLocation(state: String, district: String, consent: Boolean) {
        _selectedState.value = state
        _selectedDistrict.value = district
        _locationConsent.value = consent

        if (_languageMode.value == LanguageMode.AUTO) {
            val autoLang = AppLanguage.getStatePrimaryLanguage(state)
            if (autoLang != null) {
                _language.value = autoLang
            }
        }
        updateSuggestedRegionalLanguage(state, _language.value)

        viewModelScope.launch {
            if (repository.isUserSignedIn()) {
                val updated = _farmerProfile.value.copy(
                    state = state,
                    district = district,
                    locationConsent = consent
                )
                repository.saveUserProfile(updated)
            }
        }
    }

    fun detectLocation(context: android.content.Context) {
        viewModelScope.launch {
            _isLocating.value = true
            _statusMessage.value = "GPS સ્થાન શોધી રહ્યું છે..."
            try {
                val result = LocationHelper.getCurrentLocation(context)
                if (result != null) {
                    updateLocation(result.state, result.district, true)
                    _statusMessage.value = "સ્થાન મળ્યું: ${result.district}, ${result.state}"
                } else {
                    _statusMessage.value = "GPS સ્થાન મળ્યું નથી. કૃપા કરીને રાજ્ય અને જિલ્લો મેન્યુઅલ પસંદ કરો."
                }
            } catch (e: Exception) {
                Log.e("KisanVM", "Location detection failed", e)
                _statusMessage.value = "સ્થાન શોધવામાં ક્ષતિ: ${e.localizedMessage}"
            } finally {
                _isLocating.value = false
            }
        }
    }

    private fun updateSuggestedRegionalLanguage(state: String, current: AppLanguage) {
        val regional = IndiaGeoData.getRegionalLanguagesForState(state)
        val candidate = regional.firstOrNull { it != current }
        _suggestedRegionalLanguage.value = candidate
    }

    fun setLangLocationSheetOpen(isOpen: Boolean) {
        _isLangLocationSheetOpen.value = isOpen
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    // Scanner actions
    fun setSelectedCrop(crop: String) { _selectedCrop.value = crop }
    fun setSelectedStage(stage: String) { _selectedStage.value = stage }
    fun setCapturedBitmap(bitmap: Bitmap?) { _capturedBitmap.value = bitmap }
    fun setSymptomInput(text: String) { _symptomInput.value = text }
    fun setSelectedDistrict(district: String) { _selectedDistrict.value = district }

    fun runCropAnalysis() {
        viewModelScope.launch {
            _isAnalyzing.value = true
            _statusMessage.value = "જેમિની AI પાકનું વિશ્લેષણ કરી રહ્યું છે..."
            try {
                val result = aiService.analyzeCropHealth(
                    bitmap = _capturedBitmap.value,
                    cropName = _selectedCrop.value,
                    cropStage = _selectedStage.value,
                    farmerSymptoms = _symptomInput.value,
                    district = _selectedDistrict.value,
                    language = _language.value,
                    state = _selectedState.value,
                    agroSeason = AgroSeason.getCurrent().englishName
                )
                _currentScanResult.value = result
                _currentScreen.value = AppScreen.SCAN_RESULT
            } catch (e: Exception) {
                Log.e("KisanVM", "Analysis failed", e)
                _statusMessage.value = "વિશ્લેષણ નિષ્ફળ થયું: ${e.localizedMessage}"
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    fun saveCurrentScanToPassport() {
        val scan = _currentScanResult.value ?: return
        viewModelScope.launch {
            try {
                if (repository.isUserSignedIn()) {
                    val id = repository.saveCropScan(scan)
                    _currentScanResult.value = scan.copy(id = id)
                } else {
                    val localId = "scan_${UUID.randomUUID()}"
                    val updated = scan.copy(id = localId)
                    _scansList.value = listOf(updated) + _scansList.value
                    _currentScanResult.value = updated
                }
                _statusMessage.value = "સ્કેન પાક પાસપોર્ટમાં સાચવી લેવામાં આવ્યો છે!"
            } catch (e: Exception) {
                Log.e("KisanVM", "Failed to save scan", e)
                _statusMessage.value = "સાચવવામાં ક્ષતિ: ${e.localizedMessage}"
            }
        }
    }

    fun deleteScan(scanId: String) {
        viewModelScope.launch {
            try {
                if (repository.isUserSignedIn()) {
                    repository.deleteScan(scanId)
                }
                _scansList.value = _scansList.value.filter { it.id != scanId }
                _statusMessage.value = "સ્કેન રેકોર્ડ સફળતાપૂર્વક કાઢી નાખ્યો."
            } catch (e: Exception) {
                Log.e("KisanVM", "Failed to delete scan", e)
            }
        }
    }

    fun prepareFollowUp(scan: CropScan) {
        _selectedScanForFollowUp.value = scan
        _followUpBitmap.value = null
        _followUpNotes.value = ""
        _latestComparisonResult.value = null
        _currentScreen.value = AppScreen.FOLLOW_UP_COMPARISON
    }

    fun setFollowUpBitmap(bitmap: Bitmap?) { _followUpBitmap.value = bitmap }
    fun setFollowUpNotes(notes: String) { _followUpNotes.value = notes }

    fun runFollowUpComparison() {
        val parentScan = _selectedScanForFollowUp.value ?: return
        viewModelScope.launch {
            _isComparing.value = true
            try {
                val comparison = aiService.compareFollowUp(
                    previousScan = parentScan,
                    newBitmap = _followUpBitmap.value,
                    farmerNotes = _followUpNotes.value,
                    language = _language.value
                )
                _latestComparisonResult.value = comparison
                if (repository.isUserSignedIn()) {
                    val fuId = repository.saveFollowUp(comparison)
                    _followUpsList.value = listOf(comparison.copy(id = fuId)) + _followUpsList.value
                } else {
                    _followUpsList.value = listOf(comparison) + _followUpsList.value
                }
                _statusMessage.value = "ફોલો-અપ સરખામણી સફળ!"
            } catch (e: Exception) {
                Log.e("KisanVM", "Comparison failed", e)
            } finally {
                _isComparing.value = false
            }
        }
    }

    // Safety Checklist Toggle
    fun toggleSafetyCheck(id: String) {
        _safetyChecks.value = _safetyChecks.value.map { item ->
            if (item.id == id) item.copy(isChecked = !item.isChecked) else item
        }
    }

    // Voice & Text Assistant
    fun sendAssistantMessage(query: String) {
        if (query.isBlank()) return
        val userMsg = ChatMessage(UUID.randomUUID().toString(), isUser = true, text = query)
        _chatMessages.value = _chatMessages.value + userMsg
        _isAssistantResponding.value = true
        _assistantStreamingText.value = ""

        viewModelScope.launch {
            val responseBuilder = StringBuilder()
            aiService.askAssistantStream(
                query = query,
                language = _language.value,
                state = _selectedState.value,
                district = _selectedDistrict.value
            ).collect { chunk ->
                responseBuilder.append(chunk)
                _assistantStreamingText.value = responseBuilder.toString()
            }
            val finalReply = responseBuilder.toString().ifBlank {
                "તમારા પાકની કાળજી રાખવા બદલ આભાર. કૃપા કરીને વધુ વિગત આપો."
            }
            val aiMsg = ChatMessage(UUID.randomUUID().toString(), isUser = false, text = finalReply)
            _chatMessages.value = _chatMessages.value + aiMsg
            _assistantStreamingText.value = ""
            _isAssistantResponding.value = false

            // Auto-speak in Gujarati / Hindi if desired
            speakText(finalReply)
        }
    }

    fun speakText(text: String) {
        if (_isTtsReady.value) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "KisanDrishtiUtterance")
        }
    }

    fun stopSpeaking() {
        tts?.stop()
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }
}
