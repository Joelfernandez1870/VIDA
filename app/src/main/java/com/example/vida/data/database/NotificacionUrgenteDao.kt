package com.example.vida.data.database

import com.example.vida.data.database.MySqlConexion.getConexion
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

    fun getNotificacionesUrgentes(): List<NotificacionUrgente> {
        val connection = getConexion()
        val sql = """
        SELECT 
            nu.ID_HOSPITALES_CENTRO, 
            h.NOMBRE_LUGAR, 
            nu.ID_PACIENTE, 
            p.GRUPO_SANGUINEO, 
            p.NOMBRE,
            p.APELLIDO,
            nu.MENSAJE, 
            nu.TIPO_NOTIFICACION, 
            nu.FECHA
        FROM 
            NOTIFICACIONES_URGENTES nu
        LEFT JOIN 
            HOSPITALES_CENTROS h ON nu.ID_HOSPITALES_CENTRO = h.ID_HOSPITALES_CENTRO
        LEFT JOIN 
            PACIENTE p ON nu.ID_PACIENTE = p.ID_PACIENTE;

    """
        val notificaciones = mutableListOf<NotificacionUrgente>()

        try {
            val ps = connection!!.prepareStatement(sql)
            val rs = ps.executeQuery()

            while (rs.next()) {
                val notificacion = NotificacionUrgente(
                    idHospitalCentro = rs.getInt("ID_HOSPITALES_CENTRO"),
                    idPaciente = rs.getInt("ID_PACIENTE").takeIf { !rs.wasNull() }, // Manejo de valores nulos
                    mensaje = rs.getString("MENSAJE"),
                    tipoNotificacion = rs.getString("TIPO_NOTIFICACION"),
                    fecha = rs.getString("FECHA"),
                    nombreLugar = rs.getString("NOMBRE_LUGAR"),
                    grupoSanguineo = rs.getString("GRUPO_SANGUINEO"),
                    nombrePaciente = rs.getString("NOMBRE"),
                    apellidoPaciente = rs.getString("APELLIDO")
                )
                notificaciones.add(notificacion)
            }
            rs.close()
            ps.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            try {
                connection!!.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
        return notificaciones
    }



    fun getCantidadNotificacionesUrgentes(): Int {
        val connection = getConexion()
        val sql = "SELECT COUNT(*) FROM NOTIFICACIONES_URGENTES"
        var cantidad = 0

        try {
            val ps = connection!!.prepareStatement(sql)
            val rs = ps.executeQuery()

            if (rs.next()) {
                cantidad = rs.getInt(1)
            }
            rs.close()
            ps.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            try {
                connection!!.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
        return cantidad
    }


}
