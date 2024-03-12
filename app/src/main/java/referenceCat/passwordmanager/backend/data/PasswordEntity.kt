package referenceCat.passwordmanager.backend.data

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update

@Entity(tableName = "passwords")
data class PasswordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val website: String,
    val encryptedPassword: String,
    val initVector: String
) {}

