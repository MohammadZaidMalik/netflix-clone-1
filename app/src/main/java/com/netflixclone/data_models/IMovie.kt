package com.netflixclone.data_models

interface IMovie {
    val gitUid:String?
    val id: Int
    val title: String
    val posterPath: String?
    val backdropPath: String?
    val overview: String
    val releaseDate: String?
    val voteAverage: Double?
    val genreIds: List<Int>?
    val streamingLink: String?
}