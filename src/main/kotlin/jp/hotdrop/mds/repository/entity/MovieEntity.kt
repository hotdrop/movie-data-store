package jp.hotdrop.mds.repository.entity

/**
 * entityはRepositoryだけで使用する
 */
data class MovieEntity(
        var id: String = "",
        val title: String = "",
        val overview: String? = null,
        val imageUrl: String? = null,
        val playingDateEpoch: String? = null,
        val filmDirector: String? = null,
        val url: String? = null,
        val movieUrl: String? = null,
        var createdAtEpoch: String? = null
) {

    // idはKeyとして保存するためidを除いた項目をHashMapに変換する
    fun toHashMap(): HashMap<String, String?> = hashMapOf(
            "title" to this.title,
            "overview" to this.overview,
            "imageUrl" to this.imageUrl,
            "playingDateEpoch" to this.playingDateEpoch,
            "filmDirector" to this.filmDirector,
            "url" to this.url,
            "movieUrl" to this.movieUrl,
            "createdAtEpoch" to this.createdAtEpoch
    )

    companion object {
        const val FIELD_NUM: Int = 9

        val TakeParams =
                arrayOf("title",
                        "overview",
                        "imageUrl",
                        "playingDate",
                        "filmDirector",
                        "url",
                        "movieUrl",
                        "createdAt")

        val TakeParamsForSort =
                arrayOf("#",
                        "*->title",
                        "*->overview",
                        "*->imageUrl",
                        "*->playingDate",
                        "*->filmDirector",
                        "*->url",
                        "*->movieUrl",
                        "*->createdAt")
    }
}