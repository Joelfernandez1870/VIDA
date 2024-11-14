package com.example.vida.data.database

import com.example.vida.models.Paciente
import com.example.vida.models.PacienteSpinner
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

object PacienteDao {

    // Insertar un paciente en la base de datos
    fun insert(paciente: Paciente): Boolean {
        val connection = MySqlConexion.getConexion()
        val sql = """
            INSERT INTO PACIENTE (DNI, NOMBRE, APELLIDO, FECHA_NACIMIENTO, GRUPO_SANGUINEO, CIUDAD, PAIS, EMAIL) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """

        return try {
            val ps: PreparedStatement? = connection?.prepareStatement(sql)
            ps?.apply {
                setString(1, paciente.dni)
                setString(2, paciente.nombre)
                setString(3, paciente.apellido)
                setString(4, paciente.fechaNacimiento) // Asumimos que es String o Formato de Fecha adecuado
                setString(5, paciente.grupoSanguineo)
                setString(6, paciente.ciudad)
                setString(7, paciente.pais)
                setString(8, paciente.email) // Asegúrate de que aquí se esté asignando el valor de email
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
                    dni = resultSet.getString("DNI"),
                    nombre = resultSet.getString("NOMBRE"),
                    apellido = resultSet.getString("APELLIDO"),
                    fechaNacimiento = resultSet.getString("FECHA_NACIMIENTO"),
                    grupoSanguineo = resultSet.getString("GRUPO_SANGUINEO"),
                    ciudad = resultSet.getString("CIUDAD"),
                    pais = resultSet.getString("PAIS"),
                    email = resultSet.getString("EMAIL") // Asegúrate de que aquí esté el email
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

    fun getPacientesForSpinner(): List<PacienteSpinner> {
        val pacientesList = mutableListOf<PacienteSpinner>()
        val connection = MySqlConexion.getConexion() ?: return pacientesList
        val sql = "SELECT ID_PACIENTE, nombre FROM PACIENTE" // Solo seleccionamos el nombre

        return try {
            val ps: PreparedStatement = connection.prepareStatement(sql)
            val resultSet: ResultSet = ps.executeQuery()

            while (resultSet.next()) {
                val id = resultSet.getInt("ID_PACIENTE")  // Usamos el nombre correcto de la columna
                val nombre = resultSet.getString("nombre") ?: "" // Aseguramos que 'nombre' no sea nulo

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

}

