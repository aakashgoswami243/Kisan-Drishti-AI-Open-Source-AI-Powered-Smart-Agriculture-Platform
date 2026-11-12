package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.theme.DarkText
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.FreshLime
import com.example.ui.theme.FreshLimeLight
import com.example.ui.theme.SoftCream
import com.example.ui.theme.WarmAmber
import com.example.util.AgroSeason
import com.example.util.AppLanguage
import com.example.util.IndiaGeoData
import com.example.util.LanguageMode
import com.example.util.Localization

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageLocationBottomSheet(
    currentLanguage: AppLanguage,
    languageMode: LanguageMode,
    selectedState: String,
    selectedDistrict: String,
    locationConsent: Boolean,
    isLocating: Boolean,
    onLanguageSelect: (AppLanguage, LanguageMode) -> Unit,
    onLocationUpdate: (String, String, Boolean) -> Unit,
    onRequestGpsLocation: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Language, 1 = Location
    var langSearchQuery by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SoftCream,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
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
                            .background(ForestGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (selectedTab == 0) Icons.Default.Language else Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = FreshLime,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = Localization.get("change_language", currentLanguage),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = ForestGreen
                            )
                        )
                        Text(
                            text = "${currentLanguage.nativeName} (${currentLanguage.englishName}) • $selectedDistrict, $selectedState",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_sheet_button")
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = DarkText)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tab Selector
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = ForestGreen,
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
                            Icon(imageVector = Icons.Default.Language, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ભાષા / Languages (22)", fontWeight = FontWeight.Bold)
                        }
                    },
                    selectedContentColor = ForestGreen,
                    unselectedContentColor = Color.Gray
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("સ્થાન / Location", fontWeight = FontWeight.Bold)
                        }
                    },
                    selectedContentColor = ForestGreen,
                    unselectedContentColor = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedTab == 0) {
                // Language Tab Content
                LanguageTabContent(
                    currentLanguage = currentLanguage,
                    languageMode = languageMode,
                    searchQuery = langSearchQuery,
                    onSearchQueryChange = { langSearchQuery = it },
                    onLanguageSelect = onLanguageSelect
                )
            } else {
                // Location Tab Content
                LocationTabContent(
                    currentLanguage = currentLanguage,
                    selectedState = selectedState,
                    selectedDistrict = selectedDistrict,
                    locationConsent = locationConsent,
                    isLocating = isLocating,
                    onLocationUpdate = onLocationUpdate,
                    onRequestGpsLocation = onRequestGpsLocation,
                    onSwitchLanguage = { lang -> onLanguageSelect(lang, LanguageMode.MANUAL) }
                )
            }
        }
    }
}

