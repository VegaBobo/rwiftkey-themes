package com.rswiftkey.ui.screen.about

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.util.withContext
import com.rswiftkey.R
import com.rswiftkey.ui.components.PreferenceItem

@Composable
fun LibrariesDialog(
    onToggleDialog: () -> Unit
) {
    val libs = remember { mutableStateOf<Libs?>(null) }
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    libs.value = Libs.Builder().withContext(context).build()
    val libraries = libs.value!!.libraries
    AlertDialog(
        onDismissRequest = { onToggleDialog() },
        confirmButton = {
            TextButton(onClick = { onToggleDialog() }) {
                Text(text = stringResource(id = R.string.close))
            }
        },
        text = {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(libraries.size) {
                    val thisLibrary = libraries[it]
                    val name = thisLibrary.name
                    var licenses = ""
                    for (license in thisLibrary.licenses) {
                        licenses += license.name
                    }
                    val urlToOpen = thisLibrary.website ?: ""
                    PreferenceItem(
                        title = name,
                        description = licenses,
                        onClick = {
                            if (urlToOpen.isNotEmpty()) {
                                uriHandler.openUri(urlToOpen)
                            }
                        },
                    )
                }
            }
        }
    )
}