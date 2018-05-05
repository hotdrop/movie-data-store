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

    /**
     * idでソートしたいのでsaddで別途idを保持する
     */
    fun save(movie: Movie) {

        val entity = movie.toEntity()
        entity.id = dbClient.createMovieId().toString()
        entity.createdAtEpoch = System.currentTimeMillis().toString()

        log.info("  保存する映画情報: id=${entity.id} title=${entity.title}")

        dbClient.jedis.sadd(INDEX_KEY_FOR_SORT, entity.id)
        dbClient.jedis.hmset(entity.id, entity.toHashMap())
        dbClient.jedis.bgsave()
    }

    /**
     * idを指定してデータを取得する
     */
    fun find(id: String): Movie? {
        val results = dbClient.jedis.hmget(id, *MovieEntity.TakeParams)

        if(results.size == 0) {
            return null
        }

        return resultToEntity(results, id).toMovie()
    }

    /**
     * 映画情報を全て取得する
     */
    fun findAll(): List<Movie>? =
            selectAllOrderById().map { it.toMovie() }

    /**
     * 公開日から2ヶ月以内の映画情報を取得する
     */
    fun findNowPlaying(): List<Movie>? =
            TODO("filterでplayingDateEpochをチェック")

    /**
     * 全データを登録順（IDソートの降順）で取得する
     * idを指定した1件検索以外はこれで全部取得してからfilterかけて条件に一致するデータを取得する。
     */
    private fun selectAllOrderById(): List<MovieEntity> {

        val sortingParams = SortingParams().asc().get(*MovieEntity.TakeParamsForSort).alpha()
        val results = dbClient.jedis.sort(INDEX_KEY_FOR_SORT, sortingParams)

        val dataCount = results.size / MovieEntity.FIELD_NUM
        log.info("  取得した全映画情報のデータ数: $dataCount")

        // Redisから取得したデータはレコード形式になっていないので配列インデックスで表す
        return (0 until dataCount).map { it * MovieEntity.FIELD_NUM }
                .map {recordIndex ->
                    // dataCountを計算しているのでIndexOutOfBoundになることはない
                    resultToEntityForSort(results, recordIndex)
                }
    }

    /**
     * Redisのhmsetで保存したデータを取得する際、レコード単位ではなくシリアライズにカラムをリスト形式で取得してしまう。
     * これだと扱いにくいので、リストをEntityにして取得する。
     * 引数で指定したindexからカラム数分を取得してEntityを生成する。
     * hmgetした場合とsortした場合でidの取り方が違うので、引数で指定可能にする
     */
    private fun resultToEntity(results: List<String?>, id: String): MovieEntity {

        // 項目はidを除いた数と一致するはず
        if (results.size != MovieEntity.FIELD_NUM - 1) {
            throw IllegalStateException("  MovieID=$id の取得データ数がおかしいです。正常なMovieはデータ数${MovieEntity.FIELD_NUM - 1}に対し、${results.size}となっています。")
        }

        if (results[0].isNullOrEmpty()) {
            throw IllegalStateException("Movie Title is null. ")
        }

        // ここにきたらidとtitleがnullになることはありえない
        return MovieEntity(id = id,
                title = results[0]!!,
                overview = results[1],
                imageUrl = results[2],
                playingDateEpoch = results[3],
                filmDirector = results[4],
                url = results[5],
                movieUrl = results[6],
                createdAtEpoch = results[7])
    }

    /**
     * Sort用のMovieEntity作成メソッド
     * 本当は通常のhmgetと同じにしたかったが、ちょっとずつ処理を変える必要があって
     * あまりに読みづらくなったので別にする。
     *
     */
    private fun resultToEntityForSort(results: List<String?>, index: Int): MovieEntity {

        if (results[index].isNullOrEmpty()) {
            throw IllegalStateException("Movie ID is null. ")
        }
        if (results[index + 1].isNullOrEmpty()) {
            throw IllegalStateException("Movie Title is null. ")
        }
        if (results.size < index + 8) {
            throw IllegalStateException("Index out of bounds. result size=${results.size} index=${index + 8}")
        }

        // ここにきたらidとtitleがnullになることはありえない
        return MovieEntity(id = results[index]!!,
                title = results[index + 1]!!,
                overview = results[index + 2],
                imageUrl = results[index + 3],
                playingDateEpoch = results[index + 4],
                filmDirector = results[index + 5],
                url = results[index + 6],
                movieUrl = results[index + 7],
                createdAtEpoch = results[index + 8])
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

    companion object {
        private const val INDEX_KEY_FOR_SORT = "indices"
    }
}