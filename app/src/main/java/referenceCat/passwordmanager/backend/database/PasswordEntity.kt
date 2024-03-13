package referenceCat.passwordmanager.backend.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "passwords")
data class PasswordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val website: String,
    val encryptedPassword: String,
    val initVector: String
) {}

