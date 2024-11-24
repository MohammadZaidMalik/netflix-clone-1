package com.netflixclone.data_models

import com.netflixclone.db.entity.MovieEntity
import com.squareup.moshi.Json

data class Movie(
    @Json(name = "id") override val id: Int,
    @Json(name = "git_uid") override val gitUid: String?,
    @Json(name = "title") override val title: String,
    @Json(name = "poster_path") override val posterPath: String?,
    @Json(name = "backdrop_path") override val backdropPath: String?,
    @Json(name = "overview") override val overview: String,
    @Json(name = "release_date") override val releaseDate: String?,
    @Json(name = "vote_average") override val voteAverage: Double?,
    @Json(name = "genre_ids") override val genreIds: List<Int>?,
    @Json(name = "streaming_link") override val streamingLink: String?,
) : IMovie

fun Movie.toMediaMovie() =
    Media.Movie(id,title, posterPath, backdropPath, overview, releaseDate, voteAverage, genreIds,gitUid,streamingLink)

//fun Movie.toMovieEntity() {
//    if(gitUid == null)
//        gitUid = ""
//    return MovieEntity(id,gitUid, title, posterPath, backdropPath, overview, releaseDate, voteAverage, genreIds,streamingLink)
//}

