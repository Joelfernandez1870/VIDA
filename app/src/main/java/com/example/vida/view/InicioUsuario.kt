package com.example.vida.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.vida.R
import com.example.vida.Recordatorios_Usuario
import com.example.vida.data.database.NotificacionUrgenteDao

class InicioUsuario : AppCompatActivity() {

    private val usuarioLogeadoId: Int? = LoginActivity.sesionGlobal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_usuario)

        // Verificar si el usuario está logueado
        if (usuarioLogeadoId == null || usuarioLogeadoId == 0) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Consultar las notificaciones urgentes
        val notificacionesUrgentes = obtenerNotificacionesUrgentes()

        // Configurar el badge de notificaciones
        val btnNotificaciones = findViewById<ImageButton>(R.id.btnUrgencias)
        val notificationBadge = findViewById<TextView>(R.id.notificationBadge)

        if (notificacionesUrgentes > 0) {
            notificationBadge.visibility = View.VISIBLE
            notificationBadge.text = notificacionesUrgentes.toString()
        } else {
            notificationBadge.visibility = View.GONE
        }

        // Configurar el click listener para el botón de notificaciones
        btnNotificaciones.setOnClickListener {
            val intent = Intent(this, ListadoNotificaciones::class.java)
            startActivity(intent)
        }

        // Configurar otros botones
        findViewById<Button>(R.id.btnHospitalesMap).setOnClickListener {
            startActivity(Intent(this, HospitalesMapActivity::class.java))
        }

        findViewById<Button>(R.id.btnRecordatorio).setOnClickListener {
            startActivity(Intent(this, Recordatorios_Usuario::class.java))
        }

        findViewById<Button>(R.id.btnDonaciones).setOnClickListener {
            startActivity(Intent(this, Donaciones::class.java))
        }

        findViewById<Button>(R.id.btnBeneficios).setOnClickListener {
            startActivity(Intent(this, Beneficios::class.java))
        }

        findViewById<Button>(R.id.btnChats).setOnClickListener {
            startActivity(Intent(this, GruposChats::class.java))
        }

        findViewById<Button>(R.id.btnListaPedidos).setOnClickListener {
            startActivity(Intent(this, ListaPedidosUsuario::class.java))
        }

        findViewById<Button>(R.id.btnCerrarSesion).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun obtenerNotificacionesUrgentes(): Int {
        return NotificacionUrgenteDao.getCantidadNotificacionesUrgentes()
    }
}
