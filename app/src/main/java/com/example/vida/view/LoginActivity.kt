package com.example.vida.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.vida.R
import com.example.vida.data.database.MySqlConexion
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class LoginActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerLink: TextView
    private lateinit var loading: ProgressBar

    // Variable global para almacenar el hospitalId
    companion object {
        var sesionGlobal: Int? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        usernameEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.password)
        loginButton = findViewById(R.id.login) // Botón de login
        registerLink = findViewById(R.id.register_link)
        loading = findViewById(R.id.loading)

        // Acción para redirigir al presionar el enlace de "Registrarse"
        registerLink.setOnClickListener {
            val intent = Intent(this@LoginActivity, SeleccionRegistro::class.java)
            startActivity(intent)
        }

        // Acción para el botón de "Ingresar"
        loginButton.setOnClickListener {
            val email = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                validateLogin(email, password)
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun validateLogin(email: String, password: String) {
        loading.visibility = View.VISIBLE
        Thread {
            var connection: Connection? = null
            var resultSet: ResultSet? = null
            var preparedStatement: PreparedStatement? = null

            try {
                connection = MySqlConexion.getConexion()
                val query = """
                      SELECT TIPO_USUARIO, ID_USUARIO, ID_HOSPITALES_CENTRO
                        FROM (
                            SELECT TIPO_USUARIO, ID_USUARIO, NULL AS ID_HOSPITALES_CENTRO FROM USUARIO WHERE EMAIL = ? AND CONTRASENIA = ?
                            UNION
                            SELECT TIPO_USUARIO, NULL AS ID_USUARIO, ID_HOSPITALES_CENTRO FROM HOSPITALES_CENTROS WHERE EMAIL = ? AND CONTRASENIA = ?
                        ) AS usuarios_comb
                        LIMIT 1;
                    """.trimIndent()


                preparedStatement = connection?.prepareStatement(query)
                preparedStatement?.setString(1, email)
                preparedStatement?.setString(2, password)
                preparedStatement?.setString(3, email)
                preparedStatement?.setString(4, password)

                resultSet = preparedStatement?.executeQuery()

                if (resultSet?.next() == true) {

                    // AGARRO EL TIPO DE USUARIO
                    val tipoUsuario = resultSet.getInt("TIPO_USUARIO")

                    // Dependiendo del tipo de usuario, guardamos el ID en la variable global
                    if (tipoUsuario == 1) {  // Usuario
                        sesionGlobal = resultSet.getInt("ID_USUARIO")
                    } else if (tipoUsuario == 0) {  // Hospital
                        sesionGlobal = resultSet.getInt("ID_HOSPITALES_CENTRO")
                    }

                    // Credenciales válidas
                    runOnUiThread {
                        loading.visibility = View.GONE
                        val intent = if (tipoUsuario == 1) {
                            // Si es usuario, redirige a InicioUsuario
                            Intent(this@LoginActivity, InicioUsuario::class.java)
                        } else if (tipoUsuario == 0) {
                            // Si es hospital, redirige a InicioHospitalesYCentros
                            Intent(this@LoginActivity, InicioHospitalesYCentros::class.java)
                        } else {
                            // Si no se encuentra el tipo de usuario
                            Toast.makeText(this@LoginActivity, "Tipo de usuario desconocido", Toast.LENGTH_SHORT).show()
                            return@runOnUiThread
                        }

                        startActivity(intent)
                        finish() // Finaliza la actividad de login
                    }
                } else {
                    // Credenciales inválidas
                    runOnUiThread {
                        loading.visibility = View.GONE
                        Toast.makeText(this, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (ex: Exception) {
                Log.e("Error", ex.message.toString())
                runOnUiThread {
                    loading.visibility = View.GONE
                    Toast.makeText(this, "Error al conectarse a la base de datos", Toast.LENGTH_SHORT).show()
                }
            } finally {
                try {
                    resultSet?.close()
                    preparedStatement?.close()
                    connection?.close()
                } catch (ex: Exception) {
                    Log.e("Error closing", ex.message.toString())
                }
            }
        }.start()
    }

}

