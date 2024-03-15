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

    @Query("DELETE FROM passwords WHERE id = :id")
    abstract fun deleteById(id: Int)

    @Query("SELECT * FROM passwords WHERE id = :id")
    abstract fun getItem(id: Int): Flow<PasswordEntity>

    @Query("SELECT * FROM passwords")
    abstract fun getAllItems(): Flow<List<PasswordEntity>>

    @Query("DELETE FROM passwords")
    abstract fun deleteAll()
}