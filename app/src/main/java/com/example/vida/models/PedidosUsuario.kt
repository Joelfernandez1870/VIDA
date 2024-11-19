package com.example.vida.models

data class PedidosUsuario(
    val idEmergencia: Int,
    val nombrePaciente: String,
    val nombreHospital: String,
    val dniPaciente: String, // Nuevo campo para el DNI
    val descripcion: String,
    val fecha: String,
    val estado: String
)
