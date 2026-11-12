package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import com.example.R
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.example.model.CropScan
import com.example.ui.components.UncertaintyBadge
import com.example.ui.theme.DarkText
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.FreshLime
import com.example.ui.theme.FreshLimeLight
import com.example.ui.theme.SoftCream
import com.example.util.AppLanguage
import com.example.util.Localization

@Composable
fun CropPassportScreen(
    language: AppLanguage,
    scans: List<CropScan>,
    onSelectScan: (CropScan) -> Unit,
    onFollowUp: (CropScan) -> Unit,
    onDeleteScan: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val filteredScans = scans.filter { scan ->
        val matchesCrop = if (selectedFilter == "All") true else scan.cropName.equals(selectedFilter, ignoreCase = true)
        val matchesQuery = if (searchQuery.isBlank()) true else {
            scan.cropName.contains(searchQuery, ignoreCase = true) ||
            scan.symptomsDescription.contains(searchQuery, ignoreCase = true) ||
            scan.visibleSymptoms.any { it.contains(searchQuery, ignoreCase = true) } ||
            scan.possibleCauses.any { it.contains(searchQuery, ignoreCase = true) }
        }
        matchesCrop && matchesQuery
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftCream)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Passport Header Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ForestGreen),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.kisan_logo),
                        contentDescription = "Kisan Drishti Passport Emblem",
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = Localization.get("passport_title", language),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = Localization.get("passport_subtitle", language),
                            style = MaterialTheme.typography.bodySmall.copy(color = FreshLimeLight)
                        )
                    }
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(Localization.get("search_scans", language), fontSize = 13.sp) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = ForestGreen) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("passport_search_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ForestGreen,
                    unfocusedBorderColor = Color.LightGray
                )
            )
        }

        // Filter Chips Row
        item {
            val filterOptions = listOf("All", "Cotton", "Groundnut", "Wheat")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filterOptions) { opt ->
                    val isSelected = selectedFilter == opt
                    val displayLabel = when (opt) {
                        "All" -> Localization.get("filter_all", language)
                        "Cotton" -> Localization.get("crop_cotton", language)
                        "Groundnut" -> Localization.get("crop_groundnut", language)
                        "Wheat" -> Localization.get("crop_wheat", language)
                        else -> opt
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) ForestGreen else Color.White,
                        tonalElevation = 2.dp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { selectedFilter = opt }
                    ) {
                        Text(
                            text = displayLabel,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = if (isSelected) Color.White else DarkText,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Empty state
        if (filteredScans.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "કોઈ સ્કેન રેકોર્ડ મળ્યા નથી",
                            style = MaterialTheme.typography.titleMedium.copy(color = Color.Gray)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "નવો પાક સ્કેન કરો જેથી તે તમારા પાક પાસપોર્ટમાં ઉમેરાય.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray)
                        )
                    }
                }
            }
        } else {
            items(filteredScans) { scan ->
                PassportItemCard(
                    scan = scan,
                    language = language,
                    onView = { onSelectScan(scan) },
                    onFollowUp = { onFollowUp(scan) },
                    onDelete = { onDeleteScan(scan.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun PassportItemCard(
    scan: CropScan,
    language: AppLanguage,
    onView: () -> Unit,
    onFollowUp: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
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
                    text = "લક્ષણો: ${scan.visibleSymptoms.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = DarkText
                    ),
                    maxLines = 2
                )
            }

            if (scan.possibleCauses.isNotEmpty()) {
                Text(
                    text = "સંભાવના: ${scan.possibleCauses.first()}",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.DarkGray)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ForestGreen,
                        modifier = Modifier
                            .clickable { onView() }
                            .testTag("view_details_${scan.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Visibility, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = Localization.get("view_details", language), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = FreshLime,
                        modifier = Modifier
                            .clickable { onFollowUp() }
                            .testTag("follow_up_${scan.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Timeline, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = Localization.get("follow_up_scan", language), color = ForestGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.LightGray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
