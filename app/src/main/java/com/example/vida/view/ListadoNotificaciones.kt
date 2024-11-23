package com.example.vida.view

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
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

class ListadoNotificaciones : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_notificaciones_urgentes)

        cargarSpinners()
        configurarFiltros()

        // Configurar el ListView
        val listView = findViewById<ListView>(R.id.lvNotificaciones)

        // Cargar notificaciones desde la base de datos
        lifecycleScope.launch {
            try {
                // Consultar notificaciones en un hilo de fondo
                val notificaciones = withContext(Dispatchers.IO) {
                    NotificacionUrgenteDao.getNotificacionesUrgentes()
                }

                // Actualizar la interfaz de usuario en el hilo principal
                withContext(Dispatchers.Main) {
                    if (notificaciones.isNotEmpty()) {
                        val adapter = NotificacionAdapter(this@ListadoNotificaciones, notificaciones)
                        listView.adapter = adapter
                    } else {
                        // Mostrar un mensaje si no hay notificaciones
                        findViewById<TextView>(R.id.tvTituloNotificaciones).text = "No hay notificaciones disponibles"
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    // Mostrar un mensaje de error al usuario
                    findViewById<TextView>(R.id.tvTituloNotificaciones).text = "Error al cargar notificaciones"
                }
            }
        }
    }

    class NotificacionAdapter(
        private val context: AppCompatActivity,
        private val data: List<NotificacionUrgente>
    ) : ArrayAdapter<NotificacionUrgente>(context, R.layout.notificacion_item, data) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val inflater = context.layoutInflater
            val rowView = convertView ?: inflater.inflate(R.layout.notificacion_item, parent, false)

            val mensaje = rowView.findViewById<TextView>(R.id.tvMensaje)
            val tipoNotificacion = rowView.findViewById<TextView>(R.id.tvTipoNotificacion)
            val fecha = rowView.findViewById<TextView>(R.id.tvFecha)
            val paciente = rowView.findViewById<TextView>(R.id.tvPaciente)
            val hospital = rowView.findViewById<TextView>(R.id.tvHospital)
            val grupoSanguineo = rowView.findViewById<TextView>(R.id.tvGrupoSanguineo)

            val notificacion = data[position]
            mensaje.text = notificacion.mensaje
            tipoNotificacion.text = "Tipo: ${notificacion.tipoNotificacion}"
            fecha.text = "Fecha: ${notificacion.fecha}"
            paciente.text = "Paciente: ${notificacion.nombrePaciente} ${notificacion.apellidoPaciente}"
            hospital.text = "Hospital: ${notificacion.nombreLugar ?: "N/A"}"
            grupoSanguineo.text = "Grupo Sanguíneo: ${notificacion.grupoSanguineo ?: "N/A"}"

            return rowView
        }
    }

    private fun configurarFiltros() {
        val spinnerHospitales = findViewById<Spinner>(R.id.spinnerHospitales)
        val spinnerPacientes = findViewById<Spinner>(R.id.spinnerPacientes)
        val spinnerTiposNotificacion = findViewById<Spinner>(R.id.spinnerTiposNotificacion)

        spinnerHospitales.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val hospitalSeleccionado = parent?.getItemAtPosition(position) as String
                val pacienteSeleccionado = spinnerPacientes.selectedItem as String
                val tipoSeleccionado = spinnerTiposNotificacion.selectedItem as String
                filtrarNotificaciones(hospitalSeleccionado, pacienteSeleccionado, tipoSeleccionado)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerPacientes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val pacienteSeleccionado = parent?.getItemAtPosition(position) as String
                val hospitalSeleccionado = spinnerHospitales.selectedItem as String
                val tipoSeleccionado = spinnerTiposNotificacion.selectedItem as String
                filtrarNotificaciones(hospitalSeleccionado, pacienteSeleccionado, tipoSeleccionado)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerTiposNotificacion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val tipoSeleccionado = parent?.getItemAtPosition(position) as String
                val hospitalSeleccionado = spinnerHospitales.selectedItem as String
                val pacienteSeleccionado = spinnerPacientes.selectedItem as String
                filtrarNotificaciones(hospitalSeleccionado, pacienteSeleccionado, tipoSeleccionado)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }


    private fun filtrarNotificaciones(
        hospitalSeleccionado: String? = null,
        pacienteSeleccionado: String? = null,
        tipoSeleccionado: String? = null
    ) {
        lifecycleScope.launch {
            val hospitalFiltro = if (hospitalSeleccionado == "Todos los hospitales") null else hospitalSeleccionado
            val pacienteFiltro = if (pacienteSeleccionado == "Todos los pacientes") null else pacienteSeleccionado
            val tipoFiltro = if (tipoSeleccionado == "Todos los tipos") null else tipoSeleccionado

            val notificacionesFiltradas = withContext(Dispatchers.IO) {
                NotificacionUrgenteDao.getNotificacionesFiltradas(hospitalFiltro, pacienteFiltro, tipoFiltro)
            }

            withContext(Dispatchers.Main) {
                val adapter = NotificacionAdapter(this@ListadoNotificaciones, notificacionesFiltradas)
                findViewById<ListView>(R.id.lvNotificaciones).adapter = adapter
            }
        }
    }



    private fun cargarSpinners() {
        lifecycleScope.launch {
            val hospitales = withContext(Dispatchers.IO) {
                HospitalCentroDao.getAllHospitalesCentros() // Obtener lista de hospitales
            }
            val pacientes = withContext(Dispatchers.IO) {
                PacienteDao.getAllPacientes() // Obtener lista de pacientes
            }
            val tiposNotificacion = listOf("Todos los tipos", "Alerta", "Información", "Aviso") // Tipos fijos o dinámicos

            withContext(Dispatchers.Main) {
                // Adaptador para el spinner de hospitales
                val hospitalAdapter = ArrayAdapter(
                    this@ListadoNotificaciones,
                    android.R.layout.simple_spinner_item,
                    listOf("Todos los hospitales") + hospitales.map { it.nombreLugar }
                )
                hospitalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                findViewById<Spinner>(R.id.spinnerHospitales).adapter = hospitalAdapter

                // Adaptador para el spinner de pacientes
                val pacienteAdapter = ArrayAdapter(
                    this@ListadoNotificaciones,
                    android.R.layout.simple_spinner_item,
                    listOf("Todos los pacientes") + pacientes.map { "${it.nombre} ${it.apellido}" }
                )
                pacienteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                findViewById<Spinner>(R.id.spinnerPacientes).adapter = pacienteAdapter

                // Adaptador para el spinner de tipos de notificación
                val tipoAdapter = ArrayAdapter(
                    this@ListadoNotificaciones,
                    android.R.layout.simple_spinner_item,
                    tiposNotificacion
                )
                tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                findViewById<Spinner>(R.id.spinnerTiposNotificacion).adapter = tipoAdapter
            }
        }
    }




}
