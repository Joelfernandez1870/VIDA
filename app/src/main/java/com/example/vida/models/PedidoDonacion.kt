package com.example.vida.models

data class PedidoDonacion(

    val idPaciente: Int,       // ID del paciente que hace el pedido
    val idHospital: Int,       // ID del hospital o centro
    val descripcion: String,   // Descripción del pedido
    val fecha: String,         // Fecha del pedido en formato String
    val estado: String         // Estado del pedido (por ejemplo, "Pendiente", "Completado")
)


