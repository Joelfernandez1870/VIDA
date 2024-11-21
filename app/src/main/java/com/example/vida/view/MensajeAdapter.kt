package com.example.vida.view

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.vida.R
import com.example.vida.models.Mensaje

class MensajeAdapter(
    private val usuarioLogeadoId: Int,
    private val onMensajeEliminado: (Mensaje) -> Unit,
    private val onMensajeEditado: (Mensaje) -> Unit
) : RecyclerView.Adapter<MensajeAdapter.MensajeViewHolder>() {

    private val mensajes: MutableList<Mensaje> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MensajeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_muestra_mensaje, parent, false)
        return MensajeViewHolder(view)
    }

    override fun onBindViewHolder(holder: MensajeViewHolder, position: Int) {
        holder.bind(mensajes[position])
    }

    override fun getItemCount(): Int = mensajes.size

    fun updateMensajes(nuevosMensajes: List<Mensaje>) {
        mensajes.clear()
        mensajes.addAll(nuevosMensajes)
        notifyDataSetChanged()
    }

    inner class MensajeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textNombre: TextView = itemView.findViewById(R.id.textNombre)
        private val textContenido: TextView = itemView.findViewById(R.id.textContenido)
        private val textFecha: TextView = itemView.findViewById(R.id.textFecha)
        private val btnEditar: ImageButton = itemView.findViewById(R.id.btnModificar)
        private val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminar)

        fun bind(mensaje: Mensaje) {
            textNombre.text = mensaje.nombreUsuario
            textContenido.text = mensaje.contenido
            textFecha.text = mensaje.fechaEnvio

            // Mostrar botones solo si el mensaje pertenece al usuario logueado
            if (mensaje.ID_USUARIO == usuarioLogeadoId) {
                btnEditar.visibility = View.VISIBLE
                btnEliminar.visibility = View.VISIBLE

                btnEditar.setOnClickListener {
                    mostrarDialogoEditar(itemView.context, mensaje)
                }

                btnEliminar.setOnClickListener {
                    mostrarDialogoEliminar(itemView.context, mensaje)
                }
            } else {
                btnEditar.visibility = View.GONE
                btnEliminar.visibility = View.GONE
            }
        }

        private fun mostrarDialogoEliminar(context: Context, mensaje: Mensaje) {
            AlertDialog.Builder(context)
                .setTitle("Eliminar mensaje")
                .setMessage("¿Seguro que quieres eliminar este mensaje?")
                .setPositiveButton("Sí") { _, _ ->
                    onMensajeEliminado(mensaje)
                    Toast.makeText(context, "Mensaje eliminado", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("No", null)
                .show()
        }

        private fun mostrarDialogoEditar(context: Context, mensaje: Mensaje) {
            AlertDialog.Builder(context)
                .setTitle("Editar mensaje")
                .setMessage("Quieres editar este Mrnsaje?.")
                .setPositiveButton("OK") { _, _ ->
                    onMensajeEditado(mensaje)
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }
}
