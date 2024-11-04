package com.example.vida.data.database

import android.util.Log
import com.example.vida.models.HospitalCentro
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

object HospitalCentroDao {

    fun insert(hospitalCentro: HospitalCentro): Boolean {
        val connection = MySqlConexion.getConexion()
        val sql = "INSERT INTO HOSPITALES_CENTROS (TIPO_LUGAR, NOMBRE_LUGAR, CIUDAD, PAIS, LONGITUD, LATITUD) VALUES (?, ?, ?, ?, ?, ?)"

        try {
            val ps = connection?.prepareStatement(sql)

            //Log.d("HospitalCentro", hospitalCentro.toString())

            ps?.setString(1, hospitalCentro.tipoLugar)
            ps?.setString(2, hospitalCentro.nombreLugar)
            ps?.setString(3, hospitalCentro.ciudad)
            ps?.setString(4, hospitalCentro.pais)
            ps?.setDouble(5, hospitalCentro.longitud)
            ps?.setDouble(6, hospitalCentro.latitud)

            val rowsInserted = ps?.executeUpdate()  // Ejecutamos la inserción
            ps?.close()
            connection?.close()

            return rowsInserted != null && rowsInserted > 0  // Verifica si se insertó exitosamente

        } catch (e: SQLException) {
            e.printStackTrace()
            return false
        }
    }
    fun update(hospitalCentro: HospitalCentro) {
        val connection: Connection = MySqlConexion.getConexion()!!
        val statement: PreparedStatement = connection.prepareStatement(
            "UPDATE HOSPITALES_CENTROS SET TIPO_LUGAR = ?, NOMBRE_LUGAR = ?, CIUDAD = ?, PAIS = ?, LONGITUD = ?, LATITUD = ? " +
                    "WHERE ID_HOSPITALES_CENTRO = ?"
        )
        statement.setString(1, hospitalCentro.tipoLugar)
        statement.setString(2, hospitalCentro.nombreLugar)
        statement.setString(3, hospitalCentro.ciudad)
        statement.setString(4, hospitalCentro.pais)
        statement.setDouble(5, hospitalCentro.longitud)
        statement.setDouble(6, hospitalCentro.latitud)
//        statement.setInt(7, hospitalCentro.idHospitalesCentro)
        statement.executeUpdate()
    }

    fun delete(hospitalCentro: HospitalCentro) {
        val connection: Connection = MySqlConexion.getConexion()!!
        val statement: PreparedStatement = connection.prepareStatement(
            "DELETE FROM HOSPITALES_CENTROS WHERE ID_HOSPITALES_CENTRO = ?"
        )
//        statement.setInt(1, hospitalCentro.idHospitalesCentro)
        statement.executeUpdate()
    }

    fun getAllHospitalesCentros(): List<HospitalCentro> {
        val connection: Connection = MySqlConexion.getConexion()!!
        val hospitalesCentros = mutableListOf<HospitalCentro>()
        val statement: PreparedStatement = connection.prepareStatement("SELECT * FROM HOSPITALES_CENTROS")
        val resultSet: ResultSet = statement.executeQuery()
        while (resultSet.next()) {
            val hospitalCentro = HospitalCentro(
//                resultSet.getInt("ID_HOSPITALES_CENTRO"),
                resultSet.getString("TIPO_LUGAR"),
                resultSet.getString("NOMBRE_LUGAR"),
                resultSet.getString("CIUDAD"),
                resultSet.getString("PAIS"),
                resultSet.getDouble("LONGITUD"),
                resultSet.getDouble("LATITUD")
            )
            hospitalesCentros.add(hospitalCentro)
        }
        return hospitalesCentros
    }

    fun getHospitalCentroById(id: Int): HospitalCentro? {
        val connection: Connection = MySqlConexion.getConexion()!!
        val statement: PreparedStatement = connection.prepareStatement(
            "SELECT * FROM HOSPITALES_CENTROS WHERE ID_HOSPITALES_CENTRO = ?"
        )
        statement.setInt(1, id)
        val resultSet: ResultSet = statement.executeQuery()
        if (resultSet.next()) {
            return HospitalCentro(
//                resultSet.getInt("ID_HOSPITALES_CENTRO"),
                resultSet.getString("TIPO_LUGAR"),
                resultSet.getString("NOMBRE_LUGAR"),
                resultSet.getString("CIUDAD"),
                resultSet.getString("PAIS"),
                resultSet.getDouble("LONGITUD"),
                resultSet.getDouble("LATITUD")
            )
        }
        return null
    }
}