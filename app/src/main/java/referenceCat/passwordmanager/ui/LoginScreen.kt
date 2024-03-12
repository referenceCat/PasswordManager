package referenceCat.passwordmanager.ui

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import referenceCat.passwordmanager.R
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import referenceCat.passwordmanager.backend.PasswordsStorage

@Preview
@Composable
fun LoginScreen(modifier: Modifier = Modifier, onSuccessfulLogin: () -> Unit = {}) {
    var password by remember { mutableStateOf("") }
    var errorMessageText by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current
    Column(modifier = modifier) {
        Text(stringResource(id = R.string.enterMasterPassword))
        PasswordTextField(visible = false, label = "Password", value = password, onChange = {password = it})
        Text(errorMessageText, modifier = Modifier.padding(
            dimensionResource(id = R.dimen.padding_small)
        ))
        Button(content = {Text(stringResource(id = R.string.enter))}, onClick = {
            errorMessageText = tryLogin(context = context, onSuccessfulLogin, password)?: ""})
    }
}

fun tryLogin(context: Context, onSuccessfulLogin: () -> Unit = {}, password:String): String? {
    val result = PasswordsStorage().isMasterPasswordTrue(context, password)
    if (result != null) return result
    onSuccessfulLogin()
    return null
}