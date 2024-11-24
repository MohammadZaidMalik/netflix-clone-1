package com.netflixclone.screens

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.netflixclone.data_models.MediaBsData
import com.netflixclone.databinding.BottomSheetMediaDetailsBinding
import com.netflixclone.network.services.MediaPlayService


class MediaDetailsBottomSheet(private val data: MediaBsData) : BottomSheetDialogFragment() {
    lateinit var binding: BottomSheetMediaDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = BottomSheetMediaDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val behavior = BottomSheetBehavior.from(requireView().parent as View)

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Adjust the peek height dynamically for landscape mode
            behavior.peekHeight = resources.displayMetrics.heightPixels / 3 // 1/3 of screen height
        } else {
            // Adjust for portrait mode if needed
            behavior.peekHeight = resources.displayMetrics.heightPixels / 2 // 1/2 of screen height
        }

        // Optionally, you can make the bottom sheet fully expanded if required
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupUI()
        updateUI()
    }

    private fun setupUI() {
        binding.closeIcon.setOnClickListener { dismiss() }
        binding.detailsButton.setOnClickListener {
            if (data.mediaType == "movie") {
                val intent = Intent(activity, MovieDetailsActivity::class.java)
                intent.putExtra("id", data.mediaId)
                intent.putExtra("streamingLink", data.streamLink)
                startActivity(intent)
                dismiss()
            } else if (data.mediaType == "tv") {
                val intent = Intent(activity, TvDetailsActivity::class.java)
                intent.putExtra("id", data.mediaId)
                intent.putExtra("streamingLink", data.streamLink)
                startActivity(intent)
                dismiss()
            }
        }
//        binding.playLl.setOnClickListener {

//        }
        binding.playLl.setOnClickListener {
            MediaPlayService.play(data.streamLink,data.title,this.context);
        }
    }

    private fun updateUI() {
        if (data.mediaType == "movie") {
            Glide.with(this).load(data.posterUrl).transform(CenterCrop(), RoundedCorners(8))
                .into(binding.posterImage)
            binding.titleText.text = data.title
            binding.yearText.text = data.releaseYear
            binding.runtimeText.visibility = View.GONE
            binding.overviewText.text = data.overview
        } else if (data.mediaType == "tv") {
            Glide.with(this).load(data.posterUrl).transform(CenterCrop(), RoundedCorners(8))
                .into(binding.posterImage)
            binding.titleText.text = data.title
            binding.yearText.text = data.releaseYear
            binding.runtimeText.visibility = View.GONE
            binding.overviewText.text = data.overview
        }
    }

    companion object {
        fun newInstance(data: MediaBsData): MediaDetailsBottomSheet {
            return MediaDetailsBottomSheet(data)
        }
    }
}