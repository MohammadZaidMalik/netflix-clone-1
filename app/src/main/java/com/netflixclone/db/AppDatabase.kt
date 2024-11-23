package com.netflixclone.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.netflixclone.db.entity.MovieEntity
import com.netflixclone.db.repos.MovieEntityDao

@Database(entities = [MovieEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieEntityDao(): MovieEntityDao
}