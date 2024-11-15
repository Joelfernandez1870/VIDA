package com.example.vida.view
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.vida.R
import com.example.vida.data.database.HospitalCentroDao
import com.google.android.gms.maps.OnMapReadyCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SeleccionRegistro : AppCompatActivity() {
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
        val myCardView = findViewById<CardView>(R.id.myCardViewInclude)
        // Configurar el OnClickListener para iniciar el card view de codigo habilitacion
        btnHospital.setOnClickListener {
            myCardView.visibility = View.VISIBLE
        }

        val cardViewButtonEnviar = myCardView.findViewById<Button>(R.id.btnCardViewEnviar)
        // OnClickListener para iniciar la actividad RegistrarHospitalActivity

        cardViewButtonEnviar.setOnClickListener {
            val codigo = myCardView.findViewById<EditText>(R.id.etCardview)
            val codigoString = codigo.text.toString()
            if (!codigoString.matches("\\d{8}".toRegex())) {
                codigo.error = "El codigo debe tener 8 dígitos"
            }
            lifecycleScope.launch(Dispatchers.IO) {
                val exito = HospitalCentroDao.verifiarCodigo (codigoString)

                withContext(Dispatchers.Main) {
                    if (exito) {
                        Toast.makeText(applicationContext, "Codigo verificado", Toast.LENGTH_SHORT).show()
                        val intent = Intent(applicationContext, RegistrarHospitalActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(applicationContext, "El codigo no existe", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        val cardViewButtonCancelar = myCardView.findViewById<Button>(R.id.btnCardViewCancelar)
        cardViewButtonCancelar.setOnClickListener {
            myCardView.visibility = View.GONE
        }
    }
}
