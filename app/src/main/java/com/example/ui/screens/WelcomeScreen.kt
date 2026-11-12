package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.GoogleSignInCard
import com.example.ui.theme.DarkText
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.FreshLime
import com.example.ui.theme.FreshLimeLight
import com.example.ui.theme.SoftCream
import com.example.ui.theme.WarmAmber
import com.example.util.AppLanguage
import com.example.util.Localization

@Composable
fun WelcomeScreen(
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onOpenAllLanguages: () -> Unit = {},
    onGetStarted: () -> Unit,
    onAuthSuccess: () -> Unit,
    onGuestMode: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftCream)
            .verticalScroll(scrollState)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hero Visual Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.kisan_hero_banner_1794373231183),
                contentDescription = "Kisan Drishti Hero Agricultural Visual",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f))
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.kisan_logo),
                        contentDescription = "Kisan Drishti AI Logo",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = FreshLime
                        ) {
                            Text(
                                text = "AI AGRICULTURAL COPILOT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = ForestGreen,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = Localization.get("app_title", language),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = Localization.get("tagline", language),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = FreshLimeLight,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Language Selector: Quick Pills + 22 Languages
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val quickLangs = listOf(AppLanguage.GUJARATI, AppLanguage.HINDI, AppLanguage.ENGLISH)
            quickLangs.forEach { lang ->
                val selected = language == lang
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (selected) ForestGreen else Color.White,
                    tonalElevation = 2.dp,
                    modifier = Modifier.padding(horizontal = 3.dp)
                ) {
                    OutlinedButton(
                        onClick = { onLanguageChange(lang) },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (selected) ForestGreen else Color.White,
                            contentColor = if (selected) Color.White else DarkText
                        ),
                        border = null,
                        modifier = Modifier.height(38.dp)
                    ) {
                        Text(
                            text = lang.nativeName,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                }
            }
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = FreshLime,
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .clickable { onOpenAllLanguages() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "+૨૨ ભાષા",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Project Explanation Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = Localization.get("welcome_hero_sub", language),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = DarkText,
                        lineHeight = 22.sp
                    )
                )
                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onGetStarted,
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("get_started_button")
                ) {
                    Text(
                        text = Localization.get("get_started", language),
                        style = MaterialTheme.typography.labelLarge.copy(color = Color.White)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // How it Works Feature Highlights
        Text(
            text = if (language == AppLanguage.GUJARATI) "કિસાન દ્રષ્ટિ AI કેવી રીતે કામ કરે છે?" else "How Kisan Drishti AI Works",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = ForestGreen
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))

        FeatureHighlightCard(
            icon = Icons.Default.CameraAlt,
            title = if (language == AppLanguage.GUJARATI) "૧. પાકનો ફોટો લો" else "1. Capture Crop Photo",
            description = if (language == AppLanguage.GUJARATI) "કપાસ, મગફળી કે ઘઉંના પાંદડા અથવા ડાળીનો સ્પષ્ટ ફોટો તમારા મોબાઈલથી લો." else "Snap a clear photograph of crop foliage, stem or spots using your camera."
        )

        FeatureHighlightCard(
            icon = Icons.Default.Agriculture,
            title = if (language == AppLanguage.GUJARATI) "૨. જેમિની AI વિશ્લેષણ" else "2. Gemini Multimodal Analysis",
            description = if (language == AppLanguage.GUJARATI) "ગૂગલ જેમિની AI પાકના લક્ષણો અને રોગ/જીવાતની સંભાવના સ્પષ્ટ રીતે સમજાવે છે." else "Google Gemini AI observes visible symptoms and explains possible causes safely."
        )

        FeatureHighlightCard(
            icon = Icons.Default.FolderShared,
            title = if (language == AppLanguage.GUJARATI) "૩. ડિજિટલ ક્રોપ પાસપોર્ટ" else "3. Crop Health Passport",
            description = if (language == AppLanguage.GUJARATI) "સમય સાથે પાકના આરોગ્યની હિસ્ટ્રી સાચવો અને ફોલો-અપ સરખામણીથી સુધારો ચકાસો." else "Track your farm's health timeline with follow-up photographic comparisons."
        )

        FeatureHighlightCard(
            icon = Icons.Default.Mic,
            title = if (language == AppLanguage.GUJARATI) "૪. ગુજરાતી AI કૃષિ મિત્ર" else "4. Gujarati Voice Assistant",
            description = if (language == AppLanguage.GUJARATI) "તમારી માતૃભાષામાં ખેતીવાડીનો કોઈપણ પ્રશ્ન બોલીને કે લખીને પૂછો." else "Ask voice or text agricultural questions in your local regional language."
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Authentication Card
        GoogleSignInCard(
            language = language,
            onAuthSuccess = onAuthSuccess,
            onGuestMode = onGuestMode
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Safety Disclaimer Footer
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = WarmAmber.copy(alpha = 0.12f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Safety",
                    tint = ForestGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = Localization.get("disclaimer_text", language),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = DarkText,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun FeatureHighlightCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(FreshLimeLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ForestGreen,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.DarkGray)
                )
            }
        }
    }
}
