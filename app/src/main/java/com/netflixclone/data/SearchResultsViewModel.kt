package com.netflixclone.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.netflixclone.data_models.Media
import com.netflixclone.data_models.Movie
import com.netflixclone.data_models.toMediaMovie
import com.netflixclone.db.MovieDb
import com.netflixclone.db.entity.MovieEntity
import com.netflixclone.db.entity.toMovie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchResultsViewModel @Inject constructor() : ViewModel() {

    val popularMoviesLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val popularMovies: MutableLiveData<List<Movie>> = MutableLiveData()

    val searchResultsLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val searchResults: MutableLiveData<List<Media>> = MutableLiveData()

    fun fetchPopularMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            popularMoviesLoading.postValue(true)
            try {
                val response = MediaRepository.fetchPopularMovies(1)
                popularMovies.postValue(response.results)
                popularMoviesLoading.postValue(false)
            } catch (e: Exception) {
                popularMoviesLoading.postValue(false)
            }
        }
    }

    fun fetchSearchResults(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            searchResultsLoading.postValue(true)
            try {
//                val response = MediaRepository.fetchSearchResults(query, 1)
//                searchResults.postValue(response.results.filter { it is Media.Movie || it is Media.Tv })
                val movies:List<MovieEntity> = MovieDb.getMovieDB().movieEntityDao().findByTitle("$query%");

                searchResults.postValue(movies.map { it.toMovie().toMediaMovie() }.toList())
                searchResultsLoading.postValue(false)
            } catch (e: Exception) {
                searchResultsLoading.postValue(false)
            }
        }
    }
}