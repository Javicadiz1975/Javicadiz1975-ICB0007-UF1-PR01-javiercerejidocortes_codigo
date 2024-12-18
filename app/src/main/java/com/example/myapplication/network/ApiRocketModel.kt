package com.example.myapplication.network

import com.google.gson.annotations.SerializedName

data class ApiRocketModel(
    val name: String,
    val type: String,
    val active: Boolean,
    @SerializedName("cost_per_launch") val costPerLaunch: Long, // Mapeo correcto del campo JSON
    @SerializedName("success_rate_pct") val successRatePct: Int, // Mapeo correcto del campo JSON
    val country: String,
    val company: String,
    val wikipedia: String,
    val description: String
)

