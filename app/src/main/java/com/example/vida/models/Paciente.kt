package com.example.vida.models

data class Paciente(

    val idPaciente: Int? = null,
    val dni: String,
    val nombre: String,
    val apellido: String,
    val email: String,
    val grupoSanguineo: String,
    val fechaNacimiento: String,
    val ciudad: String,
    val pais: String,
    val hospitalId: String
) {
    override fun toString(): String {
        return "$nombre $apellido"
    }
}