package com.netflixclone.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ListTypeConvertor {

    // Convert List<String> to JSON String
    @TypeConverter
    fun fromList(list: List<Int>?): String? {
        return if (list == null) null else Gson().toJson(list)
    }

    // Convert JSON String back to List<String>
    @TypeConverter
    fun toList(data: String?): List<Int>? {
        return if (data == null) null else {
            val listType = object : TypeToken<List<String>>() {}.type
            Gson().fromJson<List<Int>>(data, listType)
        }
    }
}
