package com.example.skycarta

import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.skycarta.databinding.ActivityLobbyScreen2Binding
import com.example.skycarta.databinding.NavHeaderLobbyScreen2Binding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Lobby_screen2 : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityLobbyScreen2Binding
    private lateinit var headerBinding: NavHeaderLobbyScreen2Binding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLobbyScreen2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarLobbyScreen2.toolbar)

        // Установка Navigation Drawer
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_lobby_screen2)
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_gallery, R.id.nav_exit), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Инициализация привязки для header макета в NavigationDrawer
        val headerView = navView.getHeaderView(0)
        headerBinding = NavHeaderLobbyScreen2Binding.bind(headerView)

        // Инициализация Firebase Auth и Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val user = auth.currentUser
        user?.let {
            val userId = user.uid

            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val userName = document.data?.get("name").toString()
                        val surName = document.data?.get("surname").toString()
                        // Обновляем текст в header навигационного меню через headerBinding
                        headerBinding.nameBd.text = "$userName $surName"
                    } else {
                        headerBinding.nameBd.text = "No such document"
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Ошибка при запросе в Firestore: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.lobby_screen2, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_lobby_screen2)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}