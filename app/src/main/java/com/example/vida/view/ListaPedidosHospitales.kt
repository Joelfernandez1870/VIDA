package com.example.vida.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.vida.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ListaPedidosHospitales : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lista_pedidos_hospitales)

        // Obtener el botón FloatingActionButton por su ID
        val fabAgregarPedido = findViewById<FloatingActionButton>(R.id.fabAgregarPedido)

        // Configurar el OnClickListener para redirigir a la actividad RegistrarPedidoHospital
        fabAgregarPedido.setOnClickListener {
            val intent = Intent(this, RegistrarPedidoHospital::class.java)
            startActivity(intent)
        }
    }
}
