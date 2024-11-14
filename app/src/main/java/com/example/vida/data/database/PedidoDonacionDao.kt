package com.example.vida.data.database

import com.example.vida.models.PedidoDonacion
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException

object PedidoDonacionDao {

    // Método para insertar un nuevo pedido de donación en la base de datos
    fun insert(pedido: PedidoDonacion): Boolean {
        val connection: Connection? = MySqlConexion.getConexion()
        val sql = """
            INSERT INTO PEDIDO_DONACION (ID_PACIENTE, ID_HOSPITALES_CENTRO, DESCRIPCION, FECHA, ESTADO)
            VALUES (?, ?, ?, ?, ?)
        """

        return try {
            val ps: PreparedStatement? = connection?.prepareStatement(sql)
            ps?.apply {
                setInt(1, pedido.idPaciente)
                setInt(2, pedido.idHospital)
                setString(3, pedido.descripcion)
                setString(4, pedido.fecha) // La fecha se pasa como String
                setString(5, pedido.estado)
            }
            val rowsInserted = ps?.executeUpdate()
            ps?.close()
            connection?.close()

            rowsInserted != null && rowsInserted > 0

        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }
}
