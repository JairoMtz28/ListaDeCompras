package com.example.ejemplo1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;

public class ListaDeProductos extends AppCompatActivity {
    private ListView lista;
    private Spinner productos;
    private TextView total;
    private TextView nombre;
    private Button agregar, irMain, eliminarLista;
    private ArrayList<String[]> datos, datosFiltrados, productosFiltrados;
    private String nombreLista;
    private Adaptador adp;
    private boolean[] temporal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_de_productos);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nombre = findViewById(R.id.textViewNombre);
        lista = findViewById(R.id.lvLista);
        total = findViewById(R.id.txtTotal);
        agregar = findViewById(R.id.btnAgregar);
        irMain = findViewById(R.id.btnIrMain);
        productos = findViewById(R.id.spProductos);
        eliminarLista = findViewById(R.id.btnEliminarLista);

        datos = new ArrayList<>();
        datosFiltrados = new ArrayList<>();
        productosFiltrados = new ArrayList<>();



        String[] opciones = {"Todos los productos", "Solo comprados", "Solo no comprados"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productos.setAdapter(adapter);

        nombreLista = getIntent().getStringExtra("listaSeleccionada");
        nombre.setText("Lista: " + nombreLista);

        datos = obtenerProductos();

        this.temporal = new boolean[datos.size()];

        String nombreProducto = getIntent().getStringExtra("nuevoProducto");
        String precioProducto = getIntent().getStringExtra("nuevoPrecio");

        if (nombreProducto != null && !nombreProducto.isEmpty()) {
            datos.add(new String[]{nombreProducto, precioProducto, nombreLista});
            Log.d("MiTag", "Se agregó " + nombreProducto + " a " + nombreLista);
            guardarProductos(datos);
        }

        for (String[] producto : datos) {
            if (producto[2].equals(nombreLista)) {
                productosFiltrados.add(producto);
            }
        }

        adp = new Adaptador(this, productosFiltrados, total, nombreLista, this);
        lista.setAdapter(adp);


        productos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.arraycopy(temporal, 0, adp.seleccionados, 0, temporal.length);//cambio
                filtrarLista(productos.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        agregar.setOnClickListener(view -> crearNuevoProducto(nombreLista));
        irMain.setOnClickListener(this::mainActivity);
        eliminarLista.setOnClickListener(view -> eliminarLista());

    }




    public void eliminarLista() {
        SharedPreferences sharedPreferences = getSharedPreferences("productosPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Eliminar los productos de la lista actual
        editor.remove("productos_" + nombreLista);
        editor.remove("selecciones_" + nombreLista);
        editor.apply();

        // También eliminar la lista del SharedPreferences de MainActivity
        SharedPreferences listaPrefs = getSharedPreferences("nombresPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor listaEditor = listaPrefs.edit();

        // Obtener las listas guardadas
        String listasGuardadas = listaPrefs.getString("nombres", "");

        // Crear una nueva lista sin la lista eliminada
        if (!listasGuardadas.isEmpty()) {
            StringBuilder nuevasListas = new StringBuilder();
            String[] listas = listasGuardadas.split(",");
            for (String lista : listas) {
                if (!lista.equals(nombreLista)) {
                    nuevasListas.append(lista).append(",");
                }
            }
            listaEditor.putString("nombres", nuevasListas.toString());
            listaEditor.apply();
        }

        // Volver a la pantalla principal después de eliminar la lista
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Esto evita que queden actividades apiladas
        startActivity(intent);
        finish();
    }



    public void temporal() {
        this.temporal = new boolean[adp.seleccionados.length];
        System.arraycopy(adp.seleccionados, 0, temporal, 0, adp.seleccionados.length);
    }

    public void filtrarLista(String filtro) {
        datosFiltrados.clear();

        // Filtrar según el filtro seleccionado
        if (filtro.equals("Solo comprados")) {
            for (int i = 0; i < datos.size(); i++) {
                if (temporal[i]) {
                    datosFiltrados.add(datos.get(i));
                }
            }
            Arrays.fill(adp.seleccionados, true);
        } else if (filtro.equals("Solo no comprados")) {
            for (int i = 0; i < datos.size(); i++) {
                if (i < temporal.length && !temporal[i]) {
                    datosFiltrados.add(datos.get(i));
                }
            }
            Arrays.fill(adp.seleccionados, false);
        }

        if (filtro.equals("Todos los productos")) {
            datosFiltrados.addAll(datos);
            adp.recuperarSeleccionados();
        }

        // Actualizar el adaptador con los productos filtrados
        adp.actualizarDatos(datosFiltrados);
        adp.calcularTotal();
    }







    public void mainActivity(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void guardarProductos(ArrayList<String[]> productos) {
        SharedPreferences sharedPreferences = getSharedPreferences("productosPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        StringBuilder productosStr = new StringBuilder();
        for (String[] producto : productos) {
            productosStr.append(producto[0]).append(",")
                    .append(producto[1]).append(",")
                    .append(producto[2]).append("\n");
        }

        editor.putString("productos_" + nombreLista, productosStr.toString());
        editor.apply();
    }

    public ArrayList<String[]> obtenerProductos() {
        SharedPreferences sharedPreferences = getSharedPreferences("productosPrefs", Context.MODE_PRIVATE);
        String productosStr = sharedPreferences.getString("productos_" + nombreLista, null);

        ArrayList<String[]> listaProductos = new ArrayList<>();
        if (productosStr != null) {
            String[] productosArray = productosStr.split("\n");
            for (String productoStr : productosArray) {
                String[] producto = productoStr.split(",");
                if (producto.length == 3) {
                    listaProductos.add(producto);
                }
            }
        }
        return listaProductos;
    }

    public void crearNuevoProducto(String identificador) {
        Intent i = new Intent(this, NuevoProducto.class);
        i.putExtra("nuevoProducto", identificador);
        startActivity(i);
    }
}

