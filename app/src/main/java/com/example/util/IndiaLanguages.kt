package com.example.util

import java.util.Locale

enum class LanguageMode {
    AUTO,
    MANUAL
}

enum class AppLanguage(
    val code: String,
    val englishName: String,
    val nativeName: String,
    val script: String,
    val isRtl: Boolean,
    val hasTts: Boolean,
    val hasStt: Boolean
) {
    GUJARATI("gu", "Gujarati", "ગુજરાતી", "Gujarati", false, hasTts = true, hasStt = true),
    HINDI("hi", "Hindi", "हिन्दी", "Devanagari", false, hasTts = true, hasStt = true),
    ENGLISH("en", "English", "English", "Latin", false, hasTts = true, hasStt = true),
    MARATHI("mr", "Marathi", "मराठी", "Devanagari", false, hasTts = true, hasStt = true),
    TAMIL("ta", "Tamil", "தமிழ்", "Tamil", false, hasTts = true, hasStt = true),
    TELUGU("te", "Telugu", "తెలుగు", "Telugu", false, hasTts = true, hasStt = true),
    BENGALI("bn", "Bengali", "বাংলা", "Bengali", false, hasTts = true, hasStt = true),
    PUNJABI("pa", "Punjabi", "ਪੰਜਾਬੀ", "Gurmukhi", false, hasTts = true, hasStt = true),
    KANNADA("kn", "Kannada", "ಕನ್ನಡ", "Kannada", false, hasTts = true, hasStt = true),
    MALAYALAM("ml", "Malayalam", "മലയാളം", "Malayalam", false, hasTts = true, hasStt = true),
    ODIA("or", "Odia", "ଓଡ଼ିଆ", "Odia", false, hasTts = false, hasStt = false),
    ASSAMESE("as", "Assamese", "অসমীয়া", "Bengali-Assamese", false, hasTts = false, hasStt = false),
    URDU("ur", "Urdu", "اردو", "Arabic-Nastaliq", true, hasTts = true, hasStt = true),
    SANSKRIT("sa", "Sanskrit", "संस्कृतम्", "Devanagari", false, hasTts = false, hasStt = false),
    NEPALI("ne", "Nepali", "नेपाली", "Devanagari", false, hasTts = true, hasStt = true),
    KONKANI("kok", "Konkani", "कोंकणी", "Devanagari", false, hasTts = false, hasStt = false),
    SINDHI("sd", "Sindhi", "سنڌي", "Arabic-Sindhi", true, hasTts = false, hasStt = false),
    KASHMIRI("ks", "Kashmiri", "कॉशुर / كٲشُر", "Perso-Arabic", true, hasTts = false, hasStt = false),
    MAITHILI("mai", "Maithili", "मैथिली", "Devanagari", false, hasTts = false, hasStt = false),
    DOGRI("doi", "Dogri", "डोगरी", "Devanagari", false, hasTts = false, hasStt = false),
    BODO("brx", "Bodo", "बड़ो", "Devanagari", false, hasTts = false, hasStt = false),
    MANIPURI("mni", "Manipuri (Meitei)", "মৈতৈলোন্", "Meitei-Mayek", false, hasTts = false, hasStt = false),
    SANTALI("sat", "Santali", "ᱥᱟᱱᱛᱟᱲᱤ", "Ol Chiki", false, hasTts = false, hasStt = false);

    val displayName: String get() = englishName

    fun getLocale(): Locale {
        return when (this) {
            GUJARATI -> Locale("gu", "IN")
            HINDI -> Locale("hi", "IN")
            ENGLISH -> Locale("en", "IN")
            MARATHI -> Locale("mr", "IN")
            TAMIL -> Locale("ta", "IN")
            TELUGU -> Locale("te", "IN")
            BENGALI -> Locale("bn", "IN")
            PUNJABI -> Locale("pa", "IN")
            KANNADA -> Locale("kn", "IN")
            MALAYALAM -> Locale("ml", "IN")
            ODIA -> Locale("or", "IN")
            ASSAMESE -> Locale("as", "IN")
            URDU -> Locale("ur", "IN")
            NEPALI -> Locale("ne", "NP")
            else -> Locale(code, "IN")
        }
    }

    companion object {
        fun fromCode(code: String?): AppLanguage {
            if (code.isNullOrBlank()) return GUJARATI
            val normalized = code.trim().lowercase()
            return entries.firstOrNull {
                it.code.equals(normalized, ignoreCase = true) ||
                it.name.equals(normalized, ignoreCase = true) ||
                it.englishName.equals(normalized, ignoreCase = true)
            } ?: GUJARATI
        }

        fun resolveSmartLanguage(
            savedPref: String?,
            mode: LanguageMode,
            deviceLocale: Locale = Locale.getDefault(),
            detectedState: String? = null
        ): AppLanguage {
            // 1. If manual mode with saved preference, respect user's manual choice strictly
            if (mode == LanguageMode.MANUAL && !savedPref.isNullOrBlank()) {
                return fromCode(savedPref)
            }

            // 2. If saved preference exists and valid
            if (!savedPref.isNullOrBlank()) {
                val candidate = fromCode(savedPref)
                if (candidate != GUJARATI || savedPref.equals("gu", ignoreCase = true)) {
                    return candidate
                }
            }

            // 3. Device system language match
            val deviceLangCode = deviceLocale.language.lowercase()
            val deviceMatch = entries.firstOrNull { it.code == deviceLangCode }
            if (deviceMatch != null && mode == LanguageMode.AUTO) {
                return deviceMatch
            }

            // 4. Regional suggestion from state if in AUTO mode
            if (mode == LanguageMode.AUTO && !detectedState.isNullOrBlank()) {
                val stateMatch = getStatePrimaryLanguage(detectedState)
                if (stateMatch != null) return stateMatch
            }

            // 5. Default initial release: Gujarati, with English as fallback
            return GUJARATI
        }

        fun getStatePrimaryLanguage(state: String): AppLanguage? {
            val s = state.trim().lowercase()
            return when {
                s.contains("gujarat") -> GUJARATI
                s.contains("maharashtra") -> MARATHI
                s.contains("tamil") -> TAMIL
                s.contains("karnataka") -> KANNADA
                s.contains("kerala") -> MALAYALAM
                s.contains("bengal") -> BENGALI
                s.contains("punjab") -> PUNJABI
                s.contains("andhra") || s.contains("telangana") -> TELUGU
                s.contains("odisha") || s.contains("orissa") -> ODIA
                s.contains("assam") -> ASSAMESE
                s.contains("kashmir") -> KASHMIRI
                s.contains("goa") -> KONKANI
                s.contains("uttar pradesh") || s.contains("madhya pradesh") ||
                s.contains("rajasthan") || s.contains("bihar") ||
                s.contains("haryana") || s.contains("himachal") ||
                s.contains("uttarakhand") || s.contains("delhi") ||
                s.contains("chhattisgarh") || s.contains("jharkhand") -> HINDI
                else -> null
            }
        }
    }
}
