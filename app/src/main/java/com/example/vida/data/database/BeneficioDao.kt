package com.example.vida.data.database

import com.example.vida.models.Beneficio
import com.example.vida.models.Usuario
import com.example.vida.view.Beneficios
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

object BeneficioDao {

     fun getAllBeneficios(): List<Beneficio> {
        val connection: Connection = MySqlConexion.getConexion() ?: return emptyList()
        val beneficios = mutableListOf<Beneficio>()
        val sql = "SELECT * FROM beneficios"

        return try {
            val statement: PreparedStatement = connection.prepareStatement(sql)
            val resultSet: ResultSet = statement.executeQuery()

            while (resultSet.next()) {
                val beneficio = Beneficio(

                    idBeneficio = resultSet.getInt("ID_BENEFICIO"),
                    descripcion = resultSet.getString("DESCRIPCION"),
                    puntos_necesario = resultSet.getInt("PUNTOS_NECESARIOS"),
                    nombre_local = resultSet.getString("NOMBRE_LOCAL")
                    )
                beneficios.add(beneficio)
            }

            resultSet.close()
            statement.close()
            connection.close()

            beneficios
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

            rowsUpdated > 0  // Retorna true si al menos una fila fue actualizada
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }
}
