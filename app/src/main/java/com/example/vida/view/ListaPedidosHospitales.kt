package com.example.vida.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vida.R
import com.example.vida.data.database.MySqlConexion
import com.example.vida.models.PedidoHospital
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.sql.Connection
import java.sql.PreparedStatement

class ListaPedidosHospitales : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var pedidoHospitalAdapter: PedidoHospitalAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lista_pedidos_hospitales)

        recyclerView = findViewById(R.id.recyclerViewPedidosHospitales)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Configurar FloatingActionButton para agregar un nuevo pedido
        val fabAgregarPedido = findViewById<FloatingActionButton>(R.id.fabAgregarPedido)
        fabAgregarPedido.setOnClickListener {
            val intent = Intent(this, RegistrarPedidoHospital::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Recargar los datos cada vez que la actividad vuelva al primer plano
        cargarPedidosHospitales()
    }

    private fun cargarPedidosHospitales() {
        Thread {
            val pedidos = mutableListOf<PedidoHospital>()
            var connection: Connection? = null
            var preparedStatement: PreparedStatement? = null

            try {
                connection = MySqlConexion.getConexion()
                val query = """
                    SELECT 
                        p.ID_EMERGENCIA,
                        pa.NOMBRE AS NOMBRE_PACIENTE,
                        hc.NOMBRE_LUGAR AS NOMBRE_HOSPITAL,
                        p.DESCRIPCION,
                        p.FECHA,
                        p.ESTADO
                    FROM 
                        PEDIDO_DONACION p
                    JOIN 
                        PACIENTE pa ON p.ID_PACIENTE = pa.ID_PACIENTE
                    JOIN 
                        HOSPITALES_CENTROS hc ON p.ID_HOSPITALES_CENTRO = hc.ID_HOSPITALES_CENTRO
                """
                preparedStatement = connection?.prepareStatement(query)

                val resultSet = preparedStatement?.executeQuery()
                while (resultSet?.next() == true) {
                    val idEmergencia = resultSet.getInt("ID_EMERGENCIA")
                    val nombrePaciente = resultSet.getString("NOMBRE_PACIENTE")
                    val nombreHospital = resultSet.getString("NOMBRE_HOSPITAL")
                    val descripcion = resultSet.getString("DESCRIPCION")
                    val fecha = resultSet.getString("FECHA")
                    val estado = resultSet.getString("ESTADO")

                    val pedido = PedidoHospital(idEmergencia, nombrePaciente, nombreHospital, descripcion, fecha, estado)
                    pedidos.add(pedido)
                }

                runOnUiThread {
                    pedidoHospitalAdapter = PedidoHospitalAdapter(pedidos)
                    recyclerView.adapter = pedidoHospitalAdapter
                }
            } catch (ex: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Error al cargar los pedidos", Toast.LENGTH_SHORT).show()
                }
            } finally {
                preparedStatement?.close()
                connection?.close()
            }
        }.start()
    }
}
