package com.example.ui.components

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.example.ui.AppScreen
import com.example.ui.theme.DarkGreen
import com.example.ui.theme.DarkText
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.FreshLime
import com.example.ui.theme.FreshLimeLight
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SoftCream
import com.example.ui.theme.WarmAmber
import com.example.util.AppLanguage
import com.example.util.Localization
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KisanTopHeader(
    language: AppLanguage,
    district: String = "Rajkot",
    onOpenLangLocation: () -> Unit,
    onOpenWarnings: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    onNavigateAbout: () -> Unit
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.kisan_logo),
                    contentDescription = "Kisan Drishti Logo",
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = Localization.get("app_title", language),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Smart Farming Assistant",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = FreshLimeLight,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        },
        actions = {
            // Warning Center icon (Amber)
            IconButton(
                onClick = onOpenWarnings,
                modifier = Modifier
                    .size(34.dp)
                    .testTag("header_warning_button")
            ) {
                Icon(
                    imageVector = Icons.Default.WarningAmber,
                    contentDescription = "Warning Center",
                    tint = WarmAmber,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Notification Center icon (White)
            IconButton(
                onClick = onOpenNotifications,
                modifier = Modifier
                    .size(34.dp)
                    .testTag("header_notification_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notification Center",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            // About App button
            IconButton(
                onClick = onNavigateAbout,
                modifier = Modifier
                    .size(34.dp)
                    .testTag("about_app_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "About Kisan Drishti AI",
                    tint = FreshLime,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Language & Location Combined Pill
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = ForestGreen.copy(alpha = 0.85f),
                border = androidx.compose.foundation.BorderStroke(1.dp, FreshLime.copy(alpha = 0.5f)),
                modifier = Modifier
                    .clickable { onOpenLangLocation() }
                    .testTag("language_location_selector")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Language",
                        tint = FreshLime,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = language.nativeName,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("•", color = FreshLimeLight, fontSize = 10.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = FreshLimeLight,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = district,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = FreshLimeLight,
                            fontSize = 11.sp
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.width(6.dp))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = ForestGreen
        )
    )
}

@Composable
fun KisanBottomBar(
    currentScreen: AppScreen,
    language: AppLanguage,
    onNavigate: (AppScreen) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("kisan_bottom_nav")
    ) {
        val items = listOf(
            Triple(AppScreen.DASHBOARD, Localization.get("nav_home", language), Icons.Default.Home),
            Triple(AppScreen.CROP_SCANNER, Localization.get("nav_scan", language), Icons.Default.CameraAlt),
            Triple(AppScreen.CROP_PASSPORT, Localization.get("nav_passport", language), Icons.Default.FolderShared),
            Triple(AppScreen.VOICE_ASSISTANT, Localization.get("nav_assistant", language), Icons.Default.Mic),
            Triple(AppScreen.SAFETY_CHECK, Localization.get("nav_safety", language), Icons.Default.Security)
        )

        items.forEach { (screen, label, icon) ->
            val selected = currentScreen == screen
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(screen) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        maxLines = 1,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = DarkGreen,
                    selectedTextColor = DarkGreen,
                    indicatorColor = FreshLime,
                    unselectedIconColor = SecondaryText,
                    unselectedTextColor = SecondaryText
                )
            )
        }
    }
}

@Composable
fun UncertaintyBadge(level: String, language: AppLanguage) {
    val (bgColor, textColor, labelText) = when (level.lowercase()) {
        "low", "ઓછી", "कम" -> Triple(
            Color(0xFFE8F5E9),
            Color(0xFF2E7D32),
            if (language == AppLanguage.GUJARATI) "ઓછી અચોક્કસતા (ઉચ્ચ ચોકસાઈ)" else if (language == AppLanguage.HINDI) "कम अनिश्चितता (सटीक)" else "Low Uncertainty (Reliable)"
        )
        "medium", "મધ્યમ" -> Triple(
            Color(0xFFFFF8E1),
            Color(0xFFF57F17),
            if (language == AppLanguage.GUJARATI) "મધ્યમ અચોક્કસતા (તપાસ જરૂરી)" else if (language == AppLanguage.HINDI) "मध्यम अनिश्चितता" else "Medium Uncertainty"
        )
        else -> Triple(
            Color(0xFFFFEBEE),
            Color(0xFFC62828),
            if (language == AppLanguage.GUJARATI) "વધુ અચોક્કસતા (તજજ્ઞ તપાસ કરો)" else if (language == AppLanguage.HINDI) "उच्च अनिश्चितता (विशेषज्ञ सलाह)" else "High Uncertainty (Verify)"
        )
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = if (level.lowercase() == "low") Icons.Default.CheckCircle else Icons.Default.Warning,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = labelText,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun GoogleSignInCard(
    language: AppLanguage,
    onAuthSuccess: () -> Unit,
    onGuestMode: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.kisan_logo),
                contentDescription = "Kisan Drishti AI Logo",
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = Localization.get("sign_in_google", language),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "ગૂગલ એકાઉન્ટથી સાઇન ઇન કરી તમારો પાક પાસપોર્ટ અને સ્કેન સુરક્ષિત ક્લાઉડમાં સાચવો.",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    isLoading = true
                    errorMessage = null
                    performGoogleSignIn(
                        context = context,
                        credentialManager = credentialManager,
                        scope = coroutineScope,
                        onSuccess = {
                            isLoading = false
                            onAuthSuccess()
                        },
                        onError = { err ->
                            isLoading = false
                            errorMessage = err
                        },
                        onCancelled = {
                            isLoading = false
                        }
                    )
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("google_sign_in_button")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = Localization.get("sign_in_google", language),
                        style = MaterialTheme.typography.labelLarge.copy(color = Color.White)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onGuestMode,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("guest_mode_button")
            ) {
                Text(
                    text = Localization.get("guest_mode", language),
                    style = MaterialTheme.typography.labelMedium.copy(color = ForestGreen)
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Red)
                )
            }
        }
    }
}

private fun performGoogleSignIn(
    context: Context,
    credentialManager: CredentialManager,
    scope: CoroutineScope,
    onSuccess: () -> Unit,
    onError: (String) -> Unit,
    onCancelled: () -> Unit
) {
    val clientId = try {
        context.getString(R.string.default_web_client_id)
    } catch (e: Exception) {
        onError("Google Client ID not found: ${e.message}")
        return
    }

    val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
    val request = GetCredentialRequest.Builder().addCredentialOption(signInOption).build()

    scope.launch {
        try {
            val result = credentialManager.getCredential(context as Activity, request)
            val credential = result.credential
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                Firebase.auth.signInWithCredential(authCredential).await()
                onSuccess()
            } else {
                onError("Unexpected credential returned")
            }
        } catch (e: GetCredentialCancellationException) {
            Log.w("Auth", "Google Sign-In flow cancelled: ${e.message}", e)
            onCancelled()
        } catch (e: Exception) {
            Log.e("Auth", "Google Sign-In failed", e)
            onError(e.localizedMessage ?: "Sign-in failed")
        }
    }
}
