package com.example.vida.view

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.vida.R
import com.example.vida.data.database.HospitalCentroDao
import com.example.vida.models.HospitalCentro
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HospitalesMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var hospitalSelector: Spinner
    private lateinit var hospitalesList: List<HospitalCentro>
    private val markerMap = mutableMapOf<String, Marker>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospitales_map)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        hospitalSelector = findViewById(R.id.hospitalSelector)

        // Configurar el fragmento del mapa
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        // Cargar los hospitales en el Spinner
        loadHospitales()
    }

    private fun loadHospitales() {
        lifecycleScope.launch(Dispatchers.IO) {
            hospitalesList = HospitalCentroDao.getAllHospitalesCentros()
            val hospitalNames = hospitalesList.map { it.nombreLugar }

            withContext(Dispatchers.Main) {
                val adapter = ArrayAdapter(this@HospitalesMapActivity, android.R.layout.simple_spinner_item, hospitalNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                hospitalSelector.adapter = adapter

                hospitalSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val selectedHospital = hospitalesList[position]
                        moveToHospitalMarker(selectedHospital)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }
        }
    }

    private fun moveToHospitalMarker(hospital: HospitalCentro) {
        val position = LatLng(hospital.latitud, hospital.longitud)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15f))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true // Habilitar controles de zoom

        // Configurar el centro inicial del mapa
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
            setMapCenterOnUserLocation()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }

        // Cargar marcadores en el mapa
        loadMarkers()
    }

    private fun setMapCenterOnUserLocation() {
        // Verificar permisos antes de intentar acceder a la ubicación
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getCurrentLocation(
                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val userLatLng = LatLng(location.latitude, location.longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
                } else {
                    // Si no se puede obtener la ubicación, centrar en el primer hospital
                    setMapCenterOnFirstHospital()
                }
            }.addOnFailureListener {
                // Manejar errores al obtener la ubicación
                Toast.makeText(this, "Error al obtener la ubicación.", Toast.LENGTH_SHORT).show()
                setMapCenterOnFirstHospital()
            }
        } else {
            // Si no hay permisos, solicitar nuevamente
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }


    private fun setMapCenterOnFirstHospital() {
        lifecycleScope.launch(Dispatchers.IO) {
            val firstHospital = HospitalCentroDao.getAllHospitalesCentros().firstOrNull()
            withContext(Dispatchers.Main) {
                if (firstHospital != null) {
                    val hospitalLatLng = LatLng(firstHospital.latitud, firstHospital.longitud)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(hospitalLatLng, 15f))
                } else {
                    Toast.makeText(this@HospitalesMapActivity, "No hay hospitales disponibles.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadMarkers() {
        lifecycleScope.launch(Dispatchers.IO) {
            hospitalesList = HospitalCentroDao.getAllHospitalesCentros()

            withContext(Dispatchers.Main) {
                hospitalesList.forEach { hospital ->
                    val position = LatLng(hospital.latitud, hospital.longitud)
                    val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.hospital_icon)
                    val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, 80, 80, false)
                    val marker = map.addMarker(
                        MarkerOptions()
                            .position(position)
                            .title(hospital.nombreLugar)
                            .snippet(hospital.tipoLugar)
                            .icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap))
                    )
                    // Guardar el marcador en el mapa de marcadores
                    marker?.let { markerMap[hospital.nombreLugar] = it }
                }
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
