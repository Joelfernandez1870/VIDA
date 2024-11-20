package com.example.vida.data.database

import com.example.vida.models.Donacion
import java.sql.PreparedStatement
import java.sql.SQLException


object DonacionDao {

    fun insert(donacion: Donacion): Boolean {
        val connection = MySqlConexion.getConexion()
        val sql = """
            INSERT INTO DONACIONES (ID_USUARIO, ID_HOSPITALES_CENTRO, FECHA_DONACION, TIPO_DE_DONACION) 
            VALUES (?, ?, ?, ?)
        """

        return try {
            val ps: PreparedStatement? = connection?.prepareStatement(sql)
            ps?.apply {
                setString(1, donacion.idUsuario)
                setString(2, donacion.idHospital)
                setString(3, donacion.fecha)
                setString(4, donacion.tipoDonacion)
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
