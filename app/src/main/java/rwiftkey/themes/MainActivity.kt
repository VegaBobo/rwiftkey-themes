package rwiftkey.themes

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ipc.RootService
import dagger.hilt.android.AndroidEntryPoint
import rwiftkey.themes.remoteservice.RemoteServiceProvider
import rwiftkey.themes.rootservice.PrivilegedProvider
import rwiftkey.themes.rootservice.PrivilegedService
import rwiftkey.themes.ui.screen.RwiftkeyApp
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    var remoteService: IRemoteService? = null
        @Inject set

    companion object {
        init {
            Shell.setDefaultBuilder(
                Shell.Builder.create()
                    .setFlags(Shell.FLAG_MOUNT_MASTER)
                    .setTimeout(10)
            )
        }
    }

    private fun bindRootService() {
        val intent = Intent(this, PrivilegedService::class.java)
        RootService.bind(intent, PrivilegedProvider.connection)
        Log.d(BuildConfig.APPLICATION_ID, "bindRootService()")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        Shell.getShell {}
        setContent { RwiftkeyApp() }
        if (Shell.getShell().isRoot)
            bindRootService()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (!isFinishing)
            return

        if (Shell.getShell().isRoot) {
            RootService.unbind(PrivilegedProvider.connection)
            return
        }

        if (RemoteServiceProvider.isRemoteLikelyConnected) {
            RemoteServiceProvider.isRemoteLikelyConnected = false
            RemoteServiceProvider.run {
                requestUnbind()
                unbindService(RemoteServiceProvider.connection)
            }
            return
        }
    }

}
