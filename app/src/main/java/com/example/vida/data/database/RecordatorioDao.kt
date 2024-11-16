package com.example.vida.data.database


import com.example.vida.models.Recordatorio
import java.sql.Connection
import java.sql.PreparedStatement

import java.sql.SQLException

object RecordatorioDao {

    fun Insert(recordatorio: Recordatorio): Boolean {
        val connection: Connection = MySqlConexion.getConexion() ?: return false
        val sql = """
            INSERT INTO RECORDATORIOS (ID_USUARIO, FECHA_ENVIO, TIPO_RECORDATORIO, MENSAJE_RECORDATORIO) 
            VALUES (?, ?, ?, ?)
        """

        return try {
            // Insertamos el nuevo recordatorio
            val ps: PreparedStatement? = connection?.prepareStatement(sql)
            ps?.apply {
                setInt(1, recordatorio.id_usuario)
                setString(2, recordatorio.fecha_envio)
                setString(3, recordatorio.tipo_recordatorio)
                setString(4, recordatorio.mensaje_recordatorio)

            }
            val rowsInserted = ps?.executeUpdate()
            ps?.close()
            connection?.close()

            rowsInserted != null && rowsInserted > 0

        } catch (e: IllegalArgumentException) {
            false
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }

    }

}
