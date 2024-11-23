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
    private var hospitales = mutableListOf<String>()
    private var notificacionId: Int = -1
    private var notificacionIdPaciente: Int? = null


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
                        hospitales.add(HospitalCentroDao.getHospitalById(notificacion.idHospitalCentro)?.nombreLugar ?: "")
                    }

                    withContext(Dispatchers.Main) {
                        inputMensaje.setText(notificacion.mensaje)

                        // Seleccionar tipo de notificación
                        val tipoIndex = tiposNotificaciones.indexOf(notificacion.tipoNotificacion)
                        if (tipoIndex != -1) spinnerTipoNotificacion.setSelection(tipoIndex)

                        // Seleccionar paciente
                        val pacienteIndex = pacientes.indexOfFirst { it.idPaciente == notificacion.idPaciente }
                        if (pacienteIndex != -1) spinnerPaciente.setSelection(pacienteIndex)

                        // Seleccionar hospital
                        val hospitalIndex = hospitales.indexOf(notificacion.nombreLugar)
                        if (hospitalIndex != -1) spinnerHospital.setSelection(hospitalIndex)

                        // Configurar adaptadores para spinners
                        configurarSpinnerPaciente()
                        configurarSpinnerHospital()
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
        val adapter = ArrayAdapter<Paciente>(
            this,
            android.R.layout.simple_spinner_item,
            pacientes
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        spinnerPaciente.adapter = adapter

        spinnerPaciente.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val pacienteSeleccionado = parent.getItemAtPosition(position) as Paciente
                // Guarda el ID del paciente seleccionado
                notificacionIdPaciente = pacienteSeleccionado.idPaciente!!
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }


    private fun configurarSpinnerHospital() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            hospitales
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerHospital.adapter = adapter
    }

    private fun guardarCambios() {
        val mensaje = inputMensaje.text.toString()
        val tipoNotificacion = spinnerTipoNotificacion.selectedItem.toString()

        if (mensaje.isEmpty() || tipoNotificacion.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        if (notificacionIdPaciente == null) {
            Toast.makeText(this, "Debes seleccionar un paciente.", Toast.LENGTH_SHORT).show()
            return
        }

        val hospitalSeleccionado = hospitales.getOrNull(spinnerHospital.selectedItemPosition)
        if (hospitalSeleccionado.isNullOrEmpty()) {
            Toast.makeText(this, "Debes seleccionar un hospital.", Toast.LENGTH_SHORT).show()
            return
        }

        val fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        lifecycleScope.launch {
            val idHospitalCentro = withContext(Dispatchers.IO) {
                HospitalCentroDao.getIdByName(hospitalSeleccionado)
            }

            if (idHospitalCentro == null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditarNotificacion, "Error al obtener el hospital seleccionado.", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }

            val success = withContext(Dispatchers.IO) {
                val notificacionActualizada = NotificacionUrgente(
                    idNotificacion = notificacionId,
                    idHospitalCentro = idHospitalCentro,
                    idPaciente = notificacionIdPaciente,
                    mensaje = mensaje,
                    tipoNotificacion = tipoNotificacion,
                    fecha = fechaActual
                )
                NotificacionUrgenteDao.updateNotificacion(notificacionActualizada)
            }

            withContext(Dispatchers.Main) {
                if (success) {
                    Toast.makeText(this@EditarNotificacion, "Notificación actualizada correctamente", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditarNotificacion, "Error al actualizar la notificación", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }




}
