package com.example.vida.view

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.vida.R
import com.example.vida.data.database.HospitalCentroDao
import com.example.vida.data.database.NotificacionUrgenteDao
import com.example.vida.data.database.PacienteDao
import com.example.vida.models.HospitalCentro
import com.example.vida.models.NotificacionUrgente
import com.example.vida.models.Paciente
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EditarNotificacion : AppCompatActivity() {

    private lateinit var inputMensaje: EditText
    private lateinit var spinnerTipoNotificacion: Spinner
    private lateinit var spinnerHospital: Spinner
    private lateinit var spinnerPaciente: Spinner
    private lateinit var btnGuardarCambios: Button
    private val tiposNotificaciones = listOf("Alerta", "Aviso", "Información")
    private var pacientes = mutableListOf<Paciente>()
    private var hospitales = mutableListOf<HospitalCentro>()
    private var notificacionId: Int = -1
    private var notificacionIdPaciente: Int? = null
    private var notificacionIdHospital: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_notificacion)

        // Inicializar vistas
        inputMensaje = findViewById(R.id.Mensaje)
        spinnerTipoNotificacion = findViewById(R.id.spinnerTipoNotificacion)
        spinnerHospital = findViewById(R.id.spinnerHospital)
        spinnerPaciente = findViewById(R.id.spinnerPaciente)
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios)

        // Configurar spinner de tipo de notificación
        configurarSpinnerTipoNotificacion()

        // Obtener el ID de la notificación
        notificacionId = intent.getIntExtra("NOTIFICACION_ID", -1)
        if (notificacionId == -1) {
            Toast.makeText(this, "Error: ID de notificación no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Cargar datos de la notificación
        cargarDatosNotificacion()

        // Configurar botón de guardar
        btnGuardarCambios.setOnClickListener {
            guardarCambios()
        }
    }

    private fun configurarSpinnerTipoNotificacion() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            tiposNotificaciones
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoNotificacion.adapter = adapter
    }

    private fun cargarDatosNotificacion() {
        lifecycleScope.launch {
            try {
                val notificacion = withContext(Dispatchers.IO) {
                    NotificacionUrgenteDao.getNotificacionById(notificacionId)
                }

                if (notificacion != null) {
                    withContext(Dispatchers.IO) {
                        pacientes.clear()
                        pacientes.addAll(PacienteDao.getPacientesByHospitalId(notificacion.idHospitalCentro.toString()))

                        hospitales.clear()
                        HospitalCentroDao.getHospitalById(notificacion.idHospitalCentro)?.let {
                            hospitales.add(it) // Agrega el objeto HospitalCentro
                        }
                    }

                    withContext(Dispatchers.Main) {
                        inputMensaje.setText(notificacion.mensaje)

                        // Seleccionar tipo de notificación
                        val tipoIndex = tiposNotificaciones.indexOf(notificacion.tipoNotificacion)
                        if (tipoIndex != -1) spinnerTipoNotificacion.setSelection(tipoIndex)

                        // Configurar adaptadores para spinners
                        configurarSpinnerPaciente()
                        configurarSpinnerHospital()

                        // Seleccionar paciente
                        val pacienteIndex = pacientes.indexOfFirst { it.idPaciente == notificacion.idPaciente }
                        if (pacienteIndex != -1) spinnerPaciente.setSelection(pacienteIndex)

                        // Seleccionar hospital
                        val hospitalIndex = hospitales.indexOfFirst { it.idHospitalesCentro == notificacion.idHospitalCentro }
                        if (hospitalIndex != -1) spinnerHospital.setSelection(hospitalIndex)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@EditarNotificacion, "No se encontró la notificación", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditarNotificacion, "Error al cargar la notificación", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun configurarSpinnerPaciente() {
        if (pacientes.isEmpty()) {
            Toast.makeText(this, "No hay pacientes disponibles.", Toast.LENGTH_SHORT).show()
            return
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            pacientes.map { "${it.idPaciente} - ${it.nombre}" } // Muestra ID y nombre del paciente
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        spinnerPaciente.adapter = adapter

        spinnerPaciente.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val pacienteSeleccionado = pacientes[position]
                // Guarda el ID del paciente seleccionado
                notificacionIdPaciente = pacienteSeleccionado.idPaciente
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun configurarSpinnerHospital() {
        if (hospitales.isEmpty()) {
            Toast.makeText(this, "No hay hospitales disponibles.", Toast.LENGTH_SHORT).show()
            return
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            hospitales // Lista de objetos HospitalCentro
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        spinnerHospital.adapter = adapter

        spinnerHospital.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val hospitalSeleccionado = hospitales[position]
                // Guarda el ID del hospital seleccionado
                notificacionIdHospital = hospitalSeleccionado.idHospitalesCentro
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun guardarCambios() {
        val mensaje = inputMensaje.text.toString()
        val tipoNotificacion = spinnerTipoNotificacion.selectedItem.toString()

        if (mensaje.isEmpty() || tipoNotificacion.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios.", Toast.LENGTH_SHORT).show()
            return
        }

        if (notificacionIdPaciente == null) {
            Toast.makeText(this, "Debes seleccionar un paciente.", Toast.LENGTH_SHORT).show()
            return
        }

        if (notificacionIdHospital == null) {
            Toast.makeText(this, "Debes seleccionar un hospital.", Toast.LENGTH_SHORT).show()
            return
        }

        val fechaActual = LocalDateTime.now().toString()

        println("Preparando para entrar al bloque lifecycleScope.launch")
        lifecycleScope.launch {
            println("Entrando al bloque lifecycleScope.launch")
            val success = withContext(Dispatchers.IO) {
                println("Entrando al bloque Dispatchers.IO")
                val notificacionActualizada = NotificacionUrgente(
                    idNotificacion = notificacionId,
                    idHospitalCentro = notificacionIdHospital!!,
                    idPaciente = notificacionIdPaciente,
                    mensaje = mensaje,
                    tipoNotificacion = tipoNotificacion,
                    fecha = fechaActual
                )
                println("Datos de la notificación actualizada: $notificacionActualizada")
                NotificacionUrgenteDao.updateNotificacion(notificacionActualizada)
            }

            withContext(Dispatchers.Main) {
                println("Regresando al hilo principal")
                if (success) {
                    Toast.makeText(this@EditarNotificacion, "Notificación actualizada correctamente.", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditarNotificacion, "Error al actualizar la notificación.", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}
