package jp.hotdrop.mds.model

data class Movie(
        var id: String = "",
        val title: String = "",
        val overview: String? = null,
        val imageUrl: String? = null,
        val playingDate: String? = null,
        val filmDirector: String? = null,
        val url: String? = null,
        val movieUrl: String? = null,
        val createdAt: String? = null
) {
    companion object {
        val FIELD_NUM = 8
    }
}