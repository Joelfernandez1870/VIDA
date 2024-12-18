package com.example.vida.view

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.vida.R
import com.example.vida.data.database.NotificacionUrgenteDao
import com.example.vida.data.database.PacienteDao
import com.example.vida.models.Paciente
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class CargarPaciente : AppCompatActivity() {

    private lateinit var inputDNI: EditText
    private lateinit var inputNombre: EditText
    private lateinit var inputApellido: EditText
    private lateinit var inputEmail: EditText
//    private lateinit var inputGrupoSanguineo: EditText
    private lateinit var inputFechaNacimiento: EditText
    private lateinit var inputCiudad: EditText
    private lateinit var inputPais: EditText
    private lateinit var btnGuardar: Button
    private lateinit var spinner: Spinner
    private lateinit var grupoSanguineo : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cargar_paciente)

        // Inicializar los campos
        inputDNI = findViewById(R.id.inputDNI)
        inputNombre = findViewById(R.id.inputNombre)
        inputApellido = findViewById(R.id.inputApellido)
        inputEmail = findViewById(R.id.inputEmail)
//        inputGrupoSanguineo = findViewById(R.id.inputGrupoSanguineo)
        spinner = findViewById<Spinner>(R.id.inputGrupoSanguineo)
        inputFechaNacimiento = findViewById(R.id.inputFechaNacimiento)
        inputCiudad = findViewById(R.id.inputCiudad)
        inputPais = findViewById(R.id.inputPais)
        btnGuardar = findViewById(R.id.btnGuardar)


        // selector de fecha
        inputFechaNacimiento.setOnClickListener { mostrarDatePicker() }

        // botón de guardar
        btnGuardar.setOnClickListener { validarDatos() }

        cargarSpinner()
    }

    private fun cargarSpinner() {
        //spinner de grupos sanguineos
        val items = arrayOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        spinner.adapter = adapter
        spinner.setSelection(0)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position) as String
                grupoSanguineo = selectedItem
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
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
                inputFechaNacimiento.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun validarDatos() {
        val dni = inputDNI.text.toString()
        val nombre = inputNombre.text.toString()
        val apellido = inputApellido.text.toString()
        val email = inputEmail.text.toString()

        val fechaNacimiento = inputFechaNacimiento.text.toString()
        val ciudad = inputCiudad.text.toString()
        val pais = inputPais.text.toString()
        val hospitalId = LoginActivity.sesionGlobal.toString()
        var isEmailRegistered = PacienteDao.validateEmail(email)
        var isDniRegistered = PacienteDao.isDniRegistered(dni)

        // Validación de los campos
        if (nombre.isEmpty() || !nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+".toRegex())) {
            inputNombre.error = "Nombre solo debe contener letras"
            return
        }
        if (apellido.isEmpty() || !apellido.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+".toRegex())) {
            inputApellido.error = "Apellido solo debe contener letras"
            return
        }
        if (dni.isEmpty() || !dni.matches("\\d{7,8}".toRegex())) {
            inputDNI.error = "DNI debe tener 7 u 8 dígitos"
            return
        }
        if (isDniRegistered) {
            inputDNI.error = "Este DNI se encuentra registrado."
            return
        }

        if (isEmailRegistered) {
            inputEmail.error = "Este email se encuentra registrado."
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.error = "Email inválido"
            return
        }
//        if (grupoSanguineo.isEmpty() || !esGrupoSanguineoValido(grupoSanguineo)) {
//            inputGrupoSanguineo.error = "Grupo sanguíneo inválido"
//            return
//        }
        if (TextUtils.isEmpty(fechaNacimiento)) {
            inputFechaNacimiento.error = "Seleccione una fecha"
            return
        }
        if (ciudad.isEmpty() || !ciudad.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+".toRegex())) {
            inputCiudad.error = "Ciudad solo debe contener letras"
            return
        }
        if (pais.isEmpty() || !pais.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+".toRegex())) {
            inputPais.error = "País solo debe contener letras"
            return
        }

        // Crear paciente y ejecutar la inserción en la base de datos
        val paciente = Paciente(

            dni = dni,
            nombre = nombre,
            apellido = apellido,
            email = email,
            grupoSanguineo = grupoSanguineo,
            fechaNacimiento = fechaNacimiento,
            ciudad = ciudad,
            pais = pais,
            hospitalId = hospitalId
        )

        // Ejecutar la inserción en una coroutine
        CoroutineScope(Dispatchers.IO).launch {
            val insertado = PacienteDao.insert(paciente)
            println(insertado)
            withContext(Dispatchers.Main) {
                if (insertado) {
                    Toast.makeText(this@CargarPaciente, "Paciente registrado exitosamente", Toast.LENGTH_SHORT).show()
                    limpiarCampos()  // Limpiar los campos después del registro exitoso
                } else {
                    Toast.makeText(this@CargarPaciente, "Error al registrar el paciente", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun limpiarCampos() {
        inputDNI.text.clear()
        inputNombre.text.clear()
        inputApellido.text.clear()
        inputEmail.text.clear()
        spinner.setSelection(0)
        inputFechaNacimiento.text.clear()
        inputCiudad.text.clear()
        inputPais.text.clear()
    }

    private fun esGrupoSanguineoValido(grupo: String): Boolean {
        val gruposValidos = arrayOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
        return grupo in gruposValidos
    }
}
