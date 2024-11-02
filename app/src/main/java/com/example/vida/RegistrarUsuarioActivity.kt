package com.example.vida

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class RegistrarUsuarioActivity : AppCompatActivity() {
    private var inputDNI: EditText? = null
    private var inputNombre: EditText? = null
    private var inputApellido: EditText? = null
    private var inputEmail: EditText? = null
    private var inputContrasena: EditText? = null
    private var inputGrupoSanguineo: EditText? = null
    private var inputFechaNacimiento: EditText? = null
    private var inputCiudad: EditText? = null
    private var inputPais: EditText? = null
    private var btnGuardar: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_usuario)

        // Inicializar los campos
        inputDNI = findViewById(R.id.inputDNI)
        inputNombre = findViewById(R.id.inputNombre)
        inputApellido = findViewById(R.id.inputApellido)
        inputEmail = findViewById(R.id.inputEmail)
        inputContrasena = findViewById(R.id.inputContrasena)
        inputGrupoSanguineo = findViewById(R.id.inputGrupoSanguineo)
        inputFechaNacimiento = findViewById(R.id.inputFechaNacimiento)
        inputCiudad = findViewById(R.id.inputCiudad)
        inputPais = findViewById(R.id.inputPais)
        btnGuardar = findViewById(R.id.btnGuardar)

        // Configurar el selector de fecha
        inputFechaNacimiento?.setOnClickListener(View.OnClickListener { v: View? -> mostrarDatePicker() })

        // Configurar el botón de guardar
        btnGuardar?.setOnClickListener(View.OnClickListener { v: View? -> validarDatos() })
    }

    private fun mostrarDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]

        val datePickerDialog = DatePickerDialog(
            this,
            { view: DatePicker?, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                inputFechaNacimiento!!.setText(selectedDay.toString() + "/" + (selectedMonth + 1) + "/" + selectedYear)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun validarDatos() {
        val dni = inputDNI!!.text.toString()
        val nombre = inputNombre!!.text.toString()
        val apellido = inputApellido!!.text.toString()
        val email = inputEmail!!.text.toString()
        val contrasena = inputContrasena!!.text.toString()
        val grupoSanguineo = inputGrupoSanguineo!!.text.toString()
        val fechaNacimiento = inputFechaNacimiento!!.text.toString()
        val ciudad = inputCiudad!!.text.toString()
        val pais = inputPais!!.text.toString()

        if (!dni.matches("\\d{7,8}".toRegex())) {
            inputDNI!!.error = "DNI debe tener 7 u 8 dígitos"
            return
        }
        if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+".toRegex())) {
            inputNombre!!.error = "Nombre solo debe contener letras"
            return
        }
        if (!apellido.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+".toRegex())) {
            inputApellido!!.error = "Apellido solo debe contener letras"
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail!!.error = "Email inválido"
            return
        }
        if (!contrasena.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$".toRegex())) {
            inputContrasena!!.error = "Contraseña debe contener letras y números"
            return
        }
        if (!esGrupoSanguineoValido(grupoSanguineo)) {
            inputGrupoSanguineo!!.error = "Grupo sanguíneo inválido"
            return
        }
        if (TextUtils.isEmpty(fechaNacimiento)) {
            inputFechaNacimiento!!.error = "Seleccione una fecha"
            return
        }
        if (!ciudad.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+".toRegex())) {
            inputCiudad!!.error = "Ciudad solo debe contener letras"
            return
        }
        if (!pais.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+".toRegex())) {
            inputPais!!.error = "País solo debe contener letras"
            return
        }

        // Datos válidos
        Toast.makeText(this, "Datos validados correctamente", Toast.LENGTH_SHORT).show()
        // Aquí puedes guardar los datos o realizar otra acción
    }

    private fun esGrupoSanguineoValido(grupo: String): Boolean {
        val gruposValidos = arrayOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
        for (g in gruposValidos) {
            if (g.equals(grupo, ignoreCase = true)) {
                return true
            }
        }
        return false
    }
}
