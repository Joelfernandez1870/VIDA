package com.example.vida;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class RegistrarUsuarioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Cargar el layout del registro de usuario particular
        setContentView(R.layout.activity_registrar_usuario); // Asegúrate de que el nombre coincida con tu layout
    }
}