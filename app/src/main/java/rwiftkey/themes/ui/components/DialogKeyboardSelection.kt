package rwiftkey.themes.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import rwiftkey.themes.R
import rwiftkey.themes.model.SimpleApplication

@Composable
fun DialogKeyboardSelection(
    availKeyboards: List<SimpleApplication>,
    onDismissRequest: () -> Unit,
    onClick: (SimpleApplication) -> Unit,
) {
    AlertDialog(
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(id = R.string.select_keyboard)) },
        confirmButton = {
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
        }
    )
}