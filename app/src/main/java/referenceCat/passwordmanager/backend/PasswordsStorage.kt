package referenceCat.passwordmanager.backend

import android.content.Context
import referenceCat.passwordmanager.R
import referenceCat.passwordmanager.backend.data.OfflinePasswordsRepository
import referenceCat.passwordmanager.backend.data.PasswordEntity
import referenceCat.passwordmanager.backend.data.PasswordsDatabase
import referenceCat.passwordmanager.backend.data.PasswordsRepository
import java.math.BigInteger
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


class PasswordsStorage private constructor() {
    companion object {

        @Volatile
        private var instance: PasswordsStorage? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: PasswordsStorage().also { instance = it }
            }
    }

    fun doSomething() = "Doing something"

    init {
        if (instance == null) {
            instance = this
        }
    }

    private var keySpec: SecretKeySpec? = null

    fun generateEncryptionKey(password: String) {
        val pbKeySpec = PBEKeySpec(password.toCharArray(), "encryption key salt".toByteArray(), 1324, 256)
        val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val keyBytes = secretKeyFactory.generateSecret(pbKeySpec).encoded
        keySpec = SecretKeySpec(keyBytes, "AES")
    }

    fun encrypt(plaintext: String): Pair<String, String> {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        val encryptedText: String = byteArrayToString(cipher.doFinal(plaintext.toByteArray()))
        val iv: String = byteArrayToString(cipher.iv)
        return Pair(encryptedText, iv)
    }
    fun decrypt(cipherText: String, initVector: String): String {
        val cipher = Cipher.getInstance("AES")
        val ivSpec = IvParameterSpec(initVector.toByteArray())
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
        val decryptedText = cipher.doFinal(cipherText.toByteArray())
        return byteArrayToString(decryptedText)
    }

    private fun digestMessage(input:String): String {
        val md = MessageDigest.getInstance("SHA-256")
        return byteArrayToString(md.digest(input.toByteArray()))
    }

    private fun byteArrayToString(input: ByteArray) = BigInteger(1, input).toString(16).padStart(32, '0')

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
        // TODO check if encryption key is initialised
        val repository: PasswordsRepository = OfflinePasswordsRepository(PasswordsDatabase.getDatabase(context).passwordEntityDao())
        val (encryptedPassword, initVector) = encrypt(password)
        repository.insertItem(PasswordEntity(0, name, website, encryptedPassword, initVector))
    }
}