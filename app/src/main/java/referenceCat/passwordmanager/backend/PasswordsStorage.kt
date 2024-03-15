package referenceCat.passwordmanager.backend

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import referenceCat.passwordmanager.R
import referenceCat.passwordmanager.backend.database.OfflinePasswordsRepository
import referenceCat.passwordmanager.backend.database.PasswordEntity
import referenceCat.passwordmanager.backend.database.PasswordsDatabase
import referenceCat.passwordmanager.backend.database.PasswordsRepository
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


data class DecryptedPasswordData(
    val id: Int,
    val name: String,
    val website: String,
    val password: String
)


class PasswordsStorage private constructor() {
    companion object {

        @Volatile
        private var instance: PasswordsStorage? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: PasswordsStorage().also { instance = it }
            }
    }

    // private var keySpec: SecretKeySpec? = null
    private var masterPassword: String? = null
    private val tag = "referenceCat.passwordmanager.cryptography"

    init {
        if (instance == null) {
            instance = this
        }
    }


    private fun generateEncryptionKey(text: String): SecretKeySpec {
        val pbKeySpec =
            PBEKeySpec(text.toCharArray(), stringToByteArray("encryption key salt"), 1324, 256)
        val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val keyBytes = secretKeyFactory.generateSecret(pbKeySpec).encoded
        return SecretKeySpec(keyBytes, "AES")
    }

    private fun getCipher(): Cipher {
        return Cipher.getInstance("AES/CBC/PKCS5PADDING")
    }

    private fun encrypt(plaintext: String, key: String): Pair<String, String> {
        val keySpec: SecretKeySpec = generateEncryptionKey(key)
        val cipher = getCipher()
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        val encryptedTextBytes = cipher.doFinal(stringToByteArray(plaintext))
        val encryptedText: String = byteArrayToString(encryptedTextBytes)
        val ivBytes = cipher.iv
        val iv: String = byteArrayToString(ivBytes)
        return Pair(encryptedText, iv)
    }

    private fun decrypt(cipherText: String, initVector: String, key: String): String {
        val keySpec: SecretKeySpec = generateEncryptionKey(key)
        val ivBytes = stringToByteArray(initVector)
        val cipherTextBytes = stringToByteArray(cipherText)
        val cipher = getCipher()
        val ivSpec = IvParameterSpec(ivBytes)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
        val decryptedTextBytes = cipher.doFinal(cipherTextBytes)
        val decryptedText = byteArrayToString(decryptedTextBytes)
        return decryptedText
    }

    private fun digestMessage(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        return byteArrayToString(md.digest(stringToByteArray(input)))
    }

    private fun byteArrayToString(input: ByteArray): String {
        // return BigInteger(1, input).toString(16).padStart(32, '0')
        val charSet = StandardCharsets.ISO_8859_1
        return input.toString(charSet)
    }

    private fun stringToByteArray(input: String): ByteArray {
        val charSet = StandardCharsets.ISO_8859_1
        return input.toByteArray(charSet)
    }

    fun isMasterPasswordInitiated(context: Context): Boolean {
        val sharedPref = context.getSharedPreferences(
            context.resources.getString(R.string.pref_file_name),
            Context.MODE_PRIVATE
        )

        sharedPref.getString(
            context.resources.getString(R.string.key_master_passoword_digest),
            null
        ) ?: return false
        return true

    }

    fun applyMasterPassword(context: Context, password: String): String? {
        if (!isMasterPasswordInitiated(context)) return "Master-password isn't initiated"

        val sharedPref = context.getSharedPreferences(
            context.resources.getString(R.string.pref_file_name),
            Context.MODE_PRIVATE
        )
        val passwordDigestOnDisk: String = sharedPref.getString(
            context.resources.getString(R.string.key_master_passoword_digest),
            null
        ) ?: return "Master-password isn't initiated"

        val passwordDigestFromUser = digestMessage(password)

        if (passwordDigestFromUser != passwordDigestOnDisk) return "Incorrect password."
        masterPassword = password
        return null
    }

    fun initMasterPassword(context: Context, password: String): String? {
        if (isMasterPasswordInitiated(context)) return "Master-password is already initiated"
        val sharedPref = context.getSharedPreferences(
            context.resources.getString(R.string.pref_file_name),
            Context.MODE_PRIVATE
        )

        with(sharedPref.edit()) {
            putString(
                context.resources.getString(R.string.key_master_passoword_digest),
                digestMessage(password)
            )
            commit()
        }
        masterPassword = password
        return null
    }

    fun getAllPasswords(context: Context): Flow<List<DecryptedPasswordData>> {
        val repository: PasswordsRepository =
            OfflinePasswordsRepository(PasswordsDatabase.getDatabase(context).passwordEntityDao())
        return repository.getAllItemsStream().map { entities ->
            entities.map {
                DecryptedPasswordData(
                    it.id,
                    it.name,
                    it.website,
                    decrypt(it.encryptedPassword, it.initVector, requireNotNull(masterPassword))
                )
            }
        }
    }

    suspend fun insertPasswordData(context: Context, name: String, website: String, password: String) {
        val repository: PasswordsRepository =
            OfflinePasswordsRepository(PasswordsDatabase.getDatabase(context).passwordEntityDao())
        val (encryptedPassword, initVector) = encrypt(password, requireNotNull(masterPassword))
        repository.insertItem(PasswordEntity(0, name, website, encryptedPassword, initVector))
    }

    suspend fun deletePasswordData(context: Context, id: Int) {
        val repository: PasswordsRepository = OfflinePasswordsRepository(PasswordsDatabase.getDatabase(context).passwordEntityDao())
        repository.deleteById(id)
    }

    suspend fun updatePasswordData(context: Context, id: Int, name: String, website: String, password: String) {
        val repository: PasswordsRepository =
            OfflinePasswordsRepository(PasswordsDatabase.getDatabase(context).passwordEntityDao())
        val (encryptedPassword, initVector) = encrypt(password, requireNotNull(masterPassword))
        repository.updateItem(PasswordEntity(id, name, website, encryptedPassword, initVector))
    }

    fun isMasterPasswordApplied(): Boolean {
        return masterPassword != null
    }

    fun isBiometricAuthInitiated(context: Context): Boolean {
        val sharedPref = context.getSharedPreferences(
            context.resources.getString(R.string.pref_file_name),
            Context.MODE_PRIVATE
        )

        sharedPref.getString(
            context.resources.getString(R.string.encrypted_master_password),
            null
        ) ?: return false
        return true
    }

    fun getCipherToInitBiometricAuth(context: Context): Cipher {
        // assert(!isBiometricAuthInitiated(context))
        return getInitializedCipherForEncryption( context.resources.getString(R.string.biometric_key) )
    }

    fun getCipherToApplyBiometricAuth(context: Context): Cipher {
        // assert(!isBiometricAuthInitiated(context))
        val sharedPref = context.getSharedPreferences(
            context.resources.getString(R.string.pref_file_name),
            Context.MODE_PRIVATE
        )
        val encryptedMasterPasswordIv: String = requireNotNull( sharedPref.getString(context.resources.getString(R.string.encrypted_master_password_iv), null))
        return getInitializedCipherForDecryption( context.resources.getString(R.string.biometric_key), stringToByteArray(encryptedMasterPasswordIv))
    }
    fun initBiometricAuth(context: Context, initialisedCipher: Cipher){
        val sharedPref = context.getSharedPreferences(
            context.resources.getString(R.string.pref_file_name),
            Context.MODE_PRIVATE
        )

        with(sharedPref.edit()) {
            putString(
                context.resources.getString(R.string.encrypted_master_password),
                byteArrayToString(initialisedCipher.doFinal(stringToByteArray(requireNotNull(masterPassword))))
            )
            commit()
        }

        val sharedPref2 = context.getSharedPreferences(
            context.resources.getString(R.string.pref_file_name),
            Context.MODE_PRIVATE
        )

        with(sharedPref2.edit()) {
            putString(
                context.resources.getString(R.string.encrypted_master_password_iv),
                byteArrayToString(initialisedCipher.iv)
            )
            commit()
        }
    }

    fun applyBiometricAuth(context: Context, initialisedCipher: Cipher): Boolean {
        assert(isBiometricAuthInitiated(context))
        val sharedPref = context.getSharedPreferences(
            context.resources.getString(R.string.pref_file_name),
            Context.MODE_PRIVATE
        )
        val encryptedMasterPassword = requireNotNull( sharedPref.getString(context.resources.getString(R.string.encrypted_master_password), null))
        val decryptedMasterPassword = byteArrayToString(initialisedCipher.doFinal(stringToByteArray(encryptedMasterPassword)))

        // Log.d(null, "Password: $decryptedMasterPassword")

        return applyMasterPassword(context, decryptedMasterPassword) == null
    }

    suspend fun cleanAllData(context: Context) {
        masterPassword = null
        context.getSharedPreferences(context.resources.getString(R.string.pref_file_name), 0).edit().clear().apply();
        OfflinePasswordsRepository(PasswordsDatabase.getDatabase(context).passwordEntityDao()).deleteAll()
    }
}