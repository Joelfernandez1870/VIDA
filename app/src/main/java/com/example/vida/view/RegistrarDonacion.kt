
package com.example.vida.view

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.vida.R
import com.example.vida.data.database.DonacionDao
import com.example.vida.data.database.UsuarioDao
import com.example.vida.models.Donacion
import com.example.vida.models.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class RegistrarDonacion : AppCompatActivity() {

    private lateinit var spinnerUsuario: Spinner
    private lateinit var txtNombre: TextView
    private lateinit var txtApellido: TextView
    private lateinit var txtDni: TextView
    private lateinit var txtTipoSangre: TextView
    private lateinit var inputFechaDonacion: EditText
    private lateinit var spinnerTipoDonacion: Spinner
    private lateinit var btnGuardarDonacion: Button

    private lateinit var usuarioList: List<Usuario>
    private lateinit var tipoDonacionList: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_registrar_donacion)

    // Inicializo las vistas.
        spinnerUsuario = findViewById(R.id.spinnerUsuario)
        txtNombre = findViewById(R.id.txtNombre)
        txtApellido = findViewById(R.id.txtApellido)
        txtDni = findViewById(R.id.txtDni)
        txtTipoSangre = findViewById(R.id.txtTipoSangre)
        inputFechaDonacion = findViewById(R.id.inputFechaDonacion)
        spinnerTipoDonacion = findViewById(R.id.spinnerTipoDonacion)
        btnGuardarDonacion = findViewById(R.id.btnGuardarDonacion)

    // Cargo Lista de Usuarios desde la base de datos.
        usuarioList = UsuarioDao.getAllUsuarios()
    // Configurar el selector de fecha
        inputFechaDonacion.setOnClickListener { mostrarDatePicker() }


        val usuarioAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            usuarioList.map { "${it.nombre} ${it.apellido}" } // Muestro nombre y apellido, podria agregar DNI, etc.
        )
        usuarioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUsuario.adapter = usuarioAdapter

        // Cargo la lista de tipos de donación.
        tipoDonacionList = listOf("Sangre", "Plaquetas", "Plasma", "Dobles glóbulos rojos", "Autóloga")
        val tipoDonacionAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            tipoDonacionList
        )
        tipoDonacionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoDonacion.adapter = tipoDonacionAdapter


        // Cuando se selecciona un usuario en el Spinner, actualizo los TextViews con los datos
        spinnerUsuario.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val usuarioSeleccionado = usuarioList[position]
                txtNombre.text = "Nombre: ${usuarioSeleccionado.nombre}"
                txtApellido.text = "Apellido: ${usuarioSeleccionado.apellido}"
                txtDni.text = "DNI: ${usuarioSeleccionado.dni}"
                txtTipoSangre.text = "Tipo de Sangre: ${usuarioSeleccionado.grupoSanguineo}"
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Acción cuando toco el boton GUARDAR
        btnGuardarDonacion.setOnClickListener {
            // Obtengo los datos de la donación
            val usuarioSeleccionado = usuarioList[spinnerUsuario.selectedItemPosition]
            val tipoDonacionSeleccionado = tipoDonacionList[spinnerTipoDonacion.selectedItemPosition]
            val fechaDonacion = inputFechaDonacion.text.toString()

            val idHospital = LoginActivity.sesionGlobal!!//OBTENGO EL DATO DE LA VARIABLE GLOBAL
            val idHospitalString = idHospital.toString() //casteo por que no coincide el tipo de dato

            // Crear el objeto Donacion
            val donacion = Donacion(
                dniUsuario = usuarioSeleccionado.dni,  // Usamos el ID del usuario
                idHospital = idHospitalString,
                fecha = fechaDonacion,
                tipoDonacion = tipoDonacionSeleccionado
            )

            // Ejecutar la inserción en una coroutine
            CoroutineScope(Dispatchers.IO).launch {
                val insertado = DonacionDao.insert(donacion)
                withContext(Dispatchers.Main) {
                    if (insertado) {
                        Toast.makeText(this@RegistrarDonacion, "Donacion registrado exitosamente", Toast.LENGTH_SHORT).show()
                        CargaDePuntos(donacion.dniUsuario);
                        RegresoInterfazHospital()  // Regreso a la pagina principal del hospital.
                    } else {
                        Toast.makeText(this@RegistrarDonacion, "Error al registrar la donacion", Toast.LENGTH_SHORT).show()
                    }
                }
            }


        }
    }
    private fun mostrarDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]

        val datePickerDialog = DatePickerDialog(
            this,
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                inputFechaDonacion.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun RegresoInterfazHospital ()
    {
        val intent = Intent(this@RegistrarDonacion, InicioHospitalesYCentros::class.java)
        startActivity(intent)
    }

    private fun CargaDePuntos(dni: String) {

        val UsuarioEncontrado = UsuarioDao.getUsuarioByDni(dni)
        if (UsuarioEncontrado != null) {
            UsuarioEncontrado.puntos = UsuarioEncontrado.puntos?.plus(10)
            UsuarioDao.UpdatePuntos(UsuarioEncontrado)
        }
    }
}
