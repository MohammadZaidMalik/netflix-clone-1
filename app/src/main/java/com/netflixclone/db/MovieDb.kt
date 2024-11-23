package com.netflixclone.db

import android.content.Context
import androidx.room.Room
import java.io.File
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

object MovieDb
{
    @Volatile
    private var INSTANCE: AppDatabase? = null

    public fun  getMovieDB(applicationContext:Context):AppDatabase
    {
        return INSTANCE ?: synchronized(this) {
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "movie-database"
            ).build();
            INSTANCE = db;
            return db;
        }
    }
}