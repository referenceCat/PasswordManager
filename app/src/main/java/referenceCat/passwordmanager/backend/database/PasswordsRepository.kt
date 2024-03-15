package referenceCat.passwordmanager.backend.database

import kotlinx.coroutines.flow.Flow


interface PasswordsRepository {

    fun getAllItemsStream(): Flow<List<PasswordEntity>>

    fun getItemStream(id: Int): Flow<PasswordEntity?>

    suspend fun insertItem(item: PasswordEntity)

    suspend fun deleteItem(item: PasswordEntity)

    suspend fun updateItem(item: PasswordEntity)

    suspend fun deleteById(id: Int)

    suspend fun deleteAll()
}
