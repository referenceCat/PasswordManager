package referenceCat.passwordmanager.ui

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import referenceCat.passwordmanager.R
import referenceCat.passwordmanager.backend.PasswordsStorage


@Composable
fun RegistrationScreen(modifier: Modifier = Modifier, onSuccessfulRegistration: () -> Unit = {}) {
    val context: Context = LocalContext.current
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        MasterPasswordRegistrationForm(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
            onRegistrationClick = { password ->
                if (register(context, onSuccessfulRegistration, password))
                    onSuccessfulRegistration()
            })
    }
}

@Preview
@Composable
fun MasterPasswordRegistrationForm(
    modifier: Modifier = Modifier,
    onRegistrationClick: (password: String) -> Unit = {}
) {
    var password1 by rememberSaveable {
        mutableStateOf("")
    }

    var password2 by rememberSaveable {
        mutableStateOf("")
    }

    var errorMessageText1 by rememberSaveable {
        mutableStateOf("")
    }

    var errorMessageText2 by rememberSaveable {
        mutableStateOf("")
    }

    val context: Context = LocalContext.current
    Column(modifier = modifier) {
        Text(stringResource(id = R.string.create_master_password_description))

        Spacer(Modifier.size(dimensionResource(id = R.dimen.padding_small)))

        PasswordTextField(visible = false,
            label = stringResource(id = R.string.create_master_password_text_field),
            modifier = Modifier.fillMaxWidth(),
            value = password1,
            onChange = {
                password1 = it
                errorMessageText1 = validatePassword(context, password1) ?: ""
            }
        )

        Text(errorMessageText1)
        Spacer(Modifier.size(dimensionResource(id = R.dimen.padding_small)))

        //Text(stringResource(id = R.string.createMasterPassword))
        PasswordTextField(visible = false,
            label = stringResource(id = R.string.repeat_master_password_text_field),
            modifier = Modifier.fillMaxWidth(),
            value = password2,
            onChange = {
                password2 = it
                errorMessageText2 = if (password1 != password2) context.resources.getString(R.string.error_passwords_are_not_equal) else ""
            })

        Text(errorMessageText2)

        Spacer(Modifier.size(dimensionResource(id = R.dimen.padding_small)))

        Button(
            onClick = {
                if (validateFormData(context, password1, password2)) onRegistrationClick(password1)
            },
            modifier = Modifier.fillMaxWidth(),
            // shape = RoundedCornerShape(dimensionResource(id = R.dimen.button_rounded_corner_radius)),
        ) {
            Text(stringResource(id = R.string.enter))
        }

    }
}

fun validateFormData(context: Context, password1: String, password2: String): Boolean {
    return validatePassword(context, password1) == null && password1 == password2
}

fun register(context: Context, onSuccessfulRegistration: () -> Unit, password: String): Boolean {
    val tag = "referenceCat.passwordmanager.RegistrationScreen"
    val initResult = PasswordsStorage.getInstance().initMasterPassword(context, password)
    if (initResult != null) {
        Log.d(tag, initResult)
        return false
    }
    val applyResult = PasswordsStorage.getInstance().applyMasterPassword(context, password)
    if (applyResult != null) {
        Log.d(tag, applyResult)
        return false
    }

    onSuccessfulRegistration()
    return true
}

fun validatePassword(context: Context, password: String): String? {
    if (password.length < 8) return context.resources.getString(R.string.error_password_is_too_short)
    return null
}