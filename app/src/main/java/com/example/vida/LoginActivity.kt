package com.example.vida

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class LoginActivity : AppCompatActivity() {
    private var registerLink: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        registerLink = findViewById(R.id.register_link)

        // Acción para redirigir al presionar el enlace de "Registrarse"
        registerLink?.setOnClickListener(View.OnClickListener { // Redirigir a la nueva actividad de registro
            val intent = Intent(this@LoginActivity, RegistroActivity::class.java)
            startActivity(intent)
        })
    }
}
