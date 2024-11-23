package com.netflixclone.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.netflixclone.data_models.IMovie
import com.netflixclone.data_models.Media
import com.netflixclone.data_models.Movie
import com.squareup.moshi.Json


@Entity
data class MovieEntity(
    @PrimaryKey override val id: Int,
    @ColumnInfo(name = "title") override val title: String,
    @ColumnInfo(name = "poster_path") override val posterPath: String?,
    @ColumnInfo(name = "backdrop_path") override val backdropPath: String?,
    @ColumnInfo(name = "overview") override val overview: String,
    @ColumnInfo(name = "release_date") override val releaseDate: String?,
    @ColumnInfo(name = "vote_average") override val voteAverage: Double,
    @ColumnInfo(name = "genre_ids") override val genreIds: List<Int>,
    @ColumnInfo(name = "streaming_link") override val streamingLink: String,
) : IMovie

fun MovieEntity.toMovie() =
    Movie(id, title, posterPath, backdropPath, overview, releaseDate, voteAverage, genreIds,streamingLink)
