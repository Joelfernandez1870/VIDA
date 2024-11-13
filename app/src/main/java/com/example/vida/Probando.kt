package com.example.vida

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.vida.view.RegistrarHospitalActivity
import com.example.vida.view.RegistrarUsuarioActivity


class Probando : AppCompatActivity() {

    private lateinit var btnUsuario: Button
    private lateinit var btnHospital: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_probando)

        btnHospital= findViewById(R.id.btnHospital)
        btnUsuario = findViewById(R.id.btnUsuario)

        // Configurar el botón para redirigir
        btnHospital.setOnClickListener { RedireccionHospital() }
        btnUsuario.setOnClickListener { RedireccionUsuario() }

    }
    private fun RedireccionHospital() {
        val intent = Intent(this@Probando, RegistrarHospitalActivity::class.java)
        startActivity(intent)
    }

    private fun RedireccionUsuario() {
        val intent = Intent(this, RegistrarUsuarioActivity::class.java)
        startActivity(intent)
    }
}
