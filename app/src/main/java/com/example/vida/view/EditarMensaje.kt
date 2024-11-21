package com.example.vida.view

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.vida.R
import com.example.vida.data.database.MySqlConexion
import java.sql.Connection
import java.sql.PreparedStatement

class EditarMensaje : AppCompatActivity() {

    private lateinit var inputCuerpoMensaje: EditText
    private var mensajeId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_mensaje)

        val textNombreUsuario = findViewById<TextView>(R.id.textNombreUsuarioEditar)
        val textFecha = findViewById<TextView>(R.id.textFechaEditar)
        inputCuerpoMensaje = findViewById(R.id.inputCuerpoMensajeEditar)
        val btnModificarMensaje = findViewById<Button>(R.id.btnModificarMensaje)

        // Obtener los datos del intent
        mensajeId = intent.getIntExtra("ID_MENSAJE", -1)
        val nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO")
        val fecha = intent.getStringExtra("FECHA")
        val contenido = intent.getStringExtra("CONTENIDO")

        // Verificar datos
        if (mensajeId == -1 || nombreUsuario == null || fecha == null || contenido == null) {
            Toast.makeText(this, "Error al cargar los datos del mensaje", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Cargar datos en la UI
        textNombreUsuario.text = nombreUsuario
        textFecha.text = fecha
        inputCuerpoMensaje.setText(contenido)

        btnModificarMensaje.setOnClickListener {
            modificarMensaje()
        }
    }

    private fun modificarMensaje() {
        val nuevoContenido = inputCuerpoMensaje.text.toString().trim()
        if (nuevoContenido.isEmpty()) {
            Toast.makeText(this, "El contenido no puede estar vacío", Toast.LENGTH_SHORT).show()
            return
        }

        Thread {
            var connection: Connection? = null
            var preparedStatement: PreparedStatement? = null
            try {
                connection = MySqlConexion.getConexion()
                val query = "UPDATE MENSAJES SET CONTENIDO = ? WHERE ID_MENSAJE = ?"
                preparedStatement = connection?.prepareStatement(query)
                preparedStatement?.setString(1, nuevoContenido)
                preparedStatement?.setInt(2, mensajeId)

                val rowsAffected = preparedStatement?.executeUpdate()
                if (rowsAffected != null && rowsAffected > 0) {
                    runOnUiThread {
                        Toast.makeText(this, "Mensaje modificado con éxito", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Error al modificar el mensaje", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (ex: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Error al modificar el mensaje", Toast.LENGTH_SHORT).show()
                }
            } finally {
                preparedStatement?.close()
                connection?.close()
            }
        }.start()
    }
}
