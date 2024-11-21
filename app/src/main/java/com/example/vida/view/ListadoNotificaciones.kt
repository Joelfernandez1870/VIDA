package com.example.vida.view

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.vida.R
import com.example.vida.data.database.NotificacionUrgenteDao
import com.example.vida.models.NotificacionUrgente
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListadoNotificaciones : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_notificaciones_urgentes)

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
}
