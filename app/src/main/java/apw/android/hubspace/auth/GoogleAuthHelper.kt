package apw.android.hubspace.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

//suspend fun signInWithGoogle(
//    context: Context,
//    webClientId: String
//): GoogleIdTokenCredential? {
//    val credentialManager = CredentialManager.create(context)
//    val googleIdOption = GetGoogleIdOption.Builder()
//        .setFilterByAuthorizedAccounts(false)
//        .setServerClientId(webClientId)
//        .setAutoSelectEnabled(false)
//        .build()
//
//    val request = GetCredentialRequest.Builder()
//        .addCredentialOption(googleIdOption)
//        .build()
//
//    return try {
//        val result = credentialManager.getCredential(
//            request = request,
//            context = context
//        )
//        GoogleIdTokenCredential.createFrom(result.credential.data)
//    } catch (e: Exception) {
//        e.printStackTrace()
//        null
//    }
//}

suspend fun signInWithGoogle(
    context: Context,
    webClientId: String
): GoogleIdTokenCredential? {

    val credentialManager = CredentialManager.create(context)

    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(webClientId)
        .setAutoSelectEnabled(false)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    return try {
        Log.d("GoogleAuth", "Starting Credential Manager")

        val result = credentialManager.getCredential(
            request = request,
            context = context
        )

        Log.d("GoogleAuth", "Credential received: ${result.credential.type}")

        val credential =
            GoogleIdTokenCredential.createFrom(result.credential.data)

        Log.d("GoogleAuth", "ID token received")

        credential

    } catch (e: Exception) {
        Log.e(
            "GoogleAuth",
            "Google Credential Manager failed",
            e
        )
        null
    }
}