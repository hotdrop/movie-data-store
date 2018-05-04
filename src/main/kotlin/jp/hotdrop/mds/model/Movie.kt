package jp.hotdrop.mds.model

data class Movie(
        val id: String = "",
        val title: String = "",
        val overview: String? = null,
        val imageUrl: String? = null,
        val playingDate: String? = null,
        val filmDirector: String? = null,
        val url: String? = null,
        val movieUrl: String? = null,
        val createdAt: String? = null
)