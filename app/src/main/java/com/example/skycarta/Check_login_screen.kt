package com.example.skycarta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent

class Check_login_screen : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_login_screen)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            // Пользователь уже вошел, перенаправление на главный экран
            val intent = Intent(this, Lobby_screen2::class.java)
            startActivity(intent)
            finish() // Закрытие текущей активности
        } else {
            // Пользователь не вошел, перенаправление на экран входа/регистрации
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Закрытие текущей активности
//        }
        }
    }
}