package com.example.vida.models

data class PedidoHospital(
    val idEmergencia: Int,
    val nombrePaciente: String,
    val dniPaciente: String, // Nuevo campo para el DNI
    val nombreHospital: String,
    val descripcion: String,
    val fecha: String,
    val estado: String
)
