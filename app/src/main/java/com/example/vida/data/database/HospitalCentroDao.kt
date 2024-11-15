package com.example.vida.data.database

import android.util.Log
import com.example.vida.models.HospitalCentro
import java.io.Console
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import kotlin.math.log

object HospitalCentroDao {

    fun insert(hospitalCentro: HospitalCentro): Boolean {
        val connection = MySqlConexion.getConexion()
        val sql =
            "INSERT INTO HOSPITALES_CENTROS (TIPO_LUGAR, NOMBRE_LUGAR, DIRECCION,EMAIL, CONTRASENIA, CODIGO_HABILITACION, CIUDAD, PAIS, LONGITUD, LATITUD, TIPO_USUARIO) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)"

        try {
            val ps = connection?.prepareStatement(sql)

            ps?.setString(1, hospitalCentro.tipoLugar)
            ps?.setString(2, hospitalCentro.nombreLugar)
            ps?.setString(3, hospitalCentro.direccion)
            ps?.setString(4, hospitalCentro.correo)
            ps?.setString(5, hospitalCentro.clave)
            ps?.setString(6, hospitalCentro.codigo)
            ps?.setString(7, hospitalCentro.ciudad)
            ps?.setString(8, hospitalCentro.pais)
            ps?.setDouble(9, hospitalCentro.longitud)
            ps?.setDouble(10, hospitalCentro.latitud)
            ps?.setInt(11, hospitalCentro.tipo_usuario)

            val rowsInserted = ps?.executeUpdate()  // Ejecutamos la inserción
            ps?.close()
            connection?.close()

            return rowsInserted != null && rowsInserted > 0  // Verifica si se insertó exitosamente

        } catch (e: SQLException) {
            e.printStackTrace()
            return false
        }
    }

    fun verifiarCodigo(codidigo: String): Boolean {
        val connection = MySqlConexion.getConexion()
        if (connection != null) {
            val statement: PreparedStatement = connection.prepareStatement(
                "SELECT EXISTS(SELECT 1 FROM HOSPITALES_CENTROS WHERE CODIGO_HABILITACION = ?)"
            )
            try {
                statement.setString(1, codidigo) // Set the parameter value
                val resultSet: ResultSet = statement.executeQuery()
                if (resultSet.next()) {
                    val exists: Boolean = resultSet.getBoolean(1) // Get the boolean value
                    return exists// Use the 'exists' variable
                }else{
                    return false
                }
                connection.close()

            } catch (e: SQLException) {
                e.printStackTrace()
                return false
            }
        } else {
            println("error: conexion null")
            return false
        }
    }

//        val connection = MySqlConexion.getConexion()
//        val sql = "SELECT * EXISTS(SELECT 1 FROM HOSPITALES_CENTROS WHERE CODIGO_HABILITACION = ?)"
//
//        try {
//            val ps = connection?.prepareStatement(sql)
//
//            ps?.setString(1, codidigo)
//            val rowsSelected = ps?.executeQuery()
//            ps?.close()
//            connection?.close()
//
//            if (rowsSelected != null) {
//                return rowsSelected.getBoolean(1)
//            }
//        } catch (e: SQLException) {
//            e.printStackTrace()
//            return false
//        }
//    }
    /*
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
    }*/

}