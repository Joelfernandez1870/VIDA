package com.example.vida.view

import android.app.AlertDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.vida.R
import com.example.vida.data.database.BeneficioDao
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder

data class Beneficio(val nombre: String, val puntosRequeridos: Int, val desbloqueado: Boolean)

class Beneficios : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beneficios)

        val listViewBeneficios = findViewById<ListView>(R.id.listViewBeneficios)

        // Simulo punto (prubea) REEMPLAZAR POR PUNTOS DE USUARIO EN SESION
        val puntosUsuario = 25

        val beneficios = BeneficioDao.getAllBeneficios()

        if (beneficios.isEmpty()) {
            // Si la lista está vacía, mostrar un mensaje usando un Toast
            Toast.makeText(this, "No se encontraron beneficios disponibles.", Toast.LENGTH_SHORT).show()
        } else {

//        // Lista de beneficios precargados
//        val beneficios = listOf(
//            Beneficio("10% de Descuento en Estación de Servicios", 10, false),
//            Beneficio("15% de Descuento en Farmacity", 20, false),
//            Beneficio("25% de Descuento en Ropa Deportiva", 30, false),
//            Beneficio("2x1 en Cine", 40, false),
//            Beneficio("50% de Descuento en Restaurantes", 50, false)
//        )



            // Crear lista de beneficios desbloqueados y bloqueados
            val listaBeneficiosConEstado = beneficios.map { beneficio ->
                if (puntosUsuario >= beneficio.puntos_necesario) {
                    "✅ ${beneficio.descripcion} - Desbloqueado"
                } else {
                    "🔒 ${beneficio.descripcion} - Requiere ${beneficio.puntos_necesario} puntos"
                }
            }

            // Configurar el adaptador para mostrar la lista
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                listaBeneficiosConEstado
            )
            listViewBeneficios.adapter = adapter
        }

        // Manejar clic en los beneficios
        listViewBeneficios.setOnItemClickListener { _, _, position, _ ->
            val beneficio = beneficios[position]
            if (puntosUsuario < beneficio.puntos_necesario) {
                // Mostrar mensaje si el beneficio está bloqueado
                showToast("Este beneficio está bloqueado. Sigue donando para desbloquearlo.")
            } else {
                // Mostrar el código QR para el beneficio desbloqueado
                mostrarCodigoQR(beneficio.descripcion)
            }
        }
    }

    private fun mostrarCodigoQR(texto: String) {
        try {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(
                texto,
                BarcodeFormat.QR_CODE,
                400,
                400
            )

            // Crear un diálogo para mostrar el QR
            val imageView = ImageView(this).apply {
                setImageBitmap(bitmap)
            }
            AlertDialog.Builder(this)
                .setTitle("Código QR")
                .setView(imageView)
                .setPositiveButton("Cerrar") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        } catch (e: WriterException) {
            e.printStackTrace()
            showToast("Error al generar el código QR.")
        }
    }

    private fun showToast(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }
}