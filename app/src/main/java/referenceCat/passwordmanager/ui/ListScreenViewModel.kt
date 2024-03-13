package referenceCat.passwordmanager.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import referenceCat.passwordmanager.backend.DecryptedPasswordData
import referenceCat.passwordmanager.backend.PasswordsStorage


class ListScreenViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext
    val passwords: LiveData<List<DecryptedPasswordData>> = PasswordsStorage.getInstance().getAllPasswords(context).asLiveData()
}