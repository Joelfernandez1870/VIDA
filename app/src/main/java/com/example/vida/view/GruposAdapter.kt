package com.example.vida.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vida.R

class GruposAdapter(
    private var gruposList: List<String>,
    private val onGrupoClick: (String) -> Unit
) : RecyclerView.Adapter<GruposAdapter.GrupoViewHolder>() {

    // ViewHolder para cada grupo
    inner class GrupoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreGrupo: TextView = itemView.findViewById(R.id.textViewNombreGrupo)

        fun bind(grupo: String) {
            nombreGrupo.text = grupo
            itemView.setOnClickListener {
                onGrupoClick(grupo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GrupoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_grupo, parent, false)
        return GrupoViewHolder(view)
    }

    override fun onBindViewHolder(holder: GrupoViewHolder, position: Int) {
        holder.bind(gruposList[position])
    }

    override fun getItemCount(): Int = gruposList.size

    // Método para actualizar la lista de grupos
    fun updateData(newGruposList: List<String>) {
        gruposList = newGruposList
        notifyDataSetChanged()
    }
}
