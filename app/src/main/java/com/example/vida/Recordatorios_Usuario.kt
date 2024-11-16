package com.example.vida

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vida.data.database.MySqlConexion
import com.example.vida.data.database.UsuarioDao
import com.example.vida.models.Recordatorio
import com.example.vida.view.LoginActivity.Companion.sesionGlobalDni
import com.example.vida.view.RecordatorioAdapter
import java.sql.Connection
import java.sql.PreparedStatement

class Recordatorios_Usuario : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var recordatorioAdapter: RecordatorioAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recordatorios_usuario)

        // Inicializar el RecyclerView
        recyclerView = findViewById(R.id.recyclerViewRecordatorios)
        recyclerView.layoutManager = LinearLayoutManager(this) // Puedes usar otro LayoutManager si prefieres

        // Cargar los recordatorios al reanudar la actividad
        cargarRecordatorios()
    }

    override fun onResume() {
        super.onResume()
        // Cargar los recordatorios al reanudar la actividad
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
                // Establecer la conexión con la base de datos
                connection = MySqlConexion.getConexion()

                // Realizar la consulta SQL para obtener los recordatorios de un usuario
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
                preparedStatement?.setInt(1, usuarioEncontrado?.id!!)  // Asumiendo que idUsuario es el ID del usuario actual

                val resultSet = preparedStatement?.executeQuery()
                while (resultSet?.next() == true) {
                    val idRecordatorio = resultSet.getInt("ID_RECORDATORIO")
                    val mensajeRecordatorio = resultSet.getString("MENSAJE_RECORDATORIO")
                    val fechaEnvio = resultSet.getString("FECHA_ENVIO")
                    val tipoRecordatorio = resultSet.getString("TIPO_RECORDATORIO")

                    // Crear objeto Recordatorio
                    val recordatorio = Recordatorio(idRecordatorio, mensajeRecordatorio, fechaEnvio, tipoRecordatorio)
                    recordatorios.add(recordatorio)
                }

                runOnUiThread {
                    // Actualizar el RecyclerView con los recordatorios cargados
                    recordatorioAdapter = RecordatorioAdapter(recordatorios)
                    recyclerView.adapter = recordatorioAdapter
                }
            } catch (ex: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Error al cargar los recordatorios", Toast.LENGTH_SHORT).show()
                }
            } finally {
                // Cerrar conexiones
                preparedStatement?.close()
                connection?.close()
            }
        }.start()
    }
}