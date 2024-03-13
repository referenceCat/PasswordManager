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
import kotlinx.coroutines.launch
import referenceCat.passwordmanager.backend.PasswordsStorage

@Preview
@Composable
fun EntryEditScreen(modifier: Modifier = Modifier, onSave: () -> Unit = {}) {
    val coroutineScope = rememberCoroutineScope()

    var name by rememberSaveable {
        mutableStateOf("")
    }
    var website by rememberSaveable {
        mutableStateOf("")
    }
    var password by rememberSaveable {
        mutableStateOf("")
    }
    var errorMessage by rememberSaveable {
        mutableStateOf("")
    }

    val context: Context = LocalContext.current
    Column(modifier = modifier) {
        Text(stringResource(id = R.string.entryName))
        TextField(value = name, onValueChange = {name = it})
        Text(stringResource(id = R.string.entryWebsite))
        TextField(value = website, onValueChange = {website = it})
        Text(stringResource(id = R.string.entryPassword))
        PasswordTextField(visible = false, label = "Password", value = password, onChange = {password = it})
        Text(errorMessage)
        Button(onClick = {
            coroutineScope.launch {
                errorMessage = savePassword(context, name, website, password, onSave)?: ""
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