package com.netflixclone.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.netflixclone.db.entity.MovieEntity
import com.netflixclone.db.entity.MovieSyncLogEntity
import com.netflixclone.db.repos.MovieEntityDao
import com.netflixclone.db.repos.MovieLastSyncAtDao

@Database(entities = [MovieEntity::class,MovieSyncLogEntity::class], version = 1)
@TypeConverters(value =  [ListTypeConvertor::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieEntityDao(): MovieEntityDao
    abstract fun movieLastSyncDao(): MovieLastSyncAtDao
}