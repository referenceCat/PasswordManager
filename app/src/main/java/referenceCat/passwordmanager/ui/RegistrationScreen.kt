package referenceCat.passwordmanager.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import referenceCat.passwordmanager.R
import androidx.compose.material3.Button
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun RegistrationScreen(modifier: Modifier = Modifier, onSuccessfulRegistration: () -> Unit = {}) {
    Column(modifier = modifier) {
        Text(stringResource(id = R.string.createMasterPasswordDescription))
        Text(stringResource(id = R.string.createMasterPassword))
        PasswordTextField(visible = false, label = "password")
        Text(stringResource(id = R.string.createMasterPassword))
        PasswordTextField(visible = false, label = "password")
        Button(content = {Text(stringResource(id = R.string.enter))}, onClick = { tryRegister(onSuccessfulRegistration)})

    }
}

fun tryRegister(onSuccessfulRegistration: () -> Unit = {}) {
    onSuccessfulRegistration()
}
