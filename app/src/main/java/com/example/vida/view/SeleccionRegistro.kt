package com.example.vida.view

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.vida.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

class SeleccionRegistro : AppCompatActivity(), OnMapReadyCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_seleccion_registro) // Asegúrate de que este es el layout correcto

        // Obtener referencia al botón "Usuario Particular"
        val btnUsuarioParticular = findViewById<Button>(R.id.btnUsuarioParticular)

        // Configurar el OnClickListener para iniciar la actividad RegistrarUsuarioActivity
        btnUsuarioParticular.setOnClickListener {
            val intent = Intent(this, RegistrarUsuarioActivity::class.java)
            startActivity(intent)
        }

        // Obtener referencia al botón "Hospital"
        val btnHospital = findViewById<Button>(R.id.btnHospital)

        // Configurar el OnClickListener para iniciar la actividad RegistrarHospitalActivity
        btnHospital.setOnClickListener {
            val intent = Intent(this, RegistrarHospitalActivity::class.java)
            startActivity(intent)
        }

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap?) {
        val latitud = -34.42333
        val longitud = -58.76194

        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitud, longitud), 15f))

        //obtener la latitud y longitud del mapa con un click
        map?.setOnMapClickListener { latLng ->
            val latitude = latLng.latitude
            val longitude = latLng.longitude
//            Toast.makeText(this, "Latitude: $latitude, Longitude: $longitude", Toast.LENGTH_SHORT)
//                .show()

            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses: MutableList<Address>? = geocoder.getFromLocation(latitude, longitude, 1)

            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val address: Address = addresses[0]
                    val addressLine = address.getAddressLine(0)
                    Toast.makeText(this, "la direccion es: $addressLine", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(this, "Address not found", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }
}
