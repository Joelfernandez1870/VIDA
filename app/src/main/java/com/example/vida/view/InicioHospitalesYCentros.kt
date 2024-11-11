package com.example.vida.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.vida.R

class InicioHospitalesYCentros : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio_hospitales_ycentros)


        // Obtener referencia al botón "Carga de Donaciones"
        val btnCargaDonacioness = findViewById<Button>(R.id.btnCargaDonaciones)

// Crear el Intent para iniciar la actividad RegistrarDonacion
        btnCargaDonacioness.setOnClickListener { v: View? ->

            val intent =
                Intent(this, RegistrarDonacion::class.java)
            startActivity(intent)
        }

        // Obtener referencia al botón "Notificaciones Urgentes"
        val btnNotificacionesUrgentes = findViewById<Button>(R.id.btnNotificacionUrgente)

// Crear el Intent para iniciar la actividad Notificaciones Urgentes
        btnNotificacionesUrgentes.setOnClickListener { v: View? ->

            val intent =
                Intent(this, NotificacionesUrgentes::class.java)
            startActivity(intent)
        }

        // Obtener referencia al botón "Pedidos de donacion"
        val btnListaPedidosDonacion = findViewById<Button>(R.id.btnListaPedidosDonacion)

// Crear el Intent para iniciar la actividad Pedidos de donacion del Hospital
        btnListaPedidosDonacion.setOnClickListener { v: View? ->

            val intent =
                Intent(this, ListaPedidosHospitales::class.java)
            startActivity(intent)
        }
    }
}
