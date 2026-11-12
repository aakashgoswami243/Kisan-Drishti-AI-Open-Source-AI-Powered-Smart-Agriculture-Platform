# Kisan Drishti AI (કિસાન દ્રષ્ટિ AI)
### *Every Farmer Deserves an AI Agricultural Expert.*
**Platform:** Native Android (Kotlin + Jetpack Compose) | **Category:** Agritech | Artificial Intelligence | Social Impact

---

## 🌾 Overview
**Kisan Drishti AI** is a mobile-first, native Android application engineered for small and marginal farmers across India, with primary focus on Gujarat. It combines **Google Gemini 3.8 Flash** multimodal visual reasoning, **Firebase App Check** cryptographic attestation, and **Cloud Firestore** zero-trust user isolation to detect crop diseases, insect pests, and nutrient deficiencies directly from smartphone photographs.

---

## 🚀 Key Features

1. **AI Crop Scanner (3-Step Farmer Workflow)**:
   - **Step 1**: Select crop (Cotton / કપાસ, Groundnut / મગફળી, Wheat / ઘઉં) and crop growth stage.
   - **Step 2**: Capture high-resolution photo using Camera or choose from Gallery (with quick testing sample fallback).
   - **Step 3**: Optional farmer symptom notes & district selection (Rajkot, Junagadh, Amreli, Bhavnagar, Mehsana, Patan, etc.).

2. **Multimodal Gemini AI Analysis**:
   - Analyzes real crop foliage, stems, and leaf spots.
   - Enforces structured JSON schema (`gemini-3.8-flash`).
   - Clearly separates **visible symptoms** from **possible causes**.
   - Transparent **AI Uncertainty Indicator** (Low / Medium / High).
   - Safe, non-prescriptive IPM actions (sticky traps, neem oil, aeration, biocontrols).
   - Clear advice on when to consult local **Krishi Vigyan Kendra (KVK)** experts.

3. **Digital Crop Health Passport**:
   - Comprehensive digital ledger recording every crop scan.
   - Filter by crop, search past diagnoses, and view full reports.
   - Per-user isolated cloud storage backed by hardened Firestore security rules.

4. **Follow-Up Crop Monitoring & Comparison**:
   - Allows farmers to upload a new photo of the same crop days/weeks later.
   - Compares previous vs. current photo.
   - Evaluates visible progression and recovery without false medical certainty.

5. **"Before You Spray" Safety Check**:
   - Interactive Integrated Pest Management (IPM) advisor.
   - Checks weather conditions, pollinator safety, personal protective equipment (PPE), and prevents improper chemical tank mixes.

6. **Gujarati & 22 Indian Languages AI Voice Partner**:
   - Conversational Gemini AI assistant speaking native Gujarati, Hindi, English, and regional languages.
   - Android Speech-to-Text (`RecognizerIntent`) and Text-to-Speech audio output.
   - Quick prompt chips for regional crop challenges.

7. **About Kisan Drishti AI & Mission**:
   - Dedicated in-app architecture, security, and agricultural safety hub covering Vision, Crop Diagnostics, Gemini AI integration, and KVK Farmer Advisory.

---

## 🛠️ Technology Stack & Architecture

- **Frontend**: Jetpack Compose, Material Design 3, Kotlin Coroutines & Flow.
- **AI Engine**: Firebase AI Logic (`Firebase.ai`) with `gemini-3.8-flash`.
- **Backend & Database**: Firebase Firestore (Enterprise edition), Google Sign-In with Jetpack Credential Manager.
- **Security**: Firebase App Check attestation with debug tokens and release reCAPTCHA Enterprise.
- **Color System**: Forest Green (`#174F38`), Fresh Lime (`#D6ED85`), Soft Cream (`#F5F7EF`), Warm Amber (`#E9A23B`), Dark Text (`#24372E`).

---

## 🧪 Testing Verification Report

- **Firestore Rules (Tier 1 JS)**: 7/7 tests passed in ~3.8s (`node --test firestore.test.js`).
- **Repository Integration (Tier 2 Kotlin/Robolectric)**: 4/4 tests passed against live local emulators (`KisanRepositoryRuleTest`).
- **Compilation**: Full Gradle build verified (`compile_applet` passed).
- **Prohibited Patterns Check**: Zero unauthenticated fallbacks, zero hardcoded secrets.
