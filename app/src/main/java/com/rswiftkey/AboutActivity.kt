package com.rswiftkey

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.rswiftkey.ui.theme.SapoTheme


class AboutActivity : ComponentActivity() {

    private val showLibraryDialog = mutableStateOf(false)
    private val showEasterEgg = mutableStateOf(0)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SapoTheme {
                window.navigationBarColor = MaterialTheme.colorScheme.background.toArgb()
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = {
                            LargeTopAppBar(
                                title = {
                                    Text(
                                        text = getString(R.string.about),
                                        fontSize = 38.sp
                                    )
                                }
                            )
                        },
                        content = { innerPadding ->
                            Column(
                                Modifier
                                    .padding(innerPadding)
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState()),
                            ) {
                                LibrariesDialog()
                                Title(title = getString(R.string.application))
                                Preference(
                                    title = getString(R.string.repository),
                                    desc = getString(R.string.view_source),
                                    icon = ImageVector.vectorResource(id = R.drawable.code)
                                ) { openUrl("https://github.com/VegaBobo/rwiftkey-themes") }
                                Preference(
                                    title = getString(R.string.libraries),
                                    desc = getString(R.string.used_libs),
                                    icon = ImageVector.vectorResource(id = R.drawable.code)
                                ) { showLibraryDialog.value = true }
                                Title(title = getString(R.string.authors))
                                Preference(
                                    title = "RKBDI",
                                    desc = getString(R.string.view_on_twitter),
                                    icon = ImageVector.vectorResource(id = R.drawable.accountcircle)
                                )
                                { openUrl("https://twitter.com/RKBDI") }
                                Preference(
                                    title = "VegaBobo",
                                    desc = getString(R.string.view_on_github),
                                    icon = ImageVector.vectorResource(id = R.drawable.accountcircle)
                                )
                                { openUrl("https://github.com/VegaBobo") }
                            }
                        },
                        bottomBar = {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                if (showEasterEgg.value > 8) {
                                    AsyncImage(
                                        model = getDrawable(R.drawable.easter_egg),
                                        contentDescription = null
                                    )
                                }
                                Text(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .clickable(
                                            interactionSource = MutableInteractionSource(),
                                            indication = null
                                        ) {
                                            if (showEasterEgg.value <= 8)
                                                showEasterEgg.value = showEasterEgg.value + 1
                                        },
                                    text = "${getString(R.string.app_name)} ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    )
                }
            }
        }
    }

    private fun openUrl(url: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

    @Composable
    fun Title(title: String) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .padding(start = 16.dp)
                .padding(bottom = 8.dp)
                .padding(top = 8.dp)
                .padding(4.dp),
        )
    }

    @Composable
    fun Preference(title: String, desc: String, icon: ImageVector?, runnable: Runnable) {
        Box(
            Modifier
                .fillMaxWidth()
                .clickable { runnable.run() }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .requiredHeight(74.dp)
                    .padding(start = 8.dp)
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .padding(10.dp)
                            .size(26.dp),
                    )
                }
                Column(Modifier.padding()) {
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(bottom = 2.dp),
                        color = MaterialTheme.colorScheme.inverseSurface
                    )
                    Text(
                        text = desc,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    @Composable
    fun LibrariesDialog() {
        if (showLibraryDialog.value) {
            AlertDialog(
                modifier = Modifier.fillMaxWidth(),
                onDismissRequest = {
                    showLibraryDialog.value = false
                },
                title = {
                    Text(text = getString(R.string.libraries_list))
                },
                text = {
                    Text(
                        text = "" +
                                "com.github.topjohnwu.libsu:core\n" +
                                "com.github.topjohnwu.libsu:io\n" +
                                "com.beust:klaxon\n" +
                                "io.coil-kt:coil\n" +
                                "io.coil-kt:coil-gif\n" +
                                "io.coil-kt:coil-compose\n" +
                                "androidx.compose.ui:ui\n" +
                                "androidx.compose.ui:ui-tooling-preview\n" +
                                "androidx.compose.material:material-icons-extended\n" +
                                "androidx.core:core-ktx\n" +
                                "androidx.lifecycle:lifecycle-runtime-ktx\n" +
                                "androidx.activity:activity-compose\n" +
                                "androidx.compose.material3:material3\n" +
                                "androidx.documentfile:documentfile\n" +
                                "androidx.preference:preference-ktx\n" +
                                "androidx.datastore:datastore-preferences\n" +
                                "androidx.core:core-splashscreen" +
                                "\n\n" +
                                "** ${getString(R.string.library_info)}"
                    )
                },
                confirmButton = {
                    Text(
                        text = getString(R.string.close),
                        modifier = Modifier.clickable { showLibraryDialog.value = false })
                }
            )
        }
    }

    @Composable
    fun SimplePreference(title: String, icon: ImageVector?, runnable: Runnable) {
        Box(
            Modifier
                .fillMaxWidth()
                .clickable { runnable.run() }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .requiredHeight(46.dp)
                    .padding(start = 8.dp)
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .padding(10.dp)
                            .size(26.dp),
                    )
                }
                Column(Modifier.padding()) {
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(bottom = 2.dp),
                        color = MaterialTheme.colorScheme.inverseSurface
                    )
                }
            }
        }
    }

}