package referenceCat.passwordmanager.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import kotlinx.coroutines.launch
import referenceCat.passwordmanager.backend.DecryptedPasswordData
import referenceCat.passwordmanager.backend.PasswordsStorage
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ListScreen(modifier: Modifier = Modifier, onItemClick: (id: Int) -> Unit = {}, onActionButtonClick: () -> Unit = {}) {
    val passwordsListViewModel = viewModel<PasswordsListViewModel>()


    val dataList: List<DecryptedPasswordData> by passwordsListViewModel.passwords.observeAsState(
        initial = listOf()
    )

    Log.d(null, dataList.size.toString())

    Scaffold(modifier = modifier,
        floatingActionButton = {
        FloatingActionButton(onClick = onActionButtonClick) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add"
        )
    }
    }) {
        innerPadding -> innerPadding// Scaffold doesn't have any padding but value must be used somewhere

        LazyColumn() {
            items(dataList.size) {
                val item = dataList.get(index = it) // TODOs
                EntryItem(modifier = Modifier.padding(horizontal = 5.dp, vertical = 5.dp),
                    onClick = onItemClick,
                    id = item.id,
                    name = item.name,
                    website = item.website,
                    password = item.password)
            }

        }
    }
}

@Composable
fun EntryItem(modifier: Modifier = Modifier,
              password: String,
              website: String,
              name: String,
              onClick: (id: Int) -> Unit = {},
              id: Int) {
    Card(modifier = modifier
        .fillMaxWidth(1f)
        .padding(3.dp)){
        Text(name, modifier = Modifier.padding(5.dp))
        Text(website, modifier = Modifier.padding(5.dp))
        PasswordText(modifier = Modifier.padding(5.dp), visible = false, text = password)

        IconButton(onClick = { onClick(id) }) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_edit_24),
                contentDescription = "Edit"
            )
        }

        IconButton(onClick = {}) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_delete_24),
                contentDescription = "Delete"
            )
        }
    }
}

@Preview
@Composable
fun EntryItemPreview(modifier: Modifier = Modifier,
              password: String = "default1234",
              website: String = "default.website.com",
              name: String = "Default name",
              onClick: (id: Int) -> Unit = {},
              id: Int = 0) {
    EntryItem(password = password, website = website, name = name, id = 0, onClick = onClick)
}
