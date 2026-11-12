package com.example.data

import android.content.Context
import com.example.R
import com.example.model.CropScan
import com.example.model.FollowUpScan
import com.example.model.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class KisanRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    fun isUserSignedIn(): Boolean = auth.currentUser != null

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    // User Profile
    suspend fun getUserProfile(): UserProfile? {
        val uid = requireUserId()
        val path = "users/$uid"
        return try {
            val doc = db.collection("users").document(uid).get().await()
            if (doc.exists()) doc.toObject(UserProfile::class.java) else null
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, path)
            null
        }
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        val uid = requireUserId()
        val path = "users/$uid"
        val docRef = db.collection("users").document(uid)
        try {
            val existing = docRef.get().await()
            if (existing.exists()) {
                val updatePayload = mapOf<String, Any>(
                    "displayName" to profile.displayName.ifBlank { "Farmer" },
                    "preferredLanguage" to profile.preferredLanguage,
                    "languageMode" to profile.languageMode,
                    "state" to profile.state,
                    "district" to profile.district,
                    "locationConsent" to profile.locationConsent,
                    "primaryCrops" to profile.primaryCrops,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.update(updatePayload).await()
            } else {
                val createPayload = mapOf<String, Any>(
                    "userId" to uid,
                    "displayName" to profile.displayName.ifBlank { "Farmer" },
                    "preferredLanguage" to profile.preferredLanguage,
                    "languageMode" to profile.languageMode,
                    "state" to profile.state,
                    "district" to profile.district,
                    "locationConsent" to profile.locationConsent,
                    "primaryCrops" to profile.primaryCrops,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(createPayload).await()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            throw e
        }
    }

    // Observe all crop scans for the current farmer
    fun observeScans(): Flow<List<CropScan>> = flow {
        val uid = requireUserId()
        val path = "users/$uid/scans"
        emitAll(
            db.collection("users").document(uid).collection("scans")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(CropScan::class.java)
                        .sortedByDescending { it.createdAt?.toDate()?.time ?: 0L }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val exceptionToLog = firestoreError ?: (error as? Exception)
                    if (exceptionToLog != null && auth.currentUser?.uid == uid) {
                        handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun getScanById(scanId: String): CropScan? {
        val uid = requireUserId()
        val path = "users/$uid/scans/$scanId"
        return try {
            val doc = db.collection("users").document(uid).collection("scans").document(scanId).get().await()
            if (doc.exists()) doc.toObject(CropScan::class.java) else null
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, path)
            null
        }
    }

    suspend fun saveCropScan(scan: CropScan): String {
        val uid = requireUserId()
        val scansCollection = db.collection("users").document(uid).collection("scans")
        val docRef = if (scan.id.isNotBlank()) scansCollection.document(scan.id) else scansCollection.document()

        val payload = mutableMapOf<String, Any>(
            "userId" to uid,
            "cropName" to scan.cropName,
            "cropStage" to scan.cropStage,
            "symptomsDescription" to scan.symptomsDescription,
            "district" to scan.district,
            "photoUri" to scan.photoUri,
            "visibleSymptoms" to scan.visibleSymptoms,
            "possibleCauses" to scan.possibleCauses,
            "uncertaintyLevel" to scan.uncertaintyLevel,
            "missingInformation" to scan.missingInformation,
            "safeNextSteps" to scan.safeNextSteps,
            "expertReferralRequired" to scan.expertReferralRequired,
            "disclaimer" to scan.disclaimer,
            "language" to scan.language,
            "notes" to scan.notes,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        try {
            docRef.set(payload).await()
            return docRef.id
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun deleteScan(scanId: String) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("scans").document(scanId)
        try {
            docRef.delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    // Follow-ups
    fun observeFollowUps(): Flow<List<FollowUpScan>> = flow {
        val uid = requireUserId()
        val path = "users/$uid/follow_ups"
        emitAll(
            db.collection("users").document(uid).collection("follow_ups")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(FollowUpScan::class.java)
                        .sortedByDescending { it.createdAt?.toDate()?.time ?: 0L }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val exceptionToLog = firestoreError ?: (error as? Exception)
                    if (exceptionToLog != null && auth.currentUser?.uid == uid) {
                        handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun saveFollowUp(followUp: FollowUpScan): String {
        val uid = requireUserId()
        val collection = db.collection("users").document(uid).collection("follow_ups")
        val docRef = if (followUp.id.isNotBlank()) collection.document(followUp.id) else collection.document()

        val payload = mutableMapOf<String, Any>(
            "userId" to uid,
            "scanId" to followUp.scanId,
            "cropName" to followUp.cropName,
            "previousPhotoUri" to followUp.previousPhotoUri,
            "currentPhotoUri" to followUp.currentPhotoUri,
            "visibleDifferences" to followUp.visibleDifferences,
            "updatedObservations" to followUp.updatedObservations,
            "suggestedNextSteps" to followUp.suggestedNextSteps,
            "farmerNotes" to followUp.farmerNotes,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        try {
            docRef.set(payload).await()
            return docRef.id
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }
}
