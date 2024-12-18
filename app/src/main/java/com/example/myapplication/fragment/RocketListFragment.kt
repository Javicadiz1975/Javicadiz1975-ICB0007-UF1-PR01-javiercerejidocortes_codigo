package com.example.myapplication.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapter.RocketAdapter
import com.example.myapplication.data.RocketEntity
import com.example.myapplication.databinding.FragmentRocketListBinding

import com.example.myapplication.viewmodel.ListaRocketViewModel

class RocketListFragment : Fragment() {

    private lateinit var binding: FragmentRocketListBinding
    private lateinit var adapter: RocketAdapter
    private val viewModel: ListaRocketViewModel by viewModels()

    // Lista local para manejar los cohetes
    private var originalRocketList: List<RocketEntity> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRocketListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar Toolbar local
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        // Configurar RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Inicializar el adapter
        adapter = RocketAdapter(emptyList()) { selectedRocket ->
            navigateToDetails(selectedRocket)
        }
        binding.recyclerView.adapter = adapter

        // Observar la lista desde el ViewModel
        viewModel.rocketList.observe(viewLifecycleOwner) { rockets ->
            originalRocketList = rockets
            adapter.updateData(rockets)
        }

        // Configurar el menú utilizando MenuProvider
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_detalles, menu)

                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem.actionView as androidx.appcompat.widget.SearchView
                searchView.queryHint = "Buscar cohete..."
                searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        query?.let { filterRockets(it) }
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        newText?.let { filterRockets(it) }
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_add -> {
                        // Ir a CreaRocketFragment
                        Log.d("RocketListFragment", "Añadir Cohete seleccionado")


                        findNavController().navigate(R.id.action_rocketListFragment_to_creaRocketFragment)
                        true
                    }
                    R.id.action_logout -> {
                        // Ir a LoginFragment
                        findNavController().navigate(R.id.action_rocketListFragment_to_loginFragment)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)
    }

    private fun navigateToDetails(selectedRocket: RocketEntity) {
        val bundle = Bundle().apply {
            putParcelable("rocket", selectedRocket)
        }
        findNavController().navigate(
            R.id.action_rocketListFragment_to_detallesRocketFragment, bundle
        )
    }

    private fun filterRockets(query: String) {
        val filteredList = originalRocketList.filter {
            it.name.contains(query, ignoreCase = true) ||  // Filtrar por nombre
                    it.company.contains(query, ignoreCase = true) // Filtrar por empresa
        }
        adapter.updateData(filteredList)
    }
}