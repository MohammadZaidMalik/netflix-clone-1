package com.netflixclone

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
open class MyApplication : Application() {

    override fun onCreate() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        super.onCreate()
        instance = this
    }
    companion object {
        private var instance: MyApplication? = null

        fun getAppContext(): Context {
            return instance!!.applicationContext
        }
    }
}
