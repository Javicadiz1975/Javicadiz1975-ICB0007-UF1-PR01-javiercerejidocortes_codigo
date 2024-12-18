package com.example.myapplication.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R

import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.RocketEntity
import com.example.myapplication.databinding.FragmentDetallesRocketBinding
import com.example.myapplication.model.Dimensions
import com.example.myapplication.model.Rocket

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DetallesRocketFragment : Fragment() {

    private lateinit var binding: FragmentDetallesRocketBinding
    private var rocket: Rocket? = null  // Declara Rocket a nivel de clase
    private var isEditing = false // Para controlar el modo edición

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetallesRocketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Recuperar RocketEntity del Bundle y convertirlo a Rocket
        @Suppress("DEPRECATION")
        val rocketEntity: RocketEntity? = arguments?.getParcelable("rocket")
        rocketEntity?.let {
            rocket = it.toRocket()
            showRocketDetails(rocket!!)
        }

        // Configurar interacción con Wikipedia
        binding.tvRocketWikipedia.setOnClickListener {
            val url = rocket?.wikipedia
            if (!url.isNullOrBlank() && url.startsWith("http")) { // Verifica que el enlace empiece con "http"
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                } catch (e: Exception) {
                    showToast("No se pudo abrir el enlace de Wikipedia.")
                }
            } else {
                showToast("Enlace de Wikipedia no disponible.")
            }
        }

        // Configurar interacción con el país (Google Maps)
        binding.tvRocketCountry.setOnClickListener {
            val country = rocket?.country
            if (!country.isNullOrBlank()) {
                val gmmIntentUri = Uri.parse("geo:0,0?q=$country")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            } else {
                showToast("Ubicación no disponible")
            }
        }

        // Botón Editar
        binding.btnEditRocket.setOnClickListener {
            if (rocket != null) {  // Verificación de null
                val bundle = Bundle()
                bundle.putString("name", rocket?.name ?: "")
                bundle.putString("type", rocket?.type ?: "")
                bundle.putBoolean("active", rocket?.active ?: false)
                bundle.putLong("costPerLaunch", rocket?.cost_per_launch ?: 0L)
                bundle.putInt("successRatePct", rocket?.success_rate_pct ?: 0)
                bundle.putString("country", rocket?.country ?: "")
                bundle.putString("company", rocket?.company ?: "")
                bundle.putString("wikipedia", rocket?.wikipedia ?: "")
                bundle.putString("description", rocket?.description ?: "")

                findNavController().navigate(
                    R.id.action_detallesRocketFragment_to_editarRocketFragment,
                    bundle
                )
            } else {
                Toast.makeText(requireContext(), "No se puede editar, datos no disponibles", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón Eliminar
        binding.btnDeleteRocket.setOnClickListener {
            deleteRocket()
        }

    }

    override fun onResume() {
        super.onResume()
        // Volver a cargar los datos desde la base de datos
        rocket?.let { currentRocket ->
            lifecycleScope.launch(Dispatchers.IO) {
                val updatedRocket = AppDatabase.getDatabase(requireContext())
                    .rocketDao()
                    .getRocketByName(currentRocket.name)
                launch(Dispatchers.Main) {
                    updatedRocket?.let {
                        rocket = it.toRocket()
                        showRocketDetails(rocket!!)
                    }
                }
            }
        }
    }

    private fun showRocketDetails(rocket: Rocket) {
        binding.tvRocketName.text = rocket.name
        binding.tvRocketWikipedia.text = "Wikipedia"
        binding.tvRocketCountry.text = "País: ${rocket.country}"
        binding.tvRocketType.text = "Tipo: ${rocket.type}"
        binding.tvRocketActive.text = "Activo: ${if (rocket.active) "Sí" else "No"}"
        binding.tvRocketCost.text = "Costo por lanzamiento: $${rocket.cost_per_launch}"
        binding.tvRocketSuccessRate.text = "Tasa de éxito: ${rocket.success_rate_pct}%"
        binding.tvRocketCompany.text = "Empresa: ${rocket.company}"
        binding.tvRocketDescription.text = "Descripción: ${rocket.description}"
    }

    private fun toggleEditMode() {
        isEditing = !isEditing

        if (isEditing) {
            showToast("Modo edición activado")

            // Convertir campos en EditTexts y habilitar edición
            binding.tvRocketName.isEnabled = true
            binding.tvRocketType.isEnabled = true
            binding.tvRocketActive.isEnabled = true
            binding.tvRocketCost.isEnabled = true
            binding.tvRocketSuccessRate.isEnabled = true
            binding.tvRocketCountry.isEnabled = true
            binding.tvRocketCompany.isEnabled = true
            binding.tvRocketWikipedia.isEnabled = true
            binding.tvRocketDescription.isEnabled = true
        } else {
            showToast("Modo edición desactivado")
            saveEditedRocket() // Guardar cambios
        }
    }

    private fun saveEditedRocket() {
        val updatedRocket = RocketEntity(
            name = binding.tvRocketName.text.toString(),
            type = binding.tvRocketType.text.toString(),
            active = binding.tvRocketActive.text.toString().toBoolean(),
            costPerLaunch = binding.tvRocketCost.text.toString().toLongOrNull() ?: 0L,
            successRatePct = binding.tvRocketSuccessRate.text.toString().toIntOrNull() ?: 0,
            country = binding.tvRocketCountry.text.toString(),
            company = binding.tvRocketCompany.text.toString(),
            wikipedia = binding.tvRocketWikipedia.text.toString(),
            description = binding.tvRocketDescription.text.toString(),
            height = null,
            diameter = null
        )

        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(requireContext()).rocketDao()
            db.insertAll(listOf(updatedRocket)) // Reemplazar el cohete existente
            launch(Dispatchers.Main) {
                showToast("Cambios guardados correctamente")
            }
        }
    }

    private fun deleteRocket() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val database = AppDatabase.getDatabase(requireContext())
            val dao = database.rocketDao()

            // Eliminar el cohete actual de la base de datos
            rocket?.let {
                dao.deleteRocket(
                    RocketEntity(
                        name = it.name,
                        type = it.type,
                        active = it.active,
                        costPerLaunch = it.cost_per_launch,
                        successRatePct = it.success_rate_pct,
                        country = it.country,
                        company = it.company,
                        wikipedia = it.wikipedia,
                        description = it.description,
                        height = it.height,
                        diameter = it.diameter
                    )
                )
            }

            launch(Dispatchers.Main) {
                showToast("Cohete eliminado correctamente")
                activity?.onBackPressedDispatcher?.onBackPressed() // Usar el método moderno
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun RocketEntity.toRocket(): Rocket {
        return Rocket(
            name = this.name,
            type = this.type,
            active = this.active,
            cost_per_launch = this.costPerLaunch,
            success_rate_pct = this.successRatePct,
            country = this.country,
            company = this.company,
            wikipedia = this.wikipedia,
            description = this.description,
            height = this.height ?: Dimensions(null, null),
            diameter = this.diameter ?: Dimensions(null, null)
        )
    }
}