package com.example.ui.screens

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.model.SafetyCheckItem
import com.example.ui.theme.DarkText
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.FreshLime
import com.example.ui.theme.FreshLimeLight
import com.example.ui.theme.SoftCream
import com.example.ui.theme.WarmAmber
import com.example.util.AppLanguage
import com.example.util.Localization

@Composable
fun SafetyCheckScreen(
    language: AppLanguage,
    checks: List<SafetyCheckItem>,
    onToggleCheck: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    val allChecked = checks.all { it.isChecked }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftCream)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ForestGreen),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(FreshLime),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = ForestGreen,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = Localization.get("safety_title", language),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = Localization.get("safety_subtitle", language),
                        style = MaterialTheme.typography.bodySmall.copy(color = FreshLimeLight)
                    )
                }
            }
        }

        // Status Card (Green if all verified, Amber if pending)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (allChecked) Color(0xFFE8F5E9) else WarmAmber.copy(alpha = 0.15f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (allChecked) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (allChecked) Color(0xFF2E7D32) else WarmAmber,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (allChecked)
                            "બધી સુરક્ષા શરતો ચકાસાયેલ છે! (Safe to Proceed)"
                        else
                            "ચેતવણી: બધી શરતો ચકાસો (Safety Verification Required)",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (allChecked) Color(0xFF2E7D32) else Color(0xFFD84315)
                        )
                    )
                    Text(
                        text = if (allChecked)
                            "યોગ્ય માત્રામાં નિયત માર્ગદર્શિકા મુજબ જ છંટકાવ કરો."
                        else
                            "દવા છાંટતા પહેલા નીચેના તમામ મુદ્દાઓ ચકાસીને ટીક કરો.",
                        style = MaterialTheme.typography.bodySmall.copy(color = DarkText)
                    )
                }
            }
        }

        // Interactive Checklist Items
        checks.forEach { checkItem ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleCheck(checkItem.id) }
                    .testTag("safety_check_item_${checkItem.id}")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Checkbox(
                        checked = checkItem.isChecked,
                        onCheckedChange = { onToggleCheck(checkItem.id) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = ForestGreen,
                            checkmarkColor = FreshLime
                        )
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = Localization.get(checkItem.questionKey, language),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DarkText
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = checkItem.explanationKey,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }

        // Sustainable IPM Advice Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = FreshLimeLight.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = ForestGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "સંકલિત જીવાત વ્યવસ્થાપન (IPM) સોનેરી નિયમો",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• આર્થિક નુકસાનની માત્રા (ETL) વટાવે ત્યારે જ કેમિકલ દવાનો આખરી ઉપાય તરીકે ઉપયોગ કરો.\n" +
                           "• પ્રકાશ પિંજર (Light Traps) અને ફેરમોન ટ્રેપ વડે જીવાતોની હાજરી નોંધો.\n" +
                           "• શરૂઆતના તબક્કામાં લીંબોળીનું અર્ક (NSKE 5%) સૌથી વધુ અસરકારક અને સલામત છે.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = DarkText,
                        lineHeight = 20.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
