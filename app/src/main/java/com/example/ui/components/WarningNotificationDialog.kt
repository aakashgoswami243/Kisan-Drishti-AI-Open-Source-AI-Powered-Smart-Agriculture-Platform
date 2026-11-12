package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderColor
import com.example.ui.theme.DarkGreen
import com.example.ui.theme.ErrorBackground
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.FreshLime
import com.example.ui.theme.InfoBackground
import com.example.ui.theme.InfoBlue
import com.example.ui.theme.LightGreenSurface
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SoftIvory
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningBackground
import com.example.ui.theme.WarningText
import com.example.util.AppLanguage
import com.example.util.Localization

data class AgriWarning(
    val id: String,
    val title: String,
    val description: String,
    val severity: String, // "warning", "critical", "info"
    val timestamp: String,
    val affectedCrops: List<String>
)

data class AgriNotification(
    val id: String,
    val title: String,
    val message: String,
    val timeAgo: String,
    val isRead: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarningNotificationBottomSheet(
    initialTab: Int = 0, // 0 = Warnings, 1 = Notifications
    language: AppLanguage,
    state: String,
    district: String,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTab by remember { mutableIntStateOf(initialTab) }

    val sampleWarnings = remember(district, state) {
        listOf(
            AgriWarning(
                id = "w1",
                title = if (language == AppLanguage.GUJARATI) "ગુલાબી ઈયળ (Pink Bollworm) ચેતવણી" else "Pink Bollworm Advisory",
                description = if (language == AppLanguage.GUJARATI)
                    "$district અને આસપાસના વિસ્તારોમાં કપાસમાં ફૂલ-ઝીંડવા અવસ્થાએ ફેરોમોન ટ્રેપ ચકાસવા વિનંતી. એકરે ૫ ટ્રેપ લગાવો."
                else
                    "Pheromone trap surveillance recommended for cotton fields in $district. Install 5 traps per acre.",
                severity = "warning",
                timestamp = "Today, 08:30 AM",
                affectedCrops = listOf("Cotton")
            ),
            AgriWarning(
                id = "w2",
                title = if (language == AppLanguage.GUJARATI) "હવામાન સાવચેતી: અચાનક પવન" else "Weather Advisory: High Wind",
                description = if (language == AppLanguage.GUJARATI)
                    "આગામી ૨૪ કલાકમાં તેજ પવનની શક્યતા હોવાથી જંતુનાશક દવાનો છંટકાવ મુલતવી રાખવો."
                else
                    "Avoid chemical spraying for the next 24 hours due to sudden gusts exceeding 20 km/h.",
                severity = "critical",
                timestamp = "Yesterday",
                affectedCrops = listOf("All Crops")
            ),
            AgriWarning(
                id = "w3",
                title = if (language == AppLanguage.GUJARATI) "મગફળીમાં ગેરુ / ટિક્કા રોગ અટકાવ" else "Groundnut Tikka Prevention",
                description = if (language == AppLanguage.GUJARATI)
                    "નીચલા પાન પર કાળા ટપકાં દેખાય તો ટ્રાઇકોડર્મા અથવા નીમ તેલનો પ્રતિબંધાત્મક છંટકાવ કરવો."
                else
                    "Monitor lower leaves for dark circular spots. Consider preventive bio-fungicide application.",
                severity = "info",
                timestamp = "2 days ago",
                affectedCrops = listOf("Groundnut")
            )
        )
    }

    val sampleNotifications = remember {
        listOf(
            AgriNotification(
                id = "n1",
                title = if (language == AppLanguage.GUJARATI) "પાક સ્કેન વિશ્લેષણ સાચવાયું" else "Crop Scan Saved",
                message = if (language == AppLanguage.GUJARATI)
                    "તમારા કપાસ પાકનું AI હેલ્થ રિપોર્ટ તમારા ડિજિટલ પાસપોર્ટમાં ઉમેરાઈ ગયું છે."
                else
                    "Your cotton AI crop health diagnosis was successfully saved to your Passport.",
                timeAgo = "10 mins ago",
                isRead = false
            ),
            AgriNotification(
                id = "n2",
                title = if (language == AppLanguage.GUJARATI) "સુરક્ષા ચેકલિસ્ટ પૂર્ણ" else "Safety Checklist Completed",
                message = if (language == AppLanguage.GUJARATI)
                    "તમે છંટકાવ પહેલાંની સુરક્ષા શરતો ચકાસી લીધી છે. મિત્ર કીટકોનું રક્ષણ કરો."
                else
                    "You verified the spray checklist. Remember to protect active pollinators.",
                timeAgo = "3 hours ago",
                isRead = true
            ),
            AgriNotification(
                id = "n3",
                title = if (language == AppLanguage.GUJARATI) "AI સહાયક અપડેટ" else "Kisan Drishti AI Ready",
                message = if (language == AppLanguage.GUJARATI)
                    "Gemini 3.8 Flash મોડલ હવે ૨૨ ભારતીય ભાષાઓમાં ત્વરિત સહાય માટે સક્રિય છે."
                else
                    "Gemini 3.8 Flash is active with full multilingual agricultural reasoning.",
                timeAgo = "1 day ago",
                isRead = true
            )
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SoftIvory,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (selectedTab == 0) WarningBackground else LightGreenSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (selectedTab == 0) Icons.Default.WarningAmber else Icons.Default.Notifications,
                            contentDescription = null,
                            tint = if (selectedTab == 0) WarningText else PrimaryGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (selectedTab == 0) "કૃષિ ચેતવણી કેન્દ્ર (Alerts)" else "સૂચનાઓ (Notifications)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimaryGreen
                            )
                        )
                        Text(
                            text = "$district, $state • Live Agro Updates",
                            style = MaterialTheme.typography.bodySmall.copy(color = SecondaryText)
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_warning_dialog")
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = PrimaryText)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = PureWhite,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = if (selectedTab == 0) WarningAmber else PrimaryGreen,
                        height = 3.dp
                    )
                },
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.WarningAmber, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ચેતવણીઓ / Warnings", fontWeight = FontWeight.Bold)
                        }
                    },
                    selectedContentColor = WarningText,
                    unselectedContentColor = SecondaryText
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("સૂચના / Notices", fontWeight = FontWeight.Bold)
                        }
                    },
                    selectedContentColor = PrimaryGreen,
                    unselectedContentColor = SecondaryText
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedTab == 0) {
                // Warning Center Content
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(sampleWarnings) { warning ->
                        val isCritical = warning.severity == "critical"
                        val bgColor = if (isCritical) ErrorBackground else WarningBackground
                        val iconTint = if (isCritical) ErrorRed else WarningAmber
                        val titleColor = if (isCritical) ErrorRed else WarningText
                        val descColor = if (isCritical) Color(0xFF6B1D1D) else WarningText

                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = bgColor),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isCritical) ErrorRed.copy(alpha = 0.3f) else WarningAmber.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (isCritical) Icons.Default.Warning else Icons.Default.WarningAmber,
                                            contentDescription = null,
                                            tint = iconTint,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = warning.title,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = titleColor
                                            )
                                        )
                                    }
                                    Text(
                                        text = warning.timestamp,
                                        style = MaterialTheme.typography.bodySmall.copy(color = descColor, fontSize = 11.sp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = warning.description,
                                    style = MaterialTheme.typography.bodyMedium.copy(color = descColor, fontSize = 13.sp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    warning.affectedCrops.forEach { crop ->
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = PureWhite
                                        ) {
                                            Text(
                                                text = "🌱 $crop",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = PrimaryText,
                                                    fontWeight = FontWeight.SemiBold
                                                ),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Notification Center Content
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(sampleNotifications) { notif ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = PureWhite),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(14.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(if (!notif.isRead) LightGreenSurface else InfoBackground),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (!notif.isRead) Icons.Default.Notifications else Icons.Default.Info,
                                        contentDescription = null,
                                        tint = if (!notif.isRead) PrimaryGreen else InfoBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = notif.title,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = PrimaryText
                                            )
                                        )
                                        Text(
                                            text = notif.timeAgo,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = SecondaryText,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = notif.message,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = SecondaryText,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
