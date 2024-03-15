package referenceCat.passwordmanager.ui

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import referenceCat.passwordmanager.R
import referenceCat.passwordmanager.backend.PasswordsStorage

@Preview
@Composable
fun LoginScreen(modifier: Modifier = Modifier, onSuccessfulLogin: () -> Unit = {}) {
    val context = LocalContext.current

    Box(contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        LoginForm(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
            onLoginButtonClick = {
                return@LoginForm tryLogin(context, onSuccessfulLogin, it) ?: ""
            })

        IconButton(modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(dimensionResource(id = R.dimen.padding_medium))
            .size(dimensionResource(id = R.dimen.fingerprint_button_size)),
            onClick = { /*TODO*/ }
        ) {
            Icon(imageVector = ImageVector.vectorResource(id = R.drawable.baseline_fingerprint_48),
                contentDescription = "fingerprint button")
        }
    }

}

fun tryLogin(context: Context, onSuccessfulLogin: () -> Unit = {}, password: String): String? {
    val result = PasswordsStorage.getInstance().applyMasterPassword(context, password)
    if (result != null) return result
    onSuccessfulLogin()
    return null
}

@Preview
@Composable
fun LoginForm(modifier: Modifier = Modifier,
              onLoginButtonClick: (password: String) -> String = {""}) {

    var password by rememberSaveable { mutableStateOf("") }
    var errorMessageText by rememberSaveable { mutableStateOf("") }
    // val context = LocalContext.current

    Column(modifier = modifier) {
        PasswordTextField(
            modifier = Modifier.fillMaxWidth(),
            visible = false,
            label = stringResource(id = R.string.enter_master_password_text_field),
            value = password,
            onChange = {
                password = it
                errorMessageText = ""
            }
        )

        Text(errorMessageText, color = Color.Red)

        Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { errorMessageText = onLoginButtonClick(password) }
        ) {
            Text(stringResource(id = R.string.enter))
        }
    }
}
