package com.upc.reciclemosreciclador.Entities;

public class ResumenRecoleccion {
    String nombre;
    String urbanizacion;
    String distrito;
    int pendientes;
    int recogidas;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUrbanizacion() {
        return urbanizacion;
    }

    public void setUrbanizacion(String urbanizacion) {
        this.urbanizacion = urbanizacion;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public int getPendientes() {
        return pendientes;
    }

    public void setPendientes(int pendientes) {
        this.pendientes = pendientes;
    }

    public int getRecogidas() {
        return recogidas;
    }

    public void setRecogidas(int recogidas) {
        this.recogidas = recogidas;
    }

    @Override
    public String toString() {
        return "ResumenRecoleccion{" +
                "nombre='" + nombre + '\'' +
                ", urbanizacion='" + urbanizacion + '\'' +
                ", distrito='" + distrito + '\'' +
                ", pendientes=" + pendientes +
                ", recogidas=" + recogidas +
                '}';
    }
}
