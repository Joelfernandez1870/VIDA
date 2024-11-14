package com.example.vida.view

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vida.R
import com.example.vida.data.database.MySqlConexion
import com.example.vida.models.Mensaje
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.sql.Connection
import java.sql.PreparedStatement
import android.content.Intent

class Chats : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var mensajeAdapter: MensajeAdapter
    private var idUsuario: Int = 1 // ID predeterminado, ajusta según sea necesario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats)

        // Inicializar el RecyclerView y el adapter
        recyclerView = findViewById(R.id.recyclerViewChats)
        recyclerView.layoutManager = LinearLayoutManager(this)
        mensajeAdapter = MensajeAdapter()
        recyclerView.adapter = mensajeAdapter

        // Obtener el FloatingActionButton y configurar el clic para abrir Mensajes
        val fabAgregarChat = findViewById<FloatingActionButton>(R.id.fabAgregarChat)
        fabAgregarChat.setOnClickListener {
            // Abrir actividad para enviar mensajes
            val intent = Intent(this, Mensajes::class.java)
            intent.putExtra("idUsuario", idUsuario)
            startActivity(intent)
        }

        // Cargar mensajes
        cargarMensajes()
    }

    private fun cargarMensajes() {
        Thread {
            val mensajes = mutableListOf<Mensaje>()
            var connection: Connection? = null
            var preparedStatement: PreparedStatement? = null

            try {
                connection = MySqlConexion.getConexion()
                val query = """
                    SELECT M.ID_MENSAJE, U.NOMBRE AS NOMBRE_USUARIO, M.CONTENIDO, M.FECHA_ENVIO
                    FROM MENSAJES M
                    JOIN USUARIO U ON M.ID_USUARIO = U.ID_USUARIO
                    ORDER BY M.FECHA_ENVIO DESC
                """
                preparedStatement = connection?.prepareStatement(query)

                val resultSet = preparedStatement?.executeQuery()
                while (resultSet?.next() == true) {
                    val id = resultSet.getInt("ID_MENSAJE")
                    val nombreUsuario = resultSet.getString("NOMBRE_USUARIO")
                    val contenido = resultSet.getString("CONTENIDO")
                    val fechaEnvio = resultSet.getString("FECHA_ENVIO")
                    val mensaje = Mensaje(id, nombreUsuario, contenido, fechaEnvio)
                    mensajes.add(mensaje)
                }

                runOnUiThread {
                    mensajeAdapter.updateMensajes(mensajes)
                }
            } catch (ex: Exception) {
                Log.e("Error", ex.message.toString())
                runOnUiThread {
                    Toast.makeText(this, "Error al cargar los mensajes", Toast.LENGTH_SHORT).show()
                }
            } finally {
                preparedStatement?.close()
                connection?.close()
            }
        }.start()
    }
}
