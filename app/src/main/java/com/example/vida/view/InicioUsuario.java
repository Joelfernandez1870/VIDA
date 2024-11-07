package com.example.vida.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.vida.R;

public class InicioUsuario extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_usuario);

        // Obtener referencia al botón "Centro de Donaciones"
        Button btnCentroDonaciones = findViewById(R.id.btnHospitales);

        // Configurar el OnClickListener para iniciar la actividad Centro_Donaciones
        btnCentroDonaciones.setOnClickListener(v -> {
            Intent intent = new Intent(InicioUsuario.this, CentroDonaciones.class);
            startActivity(intent);
        });

        // Obtener referencia al botón "Donaciones"
        Button btnDonaciones = findViewById(R.id.btnDonaciones);

        // Configurar el OnClickListener para iniciar la actividad Donaciones
        btnDonaciones.setOnClickListener(v -> {
            Intent intent = new Intent(InicioUsuario.this,Donaciones.class);
            startActivity(intent);
        });

        // Obtener referencia al botón "Beneficios"
        Button btnBeneficios = findViewById(R.id.btnBeneficios);

        // Configurar el OnClickListener para iniciar la actividad Beneficios
        btnBeneficios.setOnClickListener(v -> {
            Intent intent = new Intent(InicioUsuario.this,Beneficios.class);
            startActivity(intent);
        });

        // Obtener referencia al botón "Chats"
        Button btnChats = findViewById(R.id.btnChats);

        // Configurar el OnClickListener para iniciar la actividad Chats
        btnChats.setOnClickListener(v -> {
            Intent intent = new Intent(InicioUsuario.this,Chats.class);
            startActivity(intent);
        });
        // Obtener referencia al botón "Pedidos"
        Button btnPedidos = findViewById(R.id.btnListaPedidos);

        // Configurar el OnClickListener para iniciar la actividad Pedidos
        btnPedidos.setOnClickListener(v -> {
            Intent intent = new Intent(InicioUsuario.this,ListaPedidosUsuario.class);
            startActivity(intent);
        });
    }
}
