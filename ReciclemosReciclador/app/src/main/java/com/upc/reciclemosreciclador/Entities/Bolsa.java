package com.upc.reciclemosreciclador.Entities;

import java.util.Date;

public class Bolsa {
    private Integer codigo;
    private Date creadoFecha;
    private Date recojoFecha;
    private int puntuacion;
    private boolean activa;
    private QrCode qrCode;
    private String observaciones;
    private Usuario usuario;
    private boolean validado;

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public Date getCreadoFecha() {
        return creadoFecha;
    }

    public void setCreadoFecha(Date creadoFecha) {
        this.creadoFecha = creadoFecha;
    }

    public boolean getActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public Date getRecojoFecha() {
        return recojoFecha;
    }

    public void setRecojoFecha(Date recojoFecha) {
        this.recojoFecha = recojoFecha;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    public QrCode getQrCode() {
        return qrCode;
    }

    public void setQrCode(QrCode qrCode) {
        this.qrCode = qrCode;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public boolean getValidado() {
        return validado;
    }

    public void setValidado(boolean validado) {
        this.validado = validado;
    }

    public boolean isActiva() {
        return activa;
    }

    public boolean isValidado() {
        return validado;
    }
}
