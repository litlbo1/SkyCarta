package com.example.skycarta

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.skycarta.databinding.ActivitySearchScreenBinding

class Search_screen : AppCompatActivity() {

    lateinit var binding: ActivitySearchScreenBinding
    var firstAirport: String? = null
    var firstAirportId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val airportMap = mapOf(
            "Москва" to "svo",
            "Астрахань" to "asf",
            "Самара" to "kuf",
            "Саратов" to "gsv",
            "Хабаровск" to "knv",
            "Сочи" to "aer",
            "Томск" to "tof"
        )

        val airportNames = airportMap.keys.toTypedArray()

        val airportAdapter: ArrayAdapter<String> = ArrayAdapter(
            this, android.R.layout.simple_list_item_1, airportNames
        )

        binding.listAirports.adapter = airportAdapter

        binding.searchAirport.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchAirport.clearFocus()
                if (airportMap.containsKey(query)) {
                    airportAdapter.filter.filter(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                airportAdapter.filter.filter(newText)
                return false
            }
        })

        binding.listAirports.setOnItemClickListener { parent, view, position, id ->
            val selectedAirport = parent.getItemAtPosition(position) as String
            val selectedAirportId = airportMap[selectedAirport] ?: ""
            showOptionsDialog(selectedAirport, selectedAirportId)
        }
    }

    private fun showOptionsDialog(airport: String, airportId: String) {
        val options = arrayOf("Выбрать аэропорт куда лечу", "Посмотреть рейсы из этого аэропорта")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Выберите действие для $airport")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    firstAirport = airport
                    firstAirportId = airportId
                    showSecondAirportDialog()
                }
                1 -> {
                    val intent = Intent(this, Test_Test::class.java)
                    intent.putExtra("AIRPORT_ID", airportId)
                    intent.putExtra("AIRPORT_NAME", airport) // Передаем название аэропорта
                    startActivity(intent)
                }
            }
        }
        builder.show()
    }

    private fun showSecondAirportDialog() {
        val airportMap = mapOf(
            "Москва" to "svo",
            "Астрахань" to "urwa",
            "Самара" to "uwww",
            "Саратов" to "uwsg",
            "Хабаровск" to "uhhh",
            "Сочи" to "urss",
            "Томск" to "untt"

        )

        val airportNames = airportMap.keys.toTypedArray()

        val airportAdapter: ArrayAdapter<String> = ArrayAdapter(
            this, android.R.layout.simple_list_item_1, airportNames
        )

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Выберите второй аэропорт")
        val listView = ListView(this)
        listView.adapter = airportAdapter

        listView.setOnItemClickListener { parent, view, position, id ->
            val secondAirport = parent.getItemAtPosition(position) as String
            val secondAirportId = airportMap[secondAirport] ?: ""
            val intent = Intent(this, AirportSelected_screen::class.java).apply {
                putExtra("FIRST_AIRPORT", firstAirport)
                putExtra("FIRST_AIRPORT_ID", firstAirportId)
                putExtra("SECOND_AIRPORT", secondAirport)
                putExtra("SECOND_AIRPORT_ID", secondAirportId)
            }
            startActivity(intent)
        }

        builder.setView(listView)
        builder.setNegativeButton("Отмена") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }
}
