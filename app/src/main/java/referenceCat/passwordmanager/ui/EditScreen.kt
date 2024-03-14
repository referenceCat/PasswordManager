package referenceCat.passwordmanager.ui

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import referenceCat.passwordmanager.R
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import referenceCat.passwordmanager.backend.PasswordsStorage

@Preview
@Composable
fun EditScreen(modifier: Modifier = Modifier, onSave: () -> Unit = {}, id: Int = 0, name: String = "", website: String = "", password: String = "") {
    val coroutineScope = rememberCoroutineScope()

    var nameTextFieldValue by rememberSaveable {
        mutableStateOf(name)
    }
    var websiteTextFieldValue by rememberSaveable {
        mutableStateOf(website)
    }
    var passwordTextFieldValue by rememberSaveable {
        mutableStateOf(password)
    }
    var errorMessage by rememberSaveable {
        mutableStateOf("")
    }

    val context: Context = LocalContext.current
    Column(modifier = modifier) {
        Text(stringResource(id = R.string.entryName))
        TextField(value = nameTextFieldValue, onValueChange = {nameTextFieldValue = it})
        Text(stringResource(id = R.string.entryWebsite))
        TextField(value = websiteTextFieldValue, onValueChange = {websiteTextFieldValue = it})
        Text(stringResource(id = R.string.entryPassword))
        PasswordTextField(visible = false, label = "Password", value = passwordTextFieldValue, onChange = {passwordTextFieldValue = it})
        Text(errorMessage)
        Button(onClick = {
            coroutineScope.launch() {
                errorMessage = if (id == 0)
                    savePassword(context, nameTextFieldValue, websiteTextFieldValue, passwordTextFieldValue, onSave)?: ""
                else
                    updatePassword(context, id, nameTextFieldValue, websiteTextFieldValue, passwordTextFieldValue, onSave)?: ""
            }
        }) {
            Text(text = stringResource(id = R.string.save))
        }
    }
}

suspend fun savePassword(context: Context, name: String, website: String, password: String, onSave: () -> Unit = {}): String? {
    if (name == "" || website == "" || password == "") {
        return context.resources.getString(R.string.errorInvalidFieldValue)
    }

    PasswordsStorage.getInstance().insertPasswordData(context, name, website, password)
    onSave()
    return null
}

suspend fun updatePassword(context: Context, id: Int, name: String, website: String, password: String, onUpdate: () -> Unit = {}): String? {
    if (name == "" || website == "" || password == "") {
        return context.resources.getString(R.string.errorInvalidFieldValue)
    }

    PasswordsStorage.getInstance().updatePasswordData(context, id, name, website, password)
    onUpdate()
    return null
}