package referenceCat.passwordmanager.ui

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import referenceCat.passwordmanager.R
import androidx.compose.material3.Button
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import referenceCat.passwordmanager.backend.PasswordsStorage

@Preview
@Composable
fun RegistrationScreen(modifier: Modifier = Modifier, onSuccessfulRegistration: () -> Unit = {}) {
    var password1 by rememberSaveable {
        mutableStateOf("")
    }

    var password2 by rememberSaveable {
        mutableStateOf("")
    }

    var errorMessageText by rememberSaveable {
        mutableStateOf("")
    }

    val errorPasswordsAreNotEqual = stringResource(id = R.string.errorPasswordsAreNotEqual)
    val context: Context = LocalContext.current

    Column(modifier = modifier) {
        Text(stringResource(id = R.string.createMasterPasswordDescription), modifier = Modifier.padding(
            dimensionResource(id = R.dimen.padding_small)))
        // Text(stringResource(id = R.string.createMasterPassword))
        PasswordTextField(visible = false, label = stringResource(id = R.string.createMasterPassword), modifier = Modifier.padding(
            dimensionResource(id = R.dimen.padding_small)), value = password1, onChange = {password1 = it})
        //Text(stringResource(id = R.string.createMasterPassword))
        PasswordTextField(visible = false, label = stringResource(id = R.string.repeatMasterPassword), modifier = Modifier.padding(
            dimensionResource(id = R.dimen.padding_small)), value = password2, onChange = {password2 = it})
        Text(errorMessageText, modifier = Modifier.padding(
            dimensionResource(id = R.dimen.padding_small)))
        Button(content = {Text(stringResource(id = R.string.enter))}, onClick = {errorMessageText = tryRegister(
            context = context,
            onSuccessfulRegistration = onSuccessfulRegistration,
            password1 = password1,
            password2 = password2,)?: ""},
            modifier = Modifier.padding(
            dimensionResource(id = R.dimen.padding_small)))

    }
}

fun tryRegister(context: Context, onSuccessfulRegistration: () -> Unit, password1: String, password2: String): String? {
    if (password1.length < 8) return context.resources.getString(R.string.errorPasswordIsTooShort)
    else if (password1 != password2)  return context.resources.getString(R.string.errorPasswordsAreNotEqual)
    else {
        val result = PasswordsStorage.getInstance().initMasterPassword(context, password1)
        if (result != null) return result
        PasswordsStorage.getInstance().applyMasterPassword(context, password1)
        onSuccessfulRegistration()
    }
    return null
}