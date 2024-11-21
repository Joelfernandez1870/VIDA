package com.example.vida.view

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.vida.R
import com.example.vida.data.database.HospitalCentroDao
import com.example.vida.data.database.NotificacionUrgenteDao
import com.example.vida.data.database.PacienteDao
import com.example.vida.models.NotificacionUrgente
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class NotificacionesUrgentes : AppCompatActivity() {

    private lateinit var btnGuardarNotificacion: Button
    private lateinit var mensaje: EditText
    private lateinit var nombrePaciente: Spinner
    private lateinit var nombreHospital: Spinner
    private lateinit var tipoNotificacion: Spinner
    private val idPacientes = mutableListOf<Int>()
    private val idHospitales = mutableListOf<Int>()

    object SessionGlobal {
        var hospitalId: Int? = null
        var usuarioId: Int? = null

        fun clearSession() {
            hospitalId = null
            usuarioId = null
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notificaciones_urgentes)

        // Inicializar vistas
        btnGuardarNotificacion = findViewById(R.id.btnGuardarNotificacion)
        mensaje = findViewById(R.id.Mensaje)
        nombrePaciente = findViewById(R.id.spinnerPaciente)
        nombreHospital = findViewById(R.id.spinnerHospital)
        tipoNotificacion = findViewById(R.id.spinnerTipoNotificacion)

        // Cargar datos en los Spinners
        cargarDatosEnSpinners()

        // Configurar evento de clic para guardar notificación
        btnGuardarNotificacion.setOnClickListener {
            registrarNotificacion()
        }
    }

    private fun registrarNotificacion() {
        // Obtener ID del paciente seleccionado
        val idPacienteSeleccionado = idPacientes.getOrNull(nombrePaciente.selectedItemPosition)
        val idHospitalSeleccionado = idHospitales.getOrNull(nombreHospital.selectedItemPosition)

        // Validar que los campos no estén vacíos
        val mensajeTexto = mensaje.text.toString()
        val tipoSeleccionado = tipoNotificacion.selectedItem.toString()

        if (idPacienteSeleccionado == null || idHospitalSeleccionado == null || mensajeTexto.isEmpty() || tipoSeleccionado.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear objeto de Notificación Urgente
        val notificacion = NotificacionUrgente(
            idPaciente = idPacienteSeleccionado,
            idHospitalCentro = idHospitalSeleccionado,
            mensaje = mensajeTexto,
            fecha = LocalDateTime.now().toString(),
            tipoNotificacion = tipoSeleccionado

        )

        // Insertar en la base de datos
        lifecycleScope.launch(Dispatchers.IO) {
            val success = NotificacionUrgenteDao.insert(notificacion)

            withContext(Dispatchers.Main) {
                if (success) {
                    Toast.makeText(this@NotificacionesUrgentes, "Notificación registrada con éxito.", Toast.LENGTH_SHORT).show()

                    // Limpiar los campos
                    mensaje.text.clear()
                    nombrePaciente.setSelection(0)
                    nombreHospital.setSelection(0)
                    tipoNotificacion.setSelection(0)
                } else {
                    Toast.makeText(this@NotificacionesUrgentes, "Error al registrar la notificación.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun cargarDatosEnSpinners() {
        lifecycleScope.launch {
            try {
                // Obtener el ID del hospital logeado desde la sesión global
                val idHospitalLogeado = obtenerIdHospitalLogeado()

                // Obtener datos desde los DAOs
                val hospital = withContext(Dispatchers.IO) { HospitalCentroDao.getHospitalById(idHospitalLogeado) }
                val pacientes = withContext(Dispatchers.IO) { PacienteDao.getPacientesByHospitalId(
                    idHospitalLogeado.toString()
                ) }

                if (hospital == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@NotificacionesUrgentes, "No se encontró el hospital logeado.", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                // Preparar listas de IDs y nombres
                idHospitales.clear()
                idPacientes.clear()
                hospital.idHospitalesCentro?.let { idHospitales.add(it) }
                idPacientes.addAll(pacientes.mapNotNull { it.idPaciente })

                val nombresHospitales = listOf(hospital.nombreLugar)
                val nombresPacientes = pacientes.map { "${it.nombre} ${it.apellido}" }
                val tiposNotificacion = listOf("Alerta", "Aviso", "Información")

                // Configurar adaptadores
                withContext(Dispatchers.Main) {
                    val adapterHospital = ArrayAdapter(
                        this@NotificacionesUrgentes,
                        android.R.layout.simple_spinner_item,
                        nombresHospitales
                    )
                    adapterHospital.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    nombreHospital.adapter = adapterHospital

                    val adapterPaciente = ArrayAdapter(
                        this@NotificacionesUrgentes,
                        android.R.layout.simple_spinner_item,
                        nombresPacientes
                    )
                    adapterPaciente.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    nombrePaciente.adapter = adapterPaciente

                    val adapterTipoNotificacion = ArrayAdapter(
                        this@NotificacionesUrgentes,
                        android.R.layout.simple_spinner_item,
                        tiposNotificacion
                    )
                    adapterTipoNotificacion.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    tipoNotificacion.adapter = adapterTipoNotificacion
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@NotificacionesUrgentes, "Error al cargar datos.", Toast.LENGTH_SHORT).show()
                }
                e.printStackTrace()
            }
        }
    }



    private fun obtenerIdHospitalLogeado(): Int {
        val hospitalId = LoginActivity.sesionGlobal
        return hospitalId ?: throw IllegalStateException("No se encontró el ID del hospital en la sesión.")
    }

}
