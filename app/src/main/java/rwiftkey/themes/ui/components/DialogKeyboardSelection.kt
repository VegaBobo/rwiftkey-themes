package rwiftkey.themes.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import rwiftkey.themes.R
import rwiftkey.themes.model.SimpleApplication

@Composable
fun DialogKeyboardSelection(
    modifier: Modifier = Modifier,
    availKeyboards: List<SimpleApplication>,
    onDismissRequest: () -> Unit,
    onClick: (SimpleApplication) -> Unit
) {
    AlertDialog(
        modifier = modifier
            .fillMaxWidth(),
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(id = R.string.select_keyboard)) },
        text = {
            Column {
                for (t in availKeyboards) {
                    PreferenceItem(
                        title = t.applicationName,
                        description = t.packageName,
                        icon = null,
                        onClick = { onClick(t) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text(text = stringResource(id = R.string.close))
            }
        }
    )
}
