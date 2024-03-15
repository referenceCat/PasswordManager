package referenceCat.passwordmanager.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import referenceCat.passwordmanager.R
import referenceCat.passwordmanager.backend.DecryptedPasswordData
import referenceCat.passwordmanager.backend.PasswordsStorage


val cardTitleTextStyle = TextStyle(
    fontSize = 16.sp,
    color = Color.Black,
    fontWeight = FontWeight.SemiBold,
    textAlign = TextAlign.Start,
    letterSpacing = 1.sp,
)

@Composable
fun ListScreen(
    modifier: Modifier = Modifier,
    onItemClick: (id: Int) -> Unit = {},
    onActionButtonClick: () -> Unit = {},
    onEditClick: (id: Int, name: String, website: String, password: String) -> Unit
) {
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
            onDismissRequest = { showDialog = false },
            onConfirmation = {
                coroutineScope.launch(Dispatchers.IO) {
                    PasswordsStorage.getInstance().deletePasswordData(context, intIdToDialog)
                }
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
        }) { innerPadding ->
        innerPadding// Scaffold doesn't have any padding but value must be used somewhere

        LazyColumn {
            if (dataList.isEmpty()) item { Text("Password data will be displayed here") }
            items(dataList.size) {
                val item = dataList.get(index = it) // TODOs
                EntryItem(
                    modifier = Modifier.padding(
                        top = dimensionResource(id = R.dimen.padding_small),
                        start = dimensionResource(id = R.dimen.padding_small),
                        end = dimensionResource(id = R.dimen.padding_small)
                    ),
                    onClick = onItemClick,
                    id = item.id,
                    name = item.name,
                    website = item.website,
                    password = item.password,
                    onDeleteClick = {
                        showDialog = true
                        intIdToDialog = item.id
                    },
                    onEditClick = onEditClick
                )
            }

        }
    }
}

@Composable
fun EntryItem(
    modifier: Modifier = Modifier,
    password: String,
    website: String,
    name: String,
    icon: ImageBitmap? = null,
    onClick: (id: Int) -> Unit = {},
    onDeleteClick: (id: Int) -> Unit = {},
    onEditClick: (id: Int, name: String, website: String, password: String) -> Unit = { id: Int, name: String, website: String, password: String -> null },
    id: Int
) {

    Card(
        modifier = modifier.fillMaxWidth(1f)
    ) {
        EntryNameRow(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
            icon = null,
            text = name,
            onEditClick = { onEditClick(id, name, website, password) },
            onDeleteClick = { onDeleteClick(id) }

        )
        Divider(color = Color.Gray, thickness = 0.5.dp)
        Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_very_small)))
        // Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)))
        EntryWebsiteRow(
            Modifier.padding(
                horizontal = dimensionResource(id = R.dimen.padding_small)
            ),
            website
        )
        EntryPasswordRow(
            Modifier.padding(
                horizontal = dimensionResource(id = R.dimen.padding_small)
            ), password
        )
        Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_very_small)))
    }
}

@Composable
fun EntryNameRow(
    modifier: Modifier = Modifier,
    icon: ImageBitmap?,
    text: String,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(modifier = modifier) {
        Image(
            icon ?: ImageBitmap.imageResource(R.drawable.placeholder),
            contentDescription = null,
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.item_icon_size))
                .clip(RoundedCornerShape(20))
                .align(Alignment.CenterVertically)
        )

        Text(
            text = text,
            modifier = Modifier
                //.fillMaxSize()
                .align(Alignment.CenterVertically)
                .padding(start = dimensionResource(id = R.dimen.padding_small))
                .weight(1f),
            style = cardTitleTextStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        IconButton(onClick = onEditClick) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_edit_24),
                contentDescription = null
            )
        }

        IconButton(onClick = onDeleteClick) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_delete_24),
                contentDescription = null
            )
        }
    }
}

@Composable
fun EntryWebsiteRow(modifier: Modifier = Modifier, website: String) {
    Row(modifier = modifier) {
        Column(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
        ) {
            Text(stringResource(id = R.string.website_label), color = Color.DarkGray)
            Text(
                text = website,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.DarkGray
            )
        }
        CopyButton(website)
    }
}

@Composable
fun EntryPasswordRow(modifier: Modifier = Modifier, password: String) {
    Row(modifier = modifier) {
        PasswordText(visible = false, text = password, modifier = Modifier.weight(1f))
        CopyButton(password)
    }
}

@Preview
@Composable
fun EntryItemPreview() {
    val imageBitmap = ImageBitmap.imageResource(R.drawable.placeholder)
    EntryItem(
        password = "example1234",
        website = "www.example.com",
        name = "Example",
        id = 0,
        icon = imageBitmap
    )
}

@Preview
@Composable
fun EntryItemPreviewLargeText() {
    val imageBitmap = ImageBitmap.imageResource(R.drawable.placeholder)
    EntryItem(
        password = "examplskjnvkfbdsgjvckxvjkfcnlmzzxe1234",
        website = "www.exsajdfbskndnfsnadULKASFGDFHSSFGSGAGDCGAUOample.com",
        name = "ExaWIAUEHTFDJRYUASOFIDSFHAUSGUFGUYGDUmple",
        id = 0,
        icon = imageBitmap
    )
}

fun copyToClipBoard(text: String, context: Context) {
    val clipboard: ClipboardManager = getSystemService(context, ClipboardManager::class.java) as ClipboardManager
    val clip = ClipData.newPlainText(null, text);
    clipboard.setPrimaryClip(clip);
    Toast.makeText(context, context.resources.getString(R.string.notif_text_copied), Toast.LENGTH_SHORT).show()
}

@Composable
fun CopyButton(text: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    IconButton(onClick = { copyToClipBoard(text, context) }) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.baseline_content_copy_24),
            contentDescription = null
        )
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