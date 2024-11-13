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

        et_nombreLugar = findViewById(R.id.et_nombreLugar)
        et_ciudad = findViewById(R.id.et_ciudad)
        et_pais = findViewById(R.id.et_pais)
        et_longitud = findViewById(R.id.et_longitud)
        et_latitud = findViewById(R.id.et_latitud)
        et_codigo = findViewById(R.id.et_codigo)
        et_clave = findViewById(R.id.et_clave)
        et_claverepetida = findViewById(R.id.et_claverepetida)
        et_correo = findViewById(R.id.et_correo)
        et_direccion = findViewById(R.id.et_direccion)

        btnGuardarHospital = findViewById(R.id.btnGuardarHospital)

        btnGuardarHospital.setOnClickListener {
            insertHospitalCentro()
        }
    }

    private fun insertHospitalCentro() {
        // Obtener los valores de los campos de entrada
        val tipoLugar = "Hospital"  // Valor predeterminado para tipoLugar
        val nombreLugar = et_nombreLugar.text.toString()
        val ciudad = et_ciudad.text.toString()
        val pais = et_pais.text.toString()
        val longitud = et_longitud.text.toString().toDoubleOrNull() ?: 0.0
        val latitud = et_latitud.text.toString().toDoubleOrNull() ?: 0.0

        // Crear el objeto HospitalCentro con los parámetros correctos
        val hospitalCentro = HospitalCentro(
            tipoLugar = tipoLugar,
            nombreLugar = nombreLugar,
            ciudad = ciudad,
            pais = pais,
            longitud = longitud,
            latitud = latitud
        )

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val exito = HospitalCentroDao.insert(hospitalCentro)
                withContext(Dispatchers.Main) {
                    if (exito) {
                        Toast.makeText(applicationContext, "Hospital/Centro agregado exitosamente", Toast.LENGTH_SHORT).show()
                        // limpiarFormulario() // Descomentar si se implementa la función para limpiar el formulario
                    } else {
                        Toast.makeText(applicationContext, "Error al agregar el Hospital/Centro", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("Error", e.message.toString())
            }
        }
    }
}
