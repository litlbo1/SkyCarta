package com.example.skycarta.ui.gallery

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.skycarta.MainActivity
import com.example.skycarta.Settings_screen
import com.example.skycarta.databinding.FragmentGalleryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null

    // Используем безопасное обращение к binding (?)
    // и элвис оператор (?:) чтобы избежать NullPointerException.
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val user = auth.currentUser
        user?.let {
            val userId = it.uid // or user.uid

            // Теперь обращаемся к binding, а не binding1
            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val userName = document.data?.get("name").toString()
                        val surName = document.data?.get("surname").toString()
                        val email = document.data?.get("email").toString()
                        // Обновляем текст в TextView через binding
                        binding.nameSurnameProf.text = "$userName $surName"
                        binding.emailProf.text = "$email"
                    } else {
                        binding.nameSurnameProf.text = "No such document"
                    }
                }.addOnFailureListener { e ->
                    binding.nameSurnameProf.text = e.localizedMessage
                }
        }

        binding.settingBtn.setOnClickListener {
            val i = Intent(requireContext(), Settings_screen::class.java)
            startActivity(i)
        }

        binding.textButton2.setOnClickListener{
            signOut()
        }

        val galleryViewModel =
            ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GalleryViewModel::class.java)


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun signOut() {
        // Получаем экземпляр FirebaseAuth
        val auth = FirebaseAuth.getInstance()

        // Выполняем выход пользователя из системы
        auth.signOut()

        // После выхода, сообщаем пользователю об этом
        Toast.makeText(requireContext(), "Вы вышли из системы", Toast.LENGTH_SHORT).show()

        // Перенаправление на экран логина (замените MainActivity на вашу активность входа)
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        // Закрываем активность, которая содержит этот фрагмент
        activity?.finish()
    }
}
