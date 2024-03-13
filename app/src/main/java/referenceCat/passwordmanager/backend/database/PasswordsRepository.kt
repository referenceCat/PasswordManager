package referenceCat.passwordmanager.backend.database

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [PasswordEntity] from a given data source.
 */
interface PasswordsRepository {
    /**
     * Retrieve all the items from the given data source.
     */
    fun getAllItemsStream(): Flow<List<PasswordEntity>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getItemStream(id: Int): Flow<PasswordEntity?>

    /**
     * Insert item in the data source
     */
    suspend fun insertItem(item: PasswordEntity)

    /**
     * Delete item from the data source
     */
    suspend fun deleteItem(item: PasswordEntity)

    /**
     * Update item in the data source
     */
    suspend fun updateItem(item: PasswordEntity)

    suspend fun deleteById(id: Int)
}
