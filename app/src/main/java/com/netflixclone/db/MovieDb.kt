package com.netflixclone.db

import android.content.Context
import androidx.room.Room
import com.netflixclone.MyApplication
import java.io.File
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

object MovieDb
{
    @Volatile
    private var INSTANCE: AppDatabase? = null

    public fun  getMovieDB():AppDatabase
    {
        return INSTANCE ?: synchronized(this) {
            val db = Room.databaseBuilder(
                MyApplication.getAppContext(),
                AppDatabase::class.java, "movie-database"
            )
                .allowMainThreadQueries().build();
            INSTANCE = db;
            return db;
        }
    }
}