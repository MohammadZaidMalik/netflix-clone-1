package com.netflixclone.network.services

import com.netflixclone.data_models.Media
import com.netflixclone.data_models.Movie
import com.netflixclone.data_models.TvShow
import com.netflixclone.network.models.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Call

interface GitDbService
{
    @GET("{fileName}")
    fun getPlainText( @Path("fileName") fileName: String): Call<String>
}