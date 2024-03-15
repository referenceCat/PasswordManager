package referenceCat.passwordmanager.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Typeface
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat


val passwordTextStyle = TextStyle(
    fontSize = 16.sp,
    color = Color.Black,
    fontWeight = FontWeight.Normal,
    textAlign = TextAlign.Start,
    letterSpacing = 1.sp,
    fontFamily = FontFamily(Typeface.MONOSPACE) //Here's the magic!
)

const val passwordMaskChar = '*'

@Composable
fun PasswordTextField(visible: Boolean, modifier: Modifier = Modifier, label: String, editable: Boolean = true, value: String = "", onChange: (String) -> Unit = {}, invalidData:Boolean = false) {
    // var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(visible) }

    TextField(
        value = value,
        onValueChange = { onChange(it)},
        label = { Text(label) },
        singleLine = true,
        // placeholder = { Text(label) },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(passwordMaskChar),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val image = if (passwordVisible)
                ImageVector.vectorResource(id = R.drawable.baseline_visibility_off_24)
            else ImageVector.vectorResource(id = R.drawable.baseline_visibility_24)

            // Please provide localized description for accessibility services
            val description = if (passwordVisible) "Hide password" else "Show password"

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, description)
            }
        },
        modifier = modifier,
        enabled = editable,
        textStyle = passwordTextStyle
    )
}

@Preview
@Composable
fun PreviewPasswordTextField() {
    PasswordTextField(visible = false, label = stringResource(id = R.string.defaultPassword))
}

@Composable
fun PasswordText(visible: Boolean, modifier: Modifier = Modifier, text:String) {
    var passwordVisible by rememberSaveable { mutableStateOf(visible) }
    val image = if (passwordVisible)
        ImageVector.vectorResource(id = R.drawable.baseline_visibility_off_24)
    else ImageVector.vectorResource(id = R.drawable.baseline_visibility_24)

    // Please provide localized description for accessibility services
    val description = if (passwordVisible) "Hide password" else "Show password"

    Row(modifier = modifier) {
        Text(
            text = if (passwordVisible) text else passwordMaskChar.toString().repeat(text.length),
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f, true),
            style = passwordTextStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        IconButton(onClick = { passwordVisible = !passwordVisible },
            Modifier
                .align(Alignment.CenterVertically)
                .wrapContentWidth(Alignment.End)) {
            Icon(imageVector = image, description)
        }
    }
}

@Preview
@Composable
fun PreviewPasswordText() {
    PasswordText(visible = false, text = "default1234ksjnnkjsnfsoknfiuhfdersryss", )
}

fun copyToClipBoard(text: String, context: Context) {
    val clipboard: ClipboardManager =
        ContextCompat.getSystemService(context, ClipboardManager::class.java) as ClipboardManager
    val clip = ClipData.newPlainText(null, text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(
        context,
        context.resources.getString(R.string.notif_text_copied),
        Toast.LENGTH_SHORT
    ).show()
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
