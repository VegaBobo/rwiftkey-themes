package rwiftkey.themes.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import rwiftkey.themes.ui.screen.home.PatchCollection
import rwiftkey.themes.ui.screen.home.ThemePatch

@Composable
fun ThemeDetailsMenuSheet(
    patchCollection: MutableList<PatchCollection> = arrayListOf(),
    onClickApplyPatch: (ThemePatch) -> Unit,
    onClickDeleteTheme: () -> Unit
) {
    PatchMenu(
        patchCollection = patchCollection,
        onClickApply = { onClickApplyPatch(it) }
    )
    BottomSheetDivisor()
    SimpleListButton(
        icon = Icons.Outlined.Delete,
        text = "Remove theme",
        onClick = { onClickDeleteTheme() }
    )
    Spacer(modifier = Modifier.padding(4.dp))
}