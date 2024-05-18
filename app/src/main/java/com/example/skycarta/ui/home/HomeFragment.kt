package com.example.skycarta.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.skycarta.R
import com.example.skycarta.Search_screen
import com.example.skycarta.databinding.FragmentHomeBinding
import com.example.skycarta.utils.SharedPreferenceHelper

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferenceHelper: SharedPreferenceHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sharedPreferenceHelper = SharedPreferenceHelper(requireContext())
        loadFavorites()

        binding.btnSearch.setOnClickListener {
            val intent = Intent(requireContext(), Search_screen::class.java)
            startActivity(intent)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            loadFavorites()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        return root
    }

    private fun loadFavorites() {
        val favorites = sharedPreferenceHelper.getFavorites()
        val favoritesContainer = binding.favoritesContainer
        favoritesContainer.removeAllViews()

        for ((prefix, flightData) in favorites) {
            val favoriteView = layoutInflater.inflate(R.layout.favorite_item, favoritesContainer, false)
            val favoriteFlightNumberTextView = favoriteView.findViewById<TextView>(R.id.favoriteFlightNumberTextView)
            val favoriteDepartureTimeTextView = favoriteView.findViewById<TextView>(R.id.favoriteDepartureTimeTextView)
            val favoriteArrivalTimeTextView = favoriteView.findViewById<TextView>(R.id.favoriteArrivalTimeTextView)
            val favoriteAirport1NameTextView = favoriteView.findViewById<TextView>(R.id.favoriteFirstAirportTextView)
            val favoriteAirport2NameTextView = favoriteView.findViewById<TextView>(R.id.favoriteSecondAirportTextView)
            val removeButton = favoriteView.findViewById<Button>(R.id.removeFavoriteButton)

            favoriteFlightNumberTextView.text = "Номер рейса: " + (flightData["flightNumber"] ?: "")
            favoriteDepartureTimeTextView.text = "Время вылета: " + (flightData["departureTime"] ?: "")
            favoriteArrivalTimeTextView.text = "Время прилета: " + (flightData["arrivalTime"] ?: "")
            favoriteAirport1NameTextView.text = "" + (flightData["airportName1"] ?: "")
            favoriteAirport2NameTextView.text = "" + (flightData["airportName2"] ?: "")

            removeButton.setOnClickListener {
                sharedPreferenceHelper.removeFromFavorites(prefix)
                loadFavorites()
            }

            favoritesContainer.addView(favoriteView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
