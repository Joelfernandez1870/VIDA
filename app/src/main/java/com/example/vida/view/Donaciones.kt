package com.example.vida.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.vida.R

class Donaciones : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donaciones)



        //EL SIGUIENTE BOTON "+"CREO QUE DEBERIA ESTAR EN HOPITALES TE REDIRIGE AL AGREGAR DONACION Y EL USUARIO SOLO DEBERIA VER LA DONACION!!

        // Encontramos el FloatingActionButton para agregar donaciones
        val fabAgregarDonacion = findViewById<FloatingActionButton>(R.id.fabAgregarDonacion)

        // Agregamos un listener para redirigir a la actividad de Registrar Donacion
        fabAgregarDonacion.setOnClickListener { // Crear el Intent para iniciar la actividad RegistrarDonacion
            val intent = Intent(
                this@Donaciones,
                RegistrarDonacion::class.java
            )
            startActivity(intent)
        }
    }
}