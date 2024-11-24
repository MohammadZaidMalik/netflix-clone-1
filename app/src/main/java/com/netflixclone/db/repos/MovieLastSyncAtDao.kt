package com.netflixclone.db.repos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Query
import com.netflixclone.db.entity.MovieSyncLogEntity

@Dao
interface MovieLastSyncAtDao
{
    @Query("SELECT * FROM moviesynclogentity limit 1")
    fun getAll(): List<MovieSyncLogEntity>

    @Query("SELECT * FROM moviesynclogentity WHERE id = 1")
    fun getLastSync(): MovieSyncLogEntity?

    @Update
    fun update(movieSyncLogEntity: MovieSyncLogEntity)

    @Insert
    fun insertAll(vararg movieSyncLogEntity: MovieSyncLogEntity)

    @Delete
    fun delete(movie: MovieSyncLogEntity)
}