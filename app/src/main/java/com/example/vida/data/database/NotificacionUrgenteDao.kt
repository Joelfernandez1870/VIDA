package com.example.vida.data.database

import com.example.vida.data.database.MySqlConexion.getConexion
import com.example.vida.models.NotificacionUrgente
import com.example.vida.models.NotificacionUrgenteEdicion
import com.mysql.jdbc.Statement
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale


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
            nu.ID_NOTIFICACION,
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
                    idNotificacion = rs.getInt("ID_NOTIFICACION"),
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
                nu.ID_NOTIFICACION, 
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
                nu.ID_HOSPITALES_CENTRO = ?
                AND nu.FECHA_EXPIRACION > CURRENT_TIMESTAMP
            ; """.trimIndent()

        val notificaciones = mutableListOf<NotificacionUrgente>()

        try {
            val ps = connection!!.prepareStatement(sql)
            ps.setInt(1, loggedInHospitalId)
            val rs = ps.executeQuery()

            while (rs.next()) {
                val notificacion = NotificacionUrgente(
                    idNotificacion = rs.getInt("ID_NOTIFICACION"),
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

    fun updateNotificacion(notificacionUrgente: NotificacionUrgenteEdicion): Boolean {
        val connection: Connection? = MySqlConexion.getConexion()
        val sql = """
        UPDATE NOTIFICACIONES_URGENTES 
        SET ID_HOSPITALES_CENTRO = ?, 
            ID_PACIENTE = ?, 
            MENSAJE = ?, 
            FECHA = ?, 
            FECHA_EXPIRACION = ?, 
            TIPO_NOTIFICACION = ? 
        WHERE ID_NOTIFICACION = ?;
    """.trimIndent()

        // Usamos SimpleDateFormat para manejar la fecha
        val fechaFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val fecha = fechaFormat.parse(notificacionUrgente.fecha) // Parseamos la fecha de entrada

        // Sumamos una hora a la fecha
        val calendar = Calendar.getInstance()
        calendar.time = fecha
        calendar.add(Calendar.HOUR, 1)
        val fechaExpiracion = fechaFormat.format(calendar.time)

        return try {
            val ps: PreparedStatement? = connection?.prepareStatement(sql)

            ps?.setInt(1, notificacionUrgente.idHospitalCentro)
            ps?.setInt(2, notificacionUrgente.idPaciente ?: 0)
            ps?.setString(3, notificacionUrgente.mensaje)
            ps?.setString(4, notificacionUrgente.fecha)
            ps?.setString(5, fechaExpiracion) // Fecha de expiración calculada
            ps?.setString(6, notificacionUrgente.tipoNotificacion)
            ps?.setInt(7, notificacionUrgente.idNotificacion) // El identificador único

            val rowsUpdated = ps?.executeUpdate()
            ps?.close()
            connection?.close()

            rowsUpdated != null && rowsUpdated > 0
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }


    fun getNotificacionById(idNotificacion: Int): NotificacionUrgente? {
        val connection = MySqlConexion.getConexion()
        val sql = "SELECT * FROM NOTIFICACIONES_URGENTES WHERE ID_NOTIFICACION = ?"
        var notificacion: NotificacionUrgente? = null

        try {
            val ps = connection!!.prepareStatement(sql)
            ps.setInt(1, idNotificacion)
            val resultSet = ps.executeQuery()

            if (resultSet.next()) {
                notificacion = NotificacionUrgente(
                    idNotificacion = resultSet.getInt("ID_NOTIFICACION"),
                    idHospitalCentro = resultSet.getInt("ID_HOSPITALES_CENTRO"),
                    idPaciente = resultSet.getInt("ID_PACIENTE").takeIf { !resultSet.wasNull() },
                    mensaje = resultSet.getString("MENSAJE"),
                    tipoNotificacion = resultSet.getString("TIPO_NOTIFICACION"),
                    fecha = resultSet.getString("FECHA"),
                    fechaExpiracion = resultSet.getString("FECHA_EXPIRACION")
                )
            }

            resultSet.close()
            ps.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            try {
                connection?.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }

        return notificacion
    }


    fun insertAndReturnId(notificacion: NotificacionUrgente): Int? {
        val connection = MySqlConexion.getConexion()
        val sql = """
        INSERT INTO NOTIFICACIONES_URGENTES 
        (ID_HOSPITALES_CENTRO, ID_PACIENTE, MENSAJE, FECHA, TIPO_NOTIFICACION, FECHA_EXPIRACION) 
        VALUES (?, ?, ?, ?, ?, ?)
    """.trimIndent()

        return try {
            val ps = connection?.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            ps?.setInt(1, notificacion.idHospitalCentro)
            ps?.setInt(2, notificacion.idPaciente ?: 0)
            ps?.setString(3, notificacion.mensaje)
            ps?.setString(4, notificacion.fecha)
            ps?.setString(5, notificacion.tipoNotificacion)
            ps?.setString(6, notificacion.fechaExpiracion)

            ps?.executeUpdate()

            val rs = ps?.generatedKeys
            if (rs?.next() == true) rs.getInt(1) else null
        } catch (e: SQLException) {
            e.printStackTrace()
            null
        } finally {
            connection?.close()
        }
    }

}
