package com.example.vida;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import java.util.regex.Pattern;

public class RegistrarUsuarioActivity extends AppCompatActivity {

    private EditText inputDNI, inputNombre, inputApellido, inputEmail, inputContrasena,
            inputGrupoSanguineo, inputFechaNacimiento, inputCiudad, inputPais;
    private Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario);

        // Inicializar los campos
        inputDNI = findViewById(R.id.inputDNI);
        inputNombre = findViewById(R.id.inputNombre);
        inputApellido = findViewById(R.id.inputApellido);
        inputEmail = findViewById(R.id.inputEmail);
        inputContrasena = findViewById(R.id.inputContrasena);
        inputGrupoSanguineo = findViewById(R.id.inputGrupoSanguineo);
        inputFechaNacimiento = findViewById(R.id.inputFechaNacimiento);
        inputCiudad = findViewById(R.id.inputCiudad);
        inputPais = findViewById(R.id.inputPais);
        btnGuardar = findViewById(R.id.btnGuardar);

        // Configurar el selector de fecha
        inputFechaNacimiento.setOnClickListener(v -> mostrarDatePicker());

        // Configurar el botón de guardar
        btnGuardar.setOnClickListener(v -> validarDatos());
    }

    private void mostrarDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    inputFechaNacimiento.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void validarDatos() {
        String dni = inputDNI.getText().toString();
        String nombre = inputNombre.getText().toString();
        String apellido = inputApellido.getText().toString();
        String email = inputEmail.getText().toString();
        String contrasena = inputContrasena.getText().toString();
        String grupoSanguineo = inputGrupoSanguineo.getText().toString();
        String fechaNacimiento = inputFechaNacimiento.getText().toString();
        String ciudad = inputCiudad.getText().toString();
        String pais = inputPais.getText().toString();

        if (!dni.matches("\\d{7,8}")) {
            inputDNI.setError("DNI debe tener 7 u 8 dígitos");
            return;
        }
        if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            inputNombre.setError("Nombre solo debe contener letras");
            return;
        }
        if (!apellido.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            inputApellido.setError("Apellido solo debe contener letras");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("Email inválido");
            return;
        }
        if (!contrasena.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$")) {
            inputContrasena.setError("Contraseña debe contener letras y números");
            return;
        }
        if (!esGrupoSanguineoValido(grupoSanguineo)) {
            inputGrupoSanguineo.setError("Grupo sanguíneo inválido");
            return;
        }
        if (TextUtils.isEmpty(fechaNacimiento)) {
            inputFechaNacimiento.setError("Seleccione una fecha");
            return;
        }
        if (!ciudad.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            inputCiudad.setError("Ciudad solo debe contener letras");
            return;
        }
        if (!pais.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            inputPais.setError("País solo debe contener letras");
            return;
        }

        // Datos válidos
        Toast.makeText(this, "Datos validados correctamente", Toast.LENGTH_SHORT).show();
        // Aquí puedes guardar los datos o realizar otra acción
    }

    private boolean esGrupoSanguineoValido(String grupo) {
        String[] gruposValidos = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        for (String g : gruposValidos) {
            if (g.equalsIgnoreCase(grupo)) {
                return true;
            }
        }
        return false;
    }
}
