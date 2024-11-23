package com.example.vida.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.vida.R
import com.example.vida.data.database.NotificacionUrgenteDao
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

}
