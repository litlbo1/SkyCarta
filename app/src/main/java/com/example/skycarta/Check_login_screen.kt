package com.example.skycarta

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Check_login_screen : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_login_screen)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        if (auth.currentUser != null) {
            // Пользователь уже вошел, проверяем его роль
            checkUserRole(auth.currentUser?.uid)
        } else {
            // Пользователь не вошел, перенаправление на экран входа/регистрации
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Закрытие текущей активности
        }
    }

    private fun checkUserRole(userId: String?) {
        userId?.let {
            firestore.collection("users").document(it).get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val role = document.getString("role")
                    if (role == "admin") {
                        val intent = Intent(this, Admin_main_screen::class.java)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, Lobby_screen2::class.java)
                        startActivity(intent)
                    }
                    finish() // Закрытие текущей активности
                } else {
                    Toast.makeText(this, "Ошибка: документ не существует", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Ошибка при получении данных: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
