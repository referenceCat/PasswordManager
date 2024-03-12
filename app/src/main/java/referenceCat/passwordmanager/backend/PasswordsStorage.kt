package referenceCat.passwordmanager.backend

import android.content.Context
import referenceCat.passwordmanager.R
import java.math.BigInteger
import java.security.MessageDigest

class PasswordsStorage {

    private fun digestMessage(input:String): String {
        val md = MessageDigest.getInstance("SHA-256")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }
    fun isMasterPasswordInitiated(context: Context): Boolean {
        val sharedPref = context.getSharedPreferences(context.resources.getString(R.string.key_master_passoword_digest), Context.MODE_PRIVATE)

        sharedPref.getString(context.resources.getString(R.string.key_master_passoword_digest), null) ?: return false
        return true

    }

    fun isMasterPasswordTrue(context: Context, password: String): String? {
        if (!isMasterPasswordInitiated(context)) return "Master-password isn't initiated"

        val sharedPref = context.getSharedPreferences(context.resources.getString(R.string.key_master_passoword_digest), Context.MODE_PRIVATE)
        val passwordDigestOnDisk: String = sharedPref.getString(context.resources.getString(R.string.key_master_passoword_digest), null) ?: return "Master-password isn't initiated"

        val passwordDigestFromUser = digestMessage(password)

        if (passwordDigestFromUser != passwordDigestOnDisk) return "Incorrect password."
        return null
    }

    fun initMasterPassword(context: Context, password: String): String?  {
        if (isMasterPasswordInitiated(context)) return "Master-password is already initiated"
        val sharedPref = context.getSharedPreferences(context.resources.getString(R.string.key_master_passoword_digest), Context.MODE_PRIVATE)

        with (sharedPref.edit()) {
            putString(context.resources.getString(R.string.key_master_passoword_digest), digestMessage(password))
            commit()
        }
        return null
    }
}