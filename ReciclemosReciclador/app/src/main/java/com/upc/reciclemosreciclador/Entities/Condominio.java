package com.upc.reciclemosreciclador.Entities;

import com.google.gson.annotations.SerializedName;

public class Condominio {

    @SerializedName("codigo")
    private Integer codigo;
    @SerializedName("direccion")
    private String direccion;
    @SerializedName("distrito")
    private Distrito distrito;
    @SerializedName("nombre")
    private String nombre;
    @SerializedName("urbanizacion")
    private String urbanizacion;
    @SerializedName("nombreContacto")
    private String nombreContacto;
    @SerializedName("numeroContacto")
    private String numeroContacto;
    private Reciclador reciclador;


    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Distrito getDistrito() {
        return distrito;
    }

    public void setDistrito(Distrito distrito) {
        this.distrito = distrito;
    }

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

    public String getNombreContacto() {
        return nombreContacto;
    }

    public void setNombreContacto(String nombreContacto) {
        this.nombreContacto = nombreContacto;
    }

    public String getNumeroContacto() {
        return numeroContacto;
    }

    public void setNumeroContacto(String numeroContacto) {
        this.numeroContacto = numeroContacto;
    }

    public Reciclador getReciclador() {
        return reciclador;
    }

    public void setReciclador(Reciclador reciclador) {
        this.reciclador = reciclador;
    }
}