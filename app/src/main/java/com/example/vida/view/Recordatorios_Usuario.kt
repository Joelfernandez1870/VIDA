package com.example.vida.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vida.R
import com.example.vida.data.database.MySqlConexion
import com.example.vida.data.database.UsuarioDao
import com.example.vida.models.Recordatorio
import com.example.vida.view.LoginActivity.Companion.sesionGlobalDni
import java.sql.Connection
import java.sql.PreparedStatement

class Recordatorios_Usuario : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var recordatorioAdapter: RecordatorioAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recordatorios_usuario)

        recyclerView = findViewById(R.id.recyclerViewRecordatorios)
        recyclerView.layoutManager = LinearLayoutManager(this)

        cargarRecordatorios()
    }

    override fun onResume() {
        super.onResume()
        cargarRecordatorios()
    }

    private fun cargarRecordatorios() {
        Thread {
            val recordatorios = mutableListOf<Recordatorio>()
            var connection: Connection? = null
            var preparedStatement: PreparedStatement? = null

            val dniUsuario = sesionGlobalDni ?: 0
            val usuarioEncontrado = UsuarioDao.getUsuarioByDni(dniUsuario.toString())

            try {
                connection = MySqlConexion.getConexion()
                val query = """
                    SELECT 
                        r.ID_RECORDATORIO,
                        r.MENSAJE_RECORDATORIO,
                        r.FECHA_ENVIO,
                        r.TIPO_RECORDATORIO
                    FROM 
                        RECORDATORIOS r
                    WHERE 
                        r.ID_USUARIO = ?
                """
                preparedStatement = connection?.prepareStatement(query)
                preparedStatement?.setInt(1, usuarioEncontrado?.id!!)
                val resultSet = preparedStatement?.executeQuery()

                while (resultSet?.next() == true) {
                    val idRecordatorio = resultSet.getInt("ID_RECORDATORIO")
                    val mensajeRecordatorio = resultSet.getString("MENSAJE_RECORDATORIO")
                    val fechaEnvio = resultSet.getString("FECHA_ENVIO")
                    val tipoRecordatorio = resultSet.getString("TIPO_RECORDATORIO")
                    recordatorios.add(Recordatorio(idRecordatorio, mensajeRecordatorio, fechaEnvio, tipoRecordatorio))
                }

                runOnUiThread {
                    recordatorioAdapter = RecordatorioAdapter(recordatorios) { recordatorio ->
                        confirmarEliminacion(recordatorio)
                    }
                    recyclerView.adapter = recordatorioAdapter
                }
            } catch (ex: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Error al cargar los recordatorios", Toast.LENGTH_SHORT).show()
                }
            } finally {
                preparedStatement?.close()
                connection?.close()
            }
        }.start()
    }

    private fun confirmarEliminacion(recordatorio: Recordatorio) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Recordatorio")
            .setMessage("¿Estás seguro de que deseas eliminar este recordatorio?")
            .setPositiveButton("Sí") { _, _ ->
                eliminarRecordatorio(recordatorio)
            }
            .setNegativeButton("No") { _, _ ->
                // Recargar los recordatorios para asegurarte de que la vista está actualizada
                cargarRecordatorios()
            }
            .show()
    }

    private fun eliminarRecordatorio(recordatorio: Recordatorio) {
        Thread {
            var connection: Connection? = null
            var preparedStatement: PreparedStatement? = null

            try {
                connection = MySqlConexion.getConexion()
                val query = "DELETE FROM RECORDATORIOS WHERE ID_RECORDATORIO = ?"
                preparedStatement = connection?.prepareStatement(query)
                preparedStatement?.setInt(1, recordatorio.id_usuario)
                preparedStatement?.executeUpdate()

                runOnUiThread {
                    Toast.makeText(this, "Recordatorio eliminado", Toast.LENGTH_SHORT).show()
                    cargarRecordatorios() // Recargar la lista tras eliminar
                }
            } catch (ex: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Error al eliminar el recordatorio", Toast.LENGTH_SHORT).show()
                }
            } finally {
                preparedStatement?.close()
                connection?.close()
            }
        }.start()
    }
}
