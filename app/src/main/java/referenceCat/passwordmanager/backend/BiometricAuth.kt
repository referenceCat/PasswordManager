package referenceCat.passwordmanager.backend

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec


// source https://iamjosephmj.medium.com/biometric-encryption-and-decryption-with-androidx-biometric-13741a5b0583

private val KEY_SIZE = 256
private val ANDROID_KEYSTORE = "AndroidKeyStore"
private val ENCRYPTION_BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
private val ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
private val ENCRYPTION_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
private fun getOrCreateSecretKey(keyName: String): SecretKey {
    // If Secretkey was previously created for that keyName, then grab and return it.
    val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
    keyStore.load(null) // Keystore must be loaded before it can be accessed
    keyStore.getKey(keyName, null)?.let { return it as SecretKey }
    // if you reach here, then a new SecretKey must be generated for that keyName
    val keyGenParams = KeyGenParameterSpec.Builder(
        keyName,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    )
        .setBlockModes(ENCRYPTION_BLOCK_MODE)
        .setEncryptionPaddings(ENCRYPTION_PADDING)
        .setUserAuthenticationRequired(true)
        .setKeySize(KEY_SIZE)
        .setInvalidatedByBiometricEnrollment(true)
        .build()
    val keyGenerator = KeyGenerator.getInstance(
        ENCRYPTION_ALGORITHM,
        ANDROID_KEYSTORE
    )
    keyGenerator.init(keyGenParams)
    return keyGenerator.generateKey()
}

private fun getCipher(): Cipher {
    val transformation = (ENCRYPTION_ALGORITHM + "/"
            + ENCRYPTION_BLOCK_MODE + "/"
            + ENCRYPTION_PADDING)
    return Cipher.getInstance(transformation)
}

fun getInitializedCipherForEncryption(keyName: String): Cipher {
    val cipher = getCipher()
    val secretKey = getOrCreateSecretKey(keyName)
    cipher.init(Cipher.ENCRYPT_MODE, secretKey)
    return cipher
}

fun getInitializedCipherForDecryption(
    keyName: String,
    initializationVector: ByteArray
): Cipher {
    val cipher = getCipher()
    val secretKey = getOrCreateSecretKey(keyName)
    cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(initializationVector))
    return cipher
}