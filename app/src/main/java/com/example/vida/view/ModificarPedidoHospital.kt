package com.example.vida.view

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.vida.R
import com.example.vida.data.database.PedidoDonacionDao
import com.example.vida.models.ModificarPedidoDonacion
import com.example.vida.data.database.MySqlConexion
import java.sql.Connection
import java.sql.PreparedStatement


class ModificarPedidoHospital : AppCompatActivity() {

    private lateinit var spinnerPaciente: Spinner
    private lateinit var inputFecha: EditText
    private lateinit var inputDescripcion: EditText
    private lateinit var inputEstado: EditText
    private lateinit var btnModificarPedido: Button

    private var idEmergencia: Int = -1 // Recibido del Intent
    private var pacientesList = mutableListOf<String>() // Lista de nombres de pacientes
    private var pacientesIdList = mutableListOf<Int>() // Lista de IDs de pacientes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modificar_pedido_hospital)

        // Inicialización de vistas
        spinnerPaciente = findViewById(R.id.spinnerPaciente)
        inputFecha = findViewById(R.id.inputFecha)
        inputDescripcion = findViewById(R.id.inputDescripcion)
        inputEstado = findViewById(R.id.inputEstado)
        btnModificarPedido = findViewById(R.id.btnModificarPedido)

        // Recuperar el ID del pedido de donación desde el Intent
        idEmergencia = intent.getIntExtra("ID_EMERGENCIA", -1)

        // Verificar si el ID está presente y cargar los datos
        if (idEmergencia != -1) {
            cargarDatosPedido(idEmergencia)
        } else {
            Toast.makeText(this, "ID de emergencia no válido", Toast.LENGTH_SHORT).show()
            finish() // Cerrar la actividad si el ID es inválido
        }

        // Lógica para modificar los datos
        btnModificarPedido.setOnClickListener {
            val fecha = inputFecha.text.toString()
            val descripcion = inputDescripcion.text.toString()
            val estado = inputEstado.text.toString()

            if (fecha.isEmpty() || descripcion.isEmpty() || estado.isEmpty()) {
                Toast.makeText(this, "Todos los campos deben ser llenados", Toast.LENGTH_SHORT).show()
            } else {
                // Obtener el paciente seleccionado
                val pacienteSeleccionadoId = pacientesIdList[spinnerPaciente.selectedItemPosition]

                // Crear el objeto ModificarPedidoDonacion
                val pedidoModificado = ModificarPedidoDonacion(idEmergencia, descripcion, fecha, estado)
                if (PedidoDonacionDao.update(pedidoModificado)) {
                    Toast.makeText(this, "Pedido actualizado exitosamente", Toast.LENGTH_SHORT).show()
                    finish() // Cerrar la actividad después de modificar
                } else {
                    Toast.makeText(this, "Error al actualizar el pedido", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Cargar los datos del pedido de donación para la modificación
    private fun cargarDatosPedido(idEmergencia: Int) {
        // Cargar los datos del pedido
        Thread {
            var connection: Connection? = null
            var preparedStatement: PreparedStatement? = null

            try {
                connection = MySqlConexion.getConexion()
                val query = """
                    SELECT p.FECHA, p.DESCRIPCION, p.ESTADO, p.ID_PACIENTE
                    FROM PEDIDO_DONACION p
                    WHERE p.ID_EMERGENCIA = ?
                """
                preparedStatement = connection?.prepareStatement(query)
                preparedStatement?.setInt(1, idEmergencia)
                val resultSet = preparedStatement?.executeQuery()

                if (resultSet?.next() == true) {
                    val fecha = resultSet.getString("FECHA")
                    val descripcion = resultSet.getString("DESCRIPCION")
                    val estado = resultSet.getString("ESTADO")
                    val idPaciente = resultSet.getInt("ID_PACIENTE")

                    runOnUiThread {
                        inputFecha.setText(fecha)
                        inputDescripcion.setText(descripcion)
                        inputEstado.setText(estado)

                        // Cargar el Spinner de pacientes
                        cargarPacientesSpinner(idPaciente)
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Error al cargar los datos del pedido", Toast.LENGTH_SHORT).show()
                }
            } finally {
                preparedStatement?.close()
                connection?.close()
            }
        }.start()
    }

    // Cargar la lista de pacientes en el Spinner
    private fun cargarPacientesSpinner(idPacienteSeleccionado: Int) {
        Thread {
            var connection: Connection? = null
            var preparedStatement: PreparedStatement? = null

            try {
                connection = MySqlConexion.getConexion()
                val query = """
                    SELECT ID_PACIENTE, NOMBRE
                    FROM PACIENTE
                """
                preparedStatement = connection?.prepareStatement(query)
                val resultSet = preparedStatement?.executeQuery()

                pacientesList.clear()
                pacientesIdList.clear()

                while (resultSet?.next() == true) {
                    val idPaciente = resultSet.getInt("ID_PACIENTE")
                    val nombrePaciente = resultSet.getString("NOMBRE")

                    pacientesList.add(nombrePaciente)
                    pacientesIdList.add(idPaciente)
                }

                runOnUiThread {
                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, pacientesList)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerPaciente.adapter = adapter

                    // Establecer el paciente seleccionado
                    val selectedIndex = pacientesIdList.indexOf(idPacienteSeleccionado)
                    spinnerPaciente.setSelection(selectedIndex)
                }

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Error al cargar los pacientes", Toast.LENGTH_SHORT).show()
                }
            } finally {
                preparedStatement?.close()
                connection?.close()
            }
        }.start()
    }
}
