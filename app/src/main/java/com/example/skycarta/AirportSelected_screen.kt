package com.example.skycarta

import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.skycarta.databinding.ActivityAirportSelectedScreenBinding
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class AirportSelected_screen : AppCompatActivity() {

    lateinit var binding: ActivityAirportSelectedScreenBinding
    private val flights = mutableListOf<Flights>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAirportSelectedScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val firstAirport = intent.getStringExtra("FIRST_AIRPORT")
        val secondAirport = intent.getStringExtra("SECOND_AIRPORT")
        val firstAirportId = intent.getStringExtra("FIRST_AIRPORT_ID")
        val secondAirportId = intent.getStringExtra("SECOND_AIRPORT_ID")

        binding.textViewFirstAirport.text = "$firstAirport"
        binding.textViewSecondAirport.text = "$secondAirport"

        FetchFlightsTask().execute(firstAirportId, secondAirportId)
    }

    private fun addFlightViews() {
        for (flight in flights) {
            val flightLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(16, 16, 16, 16)
            }

            val flightNumberTextView = TextView(this).apply {
                text = "Номер рейса: ${flight.flightNumber}"
                textSize = 16f
            }

            val departureTimeTextView = TextView(this).apply {
                text = "Время вылета: ${flight.departureTime}"
                textSize = 16f
            }

            val arrivalTimeTextView = TextView(this).apply {
                text = "Время прилета: ${flight.arrivalTime}"
                textSize = 16f
            }

            val addToFavoritesButton = Button(this).apply {
                text = "В избранное"
                setOnClickListener {
                    addToFavorites(flight, binding.textViewFirstAirport.text.toString(), binding.textViewSecondAirport.text.toString())
                }
            }

            flightLayout.addView(flightNumberTextView)
            flightLayout.addView(departureTimeTextView)
            flightLayout.addView(arrivalTimeTextView)
            flightLayout.addView(addToFavoritesButton)

            binding.flightContainer.addView(flightLayout)
        }
    }

    fun addToFavorites(flight: Flights, airportName1: String, airportName2: String) {
        val sharedPreferences = getSharedPreferences("favorites", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("flight_${flight.flightNumber}_flightNumber", flight.flightNumber)
        editor.putString("flight_${flight.flightNumber}_departureTime", flight.departureTime)
        editor.putString("flight_${flight.flightNumber}_arrivalTime", flight.arrivalTime)
        editor.putString("flight_${flight.flightNumber}_airportName1", airportName1)
        editor.putString("flight_${flight.flightNumber}_airportName2", airportName2)
        editor.apply()
    }

    inner class FetchFlightsTask : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            binding.progressBar.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: String?): String? {
            val firstAirportId = params[0]
            val secondAirportId = params[1]
            val urlString = "http://192.168.1.44:5500/flights?origin=$firstAirportId&destination=$secondAirportId"
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()
            return connection.inputStream.bufferedReader().readText()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            binding.progressBar.visibility = View.GONE
            if (result != null) {
                val jsonObject = JSONObject(result)
                val flightsArray = jsonObject.getJSONArray("flights")
                for (i in 0 until flightsArray.length()) {
                    val flightJson = flightsArray.getJSONObject(i)
                    val flight = Flights(
                        flightNumber = flightJson.getString("flight_number"),
                        departureTime = flightJson.getString("departure_time"),
                        arrivalTime = flightJson.getString("arrival_time")
                    )
                    flights.add(flight)
                }
                addFlightViews()
            }
        }
    }
}
