package rwiftkey.themes.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// Control server-side features, see Constants to get remote url
@Parcelize
data class FeatureFlags(
    val enable_addons_feature: Boolean = false
) : Parcelable
