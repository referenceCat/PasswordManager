package referenceCat.passwordmanager.backend.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PasswordEntityDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(passwordEntity: PasswordEntity)

    @Update
    abstract suspend fun update(passwordEntity: PasswordEntity)

    @Delete
    abstract suspend fun delete(passwordEntity: PasswordEntity)

    @Query("SELECT * from passwords WHERE id = :id")
    abstract fun getItem(id: Int): Flow<PasswordEntity>

    @Query("SELECT * from passwords")
    abstract fun getAllItems(): Flow<List<PasswordEntity>>
}