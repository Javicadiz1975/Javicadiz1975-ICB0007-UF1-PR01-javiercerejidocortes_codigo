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
import com.example.myapplication.databinding.FragmentCreaRocketBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreaRocketFragment : Fragment() {

    private var _binding: FragmentCreaRocketBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreaRocketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSaveRocket.setOnClickListener {
            saveRocket()
        }
    }

    private fun saveRocket() {
        val name = binding.etRocketName.text.toString().trim()
        val type = binding.etRocketType.text.toString().trim()
        val active = binding.etRocketActive.text.toString().trim().toBoolean()
        val costPerLaunch = binding.etRocketCost.text.toString().trim().toLongOrNull() ?: 0L
        val successRatePct = binding.etRocketSuccessRate.text.toString().trim().toIntOrNull() ?: 0
        val country = binding.etRocketCountry.text.toString().trim()
        val company = binding.etRocketCompany.text.toString().trim()
        val wikipedia = binding.etRocketWikipedia.text.toString().trim()
        val description = binding.etRocketDescription.text.toString().trim()

        // Validar campos obligatorios
        if (name.isEmpty() || type.isEmpty() || country.isEmpty() || company.isEmpty()) {
            Toast.makeText(requireContext(), "Completa todos los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear RocketEntity
        val newRocket = RocketEntity(
            name = name,
            type = type,
            active = active,
            costPerLaunch = costPerLaunch,
            successRatePct = successRatePct,
            country = country,
            company = company,
            wikipedia = wikipedia,
            description = description,
            height = null,  // Valores por defecto
            diameter = null
        )

        // Insertar en la base de datos usando Coroutines
        lifecycleScope.launch(Dispatchers.IO) {
            AppDatabase.getDatabase(requireContext()).rocketDao().insertAll(listOf(newRocket))

            launch(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Cohete guardado correctamente", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp() // Reemplaza activity?.onBackPressed()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}