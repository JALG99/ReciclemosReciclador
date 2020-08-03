package com.upc.reciclemosreciclador.Entities;

public class Producto {
    private int codigo;
    private String nombre;
    private double peso;
    private double contenido;
    private String descripcion;
    private String urlImage;
    private String barcode;
    private Categoria categoria;
    private Tipo_Contenido tipo_Contenido;

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public double getContenido() {
        return contenido;
    }

    public void setContenido(double contenido) {
        this.contenido = contenido;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Tipo_Contenido getTipo_Contenido() {
        return tipo_Contenido;
    }

    public void setTipo_Contenido(Tipo_Contenido tipo_Contenido) {
        this.tipo_Contenido = tipo_Contenido;
    }
}
