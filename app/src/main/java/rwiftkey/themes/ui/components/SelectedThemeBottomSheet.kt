package rwiftkey.themes.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import rwiftkey.themes.R
import rwiftkey.themes.core.hasConnection
import rwiftkey.themes.ui.screen.home.PatchCollection
import rwiftkey.themes.ui.screen.home.ThemePatch

@Composable
fun SelectedThemeBottomSheet(
    title: String = stringResource(R.string.untitled),
    thumbnail: ImageBitmap? = null,
    isPatchMenuVisible: Boolean = false,
    patchCollection: MutableList<PatchCollection> = arrayListOf(),
    onClickLoadPatches: () -> Unit,
    onClickApplyPatch: (ThemePatch) -> Unit,
    onClickDeleteTheme: () -> Unit,
    onDismiss: () -> Unit,
) {
    val ctx = LocalContext.current
    CustomBottomSheet(
        title = title,
        onDismiss = { onDismiss() }
    ) {
        ThemeThumb(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            thumbnail = thumbnail
        )
        Spacer(modifier = Modifier.padding(4.dp))
        if (hasConnection(ctx)) {
            SimpleListButton(
                icon = Icons.Outlined.Extension,
                text = stringResource(R.string.patch_theme),
                onClick = { onClickLoadPatches() }
            )
            if (isPatchMenuVisible) {
                PatchMenu(
                    patchCollection = patchCollection,
                    onClickApply = { onClickApplyPatch(it) }
                )
            }
            BottomSheetDivisor()
        }
        SimpleListButton(
            icon = Icons.Outlined.Delete,
            text = stringResource(R.string.remove_theme),
            onClick = { onClickDeleteTheme() }
        )
        Spacer(modifier = Modifier.padding(4.dp))
    }
}