package com.example.vida;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class RegistroActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Cargar el layout del registro
        setContentView(R.layout.activity_registro);

        // Captura el botón de Usuario Particular
        Button btnUsuarioParticular = findViewById(R.id.btnUsuarioParticular);
        // Configura el evento click para redirigir a la actividad de registro de usuario particular
        btnUsuarioParticular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistroActivity.this, RegistrarUsuarioActivity.class);
                startActivity(intent);
            }
        });

        // Captura el botón de Hospital
        Button btnHospital = findViewById(R.id.btnHospital);
        // Configura el evento click para redirigir a la actividad de registro de hospital
        btnHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistroActivity.this, RegistrarHospitalActivity.class);
                startActivity(intent);
            }
        });
    }
}
