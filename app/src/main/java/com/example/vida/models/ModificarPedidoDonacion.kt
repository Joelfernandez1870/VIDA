package com.example.vida.models

data class ModificarPedidoDonacion(
    val idEmergencia: Int,     // ID de la emergencia del pedido
    val descripcion: String,   // Descripción del pedido
    val fecha: String,         // Fecha del pedido en formato String
    val estado: String         // Estado del pedido (por ejemplo, "Pendiente", "Completado")
)