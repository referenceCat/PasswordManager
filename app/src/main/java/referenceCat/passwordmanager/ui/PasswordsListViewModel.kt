package referenceCat.passwordmanager.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import referenceCat.passwordmanager.backend.DecryptedPasswordData
import referenceCat.passwordmanager.backend.PasswordsStorage


class PasswordsListViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext
    val passwords: LiveData<List<DecryptedPasswordData>> = PasswordsStorage.getInstance().getAllPasswords(context).asLiveData()
}