package com.example.skycarta

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.skycarta.databinding.ActivityNavExitBinding
import com.google.firebase.auth.FirebaseAuth

class nav_exit : AppCompatActivity() {
    private lateinit var binding: ActivityNavExitBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavExitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogout.setOnClickListener {
            signOutUser()
        }
    }

    private fun signOutUser() {
        // Получаем экземпляр FirebaseAuth
        val auth = FirebaseAuth.getInstance()

        // Выполняем выход пользователя из системы
        auth.signOut()

        // После выхода, вы можете перенаправить пользователя на экран логина или выполнить другие действия
        Toast.makeText(this, "Вы вышли из системы", Toast.LENGTH_SHORT).show()

        // Перенаправление на экран логина (пример, замените MainActivity своей активностью логина)
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}