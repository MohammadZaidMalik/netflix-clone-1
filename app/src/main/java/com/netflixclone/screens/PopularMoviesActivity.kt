package com.netflixclone.screens

import android.os.Bundle
import android.util.DisplayMetrics
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.netflixclone.R
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
        val recyclerView: RecyclerView = findViewById(R.id.popular_movies_list)
        val spanCount = calculateSpanCount(300)
        recyclerView.layoutManager = GridLayoutManager(this, spanCount)
//        val itemWidth = resources.getDimensionPixelSize(R.dimen.item_width)

        // Calculate span count dynamically

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

    private fun calculateSpanCount(itemWidth: Int): Int {
        // Get screen width in pixels
        val displayMetrics: DisplayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels

        // Calculate span count
        return (screenWidth / itemWidth).coerceAtLeast(1) // Ensure at least one column
    }
}