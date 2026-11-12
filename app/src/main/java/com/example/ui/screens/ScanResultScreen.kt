package com.example.ui.screens

import android.graphics.Bitmap
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
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CropScan
import com.example.ui.components.UncertaintyBadge
import com.example.ui.theme.DarkText
import com.example.ui.theme.ErrorContainer
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.FreshLime
import com.example.ui.theme.FreshLimeLight
import com.example.ui.theme.SoftCream
import com.example.ui.theme.WarmAmber
import com.example.util.AppLanguage
import com.example.util.Localization

@Composable
fun ScanResultScreen(
    scan: CropScan?,
    bitmap: Bitmap?,
    language: AppLanguage,
    onSaveToPassport: () -> Unit,
    onFollowUp: () -> Unit,
    onScanAgain: () -> Unit
) {
    if (scan == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SoftCream),
            contentAlignment = Alignment.Center
        ) {
            Text("કોઈ પરિણામ મળ્યું નથી. કૃપા કરીને પાક સ્કેન કરો.")
        }
        return
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftCream)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Image & Header Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Analyzed crop photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = scan.cropName,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreen
                                )
                            )
                            if (scan.cropStage.isNotBlank()) {
                                Text(
                                    text = "અવસ્થા: ${scan.cropStage}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                                )
                            }
                        }

                        UncertaintyBadge(level = scan.uncertaintyLevel, language = language)
                    }
                }
            }
        }

        // Section 1: Visible Symptoms Observed
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = ForestGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Localization.get("visible_symptoms", language),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                scan.visibleSymptoms.forEach { symptom ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text("•", color = ForestGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = symptom,
                            style = MaterialTheme.typography.bodyMedium.copy(color = DarkText)
                        )
                    }
                }
            }
        }

        // Section 2: Possible Causes (Hypotheses)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = WarmAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Localization.get("possible_causes", language),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                scan.possibleCauses.forEach { cause ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = FreshLimeLight.copy(alpha = 0.5f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = cause,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = DarkText
                            ),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }

        // Section 3: Missing Information Needed
        if (scan.missingInformation.isNotEmpty()) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                            contentDescription = null,
                            tint = Color(0xFF1976D2),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Localization.get("missing_info", language),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = ForestGreen
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    scan.missingInformation.forEach { question ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("?", color = Color(0xFF1976D2), fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = question,
                                style = MaterialTheme.typography.bodyMedium.copy(color = DarkText)
                            )
                        }
                    }
                }
            }
        }

        // Section 4: Safe Next Steps
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Localization.get("safe_next_steps", language),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                scan.safeNextSteps.forEachIndexed { index, step ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = ForestGreen,
                            modifier = Modifier.size(20.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${index + 1}",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = step,
                            style = MaterialTheme.typography.bodyMedium.copy(color = DarkText)
                        )
                    }
                }
            }
        }

        // Section 5: Expert Referral Advisory
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (scan.expertReferralRequired) ErrorContainer.copy(alpha = 0.3f) else FreshLimeLight.copy(alpha = 0.4f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = ForestGreen,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (scan.expertReferralRequired)
                        Localization.get("expert_needed_msg", language)
                    else
                        Localization.get("expert_not_urgent_msg", language),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = DarkText
                    )
                )
            }
        }

        // Mandatory Disclaimer Notice
        Text(
            text = scan.disclaimer.ifBlank { Localization.get("disclaimer_text", language) },
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color.Gray,
                fontSize = 11.sp
            ),
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        // Action Buttons Row
        Button(
            onClick = onSaveToPassport,
            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("save_to_passport_button")
        ) {
            Icon(imageVector = Icons.Default.BookmarkBorder, contentDescription = null, tint = FreshLime)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = Localization.get("save_to_passport", language),
                style = MaterialTheme.typography.labelLarge.copy(color = Color.White)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onFollowUp,
                colors = ButtonDefaults.buttonColors(containerColor = FreshLime),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("follow_up_scan_button")
            ) {
                Icon(imageVector = Icons.Default.Timeline, contentDescription = null, tint = ForestGreen)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = Localization.get("follow_up_scan", language),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = ForestGreen,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            OutlinedButton(
                onClick = onScanAgain,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("scan_again_button")
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = ForestGreen)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = Localization.get("scan_again", language),
                    style = MaterialTheme.typography.labelMedium.copy(color = ForestGreen)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
