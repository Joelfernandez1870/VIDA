package com.example.vida.models

data class Usuario(
    val id: Int? = null,
    val dni: String,
    val nombre: String,
    val apellido: String,
    val email: String,
    val contrasenia: String,
    val grupoSanguineo: String,
    val fechaNacimiento: String, // Cambiado a String
    val ciudad: String,
    val pais: String,
    var puntos: Int? = null,

//    val es_admin: Boolean
)
