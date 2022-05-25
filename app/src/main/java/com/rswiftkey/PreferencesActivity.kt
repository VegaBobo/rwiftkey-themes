package com.rswiftkey

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.rswiftkey.ui.theme.SapoTheme
import kotlinx.coroutines.launch

class PreferencesActivity : ComponentActivity() {

    private val openDialog = mutableStateOf(false)
    private val targetKeyboard = mutableStateOf("")

    private fun deleteThemes() {
        Toast.makeText(
            this@PreferencesActivity,
            getString(R.string.please_wait),
            Toast.LENGTH_SHORT
        ).show()
        lifecycleScope.launch {
            val app = Data.readTargetKeyboard(applicationContext)
            ThemesOp(
                this@PreferencesActivity,
                null,
                app.packageName
            ).clearThemes()
            Toast.makeText(
                this@PreferencesActivity,
                getString(R.string.cleaned_installed_themes),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

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
                                        text = getString(R.string.title_activity_preferences),
                                        fontSize = 38.sp
                                    )
                                },
                            )
                        },
                        content = { innerPadding ->
                            Column(
                                Modifier
                                    .padding(innerPadding)
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                //verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                DialogChooseKeyboard()
                                Preference(
                                    title = stringResource(id = R.string.target_keyboard),
                                    desc = targetKeyboard.value,
                                    icon = ImageVector.vectorResource(id = R.drawable.keyboard)
                                ) {
                                    openDialog.value = true
                                }
                                Preference(
                                    title = stringResource(id = R.string.clear_themes),
                                    desc = stringResource(id = R.string.clean_installed_themes),
                                    icon = ImageVector.vectorResource(id = R.drawable.delete)
                                ) {
                                    deleteThemes()
                                }
                                Preference(
                                    title = stringResource(id = R.string.about),
                                    desc = stringResource(id = R.string.about_app),
                                    icon = ImageVector.vectorResource(id = R.drawable.info)
                                ) {
                                    val aboutActivity =
                                        Intent(this@PreferencesActivity, AboutActivity::class.java)
                                    startActivity(aboutActivity)
                                }
                            }
                        }
                    )
                }
            }
        }
        lifecycleScope.launch {
            targetKeyboard.value = Data.readTargetKeyboard(applicationContext).applicationName
        }
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
    fun DialogChooseKeyboard() {
        if (openDialog.value) {
            AlertDialog(
                modifier = Modifier.fillMaxWidth(),
                onDismissRequest = {
                    openDialog.value = false
                },
                title = {
                    Text(text = getString(R.string.select_keyboard))
                },
                confirmButton = {
                    val possibleTargets = Util.obtainInstalledKeyboard(this)
                    Column {
                        for (t in possibleTargets) {
                            Preference(
                                title = t.applicationName,
                                desc = t.packageName,
                                icon = null,
                                runnable = {
                                    selectTargetKeyboard(baseContext, t)
                                    openDialog.value = false
                                })
                        }
                    }
                }
            )
        }
    }

    private fun selectTargetKeyboard(c: Context, ka: KeyboardApplication) {
        lifecycleScope.launch {
            Data.setTargetKeyboard(c, ka)
            targetKeyboard.value = Data.readTargetKeyboard(applicationContext).applicationName
        }
    }

}