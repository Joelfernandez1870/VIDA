package com.example.vida.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.vida.R
import com.example.vida.data.database.MySqlConexion
import java.sql.Connection
import java.sql.PreparedStatement
import java.text.SimpleDateFormat
import java.util.Locale

class Mensajes : AppCompatActivity() {

    private var idUsuario: Int? = null
    private var fechaActual: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mensajes)

        idUsuario = LoginActivity.sesionGlobal

        if (idUsuario == null) {
            Toast.makeText(this, "ID de usuario no disponible", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val textFecha = findViewById<TextView>(R.id.textFecha)
        fechaActual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(java.util.Date())
        textFecha.text = fechaActual

        cargarNombreUsuario()

        val btnEnviar = findViewById<Button>(R.id.btnEnviar)
        btnEnviar.setOnClickListener {
            val inputCuerpoMensaje = findViewById<EditText>(R.id.inputCuerpoMensaje)
            val contenidoMensaje = inputCuerpoMensaje.text.toString()

            if (contenidoMensaje.isNotBlank()) {
                insertarMensaje(idUsuario!!, contenidoMensaje, fechaActual)
            } else {
                Toast.makeText(this, "El mensaje no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarNombreUsuario() {
        // Implementación sin cambios...
    }

    private fun insertarMensaje(idUsuario: Int, contenido: String, fechaEnvio: String) {
        Thread {
            var connection: Connection? = null
            var preparedStatement: PreparedStatement? = null

            try {
                connection = MySqlConexion.getConexion()
                val query = "INSERT INTO MENSAJES (ID_USUARIO, CONTENIDO, FECHA_ENVIO) VALUES (?, ?, ?)"
                preparedStatement = connection?.prepareStatement(query)
                preparedStatement?.setInt(1, idUsuario)
                preparedStatement?.setString(2, contenido)
                preparedStatement?.setString(3, fechaEnvio)

                val rowsInserted = preparedStatement?.executeUpdate()

                if (rowsInserted != null && rowsInserted > 0) {
                    runOnUiThread {
                        Toast.makeText(this, "Mensaje enviado", Toast.LENGTH_SHORT).show()
                        val inputCuerpoMensaje = findViewById<EditText>(R.id.inputCuerpoMensaje)
                        inputCuerpoMensaje.text.clear()

                        // Enviar broadcast para notificar a la actividad "Chats"
                        val intent = Intent("com.example.vida.REFRESCAR_CHAT")
                        sendBroadcast(intent)

                        // Cerrar la actividad Mensajes
                        finish()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Error al enviar el mensaje", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (ex: Exception) {
                Log.e("Error", ex.message.toString())
                runOnUiThread {
                    Toast.makeText(this, "Error al conectarse a la base de datos", Toast.LENGTH_SHORT).show()
                }
            } finally {
                try {
                    preparedStatement?.close()
                    connection?.close()
                } catch (ex: Exception) {
                    Log.e("Error closing", ex.message.toString())
                }
            }
        }.start()
    }
}
