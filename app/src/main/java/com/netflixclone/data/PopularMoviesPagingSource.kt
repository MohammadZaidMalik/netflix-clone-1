package com.netflixclone.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.netflixclone.data_models.Movie
import com.netflixclone.db.AppDatabase
import com.netflixclone.db.MovieDb
import com.netflixclone.db.entity.toMovie
import com.netflixclone.db.repos.MovieEntityDao
import com.netflixclone.network.services.ApiClient
import retrofit2.HttpException
import java.io.IOException

private const val STARTING_PAGE_INDEX = 1

class PopularMoviesPagingSource : PagingSource<Int, Movie>() {
    val pageSize:Int = 10
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val position = params.key ?: STARTING_PAGE_INDEX
        return try
        {
            val movieDB: MovieEntityDao= MovieDb.getMovieDB().movieEntityDao();

            val res =  movieDB.getAllByPage(pageSize,(position-1)*pageSize);
            Log.i("PopularMoviesLog","movies found : ${res.size} pageSize: $pageSize  position: $position");

//            val response = ApiClient.TMDB.fetchPopularMovies(position)
            val results = res.map { it.toMovie() };

            LoadResult.Page(
                    data = results,
                    prevKey = if (position == STARTING_PAGE_INDEX) null else position - 1,
                    nextKey = if (results.isEmpty()) null else position + 1
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                    ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}