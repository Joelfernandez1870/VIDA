package com.example.vida.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

class Chats : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var mensajeAdapter: MensajeAdapter
    private var idUsuario: Int = 1 // ID predeterminado, ajusta según sea necesario
    private var idGrupo: Int = -1 // ID del grupo, se recibirá desde la otra actividad

    private val handler = Handler(Looper.getMainLooper()) // Handler para tareas periódicas
    private val refreshInterval = 5000L // Intervalo de refresco en milisegundos (5 segundos)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats)

        // Obtener el ID del grupo desde el Intent
        idGrupo = intent.getIntExtra("ID_GRUPO", -1)

        if (idGrupo == -1) {
            Toast.makeText(this, "ID de grupo no válido", Toast.LENGTH_SHORT).show()
            finish() // Cierra la actividad si el ID no es válido
            return
        }

        // Inicializar el RecyclerView y el adapter
        recyclerView = findViewById(R.id.recyclerViewChats)
        recyclerView.layoutManager = LinearLayoutManager(this)
        mensajeAdapter = MensajeAdapter()
        recyclerView.adapter = mensajeAdapter

        // Configurar FloatingActionButton para agregar mensajes
        val fabAgregarChat = findViewById<FloatingActionButton>(R.id.fabAgregarChat)
        fabAgregarChat.setOnClickListener {
            val intent = Intent(this, Mensajes::class.java)
            intent.putExtra("idUsuario", idUsuario)
            intent.putExtra("ID_GRUPO", idGrupo) // Pasar también el ID del grupo
            startActivityForResult(intent, 1)
        }

        // Iniciar la carga inicial de mensajes
        cargarMensajes()

        // Configurar refresco periódico
        iniciarRefrescoAutomatico()
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
                    WHERE M.ID_GRUPO = ? -- Filtrar por ID_GRUPO
                    ORDER BY M.FECHA_ENVIO DESC
                """
                preparedStatement = connection?.prepareStatement(query)
                preparedStatement?.setInt(1, idGrupo) // Pasar el ID del grupo

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
                Log.e("Chats", "Error al cargar mensajes: ${ex.message}")
                runOnUiThread {
                    Toast.makeText(this, "Error al cargar los mensajes", Toast.LENGTH_SHORT).show()
                }
            } finally {
                preparedStatement?.close()
                connection?.close()
            }
        }.start()
    }

    private fun iniciarRefrescoAutomatico() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                cargarMensajes()
                handler.postDelayed(this, refreshInterval)
            }
        }, refreshInterval)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null) // Detener el handler para evitar fugas de memoria
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK) {
            cargarMensajes()
        }
    }
}
