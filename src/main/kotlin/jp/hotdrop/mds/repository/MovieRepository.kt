package jp.hotdrop.mds.repository

import jp.hotdrop.mds.model.Movie
import jp.hotdrop.mds.repository.entity.MovieEntity
import jp.hotdrop.mds.repository.ex.toDateStr
import jp.hotdrop.mds.repository.ex.toEpoch
import jp.hotdrop.mds.repository.redis.RedisClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import redis.clients.jedis.SortingParams

@Repository
class MovieRepository @Autowired constructor(
        val dbClient: RedisClient
) {

    private val log = LoggerFactory.getLogger("jp.hotdrop.mds.trace")
    private final val INDEX_KEY_FOR_SORT = "indices"

    /**
     * idでソートしたいのでsaddで別途idを保持する
     */
    fun save(movie: Movie) {
        dbClient.jedis.run {
            val entity = movie.toEntity()
            entity.id = dbClient.createMovieId().toString()
            log.debug("  保存する映画情報: id=${movie.id} title=${movie.title}")
            this.sadd(INDEX_KEY_FOR_SORT, entity.id)
            this.hmset(entity.id, entity.toHashMap())
        }
    }

    /**
     * idを指定してデータを取得する
     */
    fun find(id: String): Movie? =
            dbClient.jedis.run {
                val results = this.hmget(id, *takeParams)
                if (results.size != MovieEntity.FIELD_NUM) {
                    log.debug("  MovieID=$id の取得データ数がおかしいです。正常なMovieはデータ数${MovieEntity.FIELD_NUM}に対し、${results.size}となっています。")
                    return null
                }
                MovieEntity(id = results[0],
                        title = results[1],
                        overview = results[2],
                        imageUrl = results[3],
                        playingDateEpoch = results[4],
                        filmDirector = results[5],
                        url = results[6],
                        movieUrl = results[7],
                        createdAtEpoch = results[8]).toMovie()
            }

    /**
     * 映画情報を全て取得する
     */
    fun findAll(): List<Movie>? {

        val sortingParams = SortingParams().asc().get(*takeParams).alpha()
        val results = dbClient.jedis.sort(INDEX_KEY_FOR_SORT, sortingParams)

        val dataCount = results.size / MovieEntity.FIELD_NUM
        log.info("  取得した全映画情報のデータ数: $dataCount")

        val movies = mutableListOf<MovieEntity>()

        return (0 until dataCount)
                .map { it * MovieEntity.FIELD_NUM }
                .mapTo(movies) {
                    MovieEntity(
                            id = results[0 + it],
                            title = results[1 + it],
                            overview = results[2 + it],
                            imageUrl = results[3 + it],
                            playingDateEpoch = results[4 + it],
                            filmDirector = results[5 + it],
                            url = results[6 + it],
                            movieUrl = results[7 + it],
                            createdAtEpoch = results[8 + it]) }
                .map { it.toMovie() }
    }

    fun findNowPlaying(): List<Movie>? {
        TODO("")
    }

    private fun Movie.toEntity(): MovieEntity {
        val playingDateEpoch = this.playingDate?.toEpoch() ?: ""
        val createAtEpoch = this.createdAt?.toEpoch() ?: ""
        return MovieEntity(
                    id = this.id,
                    title = this.title,
                    overview = this.overview,
                    imageUrl = this.imageUrl,
                    playingDateEpoch = playingDateEpoch,
                    filmDirector = this.filmDirector,
                    url = this.url,
                    movieUrl = this.movieUrl,
                    createdAtEpoch = createAtEpoch)
    }

    private fun MovieEntity.toMovie(): Movie {
        val playingDate = this.playingDateEpoch?.toDateStr() ?: ""
        val createAt = this.createdAtEpoch?.toDateStr() ?: ""
        return Movie(
                id = this.id,
                title = this.title,
                overview = this.overview,
                imageUrl = this.imageUrl,
                playingDate = playingDate,
                filmDirector = this.filmDirector,
                url = this.url,
                movieUrl = this.movieUrl,
                createdAt = createAt)
    }

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