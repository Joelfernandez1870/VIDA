package com.example.vida.data.database

import com.example.vida.models.NotificacionUrgente
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException

object NotificacionUrgenteDao {
    fun insert(notificacionUrgente: NotificacionUrgente): Boolean {
        val connection: Connection? = MySqlConexion.getConexion()
        val sql = """
            INSERT INTO NOTIFICACIONES_URGENTES 
            (ID_HOSPITALES_CENTRO, ID_PACIENTE, MENSAJE, FECHA, TIPO_NOTIFICACION) 
            VALUES (?, ?, ?, ?, ?)
        """.trimIndent()

        try {
            val ps: PreparedStatement? = connection?.prepareStatement(sql)

            ps?.setInt(1, notificacionUrgente.idHospitalCentro)
            ps?.setInt(2, notificacionUrgente.idPaciente ?: 0)
            ps?.setString(3, notificacionUrgente.mensaje)
            ps?.setString(4, notificacionUrgente.fecha)
            ps?.setString(5, notificacionUrgente.tipoNotificacion)

            val rowsInserted = ps?.executeUpdate()
            ps?.close()
            connection?.close()

            return rowsInserted != null && rowsInserted > 0

        } catch (e: SQLException) {
            e.printStackTrace()
            return false
        }
    }
}
