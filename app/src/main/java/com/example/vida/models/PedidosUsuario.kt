package com.example.vida.models

data class PedidosUsuario(
    val idEmergencia: Int,
    val nombrePaciente: String,
    val nombreHospital: String,
    val descripcion: String,
    val fecha: String,
    val estado: String
)
