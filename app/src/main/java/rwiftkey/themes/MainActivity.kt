package rwiftkey.themes

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.topjohnwu.superuser.Shell
import dagger.hilt.android.AndroidEntryPoint
import rwiftkey.themes.ui.screen.RwiftkeyApp

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        init {
            Shell.setDefaultBuilder(
                Shell.Builder.create()
                    .setFlags(Shell.FLAG_MOUNT_MASTER)
                    .setTimeout(10)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        Shell.getShell {}
        setContent { RwiftkeyApp() }
    }
}