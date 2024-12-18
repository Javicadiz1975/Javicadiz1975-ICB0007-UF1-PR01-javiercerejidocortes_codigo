package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.myapplication.model.Dimensions
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "rockets")
@TypeConverters(Converters::class)
data class RocketEntity(
    @PrimaryKey val name: String,
    val type: String,
    val active: Boolean,
    val costPerLaunch: Long,
    val successRatePct: Int,
    val country: String,
    val company: String,
    val wikipedia: String,
    val description: String,
    val height: Dimensions?,   // Ahora Dimensions es Parcelable
    val diameter: Dimensions?  // Ahora Dimensions es Parcelable
) : Parcelable