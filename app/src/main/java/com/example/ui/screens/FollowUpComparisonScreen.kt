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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.model.CropScan
import com.example.model.FollowUpScan
import com.example.ui.theme.DarkText
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.FreshLime
import com.example.ui.theme.FreshLimeLight
import com.example.ui.theme.SoftCream
import com.example.ui.theme.WarmAmber
import com.example.util.AppLanguage
import com.example.util.CameraUtil
import com.example.util.Localization

@Composable
fun FollowUpComparisonScreen(
    language: AppLanguage,
    parentScan: CropScan?,
    followUpBitmap: Bitmap?,
    followUpNotes: String,
    comparisonResult: FollowUpScan?,
    isComparing: Boolean,
    onBitmapChange: (Bitmap?) -> Unit,
    onNotesChange: (String) -> Unit,
    onCompareClick: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var showPermissionRationale by remember { mutableStateOf(false) }

    val fullPictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempPhotoUri != null) {
            val sampled = CameraUtil.decodeSampledBitmap(context, tempPhotoUri!!)
            if (sampled != null) onBitmapChange(sampled)
        }
    }

    val thumbnailLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bmp: Bitmap? ->
        if (bmp != null) onBitmapChange(bmp)
    }

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

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val sampled = CameraUtil.decodeSampledBitmap(context, uri)
            if (sampled != null) onBitmapChange(sampled)
        }
    }

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
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = ForestGreen),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(FreshLime),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Timeline, contentDescription = null, tint = ForestGreen)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = Localization.get("follow_up_title", language),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = Localization.get("follow_up_desc", language),
                        style = MaterialTheme.typography.bodySmall.copy(color = FreshLimeLight)
                    )
                }
            }
        }

        // Camera Permission Rationale Alert Card if denied
        if (showPermissionRationale) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("follow_up_camera_permission_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = WarmAmber, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = Localization.get("camera_permission_title", language),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DarkText)
                            )
                        }
                        IconButton(onClick = { showPermissionRationale = false }, modifier = Modifier.size(28.dp)) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Dismiss", tint = Color.Gray, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = Localization.get("camera_permission_desc", language),
                        style = MaterialTheme.typography.bodySmall.copy(color = DarkText, lineHeight = 18.sp)
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
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = Localization.get("camera_permission_grant", language), fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                            Text(text = Localization.get("camera_permission_settings", language), fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Parent Scan Reference Box
        if (parentScan != null) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "અગાઉનો સંદર્ભ પાક: ${parentScan.cropName} (${parentScan.cropStage})",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "અગાઉના લક્ષણો: ${parentScan.visibleSymptoms.joinToString()}",
                        style = MaterialTheme.typography.bodySmall.copy(color = DarkText)
                    )
                }
            }
        }

        // Current New Photo Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = Localization.get("current_photo", language),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (followUpBitmap != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Image(
                            bitmap = followUpBitmap.asImageBitmap(),
                            contentDescription = "New follow-up photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        IconButton(
                            onClick = { onBitmapChange(null) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(28.dp)
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SoftCream)
                            .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "આજનો નવો પાક ફોટો ઉમેરો",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

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
                                .testTag("follow_up_camera_button")
                        ) {
                            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "કેમેરા")
                        }

                        OutlinedButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("follow_up_gallery_button")
                        ) {
                            Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "ગેલેરી")
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Sample photo loader button for quick emulator testing
                    Button(
                        onClick = {
                            val sample = BitmapFactory.decodeResource(context.resources, R.drawable.kisan_hero_banner_1794373231183)
                            onBitmapChange(sample)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FreshLimeLight),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "નમૂના ફોટો વાપરો (Sample Photo)",
                            style = MaterialTheme.typography.labelSmall.copy(color = ForestGreen, fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = followUpNotes,
                    onValueChange = onNotesChange,
                    placeholder = { Text("તમે લીધેલા પગલાં (દા.ત., નીમ ઓઈલ છાંટ્યું, પિયત આપ્યું...)", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForestGreen,
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onCompareClick,
                    enabled = !isComparing,
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("run_comparison_button")
                ) {
                    if (isComparing) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = FreshLime, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("સરખામણી ચાલુ છે...")
                    } else {
                        Icon(imageVector = Icons.Default.Compare, contentDescription = null, tint = FreshLime)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(Localization.get("compare_btn", language), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Comparison Result Card
        if (comparisonResult != null) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "જેમિની AI સરખામણી પરિણામ:",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "અવલોકન: ${comparisonResult.updatedObservations}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = DarkText,
                            lineHeight = 22.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "દેખીતા ફેરફારો:",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                    )
                    comparisonResult.visibleDifferences.forEach { diff ->
                        Row(modifier = Modifier.padding(vertical = 2.dp)) {
                            Text("•", color = ForestGreen, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = diff, style = MaterialTheme.typography.bodySmall.copy(color = DarkText))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "સૂચિત આગલા પગલાં:",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                    )
                    comparisonResult.suggestedNextSteps.forEach { step ->
                        Row(modifier = Modifier.padding(vertical = 2.dp)) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = step, style = MaterialTheme.typography.bodySmall.copy(color = DarkText))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
