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
        val sql = "INSERT INTO HOSPITALES_CENTROS (TIPO_LUGAR, NOMBRE_LUGAR, DIRECCION,EMAIL, CONTRASENIA, CIUDAD, PAIS, LONGITUD, LATITUD) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"

        try {
            val ps = connection?.prepareStatement(sql)

            ps?.setString(1, hospitalCentro.tipoLugar)
            ps?.setString(2, hospitalCentro.nombreLugar)
            ps?.setString(3, hospitalCentro.direccion)
            ps?.setString(4, hospitalCentro.correo )
            ps?.setString(5, hospitalCentro.clave)
            ps?.setString(6, hospitalCentro.ciudad)
            ps?.setString(7, hospitalCentro.pais)
            ps?.setDouble(8, hospitalCentro.longitud)
            ps?.setDouble(9, hospitalCentro.latitud)


            val rowsInserted = ps?.executeUpdate()  // Ejecutamos la inserción
            ps?.close()
            connection?.close()

            return rowsInserted != null && rowsInserted > 0  // Verifica si se insertó exitosamente

        } catch (e: SQLException) {
            e.printStackTrace()
            return false
        }
    }

    fun getAllHospitalesCentros(): List<HospitalCentro> {
        val hospitalesCentros = mutableListOf<HospitalCentro>()
        val connection: Connection? = MySqlConexion.getConexion()
        if (connection == null) {
            Log.e("HospitalCentroDao", "No se pudo establecer conexión con la base de datos")
            return hospitalesCentros
        }
        val sql = "SELECT ID_HOSPITALES_CENTRO, TIPO_LUGAR, NOMBRE_LUGAR, DIRECCION, EMAIL, CONTRASENIA, CIUDAD, PAIS, LONGITUD, LATITUD, TIPO_USUARIO FROM HOSPITALES_CENTROS"

        var statement: PreparedStatement? = null
        var resultSet: ResultSet? = null

        try {
            statement = connection.prepareStatement(sql)
            resultSet = statement.executeQuery()

            while (resultSet.next()) {
                val hospitalCentro = HospitalCentro(
                    idHospitalesCentro = resultSet.getInt("ID_HOSPITALES_CENTRO"),
                    tipoLugar = resultSet.getString("TIPO_LUGAR"),
                    nombreLugar = resultSet.getString("NOMBRE_LUGAR"),
                    direccion = resultSet.getString("DIRECCION"),
                    correo = resultSet.getString("EMAIL"),
                    clave = resultSet.getString("CONTRASENIA"),
                    ciudad = resultSet.getString("CIUDAD"),
                    pais = resultSet.getString("PAIS"),
                    longitud = resultSet.getDouble("LONGITUD"),
                    latitud = resultSet.getDouble("LATITUD"),
                )
                hospitalesCentros.add(hospitalCentro)
            }

            Log.d("HospitalCentroDao", "Cantidad de hospitales obtenidos: ${hospitalesCentros.size}")

        } catch (e: SQLException) {
            Log.e("HospitalCentroDao", "Error al obtener los hospitales", e)
        } finally {
            resultSet?.close()
            statement?.close()
            connection?.close()
        }

        return hospitalesCentros
    }


    fun verifiarCodigo(codigo: String): Boolean {
        val connection = MySqlConexion.getConexion()
        if (connection != null) {
            val statement: PreparedStatement = connection.prepareStatement(
                "SELECT EXISTS(SELECT 1 FROM CODIGO_HABILITACION WHERE CODIGO = ?)"
            )
            try {
                statement.setString(1, codigo) // Set the parameter value
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

    fun getHospitalById(hospitalId: Int): HospitalCentro? {
        val connection: Connection = MySqlConexion.getConexion() ?: return null
        val sql = "SELECT * FROM HOSPITALES_CENTROS WHERE ID_HOSPITALES_CENTRO = ?"

        return try {
            val ps: PreparedStatement = connection.prepareStatement(sql)
            ps.setInt(1, hospitalId)
            val resultSet: ResultSet = ps.executeQuery()

            if (resultSet.next()) {
                val hospital = HospitalCentro(
                    idHospitalesCentro = resultSet.getInt("ID_HOSPITALES_CENTRO"),
                    tipoLugar = resultSet.getString("TIPO_LUGAR") ?: "",
                    nombreLugar = resultSet.getString("NOMBRE_LUGAR") ?: "",
                    ciudad = resultSet.getString("CIUDAD") ?: "",
                    pais = resultSet.getString("PAIS") ?: "",
                    longitud = resultSet.getDouble("LONGITUD"),
                    latitud = resultSet.getDouble("LATITUD"),
                    clave = resultSet.getString("CONTRASENIA") ?: "",
                    correo = resultSet.getString("EMAIL") ?: "",
                    direccion = resultSet.getString("DIRECCION") ?: ""
                )
                resultSet.close()
                ps.close()
                connection.close()
                hospital
            } else {
                null
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            null
        }
    }

    fun getIdByName(nombreLugar: String): Int? {
        val connection = MySqlConexion.getConexion()
        val sql = "SELECT ID_HOSPITALES_CENTRO FROM HOSPITALES_CENTROS WHERE NOMBRE_LUGAR = ?"
        var idHospital: Int? = null

        try {
            val ps = connection!!.prepareStatement(sql)
            ps.setString(1, nombreLugar)
            val rs = ps.executeQuery()
            if (rs.next()) {
                idHospital = rs.getInt("ID_HOSPITALES_CENTRO")
            }
            rs.close()
            ps.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return idHospital
    }

    fun isEmailRegistered(email: String): Boolean {
        val connection = MySqlConexion.getConexion()
        if (connection != null) {
            val statement: PreparedStatement = connection.prepareStatement(
                "SELECT EXISTS(SELECT 1 FROM HOSPITALES_CENTROS WHERE EMAIL = ?)"
            )
            try {
                statement.setString(1, email) // Set the parameter value
                val resultSet: ResultSet = statement.executeQuery()
                if (resultSet.next()) {
                    val exists: Boolean = resultSet.getBoolean(1) // Get the boolean value
                    return exists// Use the 'exists' variable
                } else {
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

    */
}