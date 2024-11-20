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
    private var gruposList = mutableListOf<Pair<Int, String>>() // Lista de pares (ID_GRUPO, NOMBRE_GRUPO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grupos_chats)

        // Inicializamos el RecyclerView
        recyclerView = findViewById(R.id.recyclerViewGrupos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        gruposAdapter = GruposAdapter(gruposList.map { it.second }) { grupo ->
            // Acción cuando se selecciona un grupo
            val idGrupoSeleccionado = obtenerIdGrupoPorNombre(grupo)

            if (idGrupoSeleccionado != -1) {
                mostrarToast("Grupo seleccionado: $grupo", Toast.LENGTH_SHORT)

                // Pasar el ID_GRUPO al Intent
                val intent = Intent(this, Chats::class.java)
                intent.putExtra("ID_GRUPO", idGrupoSeleccionado) // Pasamos el ID_GRUPO al Intent
                startActivity(intent) // Inicia la actividad Chats
            } else {
                mostrarToast("Error al obtener el ID del grupo", Toast.LENGTH_SHORT)
            }
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
        val conn: Connection? = MySqlConexion.getConexion()
        if (conn != null) {
            try {
                val sql = "SELECT ID_GRUPO, NOMBRE_GRUPO FROM GRUPOS_CHAT"
                val statement: PreparedStatement = conn.prepareStatement(sql)
                val resultSet: ResultSet = statement.executeQuery()

                // Limpiar la lista antes de agregar los nuevos grupos
                gruposList.clear()

                // Procesar los resultados de la consulta
                while (resultSet.next()) {
                    val idGrupo = resultSet.getInt("ID_GRUPO")
                    val nombreGrupo = resultSet.getString("NOMBRE_GRUPO")
                    gruposList.add(Pair(idGrupo, nombreGrupo))
                }

                // Notificar al adapter que los datos han cambiado
                runOnUiThread {
                    gruposAdapter.updateData(gruposList.map { it.second })
                }
            } catch (e: Exception) {
                Log.e("GruposChats", "Error al cargar los grupos: $e")
                runOnUiThread {
                    mostrarToast("Error al cargar los grupos", Toast.LENGTH_LONG)
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
                mostrarToast("No se pudo conectar a la base de datos", Toast.LENGTH_LONG)
            }
        }
    }

    // Función para obtener el ID del grupo por el nombre del grupo
    private fun obtenerIdGrupoPorNombre(nombreGrupo: String): Int {
        var idGrupo = -1 // Valor por defecto si no se encuentra

        val conn: Connection? = MySqlConexion.getConexion()
        if (conn != null) {
            try {
                val sql = "SELECT ID_GRUPO FROM GRUPOS_CHAT WHERE UPPER(NOMBRE_GRUPO) = UPPER(?)"
                val statement: PreparedStatement = conn.prepareStatement(sql)
                statement.setString(1, nombreGrupo.trim())
                val resultSet: ResultSet = statement.executeQuery()

                if (resultSet.next()) {
                    idGrupo = resultSet.getInt("ID_GRUPO")
                } else {
                    Log.d("GruposChats", "No se encontró el grupo: $nombreGrupo")
                }
            } catch (e: Exception) {
                Log.e("GruposChats", "Error al obtener ID del grupo: $e")
            } finally {
                try {
                    conn.close()
                } catch (e: Exception) {
                    Log.e("GruposChats", "Error al cerrar la conexión: $e")
                }
            }
        } else {
            mostrarToast("No se pudo conectar a la base de datos", Toast.LENGTH_LONG)
        }

        return idGrupo
    }

    // Mostrar Toast con más control
    private fun mostrarToast(mensaje: String, duracion: Int) {
        val toast = Toast.makeText(this, mensaje, duracion)
        toast.setGravity(android.view.Gravity.BOTTOM, 0, 100)  // Ajusta la posición si es necesario
        toast.show()
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
