package referenceCat.passwordmanager.ui

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import referenceCat.passwordmanager.R
import referenceCat.passwordmanager.backend.PasswordsStorage

@Preview
@Composable
fun EditScreen(
    modifier: Modifier = Modifier,
    onSave: () -> Unit = {},
    id: Int = 0,
    name: String = "",
    website: String = "",
    password: String = ""
) {
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
    var errorMessageName by rememberSaveable {
        mutableStateOf("")
    }

    var errorMessageWebsite by rememberSaveable {
        mutableStateOf("")
    }
    var errorMessagePassword by rememberSaveable {
        mutableStateOf("")
    }

    val context: Context = LocalContext.current

    Column(modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium))) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = nameTextFieldValue,
            onValueChange = {
                nameTextFieldValue = it
                errorMessageName = ""
            },
            label = { Text(text = stringResource(id = R.string.name_text_field)) })

        Text(text = errorMessageName, color = Color.Red)

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = websiteTextFieldValue,
            onValueChange = {
                websiteTextFieldValue = it
                errorMessageWebsite = ""
            },
            label = { Text(text = stringResource(id = R.string.website_text_field)) })

        Text(text = errorMessageWebsite, color = Color.Red)

        PasswordTextField(
            modifier = Modifier.fillMaxWidth(),
            visible = false,
            label = stringResource(id = R.string.password_text_field),
            value = passwordTextFieldValue,
            onChange = {
                passwordTextFieldValue = it
                errorMessagePassword = ""
            },
        )

        Text(text = errorMessagePassword, color = Color.Red)

        Button(onClick = {
            var isValid: Boolean = true
            if (nameTextFieldValue.isEmpty()) {
                isValid = false
                errorMessageName = context.resources.getString(R.string.error_empty_field)
            }

            if (websiteTextFieldValue.isEmpty()) {
                isValid = false
                errorMessageWebsite = context.resources.getString(R.string.error_empty_field)
            }

            if (passwordTextFieldValue.isEmpty()) {
                isValid = false
                errorMessagePassword = context.resources.getString(R.string.error_empty_field)
            }

            if (isValid) {
                coroutineScope.launch {
                    if (id == 0)
                        savePassword(
                            context,
                            nameTextFieldValue,
                            websiteTextFieldValue,
                            passwordTextFieldValue,
                            onSave
                        )
                    else
                        updatePassword(
                            context,
                            id,
                            nameTextFieldValue,
                            websiteTextFieldValue,
                            passwordTextFieldValue,
                            onSave
                        )
                }
            }

        }
        ) {
            Text(text = stringResource(id = R.string.save))
        }
    }
}

suspend fun savePassword(
    context: Context,
    name: String,
    website: String,
    password: String,
    onSave: () -> Unit = {}
): Boolean {
    if (name == "" || website == "" || password == "") {
        return false
    }

    PasswordsStorage.getInstance().insertPasswordData(context, name, website, password)
    onSave()
    return true
}

suspend fun updatePassword(
    context: Context,
    id: Int,
    name: String,
    website: String,
    password: String,
    onUpdate: () -> Unit = {}
): Boolean {
    if (name == "" || website == "" || password == "") {
        return false
    }

    PasswordsStorage.getInstance().updatePasswordData(context, id, name, website, password)
    onUpdate()
    return true
}