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

        // Obtener referencias t crear los intent de cada uno

        val btnCargaDonacioness = findViewById<Button>(R.id.btnCargaDonaciones)

        btnCargaDonacioness.setOnClickListener { v: View? ->
            val intent = Intent(this, RegistrarDonacion::class.java)
            startActivity(intent)
        }


        val btnNotificacionUrgente = findViewById<Button>(R.id.btnNotificacionUrgente)

        btnNotificacionUrgente.setOnClickListener { v: View? ->
            val intent = Intent(this, ListadoNotificacionesHospital::class.java)
            startActivity(intent)
        }

        // "Pedidos de donacion"
        val btnListaPedidosDonacion = findViewById<Button>(R.id.btnListaPedidosDonacion)


        btnListaPedidosDonacion.setOnClickListener { v: View? ->
            val intent = Intent(this, ListaPedidosHospitales::class.java)
            startActivity(intent)
        }

        // "Agregar Paciente"
        val btnAgregarPaciente = findViewById<Button>(R.id.btnAgregarPaciente)


        btnAgregarPaciente.setOnClickListener { v: View? ->
            val intent = Intent(this, CargarPaciente::class.java)
            startActivity(intent)
        }

        // "Lista de Pacientes"
        val btnListaPacientes = findViewById<Button>(R.id.btnListaPacientes)


        btnListaPacientes.setOnClickListener { v: View? ->
            val intent = Intent(this, MuestraPaciente::class.java)
            startActivity(intent)
        }

        // "Cerrar Sesión"
        val btnCerrarSesion = findViewById<Button>(R.id.btnCerrarSesionHospital)

        btnCerrarSesion.setOnClickListener { v: View? ->
            val intent = Intent(this@InicioHospitalesYCentros, LoginActivity::class.java)
            startActivity(intent)
            finish() // Finaliza la actividad actual para que no esté en la pila
        }
    }
}
