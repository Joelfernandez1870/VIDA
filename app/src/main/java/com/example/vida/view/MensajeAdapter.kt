package com.example.vida.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vida.R
import com.example.vida.models.Mensaje

class MensajeAdapter : RecyclerView.Adapter<MensajeAdapter.MensajeViewHolder>() {

    private var mensajes: MutableList<Mensaje> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MensajeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_muestra_mensaje, parent, false)
        return MensajeViewHolder(view)
    }

    override fun onBindViewHolder(holder: MensajeViewHolder, position: Int) {
        val mensaje = mensajes[position]
        holder.bind(mensaje)
    }

    override fun getItemCount(): Int = mensajes.size

    fun submitList(newMensajes: List<Mensaje>) {
        mensajes = newMensajes.toMutableList()
        notifyDataSetChanged()
    }

    fun updateMensajes(nuevosMensajes: List<Mensaje>) {
        mensajes.clear()
        mensajes.addAll(nuevosMensajes)
        notifyDataSetChanged()
    }

    class MensajeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textNombre: TextView = itemView.findViewById(R.id.textNombre)
        private val textContenido: TextView = itemView.findViewById(R.id.textContenido)
        private val textFecha: TextView = itemView.findViewById(R.id.textFecha)

        fun bind(mensaje: Mensaje) {
            textNombre.text = mensaje.nombreUsuario  // Mostrar solo el nombre del usuario
            textContenido.text = mensaje.contenido
            textFecha.text = mensaje.fechaEnvio
        }
    }
}
