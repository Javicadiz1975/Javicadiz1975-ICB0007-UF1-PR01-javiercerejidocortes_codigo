package com.example.myapplication.model
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Rocket(
    val name: String,                       // Nombre
    val type: String,                       // Tipo
    val active: Boolean,                    // Activo o no
    val cost_per_launch: Long,              // Costo por lanzamiento
    val success_rate_pct: Int,              // Porcentaje de éxito
    val country: String,                    // País
    val company: String,                    // Empresa
    val wikipedia: String,                  // Enlace a Wikipedia
    val description: String,                // Descripción
    val height: Dimensions,                 // Altura
    val diameter: Dimensions                // Diámetro
) : Parcelable
@Parcelize
data class Dimensions(
    val meters: Double?,                    // Métrico
    val feet: Double?                       // Imperial
) : Parcelable