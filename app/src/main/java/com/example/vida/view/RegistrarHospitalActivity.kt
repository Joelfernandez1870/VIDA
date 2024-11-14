package com.example.vida.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
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
        val codigo = et_codigo.text.toString()
        val clave = et_clave.text.toString()
        val claverepetida = et_claverepetida.text.toString()
        val correo = et_correo.text.toString()
        val direccion = et_direccion.text.toString()
        val tipo_usuario = 0;


        // Validación de los campos
        if (!nombreLugar.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+".toRegex())) {
            et_nombreLugar.error = "Nombre del lugar solo debe contener letras"
            return
        }

        if (!ciudad.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+".toRegex())) {
            et_ciudad.error = "Ciudad solo debe contener letras"
            return
        }

        if (!pais.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+".toRegex())) {
            et_pais.error = "País solo debe contener letras"
            return
        }

        if (codigo.isEmpty() || !codigo.matches("[A-Za-z0-9]+".toRegex())) {
            et_codigo.error = "Código de habilitación inválido"
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            et_correo.error = "Email inválido"
            return
        }

        if (!clave.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$".toRegex())) {
            et_clave.error = "Contraseña debe contener al menos 6 caracteres, incluyendo letras y números"
            return
        }

        if (clave != claverepetida) {
            et_claverepetida.error = "Las claves no coinciden"
            return
        }

        // Crear el objeto HospitalCentro con los parámetros validados
        val hospitalCentro = HospitalCentro(
            tipoLugar = tipoLugar,
            nombreLugar = nombreLugar,
            ciudad = ciudad,
            pais = pais,
            longitud = longitud,
            latitud = latitud,
            codigo = codigo,
            clave = clave,
            correo = correo,
            direccion = direccion,
            tipo_usuario = tipo_usuario
        )


        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val exito = HospitalCentroDao.insert(hospitalCentro)
                withContext(Dispatchers.Main) {
                    if (exito) {
                        Toast.makeText(applicationContext, "Hospital/Centro agregado exitosamente", Toast.LENGTH_SHORT).show()
                        // limpiarFormulario() // Descomentar si se implementa la función para limpiar el formulario
                        // REGRESO A LA ACTIVIDAD ANTERIOR
                        RegresoLoginActivity();
                    } else {
                        Toast.makeText(applicationContext, "Error al agregar el Hospital/Centro", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("Error", e.message.toString())
            }
        }
    }

    private fun RegresoLoginActivity ()
    {
        val intent = Intent(this@RegistrarHospitalActivity, LoginActivity::class.java)
        startActivity(intent)
    }
}
