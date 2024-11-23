package com.netflixclone.db.repos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.netflixclone.db.entity.MovieEntity

@Dao
interface MovieEntityDao
{
    @Query("SELECT * FROM movieentity")
    fun getAll(): List<MovieEntity>

    @Query("SELECT * FROM movieentity WHERE title LIKE :title LIMIT 10")
    fun findByTitle(title: String): MovieEntity

    @Insert
    fun insertAll(vararg moveEntities: MovieEntity)

    @Delete
    fun delete(movie: MovieEntity)
}