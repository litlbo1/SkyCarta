package com.example.skycarta

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.skycarta.databinding.ActivitySettingsScreenBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Settings_screen : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsScreenBinding
    private lateinit var auth: FirebaseAuth
    private var selectedColor: Int = Color.BLUE // Начальный цвет по умолчанию
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.deleteAccount.setOnClickListener {
            showDeleteAccountDialog()
        }

        binding.changePassBtn.setOnClickListener {
            showResetPasswordDialog()
        }
    }

    private fun showResetPasswordDialog() {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialog_change_password, null)

        val emailEditText = view.findViewById<EditText>(R.id.editTextEmail)
        val sendButton = view.findViewById<Button>(R.id.buttonSendResetEmail)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Сбросить пароль")
            .setView(view)
            .setNegativeButton("Отмена", null)
            .create()

        sendButton.setOnClickListener {
            val email = emailEditText.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, введите электронную почту", Toast.LENGTH_SHORT).show()
            } else {
                sendPasswordResetEmail(email)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Письмо для сброса пароля отправлено на $email", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Ошибка отправки письма: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun showDeleteAccountDialog() {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialog_confirm_delete, null)
        val passwordEditText = view.findViewById<EditText>(R.id.editTextPassword)

        AlertDialog.Builder(this)
            .setTitle("Удаление аккаунта")
            .setView(view)
            .setPositiveButton("Да") { dialog, _ ->
                val password = passwordEditText.text.toString()
                if (password.isNotEmpty()) {
                    reauthenticateAndDeleteAccount(password)
                } else {
                    Toast.makeText(this, "Пожалуйста, введите пароль", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun reauthenticateAndDeleteAccount(password: String) {
        val user = auth.currentUser
        user?.let {
            val email = user.email
            if (email != null) {
                val credential = EmailAuthProvider.getCredential(email, password)

                user.reauthenticate(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            deleteAccount()
                        } else {
                            Toast.makeText(this, "Переавторизация не удалась: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Не удалось получить email пользователя для переавторизации", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteAccount() {
        val user = auth.currentUser
        user?.let {
            val uid = it.uid
            // Удаление данных пользователя из Firestore
            firestore.collection("users").document(uid).delete()
                .addOnSuccessListener {
                    user.delete()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Успешно удален аккаунт
                                Toast.makeText(this, "Аккаунт успешно удален" , Toast.LENGTH_SHORT).show()
                                val i = Intent(this@Settings_screen, MainActivity::class.java)
                                startActivity(i)
                            } else {
                                // Ошибка при удалении аккаунта
                                Toast.makeText(this, "Ошибка при удалении аккаунта", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Ошибка при удалении данных: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
