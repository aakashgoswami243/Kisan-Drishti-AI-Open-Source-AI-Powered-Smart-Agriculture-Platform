package com.example.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "",
    val preferredLanguage: String = "gu",
    val languageMode: String = "AUTO", // AUTO or MANUAL
    val state: String = "Gujarat",
    val district: String = "Rajkot",
    val locationConsent: Boolean = true,
    val primaryCrops: List<String> = listOf("Cotton", "Groundnut"),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class CropScan(
    @DocumentId val id: String = "",
    val userId: String = "",
    val cropName: String = "",
    val cropStage: String = "",
    val symptomsDescription: String = "",
    val district: String = "",
    val photoUri: String = "",
    val visibleSymptoms: List<String> = emptyList(),
    val possibleCauses: List<String> = emptyList(),
    val uncertaintyLevel: String = "Medium",
    val missingInformation: List<String> = emptyList(),
    val safeNextSteps: List<String> = emptyList(),
    val expertReferralRequired: Boolean = false,
    val disclaimer: String = "",
    val language: String = "gu",
    val notes: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FollowUpScan(
    @DocumentId val id: String = "",
    val userId: String = "",
    val scanId: String = "",
    val cropName: String = "",
    val previousPhotoUri: String = "",
    val currentPhotoUri: String = "",
    val visibleDifferences: List<String> = emptyList(),
    val updatedObservations: String = "",
    val suggestedNextSteps: List<String> = emptyList(),
    val farmerNotes: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

data class SupportedCrop(
    val id: String,
    val nameEn: String,
    val nameGu: String,
    val nameHi: String,
    val season: String,
    val commonPests: List<String>,
    val defaultStages: List<String>
)

data class SafetyCheckItem(
    val id: String,
    val questionKey: String,
    val explanationKey: String,
    val isChecked: Boolean = false
)

data class ChatMessage(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)
