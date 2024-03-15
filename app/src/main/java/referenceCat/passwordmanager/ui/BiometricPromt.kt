package referenceCat.passwordmanager.ui

import android.content.Context
import android.util.Log
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import referenceCat.passwordmanager.R
import referenceCat.passwordmanager.backend.getInitializedCipherForDecryption
import referenceCat.passwordmanager.backend.getInitializedCipherForEncryption
import javax.crypto.Cipher

@Composable
fun BiometricPrompt(show: Boolean, onDismiss: () -> Unit, onSuccessfulAuthentication:  (initiatedCipher: Cipher) -> Unit, onFailedAuthentication:  () -> Unit = {}, encryptionMode: Boolean, cipher: Cipher) {
    if (!show) {
        return
    }
    Log.d(null, "BiometricPrompt")

    val context = LocalContext.current

    LaunchedEffect(key1 = context) {
        val fragmentActivity = context as? FragmentActivity ?: return@LaunchedEffect
        val executor = ContextCompat.getMainExecutor(fragmentActivity)
        val biometricPrompt = BiometricPrompt(
            fragmentActivity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Log.d("BiometricPrompt", "Error: $errorCode $errString")
                    onDismiss()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    val cryptoObject =  requireNotNull(result.cryptoObject)
                    val initiatedCipher =  requireNotNull(cryptoObject.cipher)
                    onSuccessfulAuthentication(initiatedCipher)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onDismiss()
                    onFailedAuthentication()
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(context.getString(R.string.app_name))
            .setSubtitle(context.getString(R.string.auth_dialog_text))
            .setAllowedAuthenticators(BIOMETRIC_STRONG)
            .setNegativeButtonText(context.getText(R.string.cancel))
            .build()

        biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
    }
}