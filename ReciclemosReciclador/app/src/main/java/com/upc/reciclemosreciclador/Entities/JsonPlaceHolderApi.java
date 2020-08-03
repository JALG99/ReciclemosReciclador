package com.upc.reciclemosreciclador.Entities;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Url;

public interface JsonPlaceHolderApi {

    @GET
    Call<List<Probolsa>> getProbolsaByBolsa(@Url String url);

    @GET
    Call<List<Bolsa>> getBolsas(@Url String url);

    @GET
    Call<Producto> getProductoById(@Url String url);

    @GET
    Call<Bolsa> getBolsaByQRCode(@Url String url);

    @GET
    Call<Integer> getEmail(@Url String url);

    @GET
    Call<Reciclador> getCodFormalizado(@Url String url);

    @GET
    Call<List<Bolsa>> getBolsaByReciclador(@Url String url);

    @GET
    Call<List<Probolsa>> getProbolsaByReciclador(@Url String url);

    @GET
    Call<List<Probolsa>> getProductoByIdBolsa(@Url String url);

    @PUT("reciclador/registrar")
    Call<Reciclador> createReciclador(@Body Reciclador reciclador);

    @PUT
    Call<Bolsa> bolsaRecogida(@Url String url);

    @PUT
    Call<Bolsa> agregarObservacion(@Url String url);

    @PUT
    Call<Bolsa> bolsaValidada(@Url String url);

    @GET
    Call<Usuario> getUsuario(@Url String url);

    @GET
    Call<Reciclador> getRecicladorByEmailPassword(@Url String url);

    @GET
    Call<String[]> getRecicladorByEmail(@Url String url);

    @GET
    Call<QrCode> getQrCode(@Url String url);

    @PUT
    Call<Bolsa> putQrBolsa(@Url String url);

    @PUT
    Call<Probolsa> putCantidadProbolsa(@Url String url);

    @GET
    Call<Bolsa> getBolsaActiva(@Url String url);

    @DELETE
    Call<Probolsa> deleteProbolsa(@Url String url);

    @GET
    Call<List<Producto>> getProductos(@Url String url);

    @GET
    Call<List<Departamento>> getAllDepartamento(@Url String url);

    @GET
    Call<List<Distrito>> getDistritoByDepartamento(@Url String url);

    @GET
    Call<List<Condominio>> getCondominioByDistrito(@Url String url);

    @GET
    Call<Sexo> getSexoById(@Url String url);

    @GET
    Call<String[]> getUsuarioByEmail(@Url String url);

    @GET
    Call<List<Condominio>> getCondominiosByReciclador(@Url String url);

    @GET
    Call<List<Probolsa>> getProbolsasByDate(@Url String url);

    @GET
    Call<List<Bolsa>> getBolsasByDate(@Url String url);

    @GET
    Call<ContenedorTemp> getContenedorByCondominio(@Url String url);

    @POST("solicitud")
    Call<Solicitud> createSolicitud(@Body Solicitud solicitud);

}
