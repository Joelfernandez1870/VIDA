package com.example.vida.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vida.R

// Adapter para los grupos de chats
class GruposAdapter(private val grupos: List<String>, private val itemClickListener: (String) -> Unit) :
    RecyclerView.Adapter<GruposAdapter.GruposViewHolder>() {

    // ViewHolder que representa cada item
    inner class GruposViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreGrupo: TextView = itemView.findViewById(R.id.textViewNombreGrupo)

        // Configura el clic del grupo
        fun bind(grupo: String) {
            nombreGrupo.text = grupo
            itemView.setOnClickListener { itemClickListener(grupo) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GruposViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_grupo, parent, false)
        return GruposViewHolder(view)
    }

    override fun onBindViewHolder(holder: GruposViewHolder, position: Int) {
        val grupo = grupos[position]
        holder.bind(grupo)
    }

    override fun getItemCount(): Int {
        return grupos.size
    }
}
