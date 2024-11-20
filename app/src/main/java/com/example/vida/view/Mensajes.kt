package com.example.vida.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.vida.R
import com.example.vida.data.database.MySqlConexion
import java.sql.Connection
import java.sql.PreparedStatement
import java.text.SimpleDateFormat
import java.util.Locale
import java.sql.ResultSet

class Mensajes : AppCompatActivity() {

    private var idUsuario: Int? = null
    private var fechaActual: String = ""
    private var idGrupo: Int = -1  // Variable para almacenar el ID del grupo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mensajes)

        // Obtener el ID del usuario desde la sesión global
        idUsuario = LoginActivity.sesionGlobal

        // Verificar que el ID del usuario no sea nulo
        if (idUsuario == null) {
            Toast.makeText(this, "ID de usuario no disponible", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Obtener el ID del grupo desde el Intent
        idGrupo = intent.getIntExtra("ID_GRUPO", -1) // Asegúrate de que la clave coincida
        if (idGrupo == -1) {
            Toast.makeText(this, "Error al obtener el ID del grupo", Toast.LENGTH_SHORT).show()
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
                insertarMensaje(idUsuario!!, contenidoMensaje, fechaActual, idGrupo)  // Pasamos el ID del grupo
            } else {
                Toast.makeText(this, "El mensaje no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Función para cargar el nombre del usuario desde la base de datos
    private fun cargarNombreUsuario() {
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
            } catch (e: Exception) {
                Log.e("Mensajes", "Error al cargar el nombre del usuario: ${e.message}")
            } finally {
                resultSet?.close()
                preparedStatement?.close()
                connection?.close()
            }
        }.start()
    }

    // Función para insertar un mensaje en la base de datos
    private fun insertarMensaje(idUsuario: Int, contenido: String, fechaEnvio: String, idGrupo: Int) {
        Thread {
            var connection: Connection? = null
            var preparedStatement: PreparedStatement? = null

            try {
                connection = MySqlConexion.getConexion()

                // Consulta SQL para insertar un nuevo mensaje, ahora con el ID_GRUPO
                val query = """
                    INSERT INTO MENSAJES (ID_USUARIO, CONTENIDO, FECHA_ENVIO, ID_GRUPO) 
                    VALUES (?, ?, ?, ?)
                """
                preparedStatement = connection?.prepareStatement(query)
                preparedStatement?.setInt(1, idUsuario)
                preparedStatement?.setString(2, contenido)
                preparedStatement?.setString(3, fechaEnvio)
                preparedStatement?.setInt(4, idGrupo)  // Se agrega el ID del grupo

                // Ejecutar la inserción del mensaje
                preparedStatement?.executeUpdate()

                // Mostrar mensaje de éxito
                runOnUiThread {
                    Toast.makeText(this, "Mensaje enviado", Toast.LENGTH_SHORT).show()
                    finish()
                }

                // Limpiar el campo de texto del mensaje
                runOnUiThread {
                    findViewById<EditText>(R.id.inputCuerpoMensaje).setText("")
                }
            } catch (e: Exception) {
                Log.e("Mensajes", "Error al insertar mensaje: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this, "Error al enviar el mensaje", Toast.LENGTH_SHORT).show()
                }
            } finally {
                preparedStatement?.close()
                connection?.close()
            }
        }.start()
    }
}
