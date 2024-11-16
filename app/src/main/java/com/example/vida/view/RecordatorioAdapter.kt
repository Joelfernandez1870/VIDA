package com.example.vida.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vida.R
import com.example.vida.models.Recordatorio

class RecordatorioAdapter(private val recordatorios: List<Recordatorio>) : RecyclerView.Adapter<RecordatorioAdapter.RecordatorioViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordatorioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recordatorio, parent, false)
        return RecordatorioViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordatorioViewHolder, position: Int) {
        val recordatorio = recordatorios[position]
        holder.bind(recordatorio)
    }

    override fun getItemCount(): Int {
        return recordatorios.size
    }

    class RecordatorioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mensajeRecordatorio: TextView = itemView.findViewById(R.id.mensajeRecordatorio)
        private val fechaEnvio: TextView = itemView.findViewById(R.id.fechaEnvio)
        private val tipoRecordatorio: TextView = itemView.findViewById(R.id.tipoRecordatorio)

        fun bind(recordatorio: Recordatorio) {
            mensajeRecordatorio.text = recordatorio.mensaje_recordatorio
            fechaEnvio.text = recordatorio.fecha_envio
            tipoRecordatorio.text = recordatorio.tipo_recordatorio
        }
    }
}
