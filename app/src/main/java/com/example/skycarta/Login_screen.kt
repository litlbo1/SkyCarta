package com.example.skycarta

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.skycarta.databinding.ActivityLoginScreenBinding
import com.google.firebase.auth.FirebaseAuth

class Login_screen : AppCompatActivity() {

    private lateinit var binding: ActivityLoginScreenBinding
    private lateinit var auth: FirebaseAuth // Объявить как локальное свойство

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance() // Инициализировать auth
        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.login.setOnClickListener{
            val email = binding.loginEmail.text.toString().trim()
            val password = binding.loginPassword.text.toString().trim()

            //локальный экземпляр auth для запуска входа
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(this, "Вы вошли", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@Login_screen, Lobby_screen2::class.java)
                    startActivity(intent)

                } else{
                    Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val button1: TextView = findViewById(R.id.textView5)
        button1.setOnClickListener {
            val intent = Intent(this@Login_screen, register_screen::class.java)
            startActivity(intent)
        }
    }
}