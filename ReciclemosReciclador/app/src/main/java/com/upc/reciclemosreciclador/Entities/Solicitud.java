package com.upc.reciclemosreciclador.Entities;

import java.util.Date;

public class Solicitud {
    private int id;
    private String datosActualizar;
    private String sustento;
    private Date fechaSolicitud;
    private boolean activa;
    private Reciclador reciclador;
    private Usuario usuario;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDatosActualizar() {
        return datosActualizar;
    }

    public void setDatosActualizar(String datosActualizar) {
        this.datosActualizar = datosActualizar;
    }

    public String getSustento() {
        return sustento;
    }

    public void setSustento(String sustento) {
        this.sustento = sustento;
    }

    public Date getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(Date fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public Reciclador getReciclador() {
        return reciclador;
    }

    public void setReciclador(Reciclador reciclador) {
        this.reciclador = reciclador;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