@Composable
private fun LanguageTabContent(
    currentLanguage: AppLanguage,
    languageMode: LanguageMode,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onLanguageSelect: (AppLanguage, LanguageMode) -> Unit
) {
    // Mode Switcher Banner
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (languageMode == LanguageMode.AUTO) Localization.get("auto_lang", currentLanguage) else Localization.get("manual_lang", currentLanguage),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                    )
                }
                Text(
                    text = if (languageMode == LanguageMode.AUTO)
                        "Device & region suggestions enabled. Switches smoothly as you travel."
                    else
                        "Your manual choice is pinned and will never be overwritten.",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, fontSize = 11.sp)
                )
            }

            Row {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (languageMode == LanguageMode.AUTO) FreshLime else Color.Transparent,
                    modifier = Modifier.clickable { onLanguageSelect(currentLanguage, LanguageMode.AUTO) }
                ) {
                    Text(
                        text = "AUTO",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (languageMode == LanguageMode.AUTO) ForestGreen else Color.Gray
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (languageMode == LanguageMode.MANUAL) ForestGreen else Color.Transparent,
                    modifier = Modifier.clickable { onLanguageSelect(currentLanguage, LanguageMode.MANUAL) }
                ) {
                    Text(
                        text = "MANUAL",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (languageMode == LanguageMode.MANUAL) Color.White else Color.Gray
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Search TextField
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        placeholder = { Text("Search 22 Indian Languages...", fontSize = 13.sp) },
        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = ForestGreen) },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ForestGreen,
            unfocusedBorderColor = Color.LightGray,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(10.dp))

    val filteredLanguages = AppLanguage.entries.filter { lang ->
        searchQuery.isBlank() ||
        lang.englishName.contains(searchQuery, ignoreCase = true) ||
        lang.nativeName.contains(searchQuery, ignoreCase = true) ||
        lang.code.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(filteredLanguages) { lang ->
            val isSelected = lang == currentLanguage
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) ForestGreen else Color.White
                ),
                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onLanguageSelect(lang, LanguageMode.MANUAL)
                    }
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = lang.nativeName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else DarkText
                                )
                            )
                            if (lang.isRtl) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (isSelected) FreshLimeLight.copy(alpha = 0.2f) else Color(0xFFEEEEEE)
                                ) {
                                    Text(
                                        text = "RTL",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else Color.DarkGray,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "${lang.englishName} • ${lang.script}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isSelected) FreshLimeLight else Color.Gray,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (lang.hasTts) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Voice Output Ready",
                                tint = if (isSelected) FreshLime else ForestGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        if (lang.hasStt) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice Input Ready",
                                tint = if (isSelected) FreshLime else ForestGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(FreshLime),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = ForestGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocationTabContent(
    currentLanguage: AppLanguage,
    selectedState: String,
    selectedDistrict: String,
    locationConsent: Boolean,
    isLocating: Boolean,
    onLocationUpdate: (String, String, Boolean) -> Unit,
    onRequestGpsLocation: () -> Unit,
    onSwitchLanguage: (AppLanguage) -> Unit
) {
    var stateExpanded by remember { mutableStateOf(false) }
    var districtExpanded by remember { mutableStateOf(false) }
    var localState by remember(selectedState) { mutableStateOf(selectedState) }
    var localDistrict by remember(selectedDistrict) { mutableStateOf(selectedDistrict) }
    var localConsent by remember(locationConsent) { mutableStateOf(locationConsent) }

    val currentAgroSeason = remember { AgroSeason.getCurrent() }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // GPS Detect Button
        item {
            Button(
                onClick = onRequestGpsLocation,
                enabled = !isLocating,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("gps_detect_button")
            ) {
                if (isLocating) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = FreshLime, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(Localization.get("locating", currentLanguage), color = Color.White)
                } else {
                    Icon(imageVector = Icons.Default.MyLocation, contentDescription = null, tint = FreshLime)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = Localization.get("detect_location", currentLanguage),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // Agro Season Badge
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(FreshLime),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🌱", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "${Localization.get("season_label", currentLanguage)} ${currentAgroSeason.englishName}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = DarkText)
                        )
                        Text(
                            text = "${currentAgroSeason.gujaratiName} • Active: ${currentAgroSeason.months}",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, fontSize = 11.sp)
                        )
                    }
                }
            }
        }

        // State Dropdown
        item {
            Column {
                Text(
                    text = Localization.get("state_label", currentLanguage),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = ForestGreen)
                )
                Spacer(modifier = Modifier.height(4.dp))
                ExposedDropdownMenuBox(
                    expanded = stateExpanded,
                    onExpandedChange = { stateExpanded = it }
                ) {
                    OutlinedTextField(
                        value = localState,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stateExpanded) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = ForestGreen
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = stateExpanded,
                        onDismissRequest = { stateExpanded = false }
                    ) {
                        IndiaGeoData.getAllStateNames().forEach { st ->
                            DropdownMenuItem(
                                text = { Text(st) },
                                onClick = {
                                    localState = st
                                    val districts = IndiaGeoData.getDistrictsForState(st)
                                    localDistrict = districts.firstOrNull() ?: ""
                                    stateExpanded = false
                                    onLocationUpdate(localState, localDistrict, localConsent)
                                }
                            )
                        }
                    }
                }
            }
        }

        // District Dropdown
        item {
            val availableDistricts = IndiaGeoData.getDistrictsForState(localState)
            Column {
                Text(
                    text = Localization.get("district_label_field", currentLanguage),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = ForestGreen)
                )
                Spacer(modifier = Modifier.height(4.dp))
                ExposedDropdownMenuBox(
                    expanded = districtExpanded,
                    onExpandedChange = { districtExpanded = it }
                ) {
                    OutlinedTextField(
                        value = localDistrict,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = districtExpanded) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = ForestGreen
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = districtExpanded,
                        onDismissRequest = { districtExpanded = false }
                    ) {
                        availableDistricts.forEach { dist ->
                            DropdownMenuItem(
                                text = { Text(dist) },
                                onClick = {
                                    localDistrict = dist
                                    districtExpanded = false
                                    onLocationUpdate(localState, localDistrict, localConsent)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Regional Language Suggestions based on State
        item {
            val regionalLanguages = IndiaGeoData.getRegionalLanguagesForState(localState)
            val suggested = regionalLanguages.firstOrNull { it != currentLanguage }

            if (suggested != null) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = WarmAmber.copy(alpha = 0.12f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WarmAmber.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = WarmAmber, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = Localization.get("regional_suggestion_title", currentLanguage),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = DarkText)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = Localization.format("regional_suggestion_body", currentLanguage, localState, "${suggested.nativeName} (${suggested.englishName})"),
                            style = MaterialTheme.typography.bodySmall.copy(color = DarkText, fontSize = 12.sp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { onSwitchLanguage(suggested) },
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${suggested.nativeName} પસંદ કરો",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }

        // Location Privacy Consent Switch
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "સ્થાનિક કૃષિ સંદર્ભ પરવાનગી",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = DarkText)
                        )
                        Text(
                            text = Localization.get("location_consent", currentLanguage),
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, fontSize = 11.sp)
                        )
                    }
                    Switch(
                        checked = localConsent,
                        onCheckedChange = {
                            localConsent = it
                            onLocationUpdate(localState, localDistrict, localConsent)
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = ForestGreen, checkedTrackColor = FreshLime)
                    )
                }
            }
        }
    }
}
