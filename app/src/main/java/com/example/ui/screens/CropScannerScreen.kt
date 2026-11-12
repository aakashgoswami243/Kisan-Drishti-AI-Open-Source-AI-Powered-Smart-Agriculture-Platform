package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.SupportedCrop
import com.example.ui.theme.DarkText
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.FreshLime
import com.example.ui.theme.FreshLimeLight
import com.example.ui.theme.SoftCream
import com.example.ui.theme.WarmAmber
import com.example.util.AppLanguage
import com.example.util.CameraUtil
import com.example.util.Localization

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropScannerScreen(
    language: AppLanguage,
    supportedCrops: List<SupportedCrop>,
    districts: List<String>,
    selectedCrop: String,
    selectedStage: String,
    capturedBitmap: Bitmap?,
    symptomInput: String,
    selectedDistrict: String,
    isAnalyzing: Boolean,
    statusMessage: String?,
    onCropSelect: (String) -> Unit,
    onStageSelect: (String) -> Unit,
    onBitmapChange: (Bitmap?) -> Unit,
    onSymptomChange: (String) -> Unit,
    onDistrictChange: (String) -> Unit,
    onAnalyzeClick: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var showPermissionRationale by remember { mutableStateOf(false) }

    // High resolution TakePicture launcher with FileProvider
    val fullPictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempPhotoUri != null) {
            val sampled = CameraUtil.decodeSampledBitmap(context, tempPhotoUri!!)
            if (sampled != null) {
                onBitmapChange(sampled)
            }
        }
    }

    // Fallback TakePicturePreview thumbnail launcher
    val thumbnailLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            onBitmapChange(bitmap)
        }
    }

    // Camera Permission Launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            showPermissionRationale = false
            try {
                val (uri, _) = CameraUtil.createTempCropPhotoUri(context)
                tempPhotoUri = uri
                fullPictureLauncher.launch(uri)
            } catch (e: Exception) {
                thumbnailLauncher.launch(null)
            }
        } else {
            showPermissionRationale = true
        }
    }

    val requestCamera = {
        if (CameraUtil.isCameraPermissionGranted(context)) {
            showPermissionRationale = false
            try {
                val (uri, _) = CameraUtil.createTempCropPhotoUri(context)
                tempPhotoUri = uri
                fullPictureLauncher.launch(uri)
            } catch (e: Exception) {
                thumbnailLauncher.launch(null)
            }
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Activity Result Launcher for Photo Picker
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val sampled = CameraUtil.decodeSampledBitmap(context, uri)
            if (sampled != null) {
                onBitmapChange(sampled)
            }
        }
    }

    var showDistrictMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftCream)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Camera Permission Rationale Alert Card if denied
        if (showPermissionRationale) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("camera_permission_alert_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = WarmAmber,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = Localization.get("camera_permission_title", language),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DarkText
                                )
                            )
                        }

                        IconButton(
                            onClick = { showPermissionRationale = false },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Dismiss", tint = Color.Gray, modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = Localization.get("camera_permission_desc", language),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DarkText,
                            lineHeight = 18.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("request_camera_permission_button")
                        ) {
                            Text(
                                text = Localization.get("camera_permission_grant", language),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                }
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = Localization.get("camera_permission_settings", language),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Step 1: Crop & Stage Selection
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = Localization.get("step_1_title", language),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Crops Horizontal Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    supportedCrops.forEach { crop ->
                        val isSelected = selectedCrop == crop.nameEn
                        val displayName = when (language) {
                            AppLanguage.GUJARATI -> crop.nameGu
                            AppLanguage.HINDI -> crop.nameHi
                            AppLanguage.ENGLISH -> crop.nameEn
                            else -> "${crop.nameEn} (${crop.nameGu})"
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) ForestGreen else SoftCream,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onCropSelect(crop.nameEn) }
                                .testTag("crop_select_${crop.id}")
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = FreshLime,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = displayName,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else DarkText
                                    )
                                )
                                Text(
                                    text = crop.season,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) FreshLimeLight else Color.Gray,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Stage Selection Chips
                Text(
                    text = "પાકની વર્તમાન અવસ્થા (Growth Stage):",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = DarkText
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))

                val currentCropDef = supportedCrops.find { it.nameEn == selectedCrop } ?: supportedCrops.first()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    currentCropDef.defaultStages.take(3).forEach { stage ->
                        val isSelected = selectedStage == stage
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) FreshLime else SoftCream,
                            modifier = Modifier
                                .clickable { onStageSelect(stage) }
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) ForestGreen else Color.LightGray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                        ) {
                            Text(
                                text = stage,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) ForestGreen else DarkText,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Step 2: Photo Capture / Upload Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = Localization.get("step_2_title", language),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))

                if (capturedBitmap != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Image(
                            bitmap = capturedBitmap.asImageBitmap(),
                            contentDescription = "Captured crop photograph",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Close button to remove image
                        IconButton(
                            onClick = { onBitmapChange(null) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(32.dp)
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = requestCamera,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("camera_replace_button")
                        ) {
                            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = Localization.get("replace_photo", language), fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("gallery_replace_button")
                        ) {
                            Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = Localization.get("choose_gallery", language), fontSize = 12.sp)
                        }
                    }
                } else {
                    // Empty photo state with action buttons
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SoftCream)
                            .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = ForestGreen,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "પાંદડા કે જીવાતના ઉપદ્રવનો સ્પષ્ટ ફોટો લો",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = requestCamera,
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("camera_capture_button")
                        ) {
                            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = Localization.get("take_photo", language))
                        }

                        OutlinedButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("gallery_pick_button")
                        ) {
                            Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = Localization.get("choose_gallery", language))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick Sample image loader for emulator testing convenience
                    Button(
                        onClick = {
                            val sampleBitmap = BitmapFactory.decodeResource(
                                context.resources,
                                R.drawable.kisan_hero_banner_1794373231183
                            )
                            onBitmapChange(sampleBitmap)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FreshLimeLight),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("use_sample_crop_photo_button")
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "નમૂના પાક ફોટો લોડ કરો (Sample Crop Photo)",
                            style = MaterialTheme.typography.labelSmall.copy(color = ForestGreen, fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        // Step 3: Symptoms Description & District
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = Localization.get("step_3_title", language),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))

                // District selector dropdown
                Text(
                    text = Localization.get("select_district", language),
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(4.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SoftCream,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDistrictMenu = true }
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedDistrict,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = DarkText
                                )
                            )
                            Text(text = "બદલો ▼", color = ForestGreen, fontSize = 12.sp)
                        }
                    }

                    DropdownMenu(
                        expanded = showDistrictMenu,
                        onDismissRequest = { showDistrictMenu = false }
                    ) {
                        districts.forEach { dist ->
                            DropdownMenuItem(
                                text = { Text(dist) },
                                onClick = {
                                    onDistrictChange(dist.split(" ").first())
                                    showDistrictMenu = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Symptoms Text Field
                Text(
                    text = Localization.get("describe_symptoms", language),
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = symptomInput,
                    onValueChange = onSymptomChange,
                    placeholder = { Text(Localization.get("symptoms_hint", language), fontSize = 13.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp)
                        .testTag("symptoms_input_field"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreen,
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Analyze CTA Button
                Button(
                    onClick = onAnalyzeClick,
                    enabled = !isAnalyzing,
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("run_crop_analysis_button")
                ) {
                    if (isAnalyzing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = FreshLime,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = Localization.get("analyzing", language),
                            style = MaterialTheme.typography.labelLarge.copy(color = Color.White)
                        )
                    } else {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = FreshLime)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Localization.get("analyze_btn", language),
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                if (isAnalyzing) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = Localization.get("analyzing_hint", language),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Gray,
                            fontSize = 11.sp
                        ),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (statusMessage != null && !isAnalyzing) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = statusMessage,
                        style = MaterialTheme.typography.bodySmall.copy(color = ForestGreen)
                    )
                }
            }
        }
    }
}
