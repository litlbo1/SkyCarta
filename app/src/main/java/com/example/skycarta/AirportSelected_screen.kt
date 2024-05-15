package com.example.skycarta

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.skycarta.databinding.ActivityAirportSelectedScreenBinding

class AirportSelected_screen : AppCompatActivity() {

    lateinit var binding: ActivityAirportSelectedScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAirportSelectedScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Получаем данные из Intent
        val firstAirport = intent.getStringExtra("FIRST_AIRPORT")
        val secondAirport = intent.getStringExtra("SECOND_AIRPORT")

        // Устанавливаем данные в TextView
        binding.textViewFirstAirport.text = "Аэропорт 1: $firstAirport"
        binding.textViewSecondAirport.text = "Аэропорт 2: $secondAirport"
    }
}