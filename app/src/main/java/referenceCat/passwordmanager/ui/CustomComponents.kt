package referenceCat.passwordmanager.ui

import android.graphics.Typeface
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp


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
fun PasswordTextField(visible: Boolean, modifier: Modifier = Modifier, label: String, editable: Boolean = true) {
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(visible) }

    TextField(
        value = password,
        onValueChange = { password = it },
        label = { Text(label) },
        singleLine = true,
        placeholder = { Text(label) },
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
            modifier = modifier.align(Alignment.CenterVertically),
            style = passwordTextStyle
        )
        IconButton(onClick = { passwordVisible = !passwordVisible }, modifier.align(Alignment.CenterVertically)) {
            Icon(imageVector = image, description)
        }
    }
}

@Preview
@Composable
fun PreviewPasswordText() {
    PasswordText(visible = false, text = stringResource(id = R.string.defaultPassword), )
}
