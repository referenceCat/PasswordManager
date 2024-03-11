package referenceCat.passwordmanager.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import referenceCat.passwordmanager.R
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun EntryEditScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(stringResource(id = R.string.entryName))
        TextField(value = "", onValueChange = {})
        Text(stringResource(id = R.string.entryWebsite))
        TextField(value = "", onValueChange = {})
        Text(stringResource(id = R.string.entryPassword))
        PasswordTextField(visible = false, label = stringResource(id = R.string.defaultPassword))

    }
}
