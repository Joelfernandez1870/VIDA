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
import java.sql.ResultSet
import java.text.SimpleDateFormat
import java.util.Locale

class Mensajes : AppCompatActivity() {

    private var idUsuario: Int? = null
    private var fechaActual: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mensajes)

        // Obtener el ID del usuario desde la sesión global
        idUsuario = LoginActivity.sesionGlobal

        // Verificar que el ID del usuario no sea nulo
        if (idUsuario == null) {
            Toast.makeText(this, "ID de usuario no disponible", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Establecer la fecha actual
        val textFecha = findViewById<TextView>(R.id.textFecha)
        fechaActual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(java.util.Date())
        textFecha.text = fechaActual

        // Cargar el nombre del usuario
        cargarNombreUsuario()

        // Configurar el botón de enviar mensaje
        val btnEnviar = findViewById<Button>(R.id.btnEnviar)
        btnEnviar.setOnClickListener {
            val inputCuerpoMensaje = findViewById<EditText>(R.id.inputCuerpoMensaje)
            val contenidoMensaje = inputCuerpoMensaje.text.toString()

            // Verificar que el mensaje no esté vacío
            if (contenidoMensaje.isNotBlank()) {
                insertarMensaje(idUsuario!!, contenidoMensaje, fechaActual)
            } else {
                Toast.makeText(this, "El mensaje no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Función para cargar el nombre del usuario desde la base de datos
    private fun cargarNombreUsuario() {
        // Ejecutar en un hilo separado para evitar bloquear el hilo principal
        Thread {
            var connection: Connection? = null
            var preparedStatement: PreparedStatement? = null
            var resultSet: ResultSet? = null

            try {
                // Establecer la conexión con la base de datos
                connection = MySqlConexion.getConexion()

                // Consulta para obtener el nombre del usuario
                val query = "SELECT NOMBRE FROM USUARIO WHERE ID_USUARIO = ?"
                preparedStatement = connection?.prepareStatement(query)
                preparedStatement?.setInt(1, idUsuario ?: return@Thread)

                // Ejecutar la consulta y obtener el resultado
                resultSet = preparedStatement?.executeQuery()
                if (resultSet?.next() == true) {
                    val nombreUsuario = resultSet.getString("NOMBRE")

                    // Actualizar la interfaz de usuario en el hilo principal
                    runOnUiThread {
                        val textNombreUsuario = findViewById<TextView>(R.id.textNombreUsuario)
                        textNombreUsuario.text = "Bienvenido, $nombreUsuario"
                    }
                } else {
                    // Si no se encuentra el usuario
                    runOnUiThread {
                        Toast.makeText(this, "No se encontró el usuario", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (ex: Exception) {
                Log.e("Error", ex.message.toString())
                runOnUiThread {
                    Toast.makeText(this, "Error al cargar el nombre del usuario", Toast.LENGTH_SHORT).show()
                }
            } finally {
                try {
                    resultSet?.close()
                    preparedStatement?.close()
                    connection?.close()
                } catch (ex: Exception) {
                    Log.e("Error closing", ex.message.toString())
                }
            }
        }.start()
    }

    // Función para insertar un mensaje en la base de datos
    private fun insertarMensaje(idUsuario: Int, contenido: String, fechaEnvio: String) {
        Thread {
            var connection: Connection? = null
            var preparedStatement: PreparedStatement? = null

            try {
                // Establecer la conexión con la base de datos
                connection = MySqlConexion.getConexion()

                // Consulta para insertar el mensaje
                val query = "INSERT INTO MENSAJES (ID_USUARIO, CONTENIDO, FECHA_ENVIO) VALUES (?, ?, ?)"
                preparedStatement = connection?.prepareStatement(query)
                preparedStatement?.setInt(1, idUsuario)
                preparedStatement?.setString(2, contenido)
                preparedStatement?.setString(3, fechaEnvio)

                // Ejecutar la consulta
                val rowsInserted = preparedStatement?.executeUpdate()

                if (rowsInserted != null && rowsInserted > 0) {
                    // Si el mensaje se inserta correctamente
                    runOnUiThread {
                        Toast.makeText(this, "Mensaje enviado", Toast.LENGTH_SHORT).show()
                        val inputCuerpoMensaje = findViewById<EditText>(R.id.inputCuerpoMensaje)
                        inputCuerpoMensaje.text.clear()

                        // Enviar un broadcast para notificar a otras actividades
                        val intent = Intent("com.example.vida.REFRESCAR_CHAT")
                        sendBroadcast(intent)

                        // Cerrar la actividad Mensajes
                        finish()
                    }
                } else {
                    // Si hubo un error al insertar el mensaje
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
