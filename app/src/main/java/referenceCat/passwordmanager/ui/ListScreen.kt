package referenceCat.passwordmanager.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun ListScreen(modifier: Modifier = Modifier) {
    LazyColumn( modifier = modifier) {
        items(10) { EntryItem(modifier = Modifier.padding(horizontal = 5.dp, vertical = 5.dp))}
    }
}

@Preview
@Composable
fun EntryItem(modifier: Modifier = Modifier,
              password: String = "default1234",
              website: String = "default.website.com",
              name: String = "Default name") {
    Card(modifier = modifier.fillMaxWidth(1f)) {
        Text(name, modifier = Modifier.padding(5.dp))
        Text(website, modifier = Modifier.padding(5.dp))
        PasswordText(modifier = Modifier.padding(5.dp), visible = false, text = password)
    }
}

