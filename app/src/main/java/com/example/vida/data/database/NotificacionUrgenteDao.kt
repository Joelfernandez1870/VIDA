package com.example.vida.data.database

import com.example.vida.data.database.MySqlConexion.getConexion
import com.example.vida.models.NotificacionUrgente
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


object NotificacionUrgenteDao {
    fun insert(notificacionUrgente: NotificacionUrgente): Boolean {
        val connection: Connection? = MySqlConexion.getConexion()
        val sql = """
        INSERT INTO NOTIFICACIONES_URGENTES 
        (ID_HOSPITALES_CENTRO, ID_PACIENTE, MENSAJE, FECHA, FECHA_EXPIRACION, TIPO_NOTIFICACION) 
        VALUES (?, ?, ?, ?, ?, ?)
    """.trimIndent()

        val fechaExpiracion = LocalDateTime.parse(notificacionUrgente.fecha)
            .plusHours(1)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        return try {
            val ps: PreparedStatement? = connection?.prepareStatement(sql)

            ps?.setInt(1, notificacionUrgente.idHospitalCentro)
            ps?.setInt(2, notificacionUrgente.idPaciente ?: 0)
            ps?.setString(3, notificacionUrgente.mensaje)
            ps?.setString(4, notificacionUrgente.fecha)
            ps?.setString(5, fechaExpiracion) // Fecha de expiración
            ps?.setString(6, notificacionUrgente.tipoNotificacion)

            val rowsInserted = ps?.executeUpdate()
            ps?.close()
            connection?.close()

            rowsInserted != null && rowsInserted > 0
        } catch (e: SQLException) {
            e.printStackTrace()
            false
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
            nu.FECHA, 
            nu.FECHA_EXPIRACION
        FROM 
            NOTIFICACIONES_URGENTES nu
        LEFT JOIN 
            HOSPITALES_CENTROS h ON nu.ID_HOSPITALES_CENTRO = h.ID_HOSPITALES_CENTRO
        LEFT JOIN 
            PACIENTE p ON nu.ID_PACIENTE = p.ID_PACIENTE
        WHERE 
            nu.FECHA_EXPIRACION > CURRENT_TIMESTAMP;
    """.trimIndent()

        val notificaciones = mutableListOf<NotificacionUrgente>()

        try {
            val ps = connection!!.prepareStatement(sql)
            val rs = ps.executeQuery()

            while (rs.next()) {
                val notificacion = NotificacionUrgente(
                    idHospitalCentro = rs.getInt("ID_HOSPITALES_CENTRO"),
                    idPaciente = rs.getInt("ID_PACIENTE").takeIf { !rs.wasNull() },
                    mensaje = rs.getString("MENSAJE"),
                    tipoNotificacion = rs.getString("TIPO_NOTIFICACION"),
                    fecha = rs.getString("FECHA"),
                    fechaExpiracion = rs.getString("FECHA_EXPIRACION"),
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

    fun getNotificacionesUrgentesByHospital(loggedInHospitalId: Int): List<NotificacionUrgente> {
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
            nu.FECHA,
            nu.FECHA_EXPIRACION
        FROM 
            NOTIFICACIONES_URGENTES nu
        LEFT JOIN 
            HOSPITALES_CENTROS h ON nu.ID_HOSPITALES_CENTRO = h.ID_HOSPITALES_CENTRO
        LEFT JOIN 
            PACIENTE p ON nu.ID_PACIENTE = p.ID_PACIENTE
        WHERE 
            nu.ID_HOSPITALES_CENTRO = ? AND nu.FECHA_EXPIRACION > CURRENT_TIMESTAMP
    """
        val notificaciones = mutableListOf<NotificacionUrgente>()

        try {
            val ps = connection!!.prepareStatement(sql)
            ps.setInt(1, loggedInHospitalId)
            val rs = ps.executeQuery()

            while (rs.next()) {
                val notificacion = NotificacionUrgente(
                    idHospitalCentro = rs.getInt("ID_HOSPITALES_CENTRO"),
                    idPaciente = rs.getInt("ID_PACIENTE").takeIf { !rs.wasNull() },
                    mensaje = rs.getString("MENSAJE"),
                    tipoNotificacion = rs.getString("TIPO_NOTIFICACION"),
                    fecha = rs.getString("FECHA"),
                    fechaExpiracion = rs.getString("FECHA_EXPIRACION"),
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
        val sql = """
        SELECT COUNT(*) 
        FROM NOTIFICACIONES_URGENTES
        WHERE FECHA_EXPIRACION > CURRENT_TIMESTAMP
    """
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

    fun expireById(idHospitalCentro: Int, mensaje: String): Boolean {
        val connection: Connection? = MySqlConexion.getConexion()
        val sql = """
        UPDATE NOTIFICACIONES_URGENTES
        SET FECHA_EXPIRACION = CURRENT_TIMESTAMP
        WHERE ID_HOSPITALES_CENTRO = ? AND MENSAJE = ?
    """.trimIndent()

        return try {
            val ps: PreparedStatement? = connection?.prepareStatement(sql)
            ps?.setInt(1, idHospitalCentro)
            ps?.setString(2, mensaje)
            val rowsUpdated = ps?.executeUpdate()
            ps?.close()
            connection?.close()

            rowsUpdated != null && rowsUpdated > 0
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }




}
