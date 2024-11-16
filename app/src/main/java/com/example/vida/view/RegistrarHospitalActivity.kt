package com.example.vida.view

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.vida.BuildConfig
import com.example.vida.R
import com.example.vida.data.database.HospitalCentroDao
import com.example.vida.models.HospitalCentro
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegistrarHospitalActivity : AppCompatActivity(), PlaceSelectionListener {


    private lateinit var et_ciudad: EditText
    private lateinit var et_pais: EditText
    private lateinit var et_clave: EditText
    private lateinit var et_claverepetida: EditText
    private lateinit var et_correo: EditText
    private lateinit var et_direccion: EditText
    private lateinit var btnGuardarHospital: Button
    private lateinit var autocompleteFragment: AutocompleteSupportFragment

    private var latitud: Double = 0.0
    private var longitud: Double = 0.0
    private var nombreLugar: String = ""
    private var direccion: String = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_hospital)

        // Inicializar Google Places
        Places.initialize(applicationContext, BuildConfig.GOOGLE_MAPS_API_KEY)

        et_direccion = findViewById(R.id.et_direccion)
        et_ciudad = findViewById(R.id.et_ciudad)
        et_pais = findViewById(R.id.et_pais)
        et_clave = findViewById(R.id.et_clave)
        et_claverepetida = findViewById(R.id.et_claverepetida)
        et_correo = findViewById(R.id.et_correo)
        btnGuardarHospital = findViewById(R.id.btnGuardarHospital)

        // Configurar el AutocompleteSupportFragment
        autocompleteFragment = supportFragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG,
            Place.Field.ADDRESS_COMPONENTS
        ))
        autocompleteFragment.setHint("Nombre del Lugar")
        autocompleteFragment.setOnPlaceSelectedListener(this)


        // Botón para guardar el hospital
        btnGuardarHospital.setOnClickListener {
            insertHospitalCentro()
        }
    }

    private fun insertHospitalCentro() {
        // Obtener los valores de los campos de entrada
        val direction = et_direccion.text.toString()
        val ciudad = et_ciudad.text.toString()
        val pais = et_pais.text.toString()
        val clave = et_clave.text.toString()
        val claverepetida = et_claverepetida.text.toString()
        val correo = et_correo.text.toString()
        val tipoLugar = "Hospital"


        // Validación de los campos y creación del objeto HospitalCentro
        if (direction.isEmpty() || ciudad.isEmpty() || pais.isEmpty() || clave.isEmpty() || claverepetida.isEmpty() || correo.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (!pais.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+".toRegex())) {
            et_pais.error = "País solo debe contener letras"
            return
        }
        if (clave != claverepetida) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            et_correo.error = "Email inválido"
            return
        }
        if (!clave.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$".toRegex())) {
            et_clave.error = "Contrasenia debe contener al menos 6 caracteres, incluyendo letras y números"
            return
        }

        val hospitalCentro = HospitalCentro(
            tipoLugar = tipoLugar,
            nombreLugar = nombreLugar,
            ciudad = ciudad,
            pais = pais,
            longitud = longitud,
            latitud = latitud,
            clave = clave,
            correo = correo,
            direccion = direccion
        )

        lifecycleScope.launch(Dispatchers.IO) {
            val exito = HospitalCentroDao.insert(hospitalCentro)
            withContext(Dispatchers.Main) {
                if (exito) {
                    Toast.makeText(applicationContext, "Hospital/Centro agregado exitosamente", Toast.LENGTH_SHORT).show()
                    RegresoLoginActivity()
                } else {
                    Toast.makeText(applicationContext, "Error al agregar el Hospital/Centro", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onPlaceSelected(place: Place) {
        // Guarda el nombre del lugar, dirección y coordenadas
        nombreLugar = place.name ?: ""
        direccion = place.address ?: ""
        place.latLng?.let {
            latitud = it.latitude
            longitud = it.longitude
        }

        // Establecer la dirección en el campo de texto
        et_direccion.setText(direccion)

        // Extraer ciudad y país usando ADDRESS_COMPONENTS
        val addressComponents = place.addressComponents
        if (addressComponents != null) {
            for (component in addressComponents.asList()) {
                when {
                    component.types.contains("locality") -> {
                        et_ciudad.setText(component.name)  // Ciudad
                    }
                    component.types.contains("country") -> {
                        et_pais.setText(component.name)  // País
                    }
                }
            }
        }
    }

    override fun onError(status: Status) {
        Toast.makeText(this, "Error al seleccionar ubicación: ${status.statusMessage}", Toast.LENGTH_SHORT).show()
    }

    private fun RegresoLoginActivity() {
        val intent = Intent(this@RegistrarHospitalActivity, LoginActivity::class.java)
        startActivity(intent)
    }
}


