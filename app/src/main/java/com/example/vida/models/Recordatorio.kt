package com.example.vida.models

data class Recordatorio(
    val id_usuario: Int,
    val fecha_envio: String,
    val tipo_recordatorio: String,
    val mensaje_recordatorio: String

)
