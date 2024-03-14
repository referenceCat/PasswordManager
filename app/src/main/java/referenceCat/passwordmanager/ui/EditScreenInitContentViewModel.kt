package referenceCat.passwordmanager.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import referenceCat.passwordmanager.backend.DecryptedPasswordData
import referenceCat.passwordmanager.backend.PasswordsStorage

class EditScreenInitContentViewModel: ViewModel() {
    var id = mutableIntStateOf(0)
    var name = mutableStateOf("")
    var website = mutableStateOf("")
    var password = mutableStateOf("")

    fun setData(id: Int, name: String, website: String, password: String) {
        this.id.intValue = id
        this.name.value = name
        this.website.value = website
        this.password.value = password
    }

    fun clearData() {
        this.id.intValue = 0
        this.name.value = ""
        this.website.value = ""
        this.password.value = ""
    }
}