package com.example.ejemplo1;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class Adaptador extends BaseAdapter {
    private static LayoutInflater inflater = null;
    Context contexto;
    ArrayList<String[]> datos;
    boolean[] seleccionados;
    int[] cantidades;
    TextView total;
    double total1 = 0;
    String nombreLista;

    private ListaDeProductos ldp; // Agregar referencia a la actividad

    public Adaptador(Context contexto, ArrayList<String[]> datos, TextView total, String nombreLista, ListaDeProductos listaDeProductos) {
        this.contexto = contexto;
        this.datos = datos;
        this.seleccionados = new boolean[datos.size()];
        this.cantidades = new int[datos.size()];
        this.total = total;
        this.nombreLista = nombreLista; // Inicializar el nombre de la lista

        Arrays.fill(cantidades, 1);
        this.ldp = listaDeProductos;
        inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        recuperarSeleccionados();
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        final View vista = inflater.inflate(R.layout.elemento_lista, null);

        TextView titulo = vista.findViewById(R.id.txtTitulo);
        TextView precio = vista.findViewById(R.id.txtPrecio);
        CheckBox verificar = vista.findViewById(R.id.cbVerificar);
        TextView numero = vista.findViewById(R.id.txtNumero);
        Button mas = vista.findViewById(R.id.btnMas);
        Button menos = vista.findViewById(R.id.btnMenos);
        Button eliminar = vista.findViewById(R.id.btnEliminar);

        titulo.setText(datos.get(i)[0]);
        precio.setText("$" + datos.get(i)[1]);
        numeroCantidad(mas, menos, numero, i);

        numero.setText(String.valueOf(cantidades[i]));



        verificar.setChecked(seleccionados[i]);

        verificar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            seleccionados[i] = isChecked;
            calcularTotal();
            if (ldp != null) {
                ldp.temporal();
            }
            guardarSeleccionados();
            notifyDataSetChanged();
        });


        eliminar.setOnClickListener(v -> {
            // Eliminar el producto de la lista
            datos.remove(i);

            // Eliminar también el estado del checkbox y cantidad correspondiente
            boolean[] nuevoSeleccionados = new boolean[datos.size()];
            int[] nuevasCantidades = new int[datos.size()];

            int j = 0;
            for (int k = 0; k < seleccionados.length; k++) {
                if (k != i) { // Saltamos el índice eliminado
                    nuevoSeleccionados[j] = seleccionados[k];
                    nuevasCantidades[j] = cantidades[k];
                    j++;
                }
            }

            seleccionados = nuevoSeleccionados;
            cantidades = nuevasCantidades;

            // Actualizar la lista en SharedPreferences
            guardarProductos();
            guardarSeleccionados();

            // Refrescar la lista después de la eliminación
            actualizarListView();
        });

        return vista;
    }

    private void guardarSeleccionados() {
        SharedPreferences sharedPreferences = contexto.getSharedPreferences("seleccionesPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        for (int i = 0; i < datos.size(); i++) {
            String nombreProducto = datos.get(i)[0]; // Identificador único del producto
            editor.putBoolean("seleccionado_" + nombreLista + "_" + nombreProducto, seleccionados[i]);
        }

        editor.apply();
    }

    public void recuperarSeleccionados() {
        SharedPreferences sharedPreferences = contexto.getSharedPreferences("seleccionesPrefs", Context.MODE_PRIVATE);

        for (int i = 0; i < datos.size(); i++) {
            String nombreProducto = datos.get(i)[0]; // Identificador único del producto
            seleccionados[i] = sharedPreferences.getBoolean("seleccionado_" + nombreLista + "_" + nombreProducto, false);
        }

        notifyDataSetChanged(); // Refrescar la vista
    }



    private void guardarProductos() {
        SharedPreferences sharedPreferences = contexto.getSharedPreferences("productosPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Convertir los productos a una cadena separada por saltos de línea y comas
        StringBuilder productosStr = new StringBuilder();
        for (String[] producto : datos) {
            productosStr.append(producto[0]).append(",")
                    .append(producto[1]).append(",")
                    .append(producto[2]).append("\n");
        }

        // Guardar la cadena con la clave correspondiente al nombre de la lista
        editor.putString("productos_" + nombreLista, productosStr.toString());

        // Guardar el estado de las selecciones
        StringBuilder seleccionesStr = new StringBuilder();
        for (boolean seleccionado : seleccionados) {
            seleccionesStr.append(seleccionado).append(",");
        }
        editor.putString("selecciones_" + nombreLista, seleccionesStr.toString());

        editor.apply();
    }

    public void actualizarListView() {
        total.setText("Total: $" + total1);
        seleccionados = Arrays.copyOf(seleccionados, datos.size());
        cantidades = Arrays.copyOf(cantidades, datos.size());
        notifyDataSetChanged();
        total1 = 0;

        for (int i = 0; i < datos.size(); i++) {
            if (seleccionados[i]) {
                int precio = Integer.parseInt(datos.get(i)[1]);
                total1 += cantidades[i] * precio;
            }
        }
        total.setText("Total: $" + total1);
    }

    public void calcularTotal() {
        total1 = 0;
        for (int i = 0; i < datos.size(); i++) {
            if (seleccionados[i]) {
                double precio = Double.parseDouble(datos.get(i)[1]);
                total1 += cantidades[i] * precio;
            }
        }
        total.setText("Total: $" + total1);
    }

    public void actualizarDatos(ArrayList<String[]> nuevosDatos) {
        this.datos = nuevosDatos;
        notifyDataSetChanged();  // Refrescar la vista sin perder estados
    }

    public void numeroCantidad(Button mas, Button menos, TextView numero, int index) {
        mas.setOnClickListener(v -> {
            cantidades[index]++;
            numero.setText(String.valueOf(cantidades[index]));
            calcularTotal();
        });

        menos.setOnClickListener(v -> {
            if (cantidades[index] > 1) {
                cantidades[index]--;
                numero.setText(String.valueOf(cantidades[index]));
                calcularTotal();
            }
        });
    }

    @Override
    public int getCount() {
        return datos.size();
    }

    @Override
    public Object getItem(int position) {
        return datos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}

