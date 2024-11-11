package com.example.vida.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.vida.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Chats : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chats)

        // Obtener el botón FloatingActionButton por su ID
        val fabAgregarChat = findViewById<FloatingActionButton>(R.id.fabAgregarChat)

        // Configurar el OnClickListener para redirigir a la actividad Agregar chat
        fabAgregarChat.setOnClickListener {
            val intent = Intent(this, Mensajes::class.java)
            startActivity(intent)
        }
        }
    }
