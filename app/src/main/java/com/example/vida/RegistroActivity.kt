package com.example.vida

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class RegistroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Cargar el layout del registro
        setContentView(R.layout.activity_registro)

        // Captura el botón de Usuario Particular
        val btnUsuarioParticular = findViewById<Button>(R.id.btnUsuarioParticular)
        // Configura el evento click para redirigir a la actividad de registro de usuario particular
        btnUsuarioParticular.setOnClickListener {
            val intent = Intent(this@RegistroActivity, RegistrarUsuarioActivity::class.java)
            startActivity(intent)
        }

        // Captura el botón de Hospital
        val btnHospital = findViewById<Button>(R.id.btnHospital)
        // Configura el evento click para redirigir a la actividad de registro de hospital
        btnHospital.setOnClickListener {
            val intent = Intent(this@RegistroActivity, RegistrarHospitalActivity::class.java)
            startActivity(intent)
        }
    }
}
