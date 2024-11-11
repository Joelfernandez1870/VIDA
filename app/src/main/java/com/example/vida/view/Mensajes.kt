package com.example.vida.view

//import android.R
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.vida.R
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale


class Mensajes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mensajes)

        val textFecha = findViewById<TextView>(R.id.textFecha)
        val fechaActual: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(java.util.Date())
        textFecha.text = fechaActual

    }
}