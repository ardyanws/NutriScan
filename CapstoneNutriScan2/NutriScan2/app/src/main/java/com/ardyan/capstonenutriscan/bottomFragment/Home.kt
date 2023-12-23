package com.ardyan.capstonenutriscan.bottomFragment

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ardyan.capstonenutriscan.FoodAdapter
import com.ardyan.capstonenutriscan.databinding.FragmentHomeBinding
import com.ardyan.capstonenutriscan.setting.HomeViewModel

class Home : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var foodAdapter: FoodAdapter
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        foodAdapter = FoodAdapter(emptyList())

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = foodAdapter
        }

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        homeViewModel.fetchFoodData()

        homeViewModel.foodList.observe(viewLifecycleOwner, Observer { foods ->
            foods?.let {
                foodAdapter.updateData(it)
            }
        })

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterFoodList(newText)
                return true
            }
        })
    }

    private fun filterFoodList(query: String?) {
        val filteredList = homeViewModel.foodList.value?.filter { food ->
            food.namaMakanan.contains(query.orEmpty(), ignoreCase = true)
        }
        foodAdapter.updateData(filteredList.orEmpty())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
