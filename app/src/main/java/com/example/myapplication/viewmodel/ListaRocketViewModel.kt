package com.example.myapplication.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.RocketEntity
import com.example.myapplication.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListaRocketViewModel(application: Application) : AndroidViewModel(application) {

    private val rocketDao = AppDatabase.getDatabase(application).rocketDao()
    val rocketList: LiveData<List<RocketEntity>> = rocketDao.getAllRockets()

    private val apiService = ApiService.create()
    private val TAG = "ListaRocketViewModel"

    init {
        Log.d(TAG, "Iniciando ViewModel: Verificando y obteniendo cohetes...")
        checkAndFetchRockets()
    }

    private fun checkAndFetchRockets() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Verificar el conteo de la base de datos
                val dbCount = rocketDao.getCount()
                Log.d(TAG, "Número de cohetes en la base de datos: $dbCount")

                if (dbCount == 0) {
                    Log.d(TAG, "La base de datos está vacía. Obteniendo datos de la API...")

                    val response = apiService.getRockets()
                    if (response.isSuccessful) {
                        response.body()?.let { rocketsFromApi ->
                            Log.d(TAG, "Datos recibidos de la API. Total cohetes: ${rocketsFromApi.size}")

                            // Mapear datos de la API a RocketEntity
                            val rocketsToSave = rocketsFromApi.map {
                                RocketEntity(
                                    name = it.name,
                                    type = it.type,
                                    active = it.active,
                                    costPerLaunch = it.costPerLaunch,
                                    successRatePct = it.successRatePct,
                                    country = it.country,
                                    company = it.company,
                                    wikipedia = it.wikipedia,
                                    description = it.description,
                                    height = null, // Si necesitas mapear altura, ajusta aquí
                                    diameter = null
                                )
                            }

                            // Insertar en Room
                            rocketDao.insertAll(rocketsToSave)
                            Log.d(TAG, "Datos insertados correctamente en la base de datos.")
                        } ?: Log.e(TAG, "La respuesta de la API está vacía.")
                    } else {
                        Log.e(TAG, "Error en la respuesta de la API: ${response.code()} - ${response.message()}")
                    }
                } else {
                    Log.d(TAG, "La base de datos ya contiene datos. No se necesita obtener de la API.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error al obtener o guardar los datos: ${e.localizedMessage}", e)
            }
        }
    }
}

