package com.example.vida.models

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class NotificacionUrgente(
    val idNotificacion: Int,
    val idHospitalCentro: Int,
    val idPaciente: Int?,  // Puede ser nulo
    val mensaje: String,
    val tipoNotificacion: String,
    val fecha: String,
    val nombreLugar: String? = null,
    val fechaExpiracion: String? = null,
    val grupoSanguineo: String? = null,
    val nombrePaciente: String? = null,
    val apellidoPaciente: String? = null
)
