package com.example.skycarta

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.skycarta.databinding.ActivityTestTestBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.io.IOException
import java.util.concurrent.TimeUnit

class Test_Test : AppCompatActivity() {

    private lateinit var binding: ActivityTestTestBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var airportName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("favorites", Context.MODE_PRIVATE)

        val airportId = intent.getStringExtra("AIRPORT_ID")
        airportName = intent.getStringExtra("AIRPORT_NAME") ?: "" // Получить название аэропорта

        if (airportName.isNotEmpty()) {
            binding.airportNameTextView.text = "Airport: $airportName" // Установить название аэропорта
        }

        if (airportId != null) {
            fetchFlights(airportId)
        }
    }

    private fun fetchFlights(airportId: String) {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url("http://192.168.1.44:3300/flights?airport_id=$airportId")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    binding.flightInfoTextView.text = "Error: ${e.message}"
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    runOnUiThread {
                        try {
                            if (responseBody != null) {
                                val flights = JSONArray(responseBody)
                                binding.flightsContainer.removeAllViews()
                                for (i in 0 until flights.length()) {
                                    val flight = flights.getJSONObject(i)
                                    val destination = flight.getString("destination")
                                    val departureTime = flight.getString("departure_time")

                                    val flightView = layoutInflater.inflate(R.layout.flight_item, binding.flightsContainer, false)
                                    val destinationTextView = flightView.findViewById<TextView>(R.id.flightDestination)
                                    val departureTimeTextView = flightView.findViewById<TextView>(R.id.flightDepartureTime)
                                    val favoriteButton = flightView.findViewById<Button>(R.id.favoriteButton)

                                    destinationTextView.text = destination
                                    departureTimeTextView.text = departureTime

                                    favoriteButton.setOnClickListener {
                                        addToFavorites(destination, departureTime)
                                    }

                                    binding.flightsContainer.addView(flightView)
                                }
                            } else {
                                binding.flightInfoTextView.text = "Error: Response body is null"
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            binding.flightInfoTextView.text = "Error parsing response: ${e.message}"
                        }
                    }
                } else {
                    runOnUiThread {
                        binding.flightInfoTextView.text = "Server error: ${response.code}"
                    }
                }
            }
        })
    }

    private fun addToFavorites(destination: String, departureTime: String) {
        val editor = sharedPreferences.edit()
        editor.putString("$destination$departureTime", "Вылет в: $destination, Время вылета: $departureTime, Аэропорт: $airportName")
        editor.apply()
    }
}
