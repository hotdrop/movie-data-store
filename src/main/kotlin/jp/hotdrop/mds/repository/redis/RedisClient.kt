package jp.hotdrop.mds.repository.redis

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import redis.clients.jedis.Jedis

@Repository
class RedisClient @Autowired constructor(
        val redisProperties: RedisProperties
){

    private enum class Database(val selectNo: Int) { Movie(0), Key(1) }
    val jedis by lazy { create(Database.Movie) }
    // キーDBは外に出さない
    private val jedisToKey by lazy { create(Database.Key) }

    private fun create(db: Database): Jedis {
        val host = redisProperties.host ?: throw NullPointerException("not be read spring.redis.host in application.yml.")
        val port = redisProperties.port ?: throw NullPointerException("not be read spring.redis.port in application.yml.")
        return Jedis(host, port).also { it.select(db.selectNo) }
    }

    fun createMovieId(): Long {
        if (!jedisToKey.exists(MOVIE_ID)) {
            jedisToKey.set(MOVIE_ID, MOVIE_ID_FIRST_VALUE)
        }
        return jedisToKey.incr(MOVIE_ID)
    }

    companion object {
        private const val MOVIE_ID = "movieId"
        private const val MOVIE_ID_FIRST_VALUE = "0"
    }
}