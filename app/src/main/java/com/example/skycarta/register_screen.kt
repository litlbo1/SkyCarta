package com.example.skycarta

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.skycarta.databinding.ActivityRegisterScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class register_screen : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterScreenBinding
    private lateinit var auth: FirebaseAuth // Объявить как локальное свойство
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        binding = ActivityRegisterScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.singUp.setOnClickListener{
            val email = binding.editTextTextEmailAddress.text.toString().trim()
            val password = binding.editTextTextPassword.text.toString().trim()
            val name = binding.name.text.toString().trim()
            val surname = binding.surname.text.toString().trim()

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful){
                    val user = auth.currentUser
                    user?.let {
                        // Создаем новый объект для хранения информации о пользователе
                        val userMap = hashMapOf(
                            "email" to email,
                            "password" to password,
                            "name" to name,
                            "surname" to surname
                            // Добавьте другие поля, если нужно
                        )

                        // Добавляем информацию о пользователе в Firestore
                        db.collection("users").document(it.uid).set(userMap)
                            .addOnSuccessListener {
                                // Пользователь добавлен в Firestore
                                Toast.makeText(this, "Аккаунт успешно создан", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@register_screen, Lobby_screen::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                // Обработка ошибки добавления пользователя в Firestore
                                Toast.makeText(this, "Ошибка добавления в Firestore: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                else{
                    Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }

}

