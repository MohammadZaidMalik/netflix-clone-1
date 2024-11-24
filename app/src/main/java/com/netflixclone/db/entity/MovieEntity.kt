package com.netflixclone.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.netflixclone.data_models.IMovie
import com.netflixclone.data_models.Movie


@Entity
data class MovieEntity(
    @ColumnInfo(name = "id") override var id: Int,
    @PrimaryKey override var gitUid: String,
    @ColumnInfo(name = "title") override var title: String,
    @ColumnInfo(name = "poster_path") override var posterPath: String?,
    @ColumnInfo(name = "backdrop_path") override var backdropPath: String?,
    @ColumnInfo(name = "overview") override var overview: String,
    @ColumnInfo(name = "release_date") override var releaseDate: String?,
    @ColumnInfo(name = "vote_average") override var voteAverage: Double?,
    @ColumnInfo(name = "genre_ids") override var genreIds: List<Int>?,
    @ColumnInfo(name = "streaming_link") override var streamingLink: String?,
) : IMovie

fun MovieEntity.toMovie() =
    Movie(id,gitUid, title, posterPath, backdropPath, overview, releaseDate, voteAverage, genreIds,streamingLink)
