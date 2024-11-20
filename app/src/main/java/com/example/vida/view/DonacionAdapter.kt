package com.example.vida.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vida.R
import com.example.vida.models.Donacion

class DonacionAdapter(private val donaciones: List<Donacion>) : RecyclerView.Adapter<DonacionAdapter.DonacionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonacionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_donacion, parent, false)
        return DonacionViewHolder(view)
    }

    override fun onBindViewHolder(holder: DonacionViewHolder, position: Int) {
        val donacion = donaciones[position]

        // Asigna el texto dinámicamente
        holder.dniText.text = "DNI: ${donacion.idUsuario}"
        holder.hospitalText.text = "Hospital: ${donacion.idHospital}"
        holder.tipoDonacionText.text = "Tipo de donación: ${donacion.tipoDonacion}"
        holder.fechaDonacionText.text = "Fecha: ${donacion.fecha}"
    }

    override fun getItemCount(): Int = donaciones.size

    class DonacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dniText: TextView = itemView.findViewById(R.id.dniText)
        val hospitalText: TextView = itemView.findViewById(R.id.hospitalText)
        val tipoDonacionText: TextView = itemView.findViewById(R.id.tipoDonacionText)
        val fechaDonacionText: TextView = itemView.findViewById(R.id.fechaDonacionText)
    }
}
