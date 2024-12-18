package com.example.myapplication.network

import android.util.Log
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {

    // Define el endpoint para obtener la lista de cohetes
    @GET("rockets")
    suspend fun getRockets(): Response<List<ApiRocketModel>>

    companion object {
        private const val BASE_URL = "https://api.spacexdata.com/v4/"
        private const val TAG = "ApiService" // Etiqueta para los Logs

        fun create(): ApiService {
            Log.d(TAG, "Iniciando Retrofit con URL base: $BASE_URL")

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            Log.d(TAG, "Retrofit creado correctamente")
            return retrofit.create(ApiService::class.java)
        }
    }
}