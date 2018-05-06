package jp.hotdrop.mds.service

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

    fun findById(id: Long): Movie? {
        log.info("Start findById on MovieService. id=$id")
        return repository.find(id)
    }

    fun findAll(): List<Movie>? {
        log.info("Start findAll on MovieService")
        val movies = repository.findAll()
        movies?.let {
            log.info("  Number of acquired movies: ${movies.size}.")
        }
        return movies
    }

    fun findByNowPlaying(): List<Movie>? {
        log.info("Start findByNowPlaying on MovieService")
        val movies = repository.findNowPlaying()
        movies?.let {
            log.info("  Number of acquired movies: ${movies.size}.")
        }
        return movies
    }

    fun save(movies: List<Movie>) {
        log.info("Start save on MovieService")
        log.info("  Number of saved movies: ${movies.size}")
        movies.filter { validate(it) }
                .forEach { repository.store(it) }
        repository.save()
    }

    private fun validate(movie: Movie): Boolean {
        if (movie.title.isEmpty()) {
            log.info("  Title is empty. skip save on store.")
            return false
        }
        // TODO playingDateEpoch - 規定のフォーマットになっているか yyyy/MM/dd
        return true
    }
}