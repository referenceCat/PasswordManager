package referenceCat.passwordmanager.ui

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import referenceCat.passwordmanager.R
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import referenceCat.passwordmanager.backend.DecryptedPasswordData
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import referenceCat.passwordmanager.backend.PasswordsStorage
import kotlin.coroutines.CoroutineContext

@Composable
fun ListScreen(modifier: Modifier = Modifier, onItemClick: (id: Int) -> Unit = {}, onActionButtonClick: () -> Unit = {}, onEditClick: (id: Int, name: String, website: String, password: String) -> Unit) {
    val listScreenViewModel = viewModel<ListScreenViewModel>()

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val dataList: List<DecryptedPasswordData> by listScreenViewModel.passwords.observeAsState(
        initial = listOf()
    )

    var showDialog by rememberSaveable { mutableStateOf(false) }
    var intIdToDialog by rememberSaveable { mutableIntStateOf(0) }
    if (showDialog) {
        ConfirmCancelDialog(
            onDismissRequest = {showDialog = false},
            onConfirmation = {
                coroutineScope.launch(Dispatchers.IO) {PasswordsStorage.getInstance().deletePasswordData(context, intIdToDialog)}
                             showDialog = false
                             },
            dialogTitle = "Delete password data?",
            dialogText = "Password data deletion is irreversible action",
            icon = Icons.Filled.Delete
        )
    }

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
            if (dataList.isEmpty()) item() {Text("Password data will be displayed here")}
            items(dataList.size) {
                val item = dataList.get(index = it) // TODOs
                EntryItem(modifier = Modifier.padding(horizontal = 5.dp, vertical = 5.dp),
                    onClick = onItemClick,
                    id = item.id,
                    name = item.name,
                    website = item.website,
                    password = item.password,
                    onDeleteClick = {
                        showDialog = true
                        intIdToDialog = item.id
                    },
                    onEditClick = onEditClick)
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
              onDeleteClick: (id: Int) -> Unit = {},
              onEditClick: (id: Int, name: String, website: String, password: String) -> Unit,
              id: Int) {

    Card(modifier = modifier
        .fillMaxWidth(1f)
        .padding(3.dp)){
        Text(name, modifier = Modifier.padding(5.dp))
        Text(website, modifier = Modifier.padding(5.dp))
        PasswordText(modifier = Modifier.padding(5.dp), visible = false, text = password)

        IconButton(onClick = { onEditClick(id, name, website, password) }) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_edit_24),
                contentDescription = "Edit"
            )
        }

        IconButton(onClick = { onDeleteClick(id) }) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_delete_24),
                contentDescription = "Delete"
            )
        }
    }
}

@Composable
fun ConfirmCancelDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}