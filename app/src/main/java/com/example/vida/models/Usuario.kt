package com.example.vida.models

data class Usuario(
    val dni: String,
    val nombre: String,
    val apellido: String,
    val email: String,
    val contraseña: String,
    val grupoSanguineo: String,
    val fechaNacimiento: String, // Cambiado a String
    val ciudad: String,
    val pais: String,
    val puntos: Int? = null
)
