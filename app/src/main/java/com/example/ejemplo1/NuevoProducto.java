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

public class NuevoProducto extends AppCompatActivity {

    private EditText nombre, precio;
    private Button agregar, cancelar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nuevo_producto);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obtener el Intent y el nombre seleccionado
        String nombreRecibido = getIntent().getStringExtra("nuevoProducto");

        nombre = findViewById(R.id.edtNombreNuevoProducto);
        precio = findViewById(R.id.edtNuevoPrecio);

        agregar = findViewById(R.id.btnAgregar);
        cancelar = findViewById(R.id.btnCancelar);

        agregar.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                double n = Double.parseDouble(precio.getText().toString());

                crear(nombre.getText().toString(), precio.getText().toString(), nombreRecibido);
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

    public void crear(String nuevoProducto, String nuevoPrecio, String nombre) {
        Intent i = new Intent(this, ListaDeProductos.class);

        i.putExtra("nuevoProducto", nuevoProducto);
        i.putExtra("nuevoPrecio", nuevoPrecio);
        i.putExtra("listaSeleccionada", nombre);

        startActivity(i);
    }

    public void regresar (View view){
        finish();
    }
}