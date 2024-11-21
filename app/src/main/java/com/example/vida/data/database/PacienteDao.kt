package com.example.vida.data.database

import com.example.vida.models.Paciente
import com.example.vida.models.PacienteSpinner
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

object PacienteDao {

    // Método para verificar si un DNI ya está registrado
    private fun isDniRegistered(dni: String): Boolean {
        val connection: Connection = MySqlConexion.getConexion() ?: return false
        val sql = "SELECT DNI FROM PACIENTE WHERE DNI = ?"

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

    // Insertar un paciente en la base de datos
    fun insert(paciente: Paciente): Boolean {
        val connection = MySqlConexion.getConexion()
        val sql = """
            INSERT INTO PACIENTE (DNI, NOMBRE, APELLIDO, FECHA_NACIMIENTO, GRUPO_SANGUINEO, CIUDAD, PAIS, EMAIL, HOSPITAL_ID ) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """

        return try {
            // Verificar si el DNI ya está registrado
            if (isDniRegistered(paciente.dni)) {
                throw IllegalArgumentException("El DNI ingresado ya está registrado.")
            }

            // Proceder con la inserción
            val ps: PreparedStatement? = connection?.prepareStatement(sql)
            ps?.apply {
                setString(1, paciente.dni)
                setString(2, paciente.nombre)
                setString(3, paciente.apellido)
                setString(4, paciente.fechaNacimiento) // Asumimos que es String o Formato de Fecha adecuado
                setString(5, paciente.grupoSanguineo)
                setString(6, paciente.ciudad)
                setString(7, paciente.pais)
                setString(8, paciente.email)
                setString(9, paciente.hospitalId.toString())
            }
            val rowsInserted = ps?.executeUpdate()
            ps?.close()
            connection?.close()

            rowsInserted != null && rowsInserted > 0

        } catch (e: IllegalArgumentException) {
            // Mostrar mensaje específico para DNI duplicado
            println(e.message)
            false
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    // Obtener todos los pacientes
    fun getAllPacientes(): List<Paciente> {
        val connection: Connection = MySqlConexion.getConexion() ?: return emptyList()
        val pacientes = mutableListOf<Paciente>()
        val sql = "SELECT * FROM PACIENTE"

        return try {
            val statement: PreparedStatement = connection.prepareStatement(sql)
            val resultSet: ResultSet = statement.executeQuery()

            while (resultSet.next()) {
                val paciente = Paciente(
                    idPaciente = resultSet.getInt("ID_PACIENTE"),
                    dni = resultSet.getString("DNI"),
                    nombre = resultSet.getString("NOMBRE"),
                    apellido = resultSet.getString("APELLIDO"),
                    fechaNacimiento = resultSet.getString("FECHA_NACIMIENTO"),
                    grupoSanguineo = resultSet.getString("GRUPO_SANGUINEO"),
                    ciudad = resultSet.getString("CIUDAD"),
                    pais = resultSet.getString("PAIS"),
                    email = resultSet.getString("EMAIL"),
                    hospitalId = resultSet.getString("HOSPITAL_ID")
                )
                pacientes.add(paciente)
            }

            resultSet.close()
            statement.close()
            connection.close()

            pacientes
        } catch (e: SQLException) {
            e.printStackTrace()
            emptyList()
        }
    }


    // Obtener pacientes para un Spinner
    fun getPacientesForSpinner(): List<PacienteSpinner> {
        val pacientesList = mutableListOf<PacienteSpinner>()
        val connection = MySqlConexion.getConexion() ?: return pacientesList
        val sql = "SELECT ID_PACIENTE, NOMBRE FROM PACIENTE" // Solo seleccionamos el nombre

        return try {
            val ps: PreparedStatement = connection.prepareStatement(sql)
            val resultSet: ResultSet = ps.executeQuery()

            while (resultSet.next()) {
                val id = resultSet.getInt("ID_PACIENTE")  // Usamos el nombre correcto de la columna
                val nombre = resultSet.getString("NOMBRE") ?: "" // Aseguramos que 'nombre' no sea nulo

                // Crear un objeto PacienteSpinner solo con el nombre
                pacientesList.add(PacienteSpinner(id, nombre))
            }

            resultSet.close()
            ps.close()
            connection.close()

            pacientesList
        } catch (e: SQLException) {
            e.printStackTrace()
            pacientesList
        }
    }

    // Obtener todos los pacientes por ID del hospital
    fun getPacientesByHospitalId(hospitalId: String): List<Paciente> {
        val connection: Connection = MySqlConexion.getConexion() ?: return emptyList()
        val pacientes = mutableListOf<Paciente>()
        val sql = "SELECT * FROM PACIENTE WHERE HOSPITAL_ID = ?"

        return try {
            val ps: PreparedStatement = connection.prepareStatement(sql)
            ps.setString(1, hospitalId)
            val resultSet: ResultSet = ps.executeQuery()

            while (resultSet.next()) {
                val paciente = Paciente(
                    idPaciente = resultSet.getInt("ID_PACIENTE"),
                    dni = resultSet.getString("DNI"),
                    nombre = resultSet.getString("NOMBRE"),
                    apellido = resultSet.getString("APELLIDO"),
                    fechaNacimiento = resultSet.getString("FECHA_NACIMIENTO"),
                    grupoSanguineo = resultSet.getString("GRUPO_SANGUINEO"),
                    ciudad = resultSet.getString("CIUDAD"),
                    pais = resultSet.getString("PAIS"),
                    email = resultSet.getString("EMAIL"),
                    hospitalId = resultSet.getString("HOSPITAL_ID")
                )
                pacientes.add(paciente)
            }

            resultSet.close()
            ps.close()
            connection.close()

            pacientes
        } catch (e: SQLException) {
            e.printStackTrace()
            emptyList()
        }
    }

}
