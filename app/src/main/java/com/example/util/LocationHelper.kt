package com.example.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import kotlin.coroutines.resume

data class GeoLocationResult(
    val state: String,
    val district: String,
    val locality: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val source: String = "GPS"
)

sealed class LocationState {
    data object Idle : LocationState()
    data object Locating : LocationState()
    data class Success(val result: GeoLocationResult) : LocationState()
    data class Error(val message: String, val canFallbackManual: Boolean = true) : LocationState()
}

object LocationHelper {

    fun hasLocationPermission(context: Context): Boolean {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    fun isGpsEnabled(context: Context): Boolean {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return false
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    suspend fun getCurrentLocation(context: Context): GeoLocationResult? = withContext(Dispatchers.IO) {
        if (!hasLocationPermission(context)) return@withContext null

        val location = try {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            val cts = CancellationTokenSource()
            withTimeoutOrNull(8000L) {
                fusedClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cts.token).await()
            }
        } catch (_: Exception) {
            null
        } ?: getLegacyLocation(context)

        if (location != null) {
            reverseGeocode(context, location.latitude, location.longitude)
        } else {
            null
        }
    }

    private suspend fun getLegacyLocation(context: Context): Location? = withContext(Dispatchers.IO) {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return@withContext null
        try {
            val gpsLoc = if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) else null
            val netLoc = if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) else null
            when {
                gpsLoc != null && netLoc != null -> if (gpsLoc.time > netLoc.time) gpsLoc else netLoc
                gpsLoc != null -> gpsLoc
                else -> netLoc
            }
        } catch (_: SecurityException) {
            null
        }
    }

    suspend fun reverseGeocode(context: Context, latitude: Double, longitude: Double): GeoLocationResult = withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale("en", "IN"))
            val addresses: List<Address>? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine { cont ->
                    geocoder.getFromLocation(latitude, longitude, 1) { resultList ->
                        cont.resume(resultList)
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(latitude, longitude, 1)
            }

            val address = addresses?.firstOrNull()
            if (address != null) {
                val state = address.adminArea ?: IndiaGeoData.DEFAULT_STATE
                val district = address.subAdminArea ?: address.locality ?: IndiaGeoData.DEFAULT_DISTRICT
                val locality = address.subLocality ?: address.featureName ?: ""
                return@withContext GeoLocationResult(
                    state = sanitizeStateName(state),
                    district = sanitizeDistrictName(district),
                    locality = locality,
                    latitude = latitude,
                    longitude = longitude,
                    source = "GPS_GEOCODED"
                )
            }
        } catch (_: Exception) {
            // Geocoder failure or offline fallback
        }

        // Technical fallback if geocoder service unavailable
        GeoLocationResult(
            state = IndiaGeoData.DEFAULT_STATE,
            district = IndiaGeoData.DEFAULT_DISTRICT,
            latitude = latitude,
            longitude = longitude,
            source = "FALLBACK"
        )
    }

    private fun sanitizeStateName(raw: String): String {
        val clean = raw.trim()
        val match = IndiaGeoData.statesList.firstOrNull { it.stateName.equals(clean, ignoreCase = true) }
        return match?.stateName ?: clean
    }

    private fun sanitizeDistrictName(raw: String): String {
        val clean = raw.replace("District", "", ignoreCase = true).trim()
        return clean.ifBlank { IndiaGeoData.DEFAULT_DISTRICT }
    }
}
