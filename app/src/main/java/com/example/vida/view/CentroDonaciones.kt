package com.example.vida.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.vida.R

class CentroDonaciones : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_centro_donaciones)

        // Obtener referencia al botón
        val verMapaButton = findViewById<Button>(R.id.btnVerMapa1)

        // Establecer listener para el botón
        verMapaButton.setOnClickListener {
            // Crear Intent para abrir Google Maps
            val locationUri = Uri.parse("geo:0,0?q=hospitales+donación+sangre")
            val intent = Intent(Intent.ACTION_VIEW, locationUri)
            intent.setPackage("com.google.android.apps.maps")

            // Verificar si hay una app que pueda manejar el Intent
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                // Si no se encuentra Google Maps, mostrar un mensaje en el Logcat
                Log.e(
                    "centros_donaciones",
                    "No se encontró ninguna aplicación para manejar el Intent de Maps."
                )
            }
        }
    }
}