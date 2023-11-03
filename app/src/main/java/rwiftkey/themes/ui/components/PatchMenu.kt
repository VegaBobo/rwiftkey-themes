package rwiftkey.themes.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import rwiftkey.themes.R
import rwiftkey.themes.ui.screen.home.PatchCollection
import rwiftkey.themes.ui.screen.home.ThemePatch

@Composable
fun PatchMenu(
    modifier: Modifier = Modifier,
    patchCollection: MutableList<PatchCollection> = mutableStateListOf(),
    onClickApply: (ThemePatch) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(0.dp, 200.dp)
            .padding(start = 46.dp)
    ) {
        LazyColumn {
            if (patchCollection.isNotEmpty())
                items(patchCollection.size) {
                    val thisPatchCollection = patchCollection[it]
                    Text(
                        text = thisPatchCollection.title,
                        modifier = Modifier.fillMaxWidth()
                    )
                    BottomSheetDivisor()
                    for (item in thisPatchCollection.patches) {
                        Column(modifier = Modifier.clickable {
                            onClickApply(item)
                        }) {
                            Text(text = item.title, modifier = Modifier.fillMaxWidth())
                            AsyncImage(
                                model = item.thumbnail,
                                contentDescription = "thumbnail"
                            )
                        }
                    }
                    Spacer(modifier = Modifier.padding(8.dp))
                }
            else
                item { Text(text = stringResource(id = R.string.no_patches_avail)) }
        }
    }
}