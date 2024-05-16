package com.example.skycarta.ui.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        sharedPreferences = requireContext().getSharedPreferences("favorites", Context.MODE_PRIVATE)
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
        val allEntries = sharedPreferences.all
        val favoritesContainer = binding.favoritesContainer
        favoritesContainer.removeAllViews()

        for ((key, value) in allEntries) {
            val favoriteView = layoutInflater.inflate(R.layout.favorite_item, favoritesContainer, false)
            val favoriteTextView = favoriteView.findViewById<TextView>(R.id.favoriteInfoTextView)
            val removeButton = favoriteView.findViewById<Button>(R.id.removeFavoriteButton)

            favoriteTextView.text = value as String
            removeButton.setOnClickListener {
                removeFromFavorites(key)
                loadFavorites()
            }

            favoritesContainer.addView(favoriteView)
        }
    }

    private fun removeFromFavorites(key: String) {
        val editor = sharedPreferences.edit()
        editor.remove(key)
        editor.apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
