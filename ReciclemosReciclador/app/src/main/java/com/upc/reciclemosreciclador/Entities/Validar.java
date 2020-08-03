package com.upc.reciclemosreciclador.Entities;

import android.widget.ImageView;
import android.widget.TextView;

public class Validar {
    String utlimagen;
    String nombre;
    Double contenido;
    String abreviatura;
    String categoria;
    Integer cantidad;
    Boolean validado;

    public String getUtlimagen() {
        return utlimagen;
    }

    public void setUtlimagen(String utlimagen) {
        this.utlimagen = utlimagen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getContenido() {
        return contenido;
    }

    public void setContenido(Double contenido) {
        this.contenido = contenido;
    }

    public String getAbreviatura() {
        return abreviatura;
    }

    public void setAbreviatura(String abreviatura) {
        this.abreviatura = abreviatura;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Boolean getValidado() {
        return validado;
    }

    public void setValidado(Boolean validado) {
        this.validado = validado;
    }
}
