package rwiftkey.themes.model

data class Themes(
    val themes: List<Theme>
)

data class Theme(
    val id: String = "",
    val name: String = "",
    val formatVersion: Int = 0,
    val minorVersion: Int = 0,
    val hidden: Boolean = false,
    val creationTimestamp: Long = 0
)