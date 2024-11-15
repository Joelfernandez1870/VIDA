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
import com.example.vida.R
import com.example.vida.data.database.UsuarioDao
import com.example.vida.models.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class RegistrarUsuarioActivity : AppCompatActivity() {
    private lateinit var inputDNI: EditText
    private lateinit var inputNombre: EditText
    private lateinit var inputApellido: EditText
    private lateinit var inputEmail: EditText
    private lateinit var inputContrasena: EditText
    private lateinit var inputFechaNacimiento: EditText
    private lateinit var inputCiudad: EditText
    private lateinit var inputPais: EditText
    private lateinit var btnGuardar: Button
    private lateinit var grupoSanguineo :String
    private lateinit var spinner: Spinner


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_usuario)

        // Inicializar los campos
        inputDNI = findViewById(R.id.inputDNI)
        inputNombre = findViewById(R.id.inputNombre)
        inputApellido = findViewById(R.id.inputApellido)
        inputEmail = findViewById(R.id.inputEmail)
        inputContrasena = findViewById(R.id.et_longitud)  // Asegúrate de que este ID sea correcto
        inputFechaNacimiento = findViewById(R.id.inputFechaNacimiento)
        inputCiudad = findViewById(R.id.inputCiudad)
        inputPais = findViewById(R.id.inputPais)
        btnGuardar = findViewById(R.id.btnGuardar)
        spinner = findViewById<Spinner>(R.id.spGrupoSanguineo)
        // Configurar el selector de fecha
        inputFechaNacimiento.setOnClickListener { mostrarDatePicker() }

        // Configurar el botón de guardar
        btnGuardar.setOnClickListener { validarDatos() }

        cargarSpGrupoSanguineo()

    }

    private fun cargarSpGrupoSanguineo() {
        //spinner de grupos sanguineos
        val items = arrayOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        spinner.adapter = adapter
        spinner.setSelection(0)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position) as String
                Toast.makeText(this@RegistrarUsuarioActivity, "Selected: $selectedItem", Toast.LENGTH_SHORT).show()
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
        val contrasena = inputContrasena.text.toString()
        val fechaNacimiento = inputFechaNacimiento.text.toString()
        val ciudad = inputCiudad.text.toString()
        val pais = inputPais.text.toString()

        // Validación de los campos
        val usuarioExistente = UsuarioDao.getUsuarioByDni(dni)
        if (usuarioExistente != null) {
            Toast.makeText(this, "El DNI ya está registrado", Toast.LENGTH_SHORT).show()
            return
        }

        if (!dni.matches("\\d{7,8}".toRegex())) {
            inputDNI.error = "DNI debe tener 7 u 8 dígitos"
            return
        }
        if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+".toRegex())) {
            inputNombre.error = "Nombre solo debe contener letras"
            return
        }
        if (!apellido.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+".toRegex())) {
            inputApellido.error = "Apellido solo debe contener letras"
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.error = "Email inválido"
            return
        }
        if (!contrasena.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$".toRegex())) {
            inputContrasena.error = "Contrasenia debe contener al menos 6 caracteres, incluyendo letras y números"
            return
        }

        if (TextUtils.isEmpty(fechaNacimiento)) {
            inputFechaNacimiento.error = "Seleccione una fecha"
            return
        }
        if (!ciudad.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+".toRegex())) {
            inputCiudad.error = "Ciudad solo debe contener letras"
            return
        }
        if (!pais.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+".toRegex())) {
            inputPais.error = "País solo debe contener letras"
            return
        }

        // Insertar usuario en la base de datos
        val usuario = Usuario(
            dni = dni,
            nombre = nombre,
            apellido = apellido,
            email = email,
            contrasenia = contrasena,
            grupoSanguineo = grupoSanguineo,
            fechaNacimiento = fechaNacimiento,
            ciudad = ciudad,
            pais = pais,
//            es_admin = false
        )

        // Ejecutar la inserción en una coroutine
        CoroutineScope(Dispatchers.IO).launch {
            val insertado = UsuarioDao.insert(usuario)
            withContext(Dispatchers.Main) {
                if (insertado) {
                    Toast.makeText(this@RegistrarUsuarioActivity, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show()
                    limpiarCampos()  // Limpiar los campos después del registro exitoso
                } else {
                    Toast.makeText(this@RegistrarUsuarioActivity, "Error al registrar el usuario", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun limpiarCampos() {
        inputDNI.text.clear()
        inputNombre.text.clear()
        inputApellido.text.clear()
        inputEmail.text.clear()
        inputContrasena.text.clear()
        inputFechaNacimiento.text.clear()
        inputCiudad.text.clear()
        inputPais.text.clear()
    }

}
