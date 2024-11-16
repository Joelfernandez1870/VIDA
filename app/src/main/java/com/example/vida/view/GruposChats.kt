package com.example.vida.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vida.R
import com.example.vida.data.database.MySqlConexion
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class GruposChats : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var gruposAdapter: GruposAdapter
    private var gruposList = mutableListOf<String>() // Lista de nombres de grupos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grupos_chats)

        // Inicializamos el RecyclerView
        recyclerView = findViewById(R.id.recyclerViewGrupos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        gruposAdapter = GruposAdapter(gruposList) { grupo ->
            // Acción cuando se hace clic en un grupo
            Toast.makeText(this, "Grupo seleccionado: $grupo", Toast.LENGTH_SHORT).show()

            // Redirigir a la actividad ChatActivity
            val intent = Intent(this, Chats::class.java)
            startActivity(intent) // Inicia la actividad ChatActivity
        }
        recyclerView.adapter = gruposAdapter

        // Configurar FloatingActionButton para agregar un nuevo grupo
        val fabAgregarGrupo = findViewById<FloatingActionButton>(R.id.fabAgregarChat)
        fabAgregarGrupo.setOnClickListener {
            val intent = Intent(this, NuevoGrupo::class.java)
            startActivityForResult(intent, 1) // Iniciar con un código de solicitud
        }

        // Cargar los grupos desde la base de datos
        cargarGrupos()
    }

    // Función para cargar los grupos desde la base de datos
    private fun cargarGrupos() {
        val conn: Connection? = MySqlConexion.getConexion() // Llamar directamente al método getConexion()
        if (conn != null) {
            try {
                val sql = "SELECT NOMBRE_GRUPO FROM GRUPOS_CHAT" // Suponiendo que hay una tabla 'grupos' en la base de datos
                val statement: PreparedStatement = conn.prepareStatement(sql)
                val resultSet: ResultSet = statement.executeQuery()

                // Limpiar la lista antes de agregar los nuevos grupos
                gruposList.clear()

                // Procesar los resultados de la consulta
                while (resultSet.next()) {
                    val nombreGrupo = resultSet.getString("NOMBRE_GRUPO")
                    gruposList.add(nombreGrupo)
                }

                // Notificar al adapter que los datos han cambiado
                runOnUiThread {
                    gruposAdapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                Log.e("GruposChats", "Error al cargar los grupos: $e")
                runOnUiThread {
                    Toast.makeText(this, "Error al cargar los grupos", Toast.LENGTH_SHORT).show()
                }
            } finally {
                try {
                    conn.close()
                } catch (e: Exception) {
                    Log.e("GruposChats", "Error al cerrar la conexión: $e")
                }
            }
        } else {
            runOnUiThread {
                Toast.makeText(this, "No se pudo conectar a la base de datos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Manejar el resultado de la actividad 'NuevoGrupo'
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Cuando se crea un nuevo grupo, recargar la lista de grupos
            cargarGrupos()
        }
    }
}
