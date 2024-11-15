package com.example.vida.view




import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vida.R
import com.example.vida.data.database.MySqlConexion
import com.example.vida.models.PedidoHospital
import java.sql.Connection
import java.sql.PreparedStatement
import androidx.activity.enableEdgeToEdge

class ListaPedidosUsuario : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var pedidoHospitalAdapter: PedidoHospitalAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lista_pedidos_usuario)

        recyclerView = findViewById(R.id.recyclerViewPedidosUsuario)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Cargar los pedidos de donación
        cargarPedidosDeDonacion()
    }

    private fun cargarPedidosDeDonacion() {
        Thread {
            val pedidos = mutableListOf<PedidoHospital>()
            var connection: Connection? = null
            var preparedStatement: PreparedStatement? = null

            try {
                // Establecer la conexión con la base de datos
                connection = MySqlConexion.getConexion()

                // Realizar la consulta SQL
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

                    // Crear objeto PedidoHospital
                    val pedido = PedidoHospital(idEmergencia, nombrePaciente, nombreHospital, descripcion, fecha, estado)
                    pedidos.add(pedido)
                }

                runOnUiThread {
                    // Actualizar el RecyclerView con los pedidos cargados
                    pedidoHospitalAdapter = PedidoHospitalAdapter(pedidos)
                    recyclerView.adapter = pedidoHospitalAdapter
                }
            } catch (ex: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Error al cargar los pedidos", Toast.LENGTH_SHORT).show()
                }
            } finally {
                // Cerrar conexiones
                preparedStatement?.close()
                connection?.close()
            }
        }.start()
    }
}
