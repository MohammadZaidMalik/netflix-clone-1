package com.netflixclone.screens

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.netflixclone.adapters.PagedMoviesAdapter
import com.netflixclone.data.MediaViewModel
import com.netflixclone.data_models.Movie
import com.netflixclone.databinding.ActivityPopularMoviesBinding
import com.netflixclone.extensions.toMediaBsData
import com.netflixclone.network.services.ApiClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject
import retrofit2.Callback
import android.util.Log;
import retrofit2.Call
import retrofit2.Response

@AndroidEntryPoint
class PopularMoviesActivity : BaseActivity() {
    private lateinit var binding: ActivityPopularMoviesBinding
    private val viewModel by viewModels<MediaViewModel>()
    private lateinit var popularMoviesItemsAdapter: PagedMoviesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPopularMoviesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        fetchData()
    }


    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener { finish() }
        popularMoviesItemsAdapter = PagedMoviesAdapter(this::handleMovieClick)
        binding.popularMoviesList.adapter = popularMoviesItemsAdapter
    }

    private fun fetchData() {
        lifecycleScope.launchWhenCreated {
            try {
                viewModel.getPopularMovies().collectLatest {
                    popularMoviesItemsAdapter.submitData(it)
                }
            } catch (e: Exception) {
            }
        }
    }

    fun handleMovieClick(movie: Movie) {
        MediaDetailsBottomSheet.newInstance(movie.toMediaBsData())
            .show(supportFragmentManager, movie.id.toString())
    }
}