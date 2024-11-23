package com.example.vida.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.vida.R
import com.example.vida.data.database.NotificacionUrgenteDao
import com.example.vida.data.database.PacienteDao
import com.example.vida.models.NotificacionUrgente
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListadoNotificacionesHospital : AppCompatActivity() {
    private val loggedInHospitalId = LoginActivity.sesionGlobal
    companion object {
        const val REQUEST_CODE_ADD_NOTIFICATION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_notificaciones_hospital)
        cargarSpinners(loggedInHospitalId!!)
        configurarFiltros(loggedInHospitalId)


        if (loggedInHospitalId == null || loggedInHospitalId <= 0) {
            Toast.makeText(this, "No se pudo identificar el hospital logeado", Toast.LENGTH_SHORT).show()
            return
        }

        cargarNotificaciones(loggedInHospitalId)

        val btnCargarNotificaciones = findViewById<FloatingActionButton>(R.id.fabAgregarNotificacion)
        btnCargarNotificaciones.setOnClickListener {
            val intent = Intent(this, NotificacionesUrgentes::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_NOTIFICATION)
        }
    }
    override fun onResume() {
        super.onResume()
        if (loggedInHospitalId != null) {
            cargarNotificaciones(loggedInHospitalId)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_ADD_NOTIFICATION && resultCode == RESULT_OK) {
            val loggedInHospitalId = LoginActivity.sesionGlobal
            if (loggedInHospitalId != null && loggedInHospitalId > 0) {
                cargarNotificaciones(loggedInHospitalId)
            }
        }
    }

    class NotificacionAdapter(
        private val context: AppCompatActivity,
        private val data: List<NotificacionUrgente>,
        private val onNotificationDeleted: () -> Unit
    ) : ArrayAdapter<NotificacionUrgente>(context, R.layout.notificacion_hospital_item, data) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val inflater = context.layoutInflater
            val rowView = convertView ?: inflater.inflate(R.layout.notificacion_hospital_item, parent, false)

            val tipoNotificacion = rowView.findViewById<TextView>(R.id.tvTipoNotificacion)
            val fecha = rowView.findViewById<TextView>(R.id.tvFecha)
            val paciente = rowView.findViewById<TextView>(R.id.tvPaciente)
            val hospital = rowView.findViewById<TextView>(R.id.tvHospital)
            val grupoSanguineo = rowView.findViewById<TextView>(R.id.tvGrupoSanguineo)
            val btnEditar = rowView.findViewById<ImageButton>(R.id.btnEditar)
            val mensaje = rowView.findViewById<TextView>(R.id.tvMensaje)
            val btnEliminar = rowView.findViewById<ImageButton>(R.id.btnEliminar)

            val notificacion = data[position]

            mensaje.text = notificacion.mensaje
            tipoNotificacion.text = "Tipo: ${notificacion.tipoNotificacion}"
            fecha.text = "Fecha: ${notificacion.fecha}"
            paciente.text = "Paciente: ${notificacion.nombrePaciente ?: "Desconocido"} ${notificacion.apellidoPaciente ?: ""}"
            hospital.text = "Hospital: ${notificacion.nombreLugar ?: "Desconocido"}"
            grupoSanguineo.text = "Grupo Sanguíneo: ${notificacion.grupoSanguineo ?: "No especificado"}"

            // Cambiar color del texto según el tipo de notificación
            val color = when (notificacion.tipoNotificacion?.lowercase()) {
                "alerta" -> ContextCompat.getColor(context, R.color.alert_red) // Rojo para alerta
                "información" -> ContextCompat.getColor(context, R.color.info_blue) // Azul para información
                "aviso" -> ContextCompat.getColor(context, R.color.warning_orange) // Naranja para advertencia
                else -> ContextCompat.getColor(context, R.color.default_black) // Negro para otros casos
            }

            // Aplicar color al tipo de notificación y mensaje
            tipoNotificacion.setTextColor(color)
            mensaje.setTextColor(color)

            btnEditar.setOnClickListener {
                val idNotificacion = notificacion.idNotificacion
                if (idNotificacion != null && idNotificacion > 0) {
                    val intent = Intent(context, EditarNotificacion::class.java)
                    intent.putExtra("NOTIFICACION_ID", idNotificacion)
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "ID de notificación no válido en Listado", Toast.LENGTH_SHORT).show()
                }
            }

            btnEliminar.setOnClickListener {
                val builder = android.app.AlertDialog.Builder(context)
                builder.setTitle("Confirmar eliminación")
                builder.setMessage("¿Estás seguro de que deseas eliminar esta notificación?")

                builder.setPositiveButton("Eliminar") { dialog, _ ->
                    context.lifecycleScope.launch {
                        val success = withContext(Dispatchers.IO) {
                            NotificacionUrgenteDao.expireById(notificacion.idHospitalCentro, notificacion.mensaje)
                        }

                        if (success) {
                            Toast.makeText(context, "Notificación eliminada correctamente", Toast.LENGTH_SHORT).show()
                            onNotificationDeleted()
                        } else {
                            Toast.makeText(context, "Error al marcar la notificación como expirada", Toast.LENGTH_SHORT).show()
                        }
                        dialog.dismiss()
                    }
                }

                builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }

                val alertDialog = builder.create()
                alertDialog.show()
            }

            return rowView
        }
    }


    private fun cargarNotificaciones(loggedInHospitalId: Int) {
        val listView = findViewById<ListView>(R.id.lvNotificaciones)
        val tituloTextView = findViewById<TextView>(R.id.tvTituloNotificaciones)

        lifecycleScope.launch {
            try {
                val notificaciones = withContext(Dispatchers.IO) {
                    NotificacionUrgenteDao.getNotificacionesUrgentesByHospital(loggedInHospitalId)
                }

                withContext(Dispatchers.Main) {
                    if (notificaciones.isNotEmpty()) {
                        val adapter = NotificacionAdapter(this@ListadoNotificacionesHospital, notificaciones) {
                            cargarNotificaciones(loggedInHospitalId)
                        }
                        listView.adapter = adapter
                    } else {
                        tituloTextView.text = "No hay notificaciones para este hospital"
                        listView.adapter = null
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    tituloTextView.text = "Error al cargar las notificaciones"
                }
            }
        }
    }

    private fun cargarSpinners(loggedInHospitalId: Int) {
        lifecycleScope.launch {
            val pacientes = withContext(Dispatchers.IO) {
                PacienteDao.getPacientesByHospitalId(loggedInHospitalId.toString()) // Método devuelve una lista de objetos Paciente
            }
            val tiposNotificacion = listOf("Todos los tipos", "Alerta", "Información", "Aviso") // Tipos predefinidos o dinámicos

            withContext(Dispatchers.Main) {
                // Adaptador para el spinner de pacientes
                val pacienteAdapter = ArrayAdapter(
                    this@ListadoNotificacionesHospital,
                    android.R.layout.simple_spinner_item,
                    listOf("Todos los pacientes") + pacientes.map { "${it.nombre} ${it.apellido}" } // Transformar objetos a Strings
                )
                pacienteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                findViewById<Spinner>(R.id.spinnerPacientes).adapter = pacienteAdapter

                // Adaptador para el spinner de tipos de notificación
                val tipoAdapter = ArrayAdapter(
                    this@ListadoNotificacionesHospital,
                    android.R.layout.simple_spinner_item,
                    tiposNotificacion
                )
                tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                findViewById<Spinner>(R.id.spinnerTiposNotificacion).adapter = tipoAdapter
            }
        }
    }


    private fun configurarFiltros(loggedInHospitalId: Int) {
        val spinnerPacientes = findViewById<Spinner>(R.id.spinnerPacientes)
        val spinnerTiposNotificacion = findViewById<Spinner>(R.id.spinnerTiposNotificacion)

        spinnerPacientes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val pacienteSeleccionado = parent?.getItemAtPosition(position) as String
                val tipoSeleccionado = spinnerTiposNotificacion.selectedItem as String
                filtrarNotificaciones(loggedInHospitalId, pacienteSeleccionado, tipoSeleccionado)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerTiposNotificacion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val tipoSeleccionado = parent?.getItemAtPosition(position) as String
                val pacienteSeleccionado = spinnerPacientes.selectedItem as String
                filtrarNotificaciones(loggedInHospitalId, pacienteSeleccionado, tipoSeleccionado)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun filtrarNotificaciones(
        loggedInHospitalId: Int,
        pacienteSeleccionado: String?,
        tipoSeleccionado: String?
    ) {
        lifecycleScope.launch {
            val pacienteFiltro = if (pacienteSeleccionado == "Todos los pacientes") null else pacienteSeleccionado
            val tipoFiltro = if (tipoSeleccionado == "Todos los tipos") null else tipoSeleccionado

            val notificacionesFiltradas = withContext(Dispatchers.IO) {
                NotificacionUrgenteDao.getNotificacionesPorFiltros(loggedInHospitalId, pacienteFiltro, tipoFiltro)
            }

            withContext(Dispatchers.Main) {
                val listView = findViewById<ListView>(R.id.lvNotificaciones)
                if (notificacionesFiltradas.isNotEmpty()) {
                    val adapter = NotificacionAdapter(this@ListadoNotificacionesHospital, notificacionesFiltradas) {
                        filtrarNotificaciones(loggedInHospitalId, pacienteSeleccionado, tipoSeleccionado)
                    }
                    listView.adapter = adapter
                } else {
                    Toast.makeText(this@ListadoNotificacionesHospital, "No se encontraron notificaciones", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



}
