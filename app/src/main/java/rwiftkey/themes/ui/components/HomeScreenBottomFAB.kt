package rwiftkey.themes.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import rwiftkey.themes.R
import rwiftkey.themes.ui.util.launchAcResult

@Composable
fun HomeScreenBottomFAB(
    modifier: Modifier = Modifier,
    onFileSelected: (Uri) -> Unit
) {
    val ctx = LocalContext.current
    val launcherSelectFile = launchAcResult {
        onFileSelected(it.data!!.data!!)
    }

    Box(modifier = modifier) {
        RwiftkeyMainFAB(
            modifier = Modifier.padding(bottom = 16.dp),
            onClick = {
                var chooseFile = Intent(Intent.ACTION_OPEN_DOCUMENT)
                chooseFile.type = "*/*"
                val mimetypes =
                    arrayOf(
                        "application/zip",
                        "application/octet-stream"
                    )
                chooseFile.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
                chooseFile = Intent.createChooser(
                    chooseFile,
                    ctx.getString(R.string.select_theme)
                )
                launcherSelectFile.launch(chooseFile)
            }
        )
    }
}