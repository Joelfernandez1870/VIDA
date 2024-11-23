package com.example.vida.models

data class HospitalCentro(

    val idHospitalesCentro: Int? = null,
    val tipoLugar: String,
    val nombreLugar: String,
    val ciudad: String,
    val pais: String,
    val longitud: Double,
    val latitud: Double,
    val clave: String,
    val correo: String,
    val direccion: String,
) {
    override fun toString(): String {
        return nombreLugar // Esto mostrará el nombre del hospital en el Spinner
    }
}