package com.example.vida.view


import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
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
    private lateinit var searchEditText: EditText
    private var mensajes: MutableList<Mensaje> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats)

        recyclerView = findViewById(R.id.recyclerViewChats)
        recyclerView.layoutManager = LinearLayoutManager(this)
        mensajeAdapter = MensajeAdapter()
        recyclerView.adapter = mensajeAdapter

        searchEditText = findViewById(R.id.searchEditText)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filtrarMensajesPorNombre(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        val fabAgregarChat = findViewById<FloatingActionButton>(R.id.fabAgregarChat)
        fabAgregarChat.setOnClickListener {
            val intent = Intent(this, Mensajes::class.java)
            startActivity(intent)
        }

        cargarMensajes() // Carga inicial de mensajes
    }

    override fun onResume() {
        super.onResume()
        cargarMensajes() // Recargar mensajes al reanudar la actividad
    }

    private fun cargarMensajes() {
        Thread {
            mensajes.clear()
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
                    val mensaje = Mensaje(
                        resultSet.getInt("ID_MENSAJE"),
                        resultSet.getString("NOMBRE_USUARIO"),
                        resultSet.getString("CONTENIDO"),
                        resultSet.getString("FECHA_ENVIO")
                    )
                    mensajes.add(mensaje)
                }

                runOnUiThread {
                    mensajeAdapter.updateMensajes(mensajes) // Actualización directa
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

    private fun filtrarMensajesPorNombre(nombre: String) {
        val mensajesFiltrados = if (nombre.isEmpty()) {
            mensajes // Si el nombre está vacío, no hay filtro
        } else {
            mensajes.filter { it.nombreUsuario?.contains(nombre, ignoreCase = true) == true }
        }
        mensajeAdapter.updateMensajes(mensajesFiltrados)
    }
}
