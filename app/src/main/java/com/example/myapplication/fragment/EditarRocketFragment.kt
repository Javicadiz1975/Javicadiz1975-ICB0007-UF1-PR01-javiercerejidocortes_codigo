package com.example.myapplication.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.RocketEntity
import com.example.myapplication.databinding.FragmentEditarRocketBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditarRocketFragment : Fragment() {

    private lateinit var binding: FragmentEditarRocketBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditarRocketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Recuperar valores del Bundle
        val name = arguments?.getString("name") ?: ""
        val type = arguments?.getString("type") ?: ""
        val active = arguments?.getBoolean("active") ?: false
        val costPerLaunch = arguments?.getLong("costPerLaunch") ?: 0L
        val successRatePct = arguments?.getInt("successRatePct") ?: 0
        val country = arguments?.getString("country") ?: ""
        val company = arguments?.getString("company") ?: ""
        val wikipedia = arguments?.getString("wikipedia") ?: ""
        val description = arguments?.getString("description") ?: ""

        // Mostrar valores en los campos EditText
        binding.etRocketName.setText(name)
        binding.etRocketType.setText(type)
        binding.etRocketActive.setText(active.toString())
        binding.etRocketCost.setText(costPerLaunch.toString())
        binding.etRocketSuccessRate.setText(successRatePct.toString())
        binding.etRocketCountry.setText(country)
        binding.etRocketCompany.setText(company)
        binding.etRocketWikipedia.setText(wikipedia)
        binding.etRocketDescription.setText(description)

        // Guardar los cambios
        binding.btnSaveRocket.setOnClickListener {
            saveRocket()
        }

        // Cancelar y regresar a la pantalla anterior
        binding.btnCancelRocket.setOnClickListener {
            Toast.makeText(requireContext(), "Cambios descartados", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }

    }

    private fun saveRocket() {
        val updatedRocket = RocketEntity(
            name = binding.etRocketName.text.toString(), // La clave primaria debe coincidir
            type = binding.etRocketType.text.toString(),
            active = binding.etRocketActive.text.toString().toBoolean(),
            costPerLaunch = binding.etRocketCost.text.toString().toLongOrNull() ?: 0L,
            successRatePct = binding.etRocketSuccessRate.text.toString().toIntOrNull() ?: 0,
            country = binding.etRocketCountry.text.toString(),
            company = binding.etRocketCompany.text.toString(),
            wikipedia = binding.etRocketWikipedia.text.toString(),
            description = binding.etRocketDescription.text.toString(),
            height = null,  // Puedes manejar esto según sea necesario
            diameter = null
        )

        lifecycleScope.launch(Dispatchers.IO) {
            // Llamar al método updateRocket
            AppDatabase.getDatabase(requireContext()).rocketDao().updateRocket(updatedRocket)
            launch(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Cohete actualizado correctamente", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
    }
}