package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.DarkText
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.FreshLime
import com.example.ui.theme.FreshLimeLight
import com.example.ui.theme.SoftCream
import com.example.ui.theme.WarmAmber
import com.example.util.AppLanguage
import com.example.util.Localization

@Composable
fun AboutAppScreen(
    language: AppLanguage = AppLanguage.GUJARATI,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftCream)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("about_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = ForestGreen
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = Localization.get("about_title", language),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                )
                Text(
                    text = Localization.get("about_subtitle", language),
                    style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                )
            }
        }

        // Section 1: Vision & Brand Identity
        AboutInfoCard(
            sectionTitle = "Kisan Drishti AI",
            sectionBadge = "Overview",
            badgeColor = FreshLimeLight
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.kisan_logo),
                    contentDescription = "Kisan Drishti AI Official Emblem",
                    modifier = Modifier
                        .size(108.dp)
                        .clip(RoundedCornerShape(20.dp))
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Every Farmer Deserves an AI Agricultural Expert",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "• Mission: Empower Indian farmers with instant, localized crop health diagnostics using Gemini AI.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = DarkText)
                    )
                    Text(
                        "• Primary Coverage: Small & marginal farmers with cotton, groundnut, wheat, and seasonal crops.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = DarkText)
                    )
                    Text(
                        "• Core Value: Early diagnosis without expensive physical sensor setups or delayed field officer visits.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = DarkText)
                    )
                }
            }
        }

        // Section 2: Problem & Solution
        AboutInfoCard(
            sectionTitle = "The Agricultural Need & Solution",
            sectionBadge = "Agricultural Impact",
            badgeColor = FreshLimeLight
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "• The Scarcity Challenge: Agricultural extension officers serve thousands of farmers across remote rural villages, leading to days of delayed diagnosis.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = DarkText)
                )
                Text(
                    "• Language Inclusivity: Complex laboratory sheets and English-only chemical literature alienate farmers. Kisan Drishti AI offers full Gujarati, Hindi, and 20 other Indian languages.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = DarkText)
                )
                Text(
                    "• Preventing Over-Spraying: Unverified chemical tank mixes degrade soil health and drive up farmer debt. Our system prioritizes Integrated Pest Management (IPM) and organic remedies first.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = DarkText)
                )
            }
        }

        // Section 3: Architecture & Security
        AboutInfoCard(
            sectionTitle = "Technology & Data Security",
            sectionBadge = "Zero-Trust Architecture",
            badgeColor = FreshLimeLight
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "1. Mobile Frontend: Native Android with Jetpack Compose and Material Design 3.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = DarkText)
                )
                Text(
                    "2. AI Processing: Firebase AI Logic SDK with Google Gemini 3.8 Flash multimodal reasoning.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = DarkText)
                )
                Text(
                    "3. Backend Isolation: Google Cloud Firestore with per-user data isolation and granular security rules.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = DarkText)
                )
                Text(
                    "4. Farmer Authentication: Google Sign-In via Android Credential Manager.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = DarkText)
                )
            }
        }

        // Section 4: Multilingual & Geolocation
        AboutInfoCard(
            sectionTitle = "22 Scheduled Indian Languages",
            sectionBadge = "Nationwide Reach",
            badgeColor = FreshLimeLight
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "• Complete Localization: Gujarati (ગુજરાતી), Hindi (हिन्दी), English, and all scheduled Indian languages supported.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = DarkText)
                )
                Text(
                    "• Smart Geolocation: Automatic regional language suggestions based on State & District without violating user preference.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = DarkText)
                )
                Text(
                    "• Voice Capabilities: Integrated Android Text-to-Speech and Speech Recognition for intuitive audio guidance.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = DarkText)
                )
            }
        }

        // Section 5: Farmer Safety Commitment
        AboutInfoCard(
            sectionTitle = "Farmer Safety Commitment",
            sectionBadge = "Safety First",
            badgeColor = WarmAmber.copy(alpha = 0.2f)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "• AI Uncertainty Transparency: The app clearly states diagnostic confidence (Low, Medium, High) and never manufactures false certainty.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = DarkText)
                )
                Text(
                    "• KVK Expert Escalation: When visual symptoms are ambiguous, the system prompts immediate in-person referral to the nearest Krishi Vigyan Kendra.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = DarkText)
                )
                Text(
                    "• Spray Safety Checklist: Dedicated checklist to ensure personal protective equipment (PPE), weather check, and pollinator preservation.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = DarkText)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Jai Jawan, Jai Kisan!",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = ForestGreen,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun AboutInfoCard(
    sectionTitle: String,
    sectionBadge: String,
    badgeColor: Color,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeColor
                ) {
                    Text(
                        text = sectionBadge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = ForestGreen,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = FreshLime,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = sectionTitle,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = ForestGreen
                )
            )

            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}
