package com.rswiftkey

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.rswiftkey.ui.theme.SapoTheme
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    companion object {
        init {
            // Set settings before the main shell can be created
            //Shell.enableVerboseLogging = BuildConfig.DEBUG
            Shell.setDefaultBuilder(
                Shell.Builder.create()
                    .setFlags(Shell.FLAG_MOUNT_MASTER)
                    .setTimeout(10)
            )
        }
    }

    private var loadingBarVisible: MutableState<Boolean> = mutableStateOf(false)
    private val showErrorDialog = mutableStateOf(false)
    private val isRooted = mutableStateOf(true)

    private fun applicationCheck() {
        lifecycleScope.launch {
            val installedKeyboards = Util.obtainInstalledKeyboard(applicationContext)
            if (installedKeyboards.size == 1) {
                Data.setTargetKeyboard(applicationContext, installedKeyboards[0])
            } else if (installedKeyboards.size > 1) {
                val kb = Data.readTargetKeyboard(applicationContext)
                if (kb.applicationName == Data.UNKNOWN || kb.packageName == Data.UNKNOWN)
                    Data.setTargetKeyboard(applicationContext, installedKeyboards[0])
            } else {
                showErrorDialog.value = true
            }
        }
    }

    @Composable
    fun Dialog() {
        AlertDialog(
            modifier = Modifier.fillMaxWidth(),
            onDismissRequest = { finish() },
            title = { Text(text = getString(R.string.dialog_no_available_keyboard_title)) },
            text = { Text(text = getString(R.string.dialog_no_available_keyboard_desc)) },
            confirmButton = {
                Text(
                    text = getString(R.string.dialog_close),
                    Modifier.clickable { finish() })
            }
        )
    }

    @Composable
    fun NoRootDialog() {
        AlertDialog(
            modifier = Modifier.fillMaxWidth(),
            onDismissRequest = { finish() },
            title = { Text(text = getString(R.string.dialog_no_root_title)) },
            text = { Text(text = getString(R.string.dialog_no_root_desc)) },
            confirmButton = {
                Text(
                    text = getString(R.string.dialog_close),
                    Modifier.clickable { finish() })
            }
        )
    }

    private fun installTheme(uri: Uri) {
        loadingBarVisible.value = true
        lifecycleScope.launch {
            val application = Data.readTargetKeyboard(applicationContext)
            launch(Dispatchers.IO) {
                try {
                    ThemesOp(
                        applicationContext,
                        uri,
                        application.packageName
                    ).install()
                    runOnUiThread {
                        Toast.makeText(
                            this@MainActivity,
                            getString(R.string.theme_installed),
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                } catch (e: Exception) {
                    if (BuildConfig.DEBUG)
                        Log.e("installTheme", e.stackTraceToString())
                    runOnUiThread {
                        Toast.makeText(
                            this@MainActivity,
                            getString(R.string.error_theme),
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }
                loadingBarVisible.value = false
            }
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Shell.getShell {
            val p = Shell.cmd("whoami").exec()
            if (!p.out.toString().contains("root"))
                isRooted.value = false
        }
        var fileSelection: ActivityResultLauncher<Intent>? = null
        setContent {
            SapoTheme {
                window.navigationBarColor = MaterialTheme.colorScheme.background.toArgb()
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(topBar = { TopContent() },
                        content = { MainContent() },
                        bottomBar = { BottomContent(fileSelection!!) })
                    Util.obtainInstalledKeyboard(this)

                    if (showErrorDialog.value)
                        Dialog()
                    if (!isRooted.value)
                        NoRootDialog()

                }
            }
        }

        applicationCheck()

        fileSelection =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val uri = result.data!!.data
                    installTheme(uri!!)
                }
            }

        val data: Uri? = intent?.data
        if (data != null) {
            val themeUri = Util.copyFile(
                applicationContext,
                data,
                applicationContext.cacheDir.path + "/theme/" + "theme_tmp.zip"
            )
            installTheme(themeUri)
        }

    }

    @Preview(showBackground = true)
    @Composable
    fun TopContent() {
        LargeTopAppBar(
            title = { Text(text = getString(R.string.app_name), fontSize = 38.sp) },
            actions = {
                IconButton(onClick = {
                    startActivity(Intent(this@MainActivity, PreferencesActivity::class.java))
                }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.settings),
                        contentDescription = "Settings icon",
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
        )
    }

    @Composable
    fun MainContent() {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.onSecondary,
                        shape = RoundedCornerShape(36.dp)
                    )
                    .clickable {
                        if (!loadingBarVisible.value)
                            lifecycleScope.launch {
                                val application = Data.readTargetKeyboard(applicationContext)
                                Util.startSKActivity(application.packageName)
                            }
                    },
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(50.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.palette),
                        contentDescription = "Palette icon",
                        modifier = Modifier.size(96.dp)
                    )
                    Text(text = getString(R.string.open_theme_section))
                }
            }
            if (loadingBarVisible.value) {
                CircularProgressIndicator(modifier = Modifier.padding(20.dp))
            }
        }
    }

    @Composable
    fun BottomContent(arl: ActivityResultLauncher<Intent>) {
        Box(modifier = Modifier.fillMaxSize()) {
            FloatingActionButton(
                onClick = {
                    var chooseFile = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    chooseFile.type = "*/*"
                    val mimetypes = arrayOf("application/zip", "application/octet-stream")
                    chooseFile.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
                    chooseFile = Intent.createChooser(chooseFile, getString(R.string.select_theme))
                    arl.launch(chooseFile)
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(20.dp),
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.add),
                    contentDescription = "Add icon",
                    modifier = Modifier
                        .size(94.dp)
                        .padding(30.dp)
                )
            }
        }
    }

}