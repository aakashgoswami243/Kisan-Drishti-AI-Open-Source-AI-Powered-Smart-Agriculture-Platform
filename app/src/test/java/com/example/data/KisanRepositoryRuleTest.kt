package com.example.data

import com.example.base.FirestoreEmulatorTestBase
import com.example.model.CropScan
import com.example.model.FollowUpScan
import com.example.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.util.UUID

class KisanRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun saveCropScan_authenticatedFarmer_createsAndRetrievesScan() = runBlocking {
        signInTestUser(ALICE_EMAIL)
        val repository = KisanRepository(firestore)

        val scanId = "scan_${UUID.randomUUID()}"
        val scan = CropScan(
            id = scanId,
            cropName = "Cotton",
            cropStage = "Flowering",
            symptomsDescription = "Leaves curling upward with yellow margin",
            district = "Rajkot",
            visibleSymptoms = listOf("Leaf curling", "Marginal chlorosis"),
            possibleCauses = listOf("Sucking pest attack (Aphids/Jassids)"),
            uncertaintyLevel = "Low",
            safeNextSteps = listOf("Place yellow sticky traps", "Inspect underleaf surface"),
            expertReferralRequired = false,
            language = "gu"
        )

        val createdId = withTimeout(5000L) {
            repository.saveCropScan(scan)
        }
        assertEquals(scanId, createdId)

        val retrieved = withTimeout(5000L) {
            repository.getScanById(scanId)
        }
        assertNotNull(retrieved)
        assertEquals("Cotton", retrieved?.cropName)

        val list = withTimeout(5000L) {
            repository.observeScans().first { items -> items.any { it.id == scanId } }
        }
        assertTrue(list.any { it.id == scanId })
    }

    @Test
    fun saveUserProfile_authenticatedFarmer_persistsProfile() = runBlocking {
        val uniqueEmail = "farmer_profile_${UUID.randomUUID().toString().take(8)}@test.com"
        val uid = signInTestUser(uniqueEmail)
        val repository = KisanRepository(firestore)

        val profile = UserProfile(
            userId = uid,
            displayName = "Bhaveshbhai Patel",
            preferredLanguage = "gu",
            district = "Junagadh",
            primaryCrops = listOf("Cotton", "Groundnut")
        )

        withTimeout(5000L) {
            repository.saveUserProfile(profile)
        }

        val retrieved = withTimeout(5000L) {
            repository.getUserProfile()
        }
        assertNotNull(retrieved)
        assertEquals("Bhaveshbhai Patel", retrieved?.displayName)
        assertEquals("gu", retrieved?.preferredLanguage)
    }

    @Test
    fun followUpScan_authenticatedFarmer_linksToParentScan() = runBlocking {
        signInTestUser(ALICE_EMAIL)
        val repository = KisanRepository(firestore)

        val fuId = "fu_${UUID.randomUUID()}"
        val followUp = FollowUpScan(
            id = fuId,
            scanId = "scan_parent_123",
            cropName = "Groundnut",
            visibleDifferences = listOf("New leaves showing healthy green color"),
            updatedObservations = "Plant recovery observed",
            suggestedNextSteps = listOf("Maintain normal irrigation"),
            farmerNotes = "Applied compost last week"
        )

        val createdFuId = withTimeout(5000L) {
            repository.saveFollowUp(followUp)
        }
        assertEquals(fuId, createdFuId)

        val followUps = withTimeout(5000L) {
            repository.observeFollowUps().first { items -> items.any { it.id == fuId } }
        }
        assertTrue(followUps.any { it.id == fuId })
    }

    @Test
    fun unauthenticatedAccess_failsWithIllegalStateException() = runBlocking {
        auth.signOut()
        val repository = KisanRepository(firestore)

        try {
            repository.requireUserId()
            fail("Expected IllegalStateException for unauthenticated user")
        } catch (e: IllegalStateException) {
            assertTrue(e.message?.contains("User must be signed in") == true)
        }
    }

    private companion object {
        const val ALICE_EMAIL = "farmer_alice@test.com"
        const val BOB_EMAIL = "farmer_bob@test.com"
    }
}
