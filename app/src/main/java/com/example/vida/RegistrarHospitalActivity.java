package com.example.vida;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class RegistrarHospitalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Cargar el layout del registro de hospital
        setContentView(R.layout.activity_registrar_hospital); // Asegúrate de que el nombre coincida con tu layout
    }
}
