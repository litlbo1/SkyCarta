package com.example.skycarta

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.skycarta.databinding.ActivityLoginScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Login_screen : AppCompatActivity() {

    private lateinit var binding: ActivityLoginScreenBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance() // Инициализировать Firestore
        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.login.setOnClickListener {
            val email = binding.loginEmail.text.toString().trim()
            val password = binding.loginPassword.text.toString().trim()

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    checkUserRole(auth.currentUser?.uid)
                } else {
                    Toast.makeText(this, "Ошибка при входе", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val button1: TextView = findViewById(R.id.textView5)
        button1.setOnClickListener {
            val intent = Intent(this@Login_screen, register_screen::class.java)
            startActivity(intent)
        }
    }

    private fun checkUserRole(userId: String?) {
        userId?.let {
            firestore.collection("users").document(it).get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val role = document.getString("role")
                    if (role == "admin") {
                        val intent = Intent(this@Login_screen, Admin_main_screen::class.java)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this@Login_screen, Lobby_screen2::class.java)
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(this, "Ошибка: документ не существует", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Ошибка при получении данных: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
