package com.example.vida.view

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.vida.R
import com.example.vida.data.database.MySqlConexion
import java.sql.Connection
import java.sql.PreparedStatement
import java.util.*

class NuevoGrupo : AppCompatActivity() {

    private lateinit var inputFechaCreacion: EditText // Campo de fecha

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_grupo)

        // Inicializar vistas
        val btnCrearGrupo = findViewById<Button>(R.id.buttonCrearGrupo)
        val inputNombreGrupo = findViewById<EditText>(R.id.editTextNombreGrupo)
        inputFechaCreacion = findViewById(R.id.editTextFechaCreacion) // Nuevo campo de fecha

        // Configurar campo de fecha con DatePicker
        inputFechaCreacion.setOnClickListener {
            mostrarDatePicker()
        }

        // Configurar botón "Crear Grupo"
        btnCrearGrupo.setOnClickListener {
            val nombreGrupo = inputNombreGrupo.text.toString()
            val fechaCreacion = inputFechaCreacion.text.toString()

            if (nombreGrupo.isNotBlank() && fechaCreacion.isNotBlank()) {
                insertarGrupo(nombreGrupo, fechaCreacion)
            } else {
                Toast.makeText(this, "Todos los campos son obligatorios.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Establecer la fecha seleccionada en el formato "dd/MM/yyyy"
                inputFechaCreacion.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
            },
            year, month, day
        )

        datePickerDialog.show()
    }

    private fun insertarGrupo(nombreGrupo: String, fechaCreacion: String) {
        // Ejecutar la inserción en un hilo separado para evitar bloquear la UI
        Thread {
            var connection: Connection? = null
            var preparedStatement: PreparedStatement? = null

            try {
                connection = MySqlConexion.getConexion() // Obtener la conexión
                val query = "INSERT INTO GRUPOS_CHAT (NOMBRE_GRUPO, FECHA_CREACION) VALUES (?, ?)"
                preparedStatement = connection?.prepareStatement(query)

                // Establecer los valores de los parámetros
                preparedStatement?.setString(1, nombreGrupo)
                preparedStatement?.setString(2, fechaCreacion)

                // Ejecutar la actualización
                val rowsInserted = preparedStatement?.executeUpdate()

                // Verificar si se insertaron filas
                if (rowsInserted != null && rowsInserted > 0) {
                    runOnUiThread {
                        Toast.makeText(this, "Grupo creado con éxito.", Toast.LENGTH_SHORT).show()

                        // Enviar broadcast para notificar que el grupo fue creado
                        val intent = Intent("com.example.vida.REFRESCAR_GRUPOS")
                        sendBroadcast(intent)

                        // Indicar que la creación fue exitosa y finalizar la actividad
                        setResult(RESULT_OK)
                        finish()  // Finalizar la actividad y volver a la anterior
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Error al crear el grupo.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (ex: Exception) {
                Log.e("Error", ex.message.toString())
                runOnUiThread {
                    Toast.makeText(this, "Error al conectarse a la base de datos.", Toast.LENGTH_SHORT).show()
                }
            } finally {
                try {
                    // Cerrar los recursos
                    preparedStatement?.close()
                    connection?.close()
                } catch (ex: Exception) {
                    Log.e("Error closing", ex.message.toString())
                }
            }
        }.start()
    }
}
