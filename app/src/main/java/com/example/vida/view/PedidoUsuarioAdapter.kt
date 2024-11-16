package com.example.vida.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vida.R
import com.example.vida.models.PedidoHospital

class PedidoUsuarioAdapter(private val listaPedidos: List<PedidoHospital>) : RecyclerView.Adapter<PedidoUsuarioAdapter.PedidoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_muestra_pedido_usuario, parent, false)
        return PedidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = listaPedidos[position]
        holder.bind(pedido)
    }

    override fun getItemCount(): Int = listaPedidos.size

    inner class PedidoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombrePacienteTextView: TextView = itemView.findViewById(R.id.nombrePaciente)
        private val nombreHospitalTextView: TextView = itemView.findViewById(R.id.nombreHospital)
        private val descripcionTextView: TextView = itemView.findViewById(R.id.descripcionPedido)
        private val fechaTextView: TextView = itemView.findViewById(R.id.fechaPedido)
        private val estadoTextView: TextView = itemView.findViewById(R.id.estadoPedido)

        fun bind(pedido: PedidoHospital) {
            nombrePacienteTextView.text = pedido.nombrePaciente
            nombreHospitalTextView.text = pedido.nombreHospital
            descripcionTextView.text = pedido.descripcion
            fechaTextView.text = pedido.fecha
            estadoTextView.text = pedido.estado
        }
    }
}
