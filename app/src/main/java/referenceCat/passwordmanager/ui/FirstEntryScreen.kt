package referenceCat.passwordmanager.ui

import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun FirstEntryScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(stringResource(id = R.string.createMasterPasswordDescription))
        Text(stringResource(id = R.string.createMasterPassword))
        PasswordTextField()
        Text(stringResource(id = R.string.createMasterPassword))
        PasswordTextField()
        Button(content = {Text(stringResource(id = R.string.enter))}, onClick = {})

    }
}
