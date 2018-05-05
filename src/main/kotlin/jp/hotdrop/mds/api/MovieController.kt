package jp.hotdrop.mds.api

import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import jp.hotdrop.mds.model.Movie
import jp.hotdrop.mds.service.MovieService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/movies")
class MovieController @Autowired constructor(
        val service: MovieService
) {

    @ApiOperation(value = "Get Movies data", notes = "全ての映画情報を取得する。")
    @RequestMapping(method = [RequestMethod.GET])
    fun movies(): ResponseEntity<List<Movie>> {
        val movies = service.findAll() ?: return ResponseEntity(HttpStatus.NO_CONTENT)
        return ResponseEntity.ok(movies)
    }

    @ApiOperation(value = "Save Movies data", notes = "映画情報を保存する。")
    @RequestMapping(method = [(RequestMethod.POST)])
    fun movies(@ApiParam(value = "movies data") @RequestBody movies: List<Movie>) {
        service.save(movies)
    }

    @ApiOperation(value = "Get Movies data by ID", notes = "IDが一致する映画情報を取得する。")
    @RequestMapping(method = [RequestMethod.GET], value = ["{id}"])
    fun moviesById(@ApiParam(value = "movie id") @PathVariable("id") id: Long): ResponseEntity<Movie> {
        val movie = service.findById(id) ?: return ResponseEntity(HttpStatus.NO_CONTENT)
        return ResponseEntity.ok(movie)
    }

    @ApiOperation(value = "Get Now Playing Movies data", notes = "公開日から2ヶ月以内の映画情報を取得する。")
    @RequestMapping(value = ["/now-playing"], method = [RequestMethod.GET])
    fun nowPlaying(): ResponseEntity<List<Movie>> {
        val movies = service.findByNowPlaying() ?: return ResponseEntity(HttpStatus.NO_CONTENT)
        return ResponseEntity.ok(movies)
    }
}