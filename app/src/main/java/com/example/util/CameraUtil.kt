package com.example.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CameraUtil {

    fun isCameraPermissionGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun createTempCropPhotoUri(context: Context): Pair<Uri, File> {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val photoFile = File.createTempFile(
            "CROP_${timeStamp}_",
            ".jpg",
            context.cacheDir
        )
        val authority = "${context.packageName}.fileprovider"
        val uri = FileProvider.getUriForFile(context, authority, photoFile)
        return Pair(uri, photoFile)
    }

    fun decodeSampledBitmap(context: Context, uri: Uri, targetMaxDim: Int = 1200): Bitmap? {
        return try {
            // Step 1: Decode bounds only
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            var stream = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(stream, null, options)
            stream?.close()

            // Step 2: Calculate inSampleSize
            var sampleSize = 1
            val maxDim = maxOf(options.outWidth, options.outHeight)
            while (maxDim / sampleSize > targetMaxDim) {
                sampleSize *= 2
            }

            // Step 3: Decode full bitmap with sample size
            val decodeOptions = BitmapFactory.Options().apply {
                inJustDecodeBounds = false
                inSampleSize = sampleSize
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }
            stream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(stream, null, decodeOptions)
            stream?.close()
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
