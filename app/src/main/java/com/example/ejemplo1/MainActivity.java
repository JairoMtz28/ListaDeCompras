package com.example.ejemplo1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView lista;
    private FloatingActionButton nuevo;
    private ArrayList<String> nombres;  // Usamos ArrayList en lugar de String[]

    private ArrayAdapter<String> adapter; // Adaptador global

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nuevo = findViewById(R.id.fabNuevo);
        lista = findViewById(R.id.lvLista);

        // Inicializar la lista con datos
        nombres = new ArrayList<>();

        // Cargar los nombres desde SharedPreferences
        cargarNombres();

        // Crear y asignar el adaptador
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nombres);
        lista.setAdapter(adapter);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listaDeProductos(position); // Pasamos la posición correcta
            }
        });

        nuevo.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                nuevaLista(view);
            }
        });
    }

    public void listaDeProductos(int position) {
        Intent i = new Intent(this, ListaDeProductos.class);

        // Enviar el nombre del elemento seleccionado
        if (position >= 0 && position < nombres.size()) {
            i.putExtra("listaSeleccionada", nombres.get(position));
        }
        startActivity(i);
    }

    public void nuevaLista(View view) {
        Intent i = new Intent(this, NuevaLista.class);
        startActivityForResult(i, 1); // Cambia a startActivityForResult para recibir el nuevo nombre
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String nuevoNombre = data.getStringExtra("nuevoNombre");
            if (nuevoNombre != null && !nuevoNombre.isEmpty()) {
                nombres.add(nuevoNombre);
                guardarNombres(); // Guarda la lista actualizada
                adapter.notifyDataSetChanged(); // Notifica al adaptador que los datos han cambiado
            }
        }
    }

    private void guardarNombres() {
        SharedPreferences sharedPreferences = getSharedPreferences("nombresPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Convertir la lista de nombres a una cadena separada por comas
        StringBuilder nombresStr = new StringBuilder();
        for (String nombre : nombres) {
            nombresStr.append(nombre).append(",");
        }

        // Guardar la cadena
        editor.putString("nombres", nombresStr.toString());
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarNombres(); // Vuelve a cargar los nombres desde SharedPreferences
        adapter.notifyDataSetChanged(); // Notifica al adaptador que los datos han cambiado
    }


    private void cargarNombres() {
        SharedPreferences sharedPreferences = getSharedPreferences("nombresPrefs", MODE_PRIVATE);
        String nombresStr = sharedPreferences.getString("nombres", "");

        // Limpiar la lista actual
        nombres.clear();

        // Dividir la cadena en nombres y agregar a la lista
        if (!nombresStr.isEmpty()) {
            String[] nombresArray = nombresStr.split(",");
            for (String nombre : nombresArray) {
                if (!nombre.isEmpty()) {
                    nombres.add(nombre);
                }
            }
        }
    }
}
