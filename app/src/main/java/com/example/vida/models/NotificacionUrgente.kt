package com.example.vida.models

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class NotificacionUrgente(
    val idHospitalCentro: Int,
    val idPaciente: Int?,  // Puede ser nulo
    val mensaje: String,
    val tipoNotificacion: String,
    val fecha: String = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
)