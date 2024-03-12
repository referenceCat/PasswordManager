package referenceCat.passwordmanager.backend.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PasswordEntity::class], version = 2, exportSchema = false)
abstract class PasswordsDatabase: RoomDatabase() {
    abstract fun passwordEntityDao(): PasswordEntityDao

    companion object {
        @Volatile
        private var Instance: PasswordsDatabase? = null

        fun getDatabase(context: Context): PasswordsDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, PasswordsDatabase::class.java, "passwords_database")
                    .fallbackToDestructiveMigration() // TODO
                    .build()
                    .also { Instance = it }
            }
        }
    }
}