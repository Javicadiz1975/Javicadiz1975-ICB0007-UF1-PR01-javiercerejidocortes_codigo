package com.example.myapplication.data

import androidx.room.TypeConverter
import com.example.myapplication.model.Dimensions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromDimensions(dimensions: Dimensions?): String {
        return Gson().toJson(dimensions)
    }

    @TypeConverter
    fun toDimensions(json: String): Dimensions? {
        val type = object : TypeToken<Dimensions>() {}.type
        return Gson().fromJson(json, type)
    }
}