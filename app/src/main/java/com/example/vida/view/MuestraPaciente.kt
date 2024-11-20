package com.example.vida.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vida.R
import com.example.vida.models.Paciente
import android.widget.EditText
import android.widget.Toast
import com.example.vida.data.database.PacienteDao.getAllPacientesFromHospital

class MuestraPaciente : AppCompatActivity() {

    private lateinit var pacientes: List<Paciente>
    private lateinit var adapter: PacienteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_muestra_paciente)

        // Inicializar RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewPacientes)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Obtener pacientes desde la base de datos
        val idHospital = LoginActivity.sesionGlobal.toString()
        pacientes = getAllPacientesFromHospital(idHospital)
        Toast.makeText(this, "ID del Hospital: $pacientes", Toast.LENGTH_SHORT).show()

        // Configurar adaptador
        adapter = PacienteAdapter(pacientes)
        recyclerView.adapter = adapter

        // Implementar el filtro por nombre
        val searchEditText: EditText = findViewById(R.id.searchEditText)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val filteredList = pacientes.filter {
                    it.nombre.contains(s.toString(), ignoreCase = true)
                }
                adapter.updateList(filteredList)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    // Método para obtener pacientes desde la base de datos
//    private fun getAllPacientes(): List<Paciente> {
//        val pacientes = mutableListOf<Paciente>()
//        val conn: Connection? = MySqlConexion.getConexion()
//        var statement: Statement? = null
//        var resultSet: ResultSet? = null
//
//        try {
//            statement = conn?.createStatement()
//            resultSet = statement?.executeQuery("SELECT * FROM PACIENTE")
//
//            while (resultSet?.next() == true) {
//                val paciente = Paciente(
//                    idPaciente = resultSet.getInt("ID_PACIENTE"),
//                    dni = resultSet.getString("DNI"),
//                    nombre = resultSet.getString("NOMBRE"),
//                    apellido = resultSet.getString("APELLIDO"),
//                    email = resultSet.getString("EMAIL"),
//                    grupoSanguineo = resultSet.getString("GRUPO_SANGUINEO"),
//                    fechaNacimiento = resultSet.getString("FECHA_NACIMIENTO"),
//                    ciudad = resultSet.getString("CIUDAD"),
//                    pais = resultSet.getString("PAIS")
//                )
//                pacientes.add(paciente)
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        } finally {
//            resultSet?.close()
//            statement?.close()
//            conn?.close()
//        }
//        return pacientes
//    }
}
