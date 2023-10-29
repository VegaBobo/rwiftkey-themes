package rwiftkey.themes.core

import rwiftkey.themes.BuildConfig

object Constants {
    const val REMOTE_SERVICE_INTENT = "${BuildConfig.APPLICATION_ID}.REMOTE_SERVICE"
    const val PREFERENCES = "preferences"
    const val BIND_SERVICE_RETRY_DELAY_MS = 1000L
    const val BIND_SERVICE_TIMEOUT_MS = 20000L
}
