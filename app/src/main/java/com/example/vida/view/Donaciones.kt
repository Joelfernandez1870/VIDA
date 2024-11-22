package com.example.vida.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vida.R
import com.example.vida.data.database.HospitalCentroDao
import com.example.vida.data.database.MySqlConexion
import com.example.vida.data.database.UsuarioDao
import com.example.vida.models.Donacion
import com.example.vida.view.LoginActivity.Companion.sesionGlobal
import com.example.vida.view.LoginActivity.Companion.sesionGlobalDni
import java.sql.Connection
import java.sql.PreparedStatement

class Donaciones : AppCompatActivity() {

    private var idUsuario: Int? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var donacionAdapter: DonacionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donaciones)

        idUsuario = LoginActivity.sesionGlobal

        if (idUsuario == null) {
            Toast.makeText(this, "ID de usuario no disponible", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Inicializar el RecyclerView
        recyclerView = findViewById(R.id.recyclerViewDonaciones)
        recyclerView.layoutManager = LinearLayoutManager(this)
        cargarDonaciones()
    }

    override fun onResume() {
        super.onResume()
        // Cargar las donaciones
        cargarDonaciones()
    }

    private fun cargarDonaciones() {
        Thread {
            val donaciones = mutableListOf<Donacion>()
            var connection: Connection? = null
            var preparedStatement: PreparedStatement? = null

            val iDUsuario = sesionGlobal ?: 0

//            val usuarioEncontrado = UsuarioDao.getUsuarioByDni(sesionGlobalDni.toString())


            try {
                // Establecer la conexión con la base de datos
                connection = MySqlConexion.getConexion()

                // Realizar la consulta SQL para obtener las donaciones de un usuario
                val query = """
                    SELECT 
                        r.ID_DONACION,
                        r.ID_USUARIO,
                        r.ID_HOSPITALES_CENTRO,
                        r.FECHA_DONACION,
                        r.TIPO_DE_DONACION
                    FROM 
                        DONACIONES r
                    WHERE 
                        r.ID_USUARIO = ?
                """
                preparedStatement = connection?.prepareStatement(query)

                preparedStatement?.setString(1, iDUsuario.toString())

                val resultSet = preparedStatement?.executeQuery()
                while (resultSet?.next() == true) {
                    val iDUsuario = resultSet.getString("ID_USUARIO")
                    val idHospital = resultSet.getString("ID_HOSPITALES_CENTRO")
                    val fecha = resultSet.getString("FECHA_DONACION")
                    val tipoDonacion = resultSet.getString("TIPO_DE_DONACION")

                    val NombreHospital = HospitalCentroDao.getHospitalById(idHospital.toInt())?.nombreLugar ?: "Hospital desconocido"
                    // Crear objeto Donacion
                    val donacion = Donacion(iDUsuario, NombreHospital, fecha, tipoDonacion)
                    donaciones.add(donacion)
                }

                runOnUiThread {
                    donacionAdapter = DonacionAdapter(donaciones)
                    recyclerView.adapter = donacionAdapter
                }
            } catch (ex: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Error al cargar las donaciones", Toast.LENGTH_SHORT).show()
                }
            } finally {
                // Cerrar conexiones
                preparedStatement?.close()
                connection?.close()
            }
        }.start()
    }
}
