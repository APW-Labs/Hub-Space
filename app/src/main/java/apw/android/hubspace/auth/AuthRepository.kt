package apw.android.hubspace.auth

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

sealed class AuthResult {
    data class Success(val email: String) : AuthResult()
    data class Unauthorized(val message: String) : AuthResult()
    data class Failed(val message: String) : AuthResult()
}

class AuthRepository(
    val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun authenticateAndCheckWhitelist(
        idToken: String
    ): AuthResult {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        val user = try {
            Log.d("AuthRepository", "Firebase Auth START")
            val result = auth.signInWithCredential(credential).await()
            Log.d("AuthRepository", "Firebase Auth SUCCESS")
            result.user ?: return AuthResult.Failed("Firebase returned no user")
        } catch (e: Exception) {
            Log.e("AuthRepository", "Firebase Auth FAILED", e)
            auth.signOut()
            return AuthResult.Failed("Firebase Auth: ${e.localizedMessage}")
        }

        val email = user.email?.lowercase()?.trim() ?: return AuthResult.Failed("Email not found")
        Log.d("AuthRepository", "Logged in: $email")

        return try {
            Log.d("AuthRepository", "Firestore CHECK START")
            val doc = firestore.collection("whitelisted_users").document(email).get().await()
            Log.d("AuthRepository", "Firestore SUCCESS: exists=${doc.exists()}")

            if (doc.exists() && doc.getBoolean("isActive") == true) {
                AuthResult.Success(email)
            } else {
                AuthResult.Unauthorized("Access Denied: Your email ($email) is not whitelisted by admin.")
            }

        } catch (e: Exception) {
            Log.e("AuthRepository", "Firestore FAILED", e)
            auth.signOut()
            AuthResult.Failed("Firestore: ${e.localizedMessage}")
        }
    }

    suspend fun checkStatus(): AuthResult {
        return try {
            val currentUser = auth.currentUser ?: return AuthResult.Failed("User session expired. Please log in again.")
            val email = currentUser.email?.lowercase()?.trim() ?: return AuthResult.Failed("Email not found")

            val document = firestore.collection("whitelisted_users").document(email).get().await()
            if (document.exists() && document.getBoolean("isActive") == true) {
                AuthResult.Success(email)
            } else {
                AuthResult.Unauthorized("Access Denied: Your email ($email) is still not whitelisted.")
            }
        } catch (e: Exception) {
            AuthResult.Failed(e.localizedMessage ?: "Failed to check status")
        }
    }

    suspend fun isCurrentUserWhitelisted(): Boolean {
        val email = auth.currentUser?.email?.lowercase()?.trim() ?: return false
        return try {
            val doc = firestore.collection("whitelisted_users")
                .document(email)
                .get()
                .await()
            doc.exists() && doc.getBoolean("isActive") == true
        } catch (e: Exception) {
            false
        }
    }
}