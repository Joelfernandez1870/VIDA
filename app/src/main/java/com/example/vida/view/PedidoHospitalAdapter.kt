package com.example.vida.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vida.R
import com.example.vida.models.PedidoHospital

class PedidoHospitalAdapter(private val pedidos: List<PedidoHospital>) : RecyclerView.Adapter<PedidoHospitalAdapter.PedidoHospitalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoHospitalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_muestra_pedidos_hospital, parent, false)
        return PedidoHospitalViewHolder(view)
    }

    override fun onBindViewHolder(holder: PedidoHospitalViewHolder, position: Int) {
        val pedido = pedidos[position]
        holder.bind(pedido)
    }

    override fun getItemCount(): Int {
        return pedidos.size
    }

    class PedidoHospitalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombrePaciente: TextView = itemView.findViewById(R.id.nombrePaciente)
        private val nombreHospital: TextView = itemView.findViewById(R.id.nombreHospital)
        private val descripcion: TextView = itemView.findViewById(R.id.descripcion)
        private val fecha: TextView = itemView.findViewById(R.id.fecha)
        private val estado: TextView = itemView.findViewById(R.id.estado)

        fun bind(pedido: PedidoHospital) {
            nombrePaciente.text = pedido.nombrePaciente
            nombreHospital.text = pedido.nombreHospital
            descripcion.text = pedido.descripcion
            fecha.text = pedido.fecha
            estado.text = pedido.estado
        }
    }
}
