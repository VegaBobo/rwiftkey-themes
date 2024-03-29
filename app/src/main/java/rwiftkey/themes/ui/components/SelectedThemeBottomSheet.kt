package rwiftkey.themes.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import rwiftkey.themes.R
import rwiftkey.themes.ui.screen.home.PatchCollection
import rwiftkey.themes.ui.screen.home.ThemePatch

@Composable
fun SelectedThemeBottomSheet(
    title: String = stringResource(R.string.untitled),
    thumbnail: ImageBitmap? = null,
    isPatchMenuVisible: Boolean = false,
    isPatchesVisible: Boolean = false,
    patchCollection: MutableList<PatchCollection> = arrayListOf(),
    onClickLoadPatches: () -> Unit,
    onClickApplyPatch: (ThemePatch) -> Unit,
    onClickDeleteTheme: () -> Unit,
    onDismiss: () -> Unit,
) {
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
        if (isPatchMenuVisible) {
            SimpleListButton(
                icon = ImageVector.vectorResource(id = R.drawable.extension),
                text = stringResource(R.string.patch_theme),
                onClick = { onClickLoadPatches() }
            )
            if (isPatchesVisible) {
                PatchMenu(
                    patchCollection = patchCollection,
                    onClickApply = { onClickApplyPatch(it) }
                )
            }
            BottomSheetDivisor()
        }
        SimpleListButton(
            icon =  ImageVector.vectorResource(id = R.drawable.delete),
            text = stringResource(R.string.remove_theme),
            onClick = { onClickDeleteTheme() }
        )
        Spacer(modifier = Modifier.padding(4.dp))
    }
}