package com.example.vida.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vida.R
import com.example.vida.models.Paciente

class PacienteAdapter(private var pacientes: List<Paciente>) :
    RecyclerView.Adapter<PacienteAdapter.PacienteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PacienteViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_paciente, parent, false)
        return PacienteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PacienteViewHolder, position: Int) {
        val paciente = pacientes[position]

        holder.tvNombrePaciente.text = paciente.nombre
        holder.tvApellidoPaciente.text = paciente.apellido
        holder.tvDniPaciente.text = "DNI: ${paciente.dni}"
        holder.tvGrupoSanguineo.text = "Grupo Sanguíneo: ${paciente.grupoSanguineo}"
        holder.tvFechaNacimiento.text = "Fecha de Nacimiento: ${paciente.fechaNacimiento}"
        holder.tvCiudadPaciente.text = "Ciudad: ${paciente.ciudad}"
        holder.tvPaisPaciente.text = "País: ${paciente.pais}"
        holder.tvEmailPaciente.text = "Email: ${paciente.email}"
    }

    override fun getItemCount(): Int = pacientes.size

    fun updateList(newList: List<Paciente>) {
        pacientes = newList
        notifyDataSetChanged()
    }

    class PacienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombrePaciente: TextView = itemView.findViewById(R.id.tvNombrePaciente)
        val tvApellidoPaciente: TextView = itemView.findViewById(R.id.TvApellidoPaciente)
        val tvDniPaciente: TextView = itemView.findViewById(R.id.tvDniPaciente)
        val tvGrupoSanguineo: TextView = itemView.findViewById(R.id.tvGrupoSanguineo)
        val tvFechaNacimiento: TextView = itemView.findViewById(R.id.TvFechaNacimiento)
        val tvCiudadPaciente: TextView = itemView.findViewById(R.id.tvCiudadPaciente)
        val tvPaisPaciente: TextView = itemView.findViewById(R.id.TvPaisPaciente)
        val tvEmailPaciente: TextView = itemView.findViewById(R.id.tvEmailPaciente)
    }
}
