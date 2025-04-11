package com.example.ejemplo1;

public class Datos {
    String titulo;
    String[][] datos ={};

    public Datos(String titulo, String[][] datos) {
       this.titulo = titulo;
        this.datos = datos;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDatos(int i1, int i2) {
        return datos[i1][i2];
    }
}
