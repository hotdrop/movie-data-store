package jp.hotdrop.mds.service

import jp.hotdrop.mds.exception.MdsException
import jp.hotdrop.mds.model.Movie
import jp.hotdrop.mds.repository.MovieRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * APIコントローラとRepositoryの橋渡しをするクラス
 * 主に引数（リクエストパラメータ）のチェックやRepositoryの結果をコントローラがレスポンスとして投げられる形に整形する。
 * ただし、DBのことやリクエスト、レスポンスの形ややり方は知る必要はない。
 */
@Service
class MovieService @Autowired constructor(
        val repository: MovieRepository
) {

    private val log = LoggerFactory.getLogger("jp.hotdrop.mds.trace")

    fun findById(id: String): Movie? {
        id.toLongOrNull() ?: throw MdsException(400, "ID is not Long data type! id=$id.")
        return repository.find(id)
    }

    fun findAll(): List<Movie>? {
        val movies = repository.findAll()
        movies?.let {
            log.info("  取得したデータ数: ${movies.size}.")
        }
        return movies
    }

    fun findByNowPlaying(): List<Movie>? {
        TODO("未実装")
    }

    fun save(movies: List<Movie>) {
        log.info("  保存するデータ数: ${movies.size}")
        movies.filter { it.title.isNotEmpty() }
                .forEach { repository.save(it) }
    }
}