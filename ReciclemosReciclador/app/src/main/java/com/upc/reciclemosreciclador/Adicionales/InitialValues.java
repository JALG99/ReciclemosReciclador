package com.upc.reciclemosreciclador.Adicionales;

import android.app.Application;
import android.content.Context;

public class InitialValues extends Application {
    public static InitialValues initialValues;

    public static Context getContext() {
        return initialValues.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initialValues = this;
    }

    public String idReciclador;
    public String observacion;

    public String getIdReciclador() {
        return idReciclador;
    }

    public void setIdReciclador(String idUsuario) {
        this.idReciclador = idUsuario;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

}
