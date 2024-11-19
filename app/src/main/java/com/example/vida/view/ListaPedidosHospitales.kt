package com.example.vida.view

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
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
    private lateinit var searchView: SearchView
    private var listaPedidos: MutableList<PedidoHospital> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lista_pedidos_hospitales)

        recyclerView = findViewById(R.id.recyclerViewPedidosHospitales)
        recyclerView.layoutManager = LinearLayoutManager(this)

        searchView = findViewById(R.id.searchViewPedidos)
        setupSearchView()

        val fabAgregarPedido = findViewById<FloatingActionButton>(R.id.fabAgregarPedido)
        fabAgregarPedido.setOnClickListener {
            val intent = Intent(this, RegistrarPedidoHospital::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        cargarPedidosHospitales()
    }

    private fun cargarPedidosHospitales() {
        Thread {
            var connection: Connection? = null
            var preparedStatement: PreparedStatement? = null

            try {
                connection = MySqlConexion.getConexion()
                val query = """
                    SELECT 
                        p.ID_EMERGENCIA,
                        pa.NOMBRE AS NOMBRE_PACIENTE,
                        pa.DNI AS DNI_PACIENTE,
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
                listaPedidos.clear()
                while (resultSet?.next() == true) {
                    val idEmergencia = resultSet.getInt("ID_EMERGENCIA")
                    val nombrePaciente = resultSet.getString("NOMBRE_PACIENTE")
                    val dniPaciente = resultSet.getString("DNI_PACIENTE")
                    val nombreHospital = resultSet.getString("NOMBRE_HOSPITAL")
                    val descripcion = resultSet.getString("DESCRIPCION")
                    val fecha = resultSet.getString("FECHA")
                    val estado = resultSet.getString("ESTADO")

                    val pedido = PedidoHospital(
                        idEmergencia,
                        nombrePaciente,
                        dniPaciente,
                        nombreHospital,
                        descripcion,
                        fecha,
                        estado
                    )
                    listaPedidos.add(pedido)
                }

                runOnUiThread {
                    pedidoHospitalAdapter = PedidoHospitalAdapter(listaPedidos,
                        onModificar = { pedido ->
                            val intent = Intent(this, ModificarPedidoHospital::class.java)
                            intent.putExtra("ID_EMERGENCIA", pedido.idEmergencia)
                            startActivity(intent)
                        },
                        onEliminar = { pedido ->
                            eliminarPedido(pedido)
                        })
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

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredList = listaPedidos.filter {
                    it.nombrePaciente.contains(newText ?: "", ignoreCase = true)
                }
                pedidoHospitalAdapter.updateList(filteredList.toMutableList())
                return true
            }
        })
    }

    private fun eliminarPedido(pedido: PedidoHospital) {
        // Mostrar un cuadro de diálogo para confirmar la eliminación
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar Eliminación")
        builder.setMessage("¿Estás seguro que deseas eliminar este pedido de donación?")
        builder.setPositiveButton("Sí") { dialog, which ->
            // Si el usuario confirma, proceder con la eliminación
            Thread {
                var connection: Connection? = null
                var preparedStatement: PreparedStatement? = null
                try {
                    connection = MySqlConexion.getConexion()
                    val query = "DELETE FROM PEDIDO_DONACION WHERE ID_EMERGENCIA = ?"
                    preparedStatement = connection?.prepareStatement(query)
                    preparedStatement?.setInt(1, pedido.idEmergencia)

                    val rowsAffected = preparedStatement?.executeUpdate()
                    if (rowsAffected != null && rowsAffected > 0) {
                        runOnUiThread {
                            Toast.makeText(this, "Pedido eliminado con éxito", Toast.LENGTH_SHORT).show()
                            cargarPedidosHospitales() // Recargar la lista de pedidos después de la eliminación
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this, "Error al eliminar el pedido", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (ex: Exception) {
                    runOnUiThread {
                        Toast.makeText(this, "Error al eliminar el pedido", Toast.LENGTH_SHORT).show()
                    }
                } finally {
                    preparedStatement?.close()
                    connection?.close()
                }
            }.start()
        }

        builder.setNegativeButton("No") { dialog, which ->
            // Si el usuario cancela, cerrar el diálogo
            dialog.dismiss()
        }

        // Mostrar el cuadro de diálogo
        builder.show()
    }
}
