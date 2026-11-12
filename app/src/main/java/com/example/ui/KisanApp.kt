package com.example.ui

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.ui.components.KisanBottomBar
import com.example.ui.components.KisanTopHeader
import com.example.ui.components.LanguageLocationBottomSheet
import com.example.ui.components.WarningNotificationBottomSheet
import com.example.ui.screens.AboutAppScreen
import com.example.ui.screens.CropPassportScreen
import com.example.ui.screens.CropScannerScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FollowUpComparisonScreen
import com.example.ui.screens.SafetyCheckScreen
import com.example.ui.screens.ScanResultScreen
import com.example.ui.screens.VoiceAssistantScreen
import com.example.ui.screens.WelcomeScreen
import com.example.util.LanguageMode

@Composable
fun KisanApp(
    viewModel: KisanViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentScreen by viewModel.currentScreen.collectAsState()
    val language by viewModel.language.collectAsState()
    val languageMode by viewModel.languageMode.collectAsState()
    val suggestedRegionalLanguage by viewModel.suggestedRegionalLanguage.collectAsState()
    val selectedState by viewModel.selectedState.collectAsState()
    val selectedDistrict by viewModel.selectedDistrict.collectAsState()
    val locationConsent by viewModel.locationConsent.collectAsState()
    val isLocating by viewModel.isLocating.collectAsState()
    val isLangLocationSheetOpen by viewModel.isLangLocationSheetOpen.collectAsState()

    var isWarningSheetOpen by remember { mutableStateOf(false) }
    var warningSheetInitialTab by remember { mutableIntStateOf(0) }

    val farmerProfile by viewModel.farmerProfile.collectAsState()
    val scansList by viewModel.scansList.collectAsState()
    val currentScanResult by viewModel.currentScanResult.collectAsState()
    val capturedBitmap by viewModel.capturedBitmap.collectAsState()
    val selectedCrop by viewModel.selectedCrop.collectAsState()
    val selectedStage by viewModel.selectedStage.collectAsState()
    val symptomInput by viewModel.symptomInput.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val selectedScanForFollowUp by viewModel.selectedScanForFollowUp.collectAsState()
    val followUpBitmap by viewModel.followUpBitmap.collectAsState()
    val followUpNotes by viewModel.followUpNotes.collectAsState()
    val comparisonResult by viewModel.latestComparisonResult.collectAsState()
    val isComparing by viewModel.isComparing.collectAsState()
    val safetyChecks by viewModel.safetyChecks.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val streamingText by viewModel.assistantStreamingText.collectAsState()
    val isResponding by viewModel.isAssistantResponding.collectAsState()

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            viewModel.detectLocation(context)
        }
    }

    // Handle back button behavior for sub-screens
    if (currentScreen != AppScreen.DASHBOARD && currentScreen != AppScreen.WELCOME) {
        BackHandler {
            viewModel.navigateTo(AppScreen.DASHBOARD)
        }
    }

    if (currentScreen == AppScreen.WELCOME) {
        WelcomeScreen(
            language = language,
            onLanguageChange = { viewModel.setLanguage(it) },
            onOpenAllLanguages = { viewModel.setLangLocationSheetOpen(true) },
            onGetStarted = { viewModel.navigateTo(AppScreen.DASHBOARD) },
            onAuthSuccess = { viewModel.onAuthSuccess() },
            onGuestMode = { viewModel.enableGuestMode() }
        )
    } else {
        Scaffold(
            topBar = {
                KisanTopHeader(
                    language = language,
                    district = selectedDistrict,
                    onOpenLangLocation = { viewModel.setLangLocationSheetOpen(true) },
                    onOpenWarnings = {
                        warningSheetInitialTab = 0
                        isWarningSheetOpen = true
                    },
                    onOpenNotifications = {
                        warningSheetInitialTab = 1
                        isWarningSheetOpen = true
                    },
                    onNavigateAbout = { viewModel.navigateTo(AppScreen.ABOUT_APP) }
                )
            },
            bottomBar = {
                if (currentScreen != AppScreen.ABOUT_APP) {
                    KisanBottomBar(
                        currentScreen = currentScreen,
                        language = language,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentScreen) {
                    AppScreen.WELCOME -> { /* Handled above */ }
                    AppScreen.DASHBOARD -> {
                        DashboardScreen(
                            profile = farmerProfile,
                            language = language,
                            scansList = scansList,
                            state = selectedState,
                            district = selectedDistrict,
                            suggestedLanguage = suggestedRegionalLanguage,
                            onOpenLangLocation = { viewModel.setLangLocationSheetOpen(true) },
                            onAcceptSuggestedLanguage = { viewModel.setLanguage(it, LanguageMode.MANUAL) },
                            onDismissSuggestedLanguage = { /* Keep current language */ },
                            onNavigate = { viewModel.navigateTo(it) },
                            onSelectScan = { scan ->
                                viewModel.prepareFollowUp(scan)
                            }
                        )
                    }
                    AppScreen.CROP_SCANNER -> {
                        CropScannerScreen(
                            language = language,
                            supportedCrops = viewModel.supportedCropsList,
                            districts = viewModel.gujaratDistricts,
                            selectedCrop = selectedCrop,
                            selectedStage = selectedStage,
                            capturedBitmap = capturedBitmap,
                            symptomInput = symptomInput,
                            selectedDistrict = selectedDistrict,
                            isAnalyzing = isAnalyzing,
                            statusMessage = statusMessage,
                            onCropSelect = { viewModel.setSelectedCrop(it) },
                            onStageSelect = { viewModel.setSelectedStage(it) },
                            onBitmapChange = { viewModel.setCapturedBitmap(it) },
                            onSymptomChange = { viewModel.setSymptomInput(it) },
                            onDistrictChange = { viewModel.setSelectedDistrict(it) },
                            onAnalyzeClick = { viewModel.runCropAnalysis() }
                        )
                    }
                    AppScreen.SCAN_RESULT -> {
                        ScanResultScreen(
                            scan = currentScanResult,
                            bitmap = capturedBitmap,
                            language = language,
                            onSaveToPassport = { viewModel.saveCurrentScanToPassport() },
                            onFollowUp = {
                                currentScanResult?.let { viewModel.prepareFollowUp(it) }
                            },
                            onScanAgain = { viewModel.navigateTo(AppScreen.CROP_SCANNER) }
                        )
                    }
                    AppScreen.CROP_PASSPORT -> {
                        CropPassportScreen(
                            language = language,
                            scans = scansList,
                            onSelectScan = { scan ->
                                viewModel.prepareFollowUp(scan)
                            },
                            onFollowUp = { scan ->
                                viewModel.prepareFollowUp(scan)
                            },
                            onDeleteScan = { scanId ->
                                viewModel.deleteScan(scanId)
                            }
                        )
                    }
                    AppScreen.FOLLOW_UP_COMPARISON -> {
                        FollowUpComparisonScreen(
                            language = language,
                            parentScan = selectedScanForFollowUp,
                            followUpBitmap = followUpBitmap,
                            followUpNotes = followUpNotes,
                            comparisonResult = comparisonResult,
                            isComparing = isComparing,
                            onBitmapChange = { viewModel.setFollowUpBitmap(it) },
                            onNotesChange = { viewModel.setFollowUpNotes(it) },
                            onCompareClick = { viewModel.runFollowUpComparison() },
                            onBack = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                        )
                    }
                    AppScreen.SAFETY_CHECK -> {
                        SafetyCheckScreen(
                            language = language,
                            checks = safetyChecks,
                            onToggleCheck = { viewModel.toggleSafetyCheck(it) }
                        )
                    }
                    AppScreen.VOICE_ASSISTANT -> {
                        VoiceAssistantScreen(
                            language = language,
                            messages = chatMessages,
                            streamingText = streamingText,
                            isResponding = isResponding,
                            onSendMessage = { viewModel.sendAssistantMessage(it) },
                            onSpeakText = { viewModel.speakText(it) },
                            onStopSpeak = { viewModel.stopSpeaking() },
                            onOpenLangLocation = { viewModel.setLangLocationSheetOpen(true) }
                        )
                    }
                    AppScreen.ABOUT_APP -> {
                        AboutAppScreen(
                            language = language,
                            onBack = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                        )
                    }
                }
            }
        }
    }

    // Modal Bottom Sheet for 22 Languages & Geolocation Selection
    if (isLangLocationSheetOpen) {
        LanguageLocationBottomSheet(
            currentLanguage = language,
            languageMode = languageMode,
            selectedState = selectedState,
            selectedDistrict = selectedDistrict,
            locationConsent = locationConsent,
            isLocating = isLocating,
            onLanguageSelect = { selectedLang, mode ->
                viewModel.setLanguage(selectedLang, mode)
            },
            onLocationUpdate = { state, dist, consent ->
                viewModel.updateLocation(state, dist, consent)
            },
            onRequestGpsLocation = {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            },
            onDismiss = { viewModel.setLangLocationSheetOpen(false) }
        )
    }

    // Modal Bottom Sheet for Warnings and Notifications
    if (isWarningSheetOpen) {
        WarningNotificationBottomSheet(
            initialTab = warningSheetInitialTab,
            language = language,
            state = selectedState,
            district = selectedDistrict,
            onDismiss = { isWarningSheetOpen = false }
        )
    }
}
