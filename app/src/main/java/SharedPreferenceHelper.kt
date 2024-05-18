package com.example.skycarta.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceHelper(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("favorites", Context.MODE_PRIVATE)

    fun getFavorites(): Map<String, Map<String, String>> {
        val favorites = mutableMapOf<String, Map<String, String>>()
        val allEntries = sharedPreferences.all
        for ((key, value) in allEntries) {
            val parts = key.split("_")
            if (parts.size >= 3) {
                val flightNumber = parts[1]
                val field = parts[2]
                val flightData = favorites.getOrPut(flightNumber) { mutableMapOf() }
                (flightData as MutableMap)[field] = value as String
            }
        }
        return favorites
    }

    fun addToFavorites(flightNumber: String, data: Map<String, String>) {
        val editor = sharedPreferences.edit()
        for ((field, value) in data) {
            editor.putString("flight_${flightNumber}_$field", value)
        }
        editor.apply()
    }

    fun removeFromFavorites(flightNumber: String) {
        val editor = sharedPreferences.edit()
        val keysToRemove = sharedPreferences.all.keys.filter { it.startsWith("flight_${flightNumber}_") }
        for (key in keysToRemove) {
            editor.remove(key)
        }
        editor.apply()
    }
}
