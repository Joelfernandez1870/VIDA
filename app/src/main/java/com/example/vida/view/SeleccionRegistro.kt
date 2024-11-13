package com.example.vida.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.vida.R

class SeleccionRegistro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_seleccion_registro) // Asegúrate de que este es el layout correcto

        // Obtener referencia al botón "Usuario Particular"
        val btnUsuarioParticular = findViewById<Button>(R.id.btnUsuarioParticular)

        // Configurar el OnClickListener para iniciar la actividad RegistrarUsuarioActivity
        btnUsuarioParticular.setOnClickListener {
            val intent = Intent(this, RegistrarUsuarioActivity::class.java)
            startActivity(intent)
        }

        // Obtener referencia al botón "Hospital"
        val btnHospital = findViewById<Button>(R.id.btnHospital)

        // Configurar el OnClickListener para iniciar la actividad RegistrarHospitalActivity
        btnHospital.setOnClickListener {
            val intent = Intent(this, RegistrarHospitalActivity::class.java)
            startActivity(intent)
        }
    }
}
