package rwiftkey.themes.ui.screen.home

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import rwiftkey.themes.R

@Composable
fun NoKeyboardsAvailDialog(
    onClickClose: () -> Unit
) {
    AlertDialog(
        title = {
            Text(text = stringResource(id = R.string.dialog_no_available_keyboard_title))
        },
        text = {
            Text(text = stringResource(id = R.string.dialog_no_available_keyboard_desc))
        },
        icon = {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.no_keyboard),
                contentDescription = null
            )
        },
        onDismissRequest = { },
        confirmButton = {
            TextButton(onClick = { onClickClose() }) {
                Text(text = stringResource(id = R.string.close))
            }
        },
    )
}