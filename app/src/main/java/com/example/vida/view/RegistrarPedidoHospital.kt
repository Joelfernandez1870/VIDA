package com.example.vida.view

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.vida.R
import com.example.vida.data.database.PacienteDao
import com.example.vida.data.database.PedidoDonacionDao
import com.example.vida.models.PedidoDonacion
import java.util.*

class RegistrarPedidoHospital : AppCompatActivity() {

    // Lista para los nombres de los pacientes
    private val pacientes = mutableListOf<String>()
    private val idPacientes = mutableListOf<Int>() // Lista de IDs correspondientes

    // Variables de vista
    private lateinit var spinnerPaciente: Spinner
    private lateinit var spinnerEstado: Spinner
    private lateinit var inputFecha: EditText
    private lateinit var inputDescripcion: EditText
    private lateinit var btnRegistrarPedido: Button

    // ID de hospital obtenido de la sesión
    private lateinit var idHospital: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Habilitar diseño Edge-to-Edge
        setContentView(R.layout.activity_registrar_pedido_hospital)

        // Inicializar vistas
        spinnerPaciente = findViewById(R.id.spinnerPaciente)
        spinnerEstado = findViewById(R.id.spinnerEstado) // Spinner para el estado
        inputFecha = findViewById(R.id.inputFecha)
        inputDescripcion = findViewById(R.id.inputDescripcion)
        btnRegistrarPedido = findViewById(R.id.btnRegistrarPedido)

        // Obtener el ID de hospital de la sesión
        idHospital = LoginActivity.sesionGlobal.toString()

        // Cargar pacientes en el Spinner
        cargarPacientes()

        // Cargar opciones de estado en el Spinner
        cargarEstados()

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
        val pacientesList = PacienteDao.getPacientesByHospitalId(idHospital) // Obtener pacientes para el Spinner
        for (paciente in pacientesList) {
            pacientes.add(paciente.nombre)
            paciente.idPaciente?.let { idPacientes.add(it) }
        }

        // Configurar el adapter del Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, pacientes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPaciente.adapter = adapter
    }

    // Función para cargar las opciones de estado
    private fun cargarEstados() {
        val estados = listOf("Activo", "Inactivo")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, estados)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEstado.adapter = adapter
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
        val estado = spinnerEstado.selectedItem.toString()

        // Validar que los campos no estén vacíos antes de guardar
        if (fecha.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear objeto PedidoDonacion
        val pedido = PedidoDonacion(
            idPaciente = idPacienteSeleccionado,
            idHospital = idHospital,
            descripcion = descripcion,
            fecha = fecha,
            estado = estado
        )

        // Guardar el pedido en la base de datos
        val success = PedidoDonacionDao.insert(pedido)

        // Mostrar mensaje de éxito
        if (success) {
            Toast.makeText(this, "Pedido de donación registrado con éxito.", Toast.LENGTH_SHORT).show()

            // Limpiar los campos (opcional)
            inputFecha.text.clear()
            inputDescripcion.text.clear()
            spinnerPaciente.setSelection(0)
            spinnerEstado.setSelection(0)
        } else {
            Toast.makeText(this, "Error al registrar el pedido de donación.", Toast.LENGTH_SHORT).show()
        }
    }
}
