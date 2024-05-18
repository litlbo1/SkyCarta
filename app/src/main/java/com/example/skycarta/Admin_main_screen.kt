package com.example.skycarta

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.skycarta.databinding.ActivityAdminMainScreenBinding

class Admin_main_screen : AppCompatActivity() {

    lateinit var binding: ActivityAdminMainScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminMainScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.adminMessagesBtn.setOnClickListener {
            val intent = Intent(this, Messages_users_screen::class.java)
            startActivity(intent)
        }

        binding.adminInviteBtn.setOnClickListener {
            val intent2 = Intent(this, Admin_invite_screen::class.java)
            startActivity(intent2)
        }

    }
}