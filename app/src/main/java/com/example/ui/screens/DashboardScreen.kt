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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CropScan
import com.example.model.UserProfile
import com.example.ui.AppScreen
import com.example.ui.components.UncertaintyBadge
import com.example.ui.theme.DarkText
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.FreshLime
import com.example.ui.theme.FreshLimeLight
import com.example.ui.theme.SoftCream
import com.example.ui.theme.WarmAmber
import com.example.util.AppLanguage
import com.example.util.Localization

@Composable
fun DashboardScreen(
    profile: UserProfile,
    language: AppLanguage,
    scansList: List<CropScan>,
    state: String = "Gujarat",
    district: String = "Rajkot",
    suggestedLanguage: AppLanguage? = null,
    onOpenLangLocation: () -> Unit = {},
    onAcceptSuggestedLanguage: (AppLanguage) -> Unit = {},
    onDismissSuggestedLanguage: () -> Unit = {},
    onNavigate: (AppScreen) -> Unit,
    onSelectScan: (CropScan) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftCream)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Optional Regional Language Suggestion Banner
        if (suggestedLanguage != null) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = WarmAmber.copy(alpha = 0.15f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WarmAmber),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🌐", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = Localization.get("regional_suggestion_title", language),
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DarkText
                                    )
                                )
                            }
                            IconButton(
                                onClick = onDismissSuggestedLanguage,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Text("✕", color = Color.Gray, fontSize = 14.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = Localization.format("regional_suggestion_body", language, state, "${suggestedLanguage.nativeName} (${suggestedLanguage.englishName})"),
                            style = MaterialTheme.typography.bodySmall.copy(color = DarkText, fontSize = 12.sp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Button(
                                onClick = { onAcceptSuggestedLanguage(suggestedLanguage) },
                                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text(
                                    text = Localization.get("switch_now", language),
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedButton(
                                onClick = onDismissSuggestedLanguage,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text(
                                    text = Localization.get("keep_current", language),
                                    style = MaterialTheme.typography.labelSmall.copy(color = ForestGreen)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(2.dp))
            // Farmer Greeting Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ForestGreen),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = Localization.get("greeting_farmer", language),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = FreshLime,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = profile.displayName.ifBlank { "ખેડૂત મિત્ર" },
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Image(
                            painter = painterResource(id = R.drawable.kisan_logo),
                            contentDescription = "Kisan Drishti AI Logo",
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = ForestGreen.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FreshLime.copy(alpha = 0.4f)),
                        modifier = Modifier.clickable { onOpenLangLocation() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "District",
                                tint = FreshLime,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$district, $state • ${com.example.util.AgroSeason.getCurrent().englishName}",
                                style = MaterialTheme.typography.bodySmall.copy(color = FreshLimeLight, fontWeight = FontWeight.Medium)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "✎ બદલો",
                                style = MaterialTheme.typography.labelSmall.copy(color = FreshLime, fontSize = 10.sp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Primary Scan CTA Button
                    Button(
                        onClick = { onNavigate(AppScreen.CROP_SCANNER) },
                        colors = ButtonDefaults.buttonColors(containerColor = FreshLime),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("scan_crop_cta_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddAPhoto,
                            contentDescription = null,
                            tint = ForestGreen,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Localization.get("scan_crop_cta", language),
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = ForestGreen,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }

        // Quick Navigation Tiles
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Passport Tile
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigate(AppScreen.CROP_PASSPORT) }
                        .testTag("passport_tile")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE3F2FD)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FolderShared,
                                contentDescription = null,
                                tint = Color(0xFF1976D2),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = Localization.get("nav_passport", language),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )
                        )
                        Text(
                            text = "${scansList.size} Scans Saved",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                        )
                    }
                }

                // AI Voice Assistant Tile
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigate(AppScreen.VOICE_ASSISTANT) }
                        .testTag("assistant_tile")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(FreshLimeLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = ForestGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = Localization.get("nav_assistant", language),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )
                        )
                        Text(
                            text = "AI Voice Help",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                        )
                    }
                }
            }
        }

        // Before You Spray Safety Check Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = WarmAmber.copy(alpha = 0.15f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(AppScreen.SAFETY_CHECK) }
                    .testTag("safety_check_card")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(WarmAmber),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = Localization.get("safety_title", language),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )
                        )
                        Text(
                            text = "દવા છાંટતા પહેલા સુરક્ષા ચેકલિસ્ટ ચકાસો",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.DarkGray)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = DarkText
                    )
                }
            }
        }

        // Recent Scans Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Localization.get("recent_scans", language),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                )

                Text(
                    text = "બધા જુઓ >",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = ForestGreen,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.clickable { onNavigate(AppScreen.CROP_PASSPORT) }
                )
            }
        }

        // Recent Scans Cards or Empty State
        if (scansList.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Spa,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = Localization.get("no_scans_yet", language),
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(scansList.take(3)) { scan ->
                RecentScanCard(
                    scan = scan,
                    language = language,
                    onClick = { onSelectScan(scan) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun RecentScanCard(
    scan: CropScan,
    language: AppLanguage,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("recent_scan_card_${scan.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = FreshLimeLight
                    ) {
                        Text(
                            text = scan.cropName,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = ForestGreen,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = scan.cropStage,
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )
                }

                UncertaintyBadge(level = scan.uncertaintyLevel, language = language)
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (scan.visibleSymptoms.isNotEmpty()) {
                Text(
                    text = "લક્ષણ: ${scan.visibleSymptoms.first()}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = DarkText
                    )
                )
            }

            if (scan.possibleCauses.isNotEmpty()) {
                Text(
                    text = "સંભાવના: ${scan.possibleCauses.first()}",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.DarkGray)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Localization.get("view_details", language),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = ForestGreen,
                        fontWeight = FontWeight.Bold
                    )
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = ForestGreen,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
