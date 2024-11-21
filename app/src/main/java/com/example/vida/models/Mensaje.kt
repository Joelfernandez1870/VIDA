package com.example.vida.models

data class Mensaje(
    val id: Int,
    val nombreUsuario: String,  // Campo para el nombre del usuario
    val contenido: String,
    val fechaEnvio: String,
    val ID_USUARIO: Int
)