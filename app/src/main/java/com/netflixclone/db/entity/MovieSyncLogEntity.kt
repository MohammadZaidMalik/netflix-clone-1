package com.netflixclone.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.netflixclone.data_models.IMovie
import com.netflixclone.data_models.Media
import com.netflixclone.data_models.Movie
import com.squareup.moshi.Json


@Entity
data class MovieSyncLogEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "last_sync_at")  var lastSyncedAt: Long,
)
