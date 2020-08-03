package com.upc.reciclemosreciclador.Entities;

public class ResumenValidacion {
    String nombre;
    String urbanizacion;
    String distrito;
    int max;
    int pesoActual;
    int bolsas;

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

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getPesoActual() {
        return pesoActual;
    }

    public void setPesoActual(int pesoActual) {
        this.pesoActual = pesoActual;
    }

    public int getBolsas() {
        return bolsas;
    }

    public void setBolsas(int bolsas) {
        this.bolsas = bolsas;
    }

    @Override
    public String toString() {
        return "ResumenValidacion{" +
                "nombre='" + nombre + '\'' +
                ", urbanizacion='" + urbanizacion + '\'' +
                ", distrito='" + distrito + '\'' +
                ", max=" + max +
                ", pesoActual=" + pesoActual +
                ", bolsas=" + bolsas +
                '}';
    }
}
