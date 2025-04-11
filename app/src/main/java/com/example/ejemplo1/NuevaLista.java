package com.example.ejemplo1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class NuevaLista extends AppCompatActivity {

    private EditText nombre;
    private Button crear, cancelar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nueva_lista);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nombre = findViewById(R.id.edtNombreNuevoProducto);

        crear = findViewById(R.id.btnAgregar);
        cancelar = findViewById(R.id.btnCancelar);

        crear.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                crear(nombre.getText().toString());
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                regresar(view);
            }
        });
    }

    public void crear(String nuevoNombre) {
        Intent intent = new Intent();
        intent.putExtra("nuevoNombre", nuevoNombre);

        // Establecer el resultado de la actividad
        setResult(RESULT_OK, intent);

        // Cerrar la actividad actual
        finish();
    }

    public void regresar(View view) {
        finish();
    }
}
