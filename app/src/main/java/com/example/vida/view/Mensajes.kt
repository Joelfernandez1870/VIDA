package com.example.vida.view

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.vida.R
import com.example.vida.data.database.MySqlConexion
import java.sql.Connection
import java.sql.ResultSet
import java.sql.PreparedStatement
import java.text.SimpleDateFormat
import java.util.Locale

class Mensajes : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mensajes)

        // Mostrar la fecha actual en el TextView textFecha
        val textFecha = findViewById<TextView>(R.id.textFecha)
        val fechaActual: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(java.util.Date())
        textFecha.text = fechaActual

        // Llamar a la función para cargar el nombre del usuario
        cargarNombreUsuario()
    }

    private fun cargarNombreUsuario() {
        Thread {
            var connection: Connection? = null
            var resultSet: ResultSet? = null
            var preparedStatement: PreparedStatement? = null

            try {
                connection = MySqlConexion.getConexion()
                val query = "SELECT NOMBRE FROM USUARIO WHERE ID_USUARIO = ?" // Ajusta esto si necesitas un criterio diferente
                preparedStatement = connection?.prepareStatement(query)

                // Establece el ID del usuario (ajusta según tu lógica, aquí se usa un valor fijo como ejemplo)
                preparedStatement?.setInt(1, 1) // Reemplaza 1 con el ID del usuario actual

                resultSet = preparedStatement?.executeQuery()

                if (resultSet?.next() == true) {
                    val nombreUsuario = resultSet.getString("NOMBRE")

                    runOnUiThread {
                        val textNombreUsuario = findViewById<TextView>(R.id.textNombreUsuario)
                        textNombreUsuario.text = nombreUsuario
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (ex: Exception) {
                Log.e("Error", ex.message.toString())
                runOnUiThread {
                    Toast.makeText(this, "Error al conectarse a la base de datos", Toast.LENGTH_SHORT).show()
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
}
