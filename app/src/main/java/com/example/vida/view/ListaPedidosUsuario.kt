package com.example.vida.view

import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vida.R
import com.example.vida.data.database.MySqlConexion
import com.example.vida.models.PedidosUsuario
import java.sql.Connection
import java.sql.PreparedStatement

class ListaPedidosUsuario : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var pedidoUsuarioAdapter: PedidoUsuarioAdapter
    private lateinit var searchView: SearchView
    private val listaCompletaPedidos = mutableListOf<PedidosUsuario>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_pedidos_usuario)

        recyclerView = findViewById(R.id.recyclerViewPedidosUsuario)
        recyclerView.layoutManager = LinearLayoutManager(this)

        searchView = findViewById(R.id.searchViewChats)
        setupSearchView()
    }

    override fun onResume() {
        super.onResume()
        cargarPedidosDeDonacion()
    }

    private fun cargarPedidosDeDonacion() {
        Thread {
            listaCompletaPedidos.clear()
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
                while (resultSet?.next() == true) {
                    val pedido = PedidosUsuario(
                        resultSet.getInt("ID_EMERGENCIA"),
                        resultSet.getString("NOMBRE_PACIENTE"),
                        resultSet.getString("DNI_PACIENTE"),
                        resultSet.getString("NOMBRE_HOSPITAL"),
                        resultSet.getString("DESCRIPCION"),
                        resultSet.getString("FECHA"),
                        resultSet.getString("ESTADO")
                    )
                    listaCompletaPedidos.add(pedido)
                }

                runOnUiThread {
                    pedidoUsuarioAdapter = PedidoUsuarioAdapter(listaCompletaPedidos)
                    recyclerView.adapter = pedidoUsuarioAdapter
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
                filtroPorNombre(newText ?: "")
                return true
            }
        })
    }

    private fun filtroPorNombre(nombre: String) {
        val listaFiltrada = listaCompletaPedidos.filter {
            it.nombrePaciente.contains(nombre, ignoreCase = true) ||
                    it.dniPaciente.contains(nombre, ignoreCase = true)
        }
        pedidoUsuarioAdapter.actualizarLista(listaFiltrada)
    }
}
