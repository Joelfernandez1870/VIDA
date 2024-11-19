package com.example.vida.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vida.R
import com.example.vida.models.PedidoHospital

class PedidoHospitalAdapter(
    private var pedidos: MutableList<PedidoHospital>,
    private val onModificar: (PedidoHospital) -> Unit,
    private val onEliminar: (PedidoHospital) -> Unit
) : RecyclerView.Adapter<PedidoHospitalAdapter.PedidoHospitalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoHospitalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_muestra_pedidos_hospital, parent, false)
        return PedidoHospitalViewHolder(view)
    }

    override fun onBindViewHolder(holder: PedidoHospitalViewHolder, position: Int) {
        val pedido = pedidos[position]
        holder.bind(pedido)
    }

    override fun getItemCount(): Int = pedidos.size

    fun updateList(newPedidos: MutableList<PedidoHospital>) {
        this.pedidos = newPedidos
        notifyDataSetChanged()
    }

    inner class PedidoHospitalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombrePaciente: TextView = itemView.findViewById(R.id.nombrePaciente)
        private val dniPaciente: TextView = itemView.findViewById(R.id.dniPaciente)
        private val nombreHospital: TextView = itemView.findViewById(R.id.nombreHospital)
        private val descripcion: TextView = itemView.findViewById(R.id.descripcion)
        private val fecha: TextView = itemView.findViewById(R.id.fecha)
        private val estado: TextView = itemView.findViewById(R.id.estado)
        private val btnModificar: ImageButton = itemView.findViewById(R.id.btnModificar)
        private val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminar)

        fun bind(pedido: PedidoHospital) {
            nombrePaciente.text = pedido.nombrePaciente
            dniPaciente.text = pedido.dniPaciente
            nombreHospital.text = pedido.nombreHospital
            descripcion.text = pedido.descripcion
            fecha.text = pedido.fecha
            estado.text = pedido.estado

            btnModificar.setOnClickListener { onModificar(pedido) }
            btnEliminar.setOnClickListener { onEliminar(pedido) }
        }
    }
}
