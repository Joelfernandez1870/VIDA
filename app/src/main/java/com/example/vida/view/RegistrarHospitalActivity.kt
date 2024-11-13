package com.example.vida.view

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.vida.R
import com.example.vida.data.database.HospitalCentroDao
import com.example.vida.models.HospitalCentro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegistrarHospitalActivity : AppCompatActivity() {

    private lateinit var et_nombreLugar: EditText
    private lateinit var et_ciudad: EditText
    private lateinit var et_pais: EditText
    private lateinit var et_longitud: EditText
    private lateinit var et_latitud: EditText
    private lateinit var et_codigo: EditText
    private lateinit var et_clave: EditText
    private lateinit var et_claverepetida: EditText
    private lateinit var et_correo: EditText
    private lateinit var et_direccion: EditText

    private lateinit var btnGuardarHospital: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_hospital)

        et_nombreLugar = findViewById<EditText>(R.id.et_nombreLugar)
        et_ciudad = findViewById<EditText>(R.id.et_ciudad)
        et_pais = findViewById<EditText>(R.id.et_codigo)
        et_longitud = findViewById<EditText>(R.id.et_longitud)
        et_latitud = findViewById<EditText>(R.id.et_clave)
        et_correo = findViewById<EditText>(R.id.et_correo)
        et_direccion = findViewById<EditText>(R.id.et_direccion)
        et_codigo = findViewById<EditText>(R.id.et_codigo)
        et_clave = findViewById<EditText>(R.id.et_clave)
        et_claverepetida = findViewById<EditText>(R.id.et_claverepetida)



        btnGuardarHospital = findViewById(R.id.btnGuardarHospital)

        btnGuardarHospital.setOnClickListener {
            insertHospitalCentro()
        }
    }

    fun insertHospitalCentro() {
        // Obtener los valores de los campos de entrada
        val nombreLugar = et_nombreLugar.text.toString()
        val ciudad = et_ciudad.text.toString()
        val pais = et_pais.text.toString()
        val longitud = et_longitud.text.toString().toDouble()
        val latitud = et_latitud.text.toString().toDouble()
        val codigo = et_codigo.text.toString()
        val clave = et_clave.text.toString()
        val claverepetida = et_claverepetida.text.toString()
        val correo = et_correo.text.toString()

        var hospitalCentro = HospitalCentro (/*COMPLETAR CON ESTRUCTURA DE BASE DATOS*/)

        lifecycleScope.launch(Dispatchers.IO) {// Corrutina (coroutines, hilo secundario) para realizar la inserción en la base de datos
            try {
                var exito = HospitalCentroDao.insert(hospitalCentro)
                withContext(Dispatchers.Main) {
                    // Display the result in a Toast on the main thread
                    if (exito) {
                        Toast.makeText( applicationContext, "Usiario agregado exitosamente", Toast.LENGTH_SHORT).show()
                        //limpiarFormulario()
                    } else {
                        Toast.makeText(applicationContext, "Error al agregar el Usuario", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                Log.e("Error", e.message.toString())
            }
        }

    }

}
