package com.example.vida.data.database

import com.example.vida.models.Usuario
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

object UsuarioDao {

    // Método para verificar si un DNI ya está registrado
    fun isDniRegistered(dni: String): Boolean {
        val connection: Connection = MySqlConexion.getConexion() ?: return false
        val sql = "SELECT DNI FROM USUARIO WHERE DNI = ?"

        return try {
            val ps: PreparedStatement = connection.prepareStatement(sql)
            ps.setString(1, dni)
            val resultSet: ResultSet = ps.executeQuery()
            val exists = resultSet.next()
            resultSet.close()
            ps.close()
            exists
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    // Método para verificar si un Email ya está registrado
    fun isEmailRegistered(email: String): Boolean {
        val connection: Connection = MySqlConexion.getConexion() ?: return false
        val sql = "SELECT EMAIL FROM USUARIO WHERE LOWER(EMAIL) = LOWER(?)"

        return try {
            val ps: PreparedStatement = connection.prepareStatement(sql)
            ps.setString(1, email)
            val resultSet: ResultSet = ps.executeQuery()
            val exists = resultSet.next()
            resultSet.close()
            ps.close()
            exists
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    // Método para insertar un nuevo usuario
    fun insert(usuario: Usuario): Boolean {
        val connection = MySqlConexion.getConexion()
        val sql = """
            INSERT INTO USUARIO (DNI, NOMBRE, APELLIDO, EMAIL, CONTRASENIA, GRUPO_SANGUINEO, FECHA_NACIMIENTO, CIUDAD, PAIS) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """

        return try {

            // Procedemos a insertar el nuevo usuario
            val ps: PreparedStatement? = connection?.prepareStatement(sql)
            ps?.apply {
                setString(1, usuario.dni)
                setString(2, usuario.nombre)
                setString(3, usuario.apellido)
                setString(4, usuario.email)
                setString(5, usuario.contrasenia)
                setString(6, usuario.grupoSanguineo)
                setString(7, usuario.fechaNacimiento)
                setString(8, usuario.ciudad)
                setString(9, usuario.pais)
            }
            val rowsInserted = ps?.executeUpdate()
            ps?.close()
            connection?.close()

            rowsInserted != null && rowsInserted > 0

        } catch (e: IllegalArgumentException) {
            // Aquí mostramos el mensaje de error especificado para el campo duplicado
            println(e.message)
            false
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    // Método para actualizar un usuario existente
    fun update(usuario: Usuario): Boolean {
        val connection: Connection = MySqlConexion.getConexion() ?: return false
        val sql = """
            UPDATE USUARIO SET NOMBRE = ?, APELLIDO = ?, EMAIL = ?, CONTRASENIA = ?, GRUPO_SANGUINEO = ?, 
            FECHA_NACIMIENTO = ?, CIUDAD = ?, PAIS = ?, PUNTOS = ? WHERE DNI = ?
        """

        return try {
            val ps: PreparedStatement = connection.prepareStatement(sql)
            ps.setString(1, usuario.nombre)
            ps.setString(2, usuario.apellido)
            ps.setString(3, usuario.email)
            ps.setString(4, usuario.contrasenia)
            ps.setString(5, usuario.grupoSanguineo)
            ps.setString(6, usuario.fechaNacimiento)
            ps.setString(7, usuario.ciudad)
            ps.setString(8, usuario.pais)
            if (usuario.puntos != null) {
                ps.setInt(1, usuario.puntos ?: 0)
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

    // Método para obtener un usuario por DNI
    fun getUsuarioByDni(dni: String): Usuario? {
        val connection: Connection = MySqlConexion.getConexion() ?: return null
        val sql = "SELECT * FROM USUARIO WHERE DNI = ?"

        return try {
            val ps: PreparedStatement = connection.prepareStatement(sql)
            ps.setString(1, dni)
            val resultSet: ResultSet = ps.executeQuery()

            val usuario = if (resultSet.next()) {
                Usuario(
                    id = resultSet.getInt("ID_USUARIO"),
                    dni = resultSet.getString("DNI"),
                    nombre = resultSet.getString("NOMBRE"),
                    apellido = resultSet.getString("APELLIDO"),
                    email = resultSet.getString("EMAIL"),
                    contrasenia = resultSet.getString("CONTRASENIA"),
                    grupoSanguineo = resultSet.getString("GRUPO_SANGUINEO"),
                    fechaNacimiento = resultSet.getString("FECHA_NACIMIENTO"),
                    ciudad = resultSet.getString("CIUDAD"),
                    pais = resultSet.getString("PAIS"),
                    puntos = resultSet.getInt("PUNTOS").takeIf { !resultSet.wasNull() },
                )
            } else null

            resultSet.close()
            ps.close()
            connection.close()

            usuario
        } catch (e: SQLException) {
            e.printStackTrace()
            null
        }
    }

    // Método para obtener todos los usuarios
    fun getAllUsuarios(): List<Usuario> {
        val connection: Connection = MySqlConexion.getConexion() ?: return emptyList()
        val usuarios = mutableListOf<Usuario>()
        val sql = "SELECT * FROM USUARIO"

        return try {
            val statement: PreparedStatement = connection.prepareStatement(sql)
            val resultSet: ResultSet = statement.executeQuery()

            while (resultSet.next()) {
                val usuario = Usuario(
                    id = resultSet.getInt("ID_USUARIO"),
                    dni = resultSet.getString("DNI"),
                    nombre = resultSet.getString("NOMBRE"),
                    apellido = resultSet.getString("APELLIDO"),
                    email = resultSet.getString("EMAIL"),
                    contrasenia = resultSet.getString("CONTRASENIA"),
                    grupoSanguineo = resultSet.getString("GRUPO_SANGUINEO"),
                    fechaNacimiento = resultSet.getString("FECHA_NACIMIENTO"),
                    ciudad = resultSet.getString("CIUDAD"),
                    pais = resultSet.getString("PAIS"),
                    puntos = resultSet.getInt("PUNTOS").takeIf { !resultSet.wasNull() },
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

    fun UpdatePuntos(usuario: Usuario): Boolean {
        val connection: Connection = MySqlConexion.getConexion() ?: return false
        val sql = """
        UPDATE USUARIO SET PUNTOS = ? WHERE DNI = ?
        """
        return try {
            val ps: PreparedStatement = connection.prepareStatement(sql)
            ps.setInt(1, usuario.puntos ?: 0)
            ps.setString(2, usuario.dni)

            val rowsUpdated = ps.executeUpdate()

            ps.close()
            connection.close()

            rowsUpdated > 0  // retornamos true si se actualizo por lo menos 1 columna
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    fun getDNIById(id: Int): Int? {
        val connection: Connection = MySqlConexion.getConexion() ?: return null
        val sql = "SELECT DNI FROM USUARIO WHERE ID_USUARIO = ?"

        return try {
            val ps: PreparedStatement = connection.prepareStatement(sql)
            ps.setInt(1, id)
            val resultSet: ResultSet = ps.executeQuery()

            if (resultSet.next()) {
                resultSet.getInt("DNI") // Retorna el valor del DNI
            } else {
                null
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            null
        } finally {
            try {
                connection.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
    }
}
