package com.example.vida.view

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.vida.R
import com.example.vida.data.database.PacienteDao
import com.example.vida.data.database.PedidoDonacionDao
import com.example.vida.models.PacienteSpinner
import java.util.*
import com.example.vida.models.PedidoDonacion

class RegistrarPedidoHospital : AppCompatActivity() {

    // Lista para los nombres de los pacientes
    private val pacientes = mutableListOf<String>()
    private val idPacientes = mutableListOf<Int>() // Lista de IDs correspondientes

    // Variables de vista
    private lateinit var spinnerPaciente: Spinner
    private lateinit var inputFecha: EditText
    private lateinit var inputDescripcion: EditText
    private lateinit var inputEstado: EditText
    private lateinit var btnRegistrarPedido: Button

    // ID de hospital obtenido de la sesión
    private var idHospital: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Habilitar diseño Edge-to-Edge
        setContentView(R.layout.activity_registrar_pedido_hospital)

        // Inicializar vistas
        spinnerPaciente = findViewById(R.id.spinnerPaciente)
        inputFecha = findViewById(R.id.inputFecha)
        inputDescripcion = findViewById(R.id.inputDescripcion)
        inputEstado = findViewById(R.id.inputEstado)
        btnRegistrarPedido = findViewById(R.id.btnRegistrarPedido)

        // Obtener el ID de hospital de la sesión
        idHospital = LoginActivity.sesionGlobal

        // Cargar pacientes en el Spinner
        cargarPacientes()

        // Configurar DatePicker para el campo de fecha
        inputFecha.setOnClickListener {
            mostrarDatePicker()
        }

        // Configurar listener para el botón de registro
        btnRegistrarPedido.setOnClickListener {
            registrarPedido()
        }
    }

    // Función para cargar pacientes desde la base de datos
    private fun cargarPacientes() {
        val pacientesList = PacienteDao.getPacientesForSpinner() // Obtener pacientes para el Spinner
        for (paciente in pacientesList) {
            pacientes.add(paciente.nombre)
            idPacientes.add(paciente.id)
        }

        // Configurar el adapter del Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, pacientes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPaciente.adapter = adapter
    }

    // Función para mostrar el DatePicker y seleccionar la fecha como String
    private fun mostrarDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Formato de fecha "dd/MM/yyyy" como String
                inputFecha.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
            },
            year, month, day
        )

        datePickerDialog.show()
    }

    // Función para registrar el pedido en la base de datos
    private fun registrarPedido() {
        // Obtener ID del paciente seleccionado
        val idPacienteSeleccionado = idPacientes[spinnerPaciente.selectedItemPosition]

        // Obtener otros valores de los campos de entrada
        val fecha = inputFecha.text.toString()
        val descripcion = inputDescripcion.text.toString()
        val estado = inputEstado.text.toString()

        // Validar que los campos no estén vacíos antes de guardar
        if (fecha.isEmpty() || descripcion.isEmpty() || estado.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        // Validar que el ID de hospital no sea nulo
        if (idHospital == null) {
            Toast.makeText(this, "Error: ID de hospital no disponible.", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear objeto PedidoDonacion
        val pedido = PedidoDonacion(
            idPaciente = idPacienteSeleccionado,
            idHospital = idHospital!!,
            descripcion = descripcion,
            fecha = fecha,
            estado = estado
        )

        // Guardar el pedido en la base de datos (ejemplo con PedidoDonacionDao)
        val success = PedidoDonacionDao.insert(pedido)

        // Mostrar mensaje de éxito
        if (success) {
            Toast.makeText(this, "Pedido de donación registrado con éxito.", Toast.LENGTH_SHORT).show()

            // Limpiar los campos (opcional)
            inputFecha.text.clear()
            inputDescripcion.text.clear()
            inputEstado.text.clear()
            spinnerPaciente.setSelection(0)
        } else {
            Toast.makeText(this, "Error al registrar el pedido de donación.", Toast.LENGTH_SHORT).show()
        }
    }
}
