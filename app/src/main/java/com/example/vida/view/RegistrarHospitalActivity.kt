package com.example.vida.view

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import com.example.vida.R
import com.example.vida.data.database.HospitalCentroDao
import com.example.vida.data.database.MySqlConexion
import com.example.vida.models.HospitalCentro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.log

class RegistrarHospitalActivity : AppCompatActivity() {

    private lateinit var et_tipoLugar: EditText
    private lateinit var et_nombreLugar: EditText
    private lateinit var et_ciudad: EditText
    private lateinit var et_pais: EditText
    private lateinit var et_longitud: EditText
    private lateinit var et_latitud: EditText
    private lateinit var btnGuardarHospital: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_hospital)


        et_tipoLugar = findViewById<EditText>(R.id.et_tipoLugar)
        et_nombreLugar = findViewById<EditText>(R.id.et_nombreLugar)
        et_ciudad = findViewById<EditText>(R.id.et_ciudad)
        et_pais = findViewById<EditText>(R.id.et_pais)
        et_longitud = findViewById<EditText>(R.id.et_longitud)
        et_latitud = findViewById<EditText>(R.id.et_latitud)

        btnGuardarHospital = findViewById(R.id.btnGuardarHospital)

        btnGuardarHospital.setOnClickListener {
            insertHospitalCentro()
        }
    }

    fun insertHospitalCentro() {
        // Obtener los valores de los campos de entrada
        val tipoLugar = et_tipoLugar.text.toString()
        val nombreLugar = et_nombreLugar.text.toString()
        val ciudad = et_ciudad.text.toString()
        val pais = et_pais.text.toString()
        val longitud = et_longitud.text.toString().toDouble()
        val latitud = et_latitud.text.toString().toDouble()

        var hospitalCentro = HospitalCentro(tipoLugar, nombreLugar, ciudad, pais, longitud, latitud)

        val exito = HospitalCentroDao.insert(hospitalCentro)

        if (exito) {
            Toast.makeText(this, "Usiario agregado exitosamente", Toast.LENGTH_SHORT).show()
            //limpiarFormulario()
        } else {
            Toast.makeText(this, "Error al agregar el Usuario", Toast.LENGTH_SHORT).show()
        }


    }
}
