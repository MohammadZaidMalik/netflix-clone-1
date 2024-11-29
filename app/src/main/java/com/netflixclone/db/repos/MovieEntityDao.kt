package com.netflixclone.db.repos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.netflixclone.db.entity.MovieEntity

@Dao
interface MovieEntityDao
{
    @Query("SELECT * FROM movieentity")
    fun getAll(): List<MovieEntity>

    @Query("SELECT * FROM movieentity ORDER BY timestamp desc  LIMIT :pageSize OFFSET :rowOffset")
    fun getAllByPage(pageSize:Int, rowOffset:Int): List<MovieEntity>


    @Query("SELECT * FROM movieentity where gitUid = :id")
    fun findById(id:String): MovieEntity?

    @Query("SELECT * FROM movieentity WHERE title LIKE :title LIMIT 10")
    fun findByTitle(title: String): List<MovieEntity>



    @Insert
    fun insertAll(vararg moveEntities: MovieEntity)

    @Delete
    fun delete(movie: MovieEntity)

    @Update
    fun update(movieEntity: MovieEntity)
}