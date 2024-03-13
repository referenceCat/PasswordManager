package referenceCat.passwordmanager.backend.database
import kotlinx.coroutines.flow.Flow

class OfflinePasswordsRepository(private val itemDao: PasswordEntityDao) : PasswordsRepository {
    override fun getAllItemsStream(): Flow<List<PasswordEntity>> = itemDao.getAllItems()

    override fun getItemStream(id: Int): Flow<PasswordEntity?> = itemDao.getItem(id)

    override suspend fun insertItem(item: PasswordEntity) = itemDao.insert(item)

    override suspend fun deleteItem(item: PasswordEntity) = itemDao.delete(item)

    override suspend fun updateItem(item: PasswordEntity) = itemDao.update(item)

    override suspend fun deleteById(id: Int) = itemDao.deleteById(id)
}
