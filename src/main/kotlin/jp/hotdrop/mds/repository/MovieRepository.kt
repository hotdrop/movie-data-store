package jp.hotdrop.mds.repository

import jp.hotdrop.mds.model.Movie
import jp.hotdrop.mds.repository.redis.RedisClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class MovieRepository @Autowired constructor(
        val dbClient: RedisClient
) {

    private val log = LoggerFactory.getLogger("jp.hotdrop.mds.trace")

    fun save(movie: Movie) {
        dbClient.jedis.run {
            movie.id = dbClient.createMovieId().toString()
            log.debug("  保存する映画情報: id=${movie.id} title=${movie.title}")
            // idでソートしたいのでsaddで別途idを保持する
            this.sadd("indices", movie.id)
            this.hmset(movie.id, movie.toHashMap())
        }
    }

    fun find(id: String): Movie? =
            dbClient.jedis.run {
                val results = this.hmget(id, *takeParams)
                if (results.size != Movie.FIELD_NUM) {
                    log.debug("  MovieID=$id の取得データ数がおかしいです。正常なMovieはデータ数${Movie.FIELD_NUM}に対し、${results.size}となっています。")
                    return null
                }
                Movie(id = results[0],
                        title = results[1],
                        overview = results[2],
                        imageUrl = results[3],
                        playingDate = results[4],
                        filmDirector = results[5],
                        url = results[6],
                        movieUrl = results[7],
                        createdAt = results[8])
            }

    fun findAll(): List<Movie>? = TODO()

    // idはKeyとして保存するためidを除いた項目をHashMapに変換する
    private fun Movie.toHashMap(): HashMap<String, String?> = hashMapOf(
            "title" to this.title,
            "overview" to this.overview,
            "imageUrl" to this.imageUrl,
            "playingDate" to this.playingDate,
            "filmDirector" to this.filmDirector,
            "url" to this.url,
            "movieUrl" to this.movieUrl,
            "createdAt" to this.createdAt
    )

    private val takeParams =
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