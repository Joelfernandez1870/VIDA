package com.example.vida.data.database

import com.example.vida.models.Usuario
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

object UsuarioDao {

    fun insert(usuario: Usuario): Boolean {
        val connection = MySqlConexion.getConexion()
        val sql = """
            INSERT INTO USUARIO (DNI, NOMBRE, APELLIDO, EMAIL, CONTRASEÑA, GRUPO_SANGUINEO, FECHA_NACIMIENTO, CIUDAD, PAIS) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """

        return try {
            val ps: PreparedStatement? = connection?.prepareStatement(sql)
            ps?.apply {
                setString(1, usuario.dni)
                setString(2, usuario.nombre)
                setString(3, usuario.apellido)
                setString(4, usuario.email)
                setString(5, usuario.contraseña)
                setString(6, usuario.grupoSanguineo)
                setString(7, usuario.fechaNacimiento) // Ahora se establece como String
                setString(8, usuario.ciudad)
                setString(9, usuario.pais)
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

    fun update(usuario: Usuario): Boolean {
        val connection: Connection = MySqlConexion.getConexion() ?: return false
        val sql = """
            UPDATE USUARIO SET NOMBRE = ?, APELLIDO = ?, EMAIL = ?, CONTRASEÑA = ?, GRUPO_SANGUINEO = ?, 
            FECHA_NACIMIENTO = ?, CIUDAD = ?, PAIS = ?, PUNTOS = ? WHERE DNI = ?
        """

        return try {
            val ps: PreparedStatement = connection.prepareStatement(sql)
            ps.setString(1, usuario.nombre)
            ps.setString(2, usuario.apellido)
            ps.setString(3, usuario.email)
            ps.setString(4, usuario.contraseña)
            ps.setString(5, usuario.grupoSanguineo)
            ps.setString(6, usuario.fechaNacimiento) // Ahora se establece como String
            ps.setString(7, usuario.ciudad)
            ps.setString(8, usuario.pais)
            if (usuario.puntos != null) {
                ps.setInt(9, usuario.puntos)
            } else {
                ps.setNull(9, java.sql.Types.INTEGER)
            }
            ps.setString(10, usuario.dni)

            val rowsUpdated = ps.executeUpdate()
            ps.close()
            connection.close()

            rowsUpdated > 0
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    fun getUsuarioByDni(dni: String): Usuario? {
        val connection: Connection = MySqlConexion.getConexion() ?: return null
        val sql = "SELECT * FROM USUARIO WHERE DNI = ?"

        return try {
            val ps: PreparedStatement = connection.prepareStatement(sql)
            ps.setString(1, dni)
            val resultSet: ResultSet = ps.executeQuery()

            if (resultSet.next()) {
                Usuario(
                    dni = resultSet.getString("DNI"),
                    nombre = resultSet.getString("NOMBRE"),
                    apellido = resultSet.getString("APELLIDO"),
                    email = resultSet.getString("EMAIL"),
                    contraseña = resultSet.getString("CONTRASEÑA"),
                    grupoSanguineo = resultSet.getString("GRUPO_SANGUINEO"),
                    fechaNacimiento = resultSet.getString("FECHA_NACIMIENTO"), // Ahora se obtiene como String
                    ciudad = resultSet.getString("CIUDAD"),
                    pais = resultSet.getString("PAIS"),
                    puntos = resultSet.getInt("PUNTOS").takeIf { !resultSet.wasNull() }
                )
            } else null
        } catch (e: SQLException) {
            e.printStackTrace()
            null
        }
    }

    fun getAllUsuarios(): List<Usuario> {
        val connection: Connection = MySqlConexion.getConexion() ?: return emptyList()
        val usuarios = mutableListOf<Usuario>()
        val sql = "SELECT * FROM USUARIO"

        return try {
            val statement: PreparedStatement = connection.prepareStatement(sql)
            val resultSet: ResultSet = statement.executeQuery()

            while (resultSet.next()) {
                val usuario = Usuario(
                    dni = resultSet.getString("DNI"),
                    nombre = resultSet.getString("NOMBRE"),
                    apellido = resultSet.getString("APELLIDO"),
                    email = resultSet.getString("EMAIL"),
                    contraseña = resultSet.getString("CONTRASEÑA"),
                    grupoSanguineo = resultSet.getString("GRUPO_SANGUINEO"),
                    fechaNacimiento = resultSet.getString("FECHA_NACIMIENTO"), // Ahora se obtiene como String
                    ciudad = resultSet.getString("CIUDAD"),
                    pais = resultSet.getString("PAIS"),
                    puntos = resultSet.getInt("PUNTOS").takeIf { !resultSet.wasNull() }
                )
                usuarios.add(usuario)
            }

            resultSet.close()
            statement.close()
            connection.close()

            usuarios
        } catch (e: SQLException) {
            e.printStackTrace()
            emptyList()
        }
    }
}
