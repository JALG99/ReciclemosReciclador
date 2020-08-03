package com.upc.reciclemosreciclador.Entities;

public class QrCode {

    private int codigo;
    private String creadoFecha;
    private String qrCode;

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getCreadoFecha() {
        return creadoFecha;
    }

    public void setCreadoFecha(String creadoFecha) {
        this.creadoFecha = creadoFecha;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}
