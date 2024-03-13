package referenceCat.passwordmanager.backend

import android.content.Context
import android.util.Base64
import android.util.Log
import referenceCat.passwordmanager.R
import referenceCat.passwordmanager.backend.database.OfflinePasswordsRepository
import referenceCat.passwordmanager.backend.database.PasswordEntity
import referenceCat.passwordmanager.backend.database.PasswordsDatabase
import referenceCat.passwordmanager.backend.database.PasswordsRepository
import java.nio.ByteBuffer
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

    private var keySpec: SecretKeySpec? = null

    private val decryptedPasswordsData: MutableList<DecryptedPasswordData> = mutableListOf()

    private val tag = "referenceCat.passwordmanager.cryptography"
    init {
        if (instance == null) {
            instance = this
        }
    }


    private fun generateEncryptionKey(password: String) {
        val pbKeySpec = PBEKeySpec(password.toCharArray(), stringToByteArray("encryption key salt"), 1324, 256)
        val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val keyBytes = secretKeyFactory.generateSecret(pbKeySpec).encoded
        keySpec = SecretKeySpec(keyBytes, "AES")
    }

    private fun encrypt(plaintext: String): Pair<String, String> {
        assert(keySpec != null) { "can't encrypt without key" }

        // Log.d(tag, "encrypt: $plaintext")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        val encryptedTextBytes = cipher.doFinal(stringToByteArray(plaintext))
        val encryptedText: String = byteArrayToString(encryptedTextBytes)
        val ivBytes = cipher.iv
        val iv: String = byteArrayToString(ivBytes)
        // Log.d(tag, "result: $encryptedText, iv: $iv")
        // Log.d(tag, "bytes: ${encryptedTextBytes.contentToString()}, iv: ${ivBytes.contentToString()}")
        return Pair(encryptedText, iv)
    }
    private fun decrypt(cipherText: String, initVector: String): String {
        assert(keySpec != null) { "can't decrypt without key" }
        // Log.d(tag, "decrypt: $cipherText, iv: $initVector")

        val ivBytes = stringToByteArray(initVector)
        val cipherTextBytes = stringToByteArray(cipherText)
        // Log.d(tag, "bytes: ${cipherTextBytes.contentToString()}, iv: ${ivBytes.contentToString()}")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        val ivSpec = IvParameterSpec(ivBytes)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
        val decryptedTextBytes = cipher.doFinal(cipherTextBytes)
        val decryptedText = byteArrayToString(decryptedTextBytes)
        // Log.d(tag, "result: $decryptedText")
        return decryptedText
    }

    private fun digestMessage(input:String): String {
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
        val sharedPref = context.getSharedPreferences(context.resources.getString(R.string.key_master_passoword_digest), Context.MODE_PRIVATE)

        sharedPref.getString(context.resources.getString(R.string.key_master_passoword_digest), null) ?: return false
        return true

    }

    fun applyMasterPassword(context: Context, password: String): String? {
        if (!isMasterPasswordInitiated(context)) return "Master-password isn't initiated"

        val sharedPref = context.getSharedPreferences(context.resources.getString(R.string.key_master_passoword_digest), Context.MODE_PRIVATE)
        val passwordDigestOnDisk: String = sharedPref.getString(context.resources.getString(R.string.key_master_passoword_digest), null) ?: return "Master-password isn't initiated"

        val passwordDigestFromUser = digestMessage(password)

        if (passwordDigestFromUser != passwordDigestOnDisk) return "Incorrect password."
        generateEncryptionKey(password)
        return null
    }

    fun initMasterPassword(context: Context, password: String): String?  {
        if (isMasterPasswordInitiated(context)) return "Master-password is already initiated"
        val sharedPref = context.getSharedPreferences(context.resources.getString(R.string.key_master_passoword_digest), Context.MODE_PRIVATE)

        with (sharedPref.edit()) {
            putString(context.resources.getString(R.string.key_master_passoword_digest), digestMessage(password))
            commit()
        }
        generateEncryptionKey(password)
        return null
    }

    suspend fun savePassword(context: Context, name: String, website: String, password: String){
        val repository: PasswordsRepository = OfflinePasswordsRepository(PasswordsDatabase.getDatabase(context).passwordEntityDao())
        val (encryptedPassword, initVector) = encrypt(password)
        repository.insertItem(PasswordEntity(0, name, website, encryptedPassword, initVector))
    }

    suspend fun updateData(context: Context) {
        decryptedPasswordsData.clear()
        val repository: PasswordsRepository = OfflinePasswordsRepository(PasswordsDatabase.getDatabase(context).passwordEntityDao())
        repository.getAllItemsStream().collect { entities ->
            entities.forEach {
                decryptedPasswordsData.add(
                    DecryptedPasswordData(
                        it.id,
                        it.name,
                        it.website,
                        decrypt(it.encryptedPassword, it.initVector)
                    )
                )
            }
        }
    }

    fun getData(): List<DecryptedPasswordData> {
        return decryptedPasswordsData.toList()
    }
}