package rwiftkey.themes.model

import android.graphics.Bitmap
import android.os.Parcelable
import com.beust.klaxon.Json
import kotlinx.parcelize.Parcelize

data class Themes(
    val themes: List<Theme>
)

@Parcelize
data class Theme(
    val id: String = "",
    val name: String = "",
    val formatVersion: Int = 0,
    val minorVersion: Int = 0,
    val hidden: Boolean = false,
    val creationTimestamp: Long = 0,

    @Json(ignored = true)
    var thumbnail: Bitmap? = null
) : Parcelable