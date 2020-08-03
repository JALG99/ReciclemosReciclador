package com.upc.reciclemosreciclador.Inicio;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.upc.reciclemosreciclador.Adicionales.dbHelper;
import com.upc.reciclemosreciclador.Entities.Bolsa;
import com.upc.reciclemosreciclador.Entities.Condominio;
import com.upc.reciclemosreciclador.Entities.ContenedorTemp;
import com.upc.reciclemosreciclador.Entities.JsonPlaceHolderApi;
import com.upc.reciclemosreciclador.Entities.Probolsa;
import com.upc.reciclemosreciclador.Entities.Producto;
import com.upc.reciclemosreciclador.Entities.Reciclador;
import com.upc.reciclemosreciclador.Entities.Usuario;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.util.stream.Collectors.toMap;

public class APIToSQLite {

    dbHelper helper;
    SQLiteDatabase db;
    Retrofit retrofit;
    Context context;
    private int [] xDataMonthOrWeek= {0,0,0,0,0,0,0};
    private int [] xDataMonthOrWeekBolsas= {0,0,0,0,0,0,0};
    private int [] xDataYearBolsas= {0,0,0,0,0,0,0,0,0,0,0,0};
    private int [] xDataMonthOrWeekPuntos= {0,0,0,0,0,0,0};
    private double [] pesosbyMes = {0,0,0,0,0,0,0,0,0,0,0,0};
    private int [] plasticobyMes = {0,0,0,0,0,0,0,0,0,0,0,0};
    private int [] papelbyMes = {0,0,0,0,0,0,0,0,0,0,0,0};
    private Map<Integer,Double> pesoEnero = new HashMap<>();
    private Map<Integer,Double> pesoFebrero = new HashMap<>();
    private Map<Integer,Double> pesoMarzo = new HashMap<>();
    private Map<Integer,Double> pesoAbril = new HashMap<>();
    private Map<Integer,Double> pesoMayo = new HashMap<>();
    private Map<Integer,Double> pesoJunio = new HashMap<>();
    private Map<Integer,Double> pesoJulio = new HashMap<>();
    private Map<Integer,Double> pesoAgosto = new HashMap<>();
    private Map<Integer,Double> pesoSetiembre = new HashMap<>();
    private Map<Integer,Double> pesoOctubre = new HashMap<>();
    private Map<Integer,Double> pesoNoviembre = new HashMap<>();
    private Map<Integer,Double> pesoDiciembre = new HashMap<>();

    private Map<Integer,Double> plasticoEnero = new HashMap<>();
    private Map<Integer,Double> plasticoFebrero = new HashMap<>();
    private Map<Integer,Double> plasticoMarzo = new HashMap<>();
    private Map<Integer,Double> plasticoAbril = new HashMap<>();
    private Map<Integer,Double> plasticoMayo = new HashMap<>();
    private Map<Integer,Double> plasticoJunio = new HashMap<>();
    private Map<Integer,Double> plasticoJulio = new HashMap<>();
    private Map<Integer,Double> plasticoAgosto = new HashMap<>();
    private Map<Integer,Double> plasticoSetiembre = new HashMap<>();
    private Map<Integer,Double> plasticoOctubre = new HashMap<>();
    private Map<Integer,Double> plasticoNoviembre = new HashMap<>();
    private Map<Integer,Double> plasticoDiciembre = new HashMap<>();
    private Map<Integer,Double> papelEnero = new HashMap<>();
    private Map<Integer,Double> papelFebrero = new HashMap<>();
    private Map<Integer,Double> papelMarzo = new HashMap<>();
    private Map<Integer,Double> papelAbril = new HashMap<>();
    private Map<Integer,Double> papelMayo = new HashMap<>();
    private Map<Integer,Double> papelJunio = new HashMap<>();
    private Map<Integer,Double> papelJulio = new HashMap<>();
    private Map<Integer,Double> papelAgosto = new HashMap<>();
    private Map<Integer,Double> papelSetiembre = new HashMap<>();
    private Map<Integer,Double> papelOctubre = new HashMap<>();
    private Map<Integer,Double> papelNoviembre = new HashMap<>();
    private Map<Integer,Double> papelDiciembre = new HashMap<>();
    private int cantidad_condominios = 0;
    private double countPlastico, pesoPlastico, puntosPlastico;
    private double countPapelCarton, pesoPapelCarton, puntosPapelCarton;
    private double countMetal, pesoMetal, puntosMetal;
    private double countVidrio, pesoVidrio, puntosVidrio;

    private double [][] cant_residuos_mensuales_condominio = new double [12][2];

    public APIToSQLite(Context context, String tipo){
        helper = new dbHelper(context,"Usuario.sqlite", null, 1);
        db = helper.getWritableDatabase();
        if(!tipo.equals("actualizar")) {
            helper.DropCreate(db);
        }
        else{
            helper.UpdateTable(db);
        }
        this.context = context;
        retrofit = new Retrofit.Builder()
                .baseUrl("https://recyclerapiresttdp.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public void InsertReciclador(String email, String password) throws IOException {
        JsonPlaceHolderApi jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);
        Call<Reciclador> call=jsonPlaceHolderApi.getRecicladorByEmailPassword("/reciclador/correo/" + email + "/" + password);
        Response<Reciclador> response = call.execute();
        String[] ayuda = new String[12];
        ayuda[0] = response.body().getNombre();
        ayuda[1] = response.body().getApellido();
        ayuda[2] = response.body().getDireccion();
        ayuda[3] = response.body().getDni();
        ayuda[4] = response.body().getEmail();
        ayuda[5] = response.body().getFecha_Nacimiento();
        ayuda[6] = response.body().getCelular();
        ayuda[7] = response.body().getCodigo().toString();
        ayuda[8] = response.body().getDistrito().getNombre();
        ayuda[9] = response.body().getDistrito().getDepartamento().getNombre();
        ayuda[10] = response.body().getAsociacion().getNombre();
        ayuda[11] = response.body().getCodFormalizado();
        String query = "insert into Reciclador (nombre, apellido, direccion, dni, email, fecha_Nacimiento, celular, codigo, distrito_name, departamento_name, asociacion_name, codFormalizado) " +
                "values ('" + ayuda[0] + "', '" + ayuda[1] + "', '" + ayuda[2] + "', '" + ayuda[3] + "', '" + ayuda[4] + "', '" + ayuda[5] + "', '"
                + ayuda[6] + "', '" + ayuda[7] + "', '" + ayuda[8] + "', '" + ayuda[9] + "', '" + ayuda[10] + "', '" + ayuda[11] + "')";
        db.execSQL(query);
        System.out.println(query);


        Log.e("TAG","onResponse:" + response.toString());
        /*call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if(response.isSuccessful()) {
                    String[] ayuda = new String[10];
                    ayuda[0] = response.body().getNombre();
                    ayuda[1] = response.body().getApellido();
                    ayuda[2] = response.body().getCondominio().getNombre();
                    ayuda[3] = response.body().getDireccion();
                    ayuda[4] = response.body().getDni();
                    ayuda[5] = response.body().getEmail();
                    ayuda[6] = response.body().getFechanacimiento();
                    ayuda[7] = response.body().getSexo().getNombre();
                    ayuda[8] = response.body().getTelefono();
                    ayuda[9] = response.body().getCodigo().toString();
                    String query = "insert into Usuario (nombre, apellido, condominio, direccion, dni, email, fecha_Nacimiento, sexo, telefono, codigo) " +
                            "values ('" + ayuda[0] + "', '" + ayuda[1] + "', '" + ayuda[2] + "', '" + ayuda[3] + "', '" + ayuda[4] + "', '" + ayuda[5] + "', '"
                            + ayuda[6] + "', '" + ayuda[7] + "', '" + ayuda[8] + "', " + ayuda[9] + ")";
                    db.execSQL(query);
                    System.out.println(query);
                    Log.e("TAG","onResponse:" + response.toString());
                }else{
                    Log.e("TAG","onResponse:" + response.toString());
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e("TAG","onFailure:" + t.getMessage());
            }
        });*/
    }

    public void getCondominiosByReciclador()throws IOException, InterruptedException{
        dbHelper helper = new dbHelper(context, "Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor fila = db.rawQuery("select codigo from Reciclador", null);
        fila.moveToFirst();
        String codigo = fila.getString(fila.getColumnIndex("codigo"));
        JsonPlaceHolderApi jsonPlaceHolderApi =retrofit.create(JsonPlaceHolderApi.class);
        Call<List<Condominio>> call=jsonPlaceHolderApi.getCondominiosByReciclador("condominio/reciclador/"+codigo);
        Response<List<Condominio>> response = call.execute();
        for(Condominio p: response.body()) {
            cantidad_condominios += 1;
            pesoEnero.put(p.getCodigo(), 0.0);
            pesoFebrero.put(p.getCodigo(), 0.0);
            pesoMarzo.put(p.getCodigo(), 0.0);
            pesoAbril.put(p.getCodigo(), 0.0);
            pesoMayo.put(p.getCodigo(), 0.0);
            pesoJunio.put(p.getCodigo(), 0.0);
            pesoJulio.put(p.getCodigo(), 0.0);
            pesoAgosto.put(p.getCodigo(), 0.0);
            pesoSetiembre.put(p.getCodigo(), 0.0);
            pesoOctubre.put(p.getCodigo(), 0.0);
            pesoNoviembre.put(p.getCodigo(), 0.0);
            pesoDiciembre.put(p.getCodigo(), 0.0);
            plasticoEnero.put(p.getCodigo(), 0.0);
            plasticoFebrero.put(p.getCodigo(), 0.0);
            plasticoMarzo.put(p.getCodigo(), 0.0);
            plasticoAbril.put(p.getCodigo(), 0.0);
            plasticoMayo.put(p.getCodigo(), 0.0);
            plasticoJunio.put(p.getCodigo(), 0.0);
            plasticoJulio.put(p.getCodigo(), 0.0);
            plasticoAgosto.put(p.getCodigo(), 0.0);
            plasticoSetiembre.put(p.getCodigo(), 0.0);
            plasticoOctubre.put(p.getCodigo(), 0.0);
            plasticoNoviembre.put(p.getCodigo(), 0.0);
            plasticoDiciembre.put(p.getCodigo(), 0.0);

            papelEnero.put(p.getCodigo(), 0.0);
            papelFebrero.put(p.getCodigo(), 0.0);
            papelMarzo.put(p.getCodigo(), 0.0);
            papelAbril.put(p.getCodigo(), 0.0);
            papelMayo.put(p.getCodigo(), 0.0);
            papelJunio.put(p.getCodigo(), 0.0);
            papelJulio.put(p.getCodigo(), 0.0);
            papelAgosto.put(p.getCodigo(), 0.0);
            papelSetiembre.put(p.getCodigo(), 0.0);
            papelOctubre.put(p.getCodigo(), 0.0);
            papelNoviembre.put(p.getCodigo(), 0.0);
            papelDiciembre.put(p.getCodigo(), 0.0);
            String query = "insert into Condominio (codigo,direccion , distrito , nombre , urbanizacion , nombrecontacto , numerocontacto , reciclador ) " +
                    "values ("+p.getCodigo()+",'" + p.getDireccion() + "', '" + p.getDistrito().getNombre() + "', '" + p.getNombre() + "', '" + p.getUrbanizacion() + "', '" + p.getNombreContacto()
                    + "', '" + p.getNumeroContacto() + "', " + p.getReciclador().getCodigo() + ")";
            db.execSQL(query);
            System.out.println(query);
        }

    }

    public void getResiduosByWeekOrMonth(String urlDate) throws IOException, InterruptedException{

        dbHelper helper = new dbHelper(context, "Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor fila = db.rawQuery("select codigo from Reciclador", null);
        fila.moveToFirst();
        String idReciclador = fila.getString(fila.getColumnIndex("codigo"));
        Cursor condominiosDB = db.rawQuery("select codigo, nombre from Condominio",null);
        if(condominiosDB.moveToFirst()) {
            do {
                initialCounter();
                int valor = 0;
                int codigo = condominiosDB.getInt(0);
                String nombre = condominiosDB.getString(1);
                JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
                Call<List<Probolsa>> call = jsonPlaceHolderApi.getProbolsasByDate("probolsa/reciclador/" + urlDate +Integer.toString(codigo)+"/"+idReciclador);
                Response<List<Probolsa>> response = call.execute();
                for (Probolsa bolsasbydate : response.body()) {
                    valor++;
                    if (bolsasbydate.getBolsa().getRecojoFecha() != null) {
                        if (bolsasbydate.getProducto().getCategoria().getNombre().equals("Plastico") || bolsasbydate.getProducto().getCategoria().getNombre().equals("Papel/Carton")) {
                            if (urlDate.equals("bolsasWeek/") || urlDate.equals("bolsasMonth/")) {
                                Date dia = bolsasbydate.getBolsa().getRecojoFecha();
                                SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
                                if (simpleDateformat.format(dia).equals("Monday")) {
                                    xDataMonthOrWeek[0] += bolsasbydate.getCantidad();
                                    xDataMonthOrWeekPuntos[0] += bolsasbydate.getPuntuacion();
                                } else if (simpleDateformat.format(dia).equals("Tuesday")) {
                                    xDataMonthOrWeek[1] += bolsasbydate.getCantidad();
                                    xDataMonthOrWeekPuntos[1] += bolsasbydate.getPuntuacion();
                                } else if (simpleDateformat.format(dia).equals("Wednesday")) {
                                    xDataMonthOrWeek[2] += bolsasbydate.getCantidad();
                                    xDataMonthOrWeekPuntos[2] += bolsasbydate.getPuntuacion();
                                } else if (simpleDateformat.format(dia).equals("Thursday")) {
                                    xDataMonthOrWeek[3] += bolsasbydate.getCantidad();
                                    xDataMonthOrWeekPuntos[3] += bolsasbydate.getPuntuacion();
                                } else if (simpleDateformat.format(dia).equals("Friday")) {
                                    xDataMonthOrWeek[4] += bolsasbydate.getCantidad();
                                    xDataMonthOrWeekPuntos[4] += bolsasbydate.getPuntuacion();
                                } else if (simpleDateformat.format(dia).equals("Saturday")) {
                                    xDataMonthOrWeek[5] += bolsasbydate.getCantidad();
                                    xDataMonthOrWeekPuntos[5] += bolsasbydate.getPuntuacion();
                                } else {
                                    xDataMonthOrWeek[6] += bolsasbydate.getCantidad();
                                    xDataMonthOrWeekPuntos[6] += bolsasbydate.getPuntuacion();
                                }
                                addingValues(bolsasbydate);
                            }
                        }
                    }
                }
                if (valor > 0) {
                  /*  generateQuery(urlDate, "Plastico", codigo, countPlastico, pesoPlastico, puntosPlastico);
                    generateQuery(urlDate, "Vidrio", codigo, countVidrio, pesoVidrio, puntosVidrio);
                    generateQuery(urlDate, "Papel", codigo, countPapelCarton, pesoPapelCarton, puntosPapelCarton);
                    generateQuery(urlDate, "Metal", codigo, countMetal, pesoMetal, puntosMetal);*/

                    String query = "insert into DatosDiarios(tipo,frecuenciatipo,tipocondominio,lunes,martes,miercoles,jueves,viernes,sabado,domingo) " +
                            "values ('residuos','"+urlDate+"',"+codigo+", " + xDataMonthOrWeek[0] + ", " + xDataMonthOrWeek[1] + ", "
                            + xDataMonthOrWeek[2] + ", " + xDataMonthOrWeek[3] + ", " + xDataMonthOrWeek[4] + "," + xDataMonthOrWeek[5] + "," + xDataMonthOrWeek[6] + ")";
                    db.execSQL(query);
                    System.out.println(query);

                    String query2 = "insert into DatosDiarios(tipo,frecuenciatipo,tipocondominio,lunes,martes,miercoles,jueves,viernes,sabado,domingo) " +
                            "values ('puntos','"+urlDate+"',"+codigo+", " + xDataMonthOrWeekPuntos[0] + ", " + xDataMonthOrWeekPuntos[1] + ", "
                            + xDataMonthOrWeekPuntos[2] + ", " + xDataMonthOrWeekPuntos[3] + ", " + xDataMonthOrWeekPuntos[4] + "," + xDataMonthOrWeekPuntos[5] + "," + xDataMonthOrWeekPuntos[6] + ")";
                    db.execSQL(query2);
                    System.out.println(query2);

                }

                Log.e("TAG", "onResponse:" + response.toString());

                try {
                    getBolsasByWeekorMonth(urlDate,codigo,idReciclador);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } while (condominiosDB.moveToNext());
        }
    }

    public void getBolsasByWeekorMonth(String urlDate, int codigo,String idReciclador) throws IOException, InterruptedException, ParseException {
        initialCounter();
        int valor = 0;
        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<List<Bolsa>> call = jsonPlaceHolderApi.getBolsasByDate("bolsa/reciclador/" + urlDate + Integer.toString(codigo)+"/"+idReciclador);
        Response<List<Bolsa>> response = call.execute();
        for (Bolsa bolsasWeekMonth : response.body()) {
            valor++;
            if (bolsasWeekMonth.getRecojoFecha() != null) {
                Date dia = bolsasWeekMonth.getRecojoFecha();
                SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
                if (simpleDateformat.format(dia).equals("Monday")) {
                    xDataMonthOrWeekBolsas[0] += 1;
                } else if (simpleDateformat.format(dia).equals("Tuesday"))
                    xDataMonthOrWeekBolsas[1] += 1;
                else if (simpleDateformat.format(dia).equals("Wednesday"))
                    xDataMonthOrWeekBolsas[2] += 1;
                else if (simpleDateformat.format(dia).equals("Thursday")) {
                    xDataMonthOrWeekBolsas[3] += 1;
                } else if (simpleDateformat.format(dia).equals("Friday"))
                    xDataMonthOrWeekBolsas[4] += 1;
                else if (simpleDateformat.format(dia).equals("Saturday"))
                    xDataMonthOrWeekBolsas[5] += 1;
                else
                    xDataMonthOrWeekBolsas[6] += 1;
            }

        }
        if(valor > 0) {
            String query = "insert into DatosDiarios(tipo,frecuenciatipo,tipocondominio,lunes,martes,miercoles,jueves,viernes,sabado,domingo) " +
                    "values ('bolsas','"+urlDate+"',"+codigo+", " + xDataMonthOrWeekBolsas[0] + ", " + xDataMonthOrWeekBolsas[1] + ", "
                    + xDataMonthOrWeekBolsas[2] + ", " + xDataMonthOrWeekBolsas[3] + ", " + xDataMonthOrWeekBolsas[4] + "," + xDataMonthOrWeekBolsas[5] + "," + xDataMonthOrWeekBolsas[6] + ")";
            db.execSQL(query);
            System.out.println(query);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void obtenerBolsasByYear(String urlDate )throws IOException, InterruptedException {
        dbHelper helper = new dbHelper(context, "Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor fila = db.rawQuery("select codigo from Reciclador", null);
        fila.moveToFirst();
        String idReciclador = fila.getString(fila.getColumnIndex("codigo"));
        Cursor condominiosDB = db.rawQuery("select codigo, nombre from Condominio", null);
        if (condominiosDB.moveToFirst()) {
            do {
                initialCounter();
                int valor = 0;
                int codigo = condominiosDB.getInt(0);
                JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
                Call<List<Probolsa>> call = jsonPlaceHolderApi.getProbolsasByDate("probolsa/reciclador/" + urlDate +Integer.toString(codigo)+"/"+idReciclador);
                Response<List<Probolsa>> response = call.execute();
                for (Probolsa bolsasbydate : response.body()) {
                    valor++;
                    if (bolsasbydate.getBolsa().getRecojoFecha() != null) {
                        if (bolsasbydate.getProducto().getCategoria().getNombre().equals("Plástico") || bolsasbydate.getProducto().getCategoria().getNombre().equals("Papel/Cartón")) {
                            Date dia = bolsasbydate.getBolsa().getRecojoFecha();
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(dia);
                            if (cal.get(Calendar.MONTH) == 0) {
                                pesosbyMes[0] += bolsasbydate.getPeso();
                                pesoEnero.put(condominiosDB.getInt(0), pesoEnero.get(condominiosDB.getInt(0)) + bolsasbydate.getPeso() );

                                if(bolsasbydate.getProducto().getCategoria().getNombre().equals("Plástico")) {
                                    plasticobyMes[0] += bolsasbydate.getPeso();
                                    plasticoEnero.put(condominiosDB.getInt(0), plasticoEnero.get(condominiosDB.getInt(0)) + bolsasbydate.getPeso() );

                                }
                                else {
                                    papelbyMes[0] += bolsasbydate.getPeso();
                                    papelEnero.put(condominiosDB.getInt(0), papelEnero.get(condominiosDB.getInt(0)) + bolsasbydate.getPeso() );

                                }
                            } else if (cal.get(Calendar.MONTH) == 1) {
                                pesoFebrero.put(condominiosDB.getInt(0), pesoFebrero.get(condominiosDB.getInt(0)) + bolsasbydate.getPeso() );

                                pesosbyMes[1] += bolsasbydate.getPeso();
                                if(bolsasbydate.getProducto().getCategoria().getNombre().equals("Plástico")) {
                                    plasticobyMes[1] += bolsasbydate.getPeso();
                                    plasticoFebrero.put(condominiosDB.getInt(0), plasticoFebrero.get(condominiosDB.getInt(0)) + bolsasbydate.getPeso() );
                                }
                                else {
                                    papelbyMes[1] += bolsasbydate.getPeso();
                                    papelFebrero.put(condominiosDB.getInt(0), papelFebrero.get(condominiosDB.getInt(0)) + bolsasbydate.getPeso() );
                                }
                            } else if (cal.get(Calendar.MONTH) == 2) {
                                pesoMarzo.put(condominiosDB.getInt(0), pesoMarzo.get(condominiosDB.getInt(0)) + bolsasbydate.getPeso() );
                                pesosbyMes[2] += bolsasbydate.getPeso();
                                if(bolsasbydate.getProducto().getCategoria().getNombre().equals("Plástico")) {
                                    plasticobyMes[2] += bolsasbydate.getPeso();
                                    plasticoMarzo.put(condominiosDB.getInt(0), plasticoMarzo.get(condominiosDB.getInt(0)) + bolsasbydate.getPeso() );
                                }
                                else {
                                    papelbyMes[2] += bolsasbydate.getPeso();
                                    papelMarzo.put(condominiosDB.getInt(0), papelMarzo.get(condominiosDB.getInt(0)) + bolsasbydate.getPeso() );
                                }
                            } else if (cal.get(Calendar.MONTH) == 3) {
                                pesosbyMes[3] += bolsasbydate.getPeso();
                                pesoAbril.put(condominiosDB.getInt(0), pesoAbril.get(condominiosDB.getInt(0)) + bolsasbydate.getPeso() );
                                if(bolsasbydate.getProducto().getCategoria().getNombre().equals("Plástico")) {
                                    plasticobyMes[3] += bolsasbydate.getPeso();
                                    plasticoAbril.put(condominiosDB.getInt(0), plasticoAbril.get(condominiosDB.getInt(0)) + bolsasbydate.getPeso() );
                                }
                                else {
                                    papelbyMes[3] += bolsasbydate.getPeso();
                                    papelAbril.put(condominiosDB.getInt(0), papelAbril.get(condominiosDB.getInt(0)) + bolsasbydate.getPeso() );
                                }
                            } else if (cal.get(Calendar.MONTH) == 4) {
                                pesoMayo.put(condominiosDB.getInt(0), pesoMayo.get(condominiosDB.getInt(0)) + bolsasbydate.getPeso() );
                                pesosbyMes[4] += bolsasbydate.getPeso();
                                if(bolsasbydate.getProducto().getCategoria().getNombre().equals("Plástico")) {
                                    plasticobyMes[4] += bolsasbydate.getPeso();
                                    plasticoMayo.put(condominiosDB.getInt(0), plasticoMayo.get(condominiosDB.getInt(0)) + bolsasbydate.getPeso() );
                                }
                                else {
                                    papelbyMes[4] += bolsasbydate.getPeso();
                                    papelMayo.put(condominiosDB.getInt(0), papelMayo.get(condominiosDB.getInt(0)) + bolsasbydate.getPeso() );
                                }
                            } else if (cal.get(Calendar.MONTH) == 5) {
                                pesoJunio.put(condominiosDB.getInt(0), pesoJunio.get(condominiosDB.getInt(0)) + bolsasbydate.getPeso() );
                                pesosbyMes[5] += bolsasbydate.getPeso();
                                if(bolsasbydate.getProducto().getCategoria().getNombre().equals("Plástico")) {
                                    plasticobyMes[5] += bolsasbydate.getPeso();
                                    plasticoJunio.put(condominiosDB.getInt(0), plasticoJunio.get(condominiosDB.getInt(0)) + bolsasbydate.getPeso() );
                                }
                                else {
                                    papelbyMes[5] += bolsasbydate.getPeso();
                                    papelJunio.put(condominiosDB.getInt(0), papelJunio.get(condominiosDB.getInt(0)) + bolsasbydate.getPeso() );
                                }
                            } else if (cal.get(Calendar.MONTH) == 6) {
                                pesoJulio.put(condominiosDB.getInt(0), pesoJulio.get(condominiosDB.getInt(0)) + bolsasbydate.getPeso() );
                                pesosbyMes[6] += bolsasbydate.getPeso();
                                if(bolsasbydate.getProducto().getCategoria().getNombre().equals("Plástico")) {
                                    plasticobyMes[6] += bolsasbydate.getPeso();
                                    plasticoJulio.put(condominiosDB.getInt(0), plasticoJulio.get(condominiosDB.getInt(0)) + bolsasbydate.getPeso() );

                                }
                                else {
                                    papelbyMes[6] += bolsasbydate.getPeso();
                                    papelJulio.put(condominiosDB.getInt(0), papelJulio.get(condominiosDB.getInt(0)) + bolsasbydate.getPeso() );
                                }
                            } else if (cal.get(Calendar.MONTH) == 7) {
                                pesoAgosto.put(bolsasbydate.getProducto().getCodigo(), pesoAgosto.get(condominiosDB.getInt(0)) + bolsasbydate.getPeso() );
                                pesosbyMes[7] += bolsasbydate.getPeso();
                                if(bolsasbydate.getProducto().getCategoria().getNombre().equals("Plástico")) {
                                    plasticobyMes[7] += bolsasbydate.getPeso();
                                    plasticoAgosto.put(bolsasbydate.getProducto().getCodigo(), plasticoAgosto.get(condominiosDB.getInt(0)) + bolsasbydate.getPeso() );
                                }
                                else {
                                    papelbyMes[7] += bolsasbydate.getPeso();
                                    papelAgosto.put(bolsasbydate.getProducto().getCodigo(), papelAgosto.get(condominiosDB.getInt(0)) + bolsasbydate.getPeso() );
                                }
                            } else if (cal.get(Calendar.MONTH) == 8) {
                                pesoSetiembre.put(condominiosDB.getInt(0), pesoSetiembre.get(condominiosDB.getInt(0)) + bolsasbydate.getPeso() );
                                pesosbyMes[8] += bolsasbydate.getPeso();
                                if(bolsasbydate.getProducto().getCategoria().getNombre().equals("Plástico")) {
                                    plasticobyMes[8] += bolsasbydate.getPeso();
                                    plasticoSetiembre.put(condominiosDB.getInt(0), plasticoSetiembre.get(condominiosDB.getInt(0)) + bolsasbydate.getPeso() );
                                }
                                else {
                                    papelbyMes[8] += bolsasbydate.getPeso();
                                    papelSetiembre.put(condominiosDB.getInt(0), papelSetiembre.get(condominiosDB.getInt(0)) + bolsasbydate.getPeso() );
                                }
                            } else if (cal.get(Calendar.MONTH) == 9) {
                                pesoOctubre.put(condominiosDB.getInt(0), pesoOctubre.get(condominiosDB.getInt(0)) + bolsasbydate.getPeso() );
                                pesosbyMes[9] += bolsasbydate.getPeso();
                                if(bolsasbydate.getProducto().getCategoria().getNombre().equals("Plástico")) {
                                    plasticobyMes[9] += bolsasbydate.getPeso();
                                    plasticoOctubre.put(condominiosDB.getInt(0), plasticoOctubre.get(condominiosDB.getInt(0)) + bolsasbydate.getPeso() );
                                }
                                else {
                                    papelbyMes[9] += bolsasbydate.getPeso();
                                    papelOctubre.put(condominiosDB.getInt(0), papelOctubre.get(condominiosDB.getInt(0)) + bolsasbydate.getPeso() );
                                }
                            } else if (cal.get(Calendar.MONTH) == 10) {
                                pesoNoviembre.put(condominiosDB.getInt(0), pesoNoviembre.get(condominiosDB.getInt(0)) + bolsasbydate.getPeso() );
                                pesosbyMes[10] += bolsasbydate.getPeso();
                                if(bolsasbydate.getProducto().getCategoria().getNombre().equals("Plástico")) {
                                    plasticobyMes[10] += bolsasbydate.getPeso();
                                    plasticoNoviembre.put(condominiosDB.getInt(0), plasticoNoviembre.get(condominiosDB.getInt(0)) + bolsasbydate.getPeso() );
                                }
                                else {
                                    papelbyMes[10] += bolsasbydate.getPeso();
                                    papelNoviembre.put(condominiosDB.getInt(0), papelNoviembre.get(condominiosDB.getInt(0)) + bolsasbydate.getPeso() );
                                }
                            } else if (cal.get(Calendar.MONTH) == 11) {
                                pesoDiciembre.put(condominiosDB.getInt(0), pesoDiciembre.get(condominiosDB.getInt(0)) + bolsasbydate.getPeso() );
                                pesosbyMes[11] += bolsasbydate.getPeso();
                                if(bolsasbydate.getProducto().getCategoria().getNombre().equals("Plástico")) {
                                    plasticobyMes[11] += bolsasbydate.getPeso();
                                    plasticoDiciembre.put(condominiosDB.getInt(0), plasticoDiciembre.get(condominiosDB.getInt(0)) + bolsasbydate.getPeso() );
                                }
                                else {
                                    papelbyMes[11] += bolsasbydate.getPeso();
                                    papelDiciembre.put(condominiosDB.getInt(0), papelDiciembre.get(condominiosDB.getInt(0)) + bolsasbydate.getPeso() );
                                }
                            }
                            addingValues(bolsasbydate);
                        }
                    }

                }
                if (valor > 0) {
                   /* generateQuery("Year", "Plastico", codigo, countPlastico, pesoPlastico, puntosPlastico);
                    generateQuery("Year", "Vidrio", codigo, countVidrio, pesoVidrio, puntosVidrio);
                    generateQuery("Year", "Papel", codigo, countPapelCarton, pesoPapelCarton, puntosPapelCarton);
                    generateQuery("Year", "Metal", codigo, countMetal, pesoMetal, puntosMetal);*/
                    String query = "insert into DatosAnuales(tipocondominio,enero,febrero,marzo,abril,mayo,junio,julio,agosto,setiembre,octubre,noviembre,diciembre,tipo) " +
                            "values ( "+codigo+"," + pesosbyMes[0] + ", " + pesosbyMes[1] + ", "
                            + pesosbyMes[2] + "," + pesosbyMes[3] + "," + pesosbyMes[4] + ","
                            + pesosbyMes[5] + "," + pesosbyMes[6] + "," + pesosbyMes[7] + ","
                            + pesosbyMes[8]+ "," + pesosbyMes[9] + "," + pesosbyMes[10] + ","
                            + pesosbyMes[11] + ",'pesos')";
                    db.execSQL(query);
                    System.out.println(query);

                    String query2 = "insert into DatosAnuales(tipocondominio,enero,febrero,marzo,abril,mayo,junio,julio,agosto,setiembre,octubre,noviembre,diciembre,tipo) " +
                            "values ( "+codigo+"," + plasticobyMes[0] + ", " + plasticobyMes[1] + ", "
                            + plasticobyMes[2] + "," + plasticobyMes[3] + "," + plasticobyMes[4] + ","
                            + plasticobyMes[5] + "," + plasticobyMes[6] + "," + plasticobyMes[7] + ","
                            + plasticobyMes[8]+ "," + plasticobyMes[9] + "," + plasticobyMes[10] + ","
                            + plasticobyMes[11] + ",'Plástico')";
                    db.execSQL(query2);
                    System.out.println(query2);

                    String query3 = "insert into DatosAnuales(tipocondominio,enero,febrero,marzo,abril,mayo,junio,julio,agosto,setiembre,octubre,noviembre,diciembre,tipo) " +
                            "values ( "+codigo+"," + papelbyMes[0] + ", " + papelbyMes[1] + ", "
                            + papelbyMes[2] + "," + papelbyMes[3] + "," + papelbyMes[4] + ","
                            + papelbyMes[5] + "," + papelbyMes[6] + "," + papelbyMes[7] + ","
                            + papelbyMes[8]+ "," + papelbyMes[9] + "," + papelbyMes[10] + ","
                            + papelbyMes[11] + ",'Papel/Cartón')";
                    db.execSQL(query3);
                    System.out.println(query3);

                }

                Log.e("TAG", "onResponse:" + response.toString());
                try {
                    obtenerLasBolsasMonth(urlDate,codigo,idReciclador);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } while (condominiosDB.moveToNext());
            ordenar();
            ordenarTipo();

        }
    }

    public void obtenerLasBolsasMonth(String urlDate, int codigo,String idReciclador) throws IOException, InterruptedException, ParseException {
        initialCounter();
        int valor = 0;
        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<List<Bolsa>> call = jsonPlaceHolderApi.getBolsasByDate("bolsa/reciclador/" + urlDate + Integer.toString(codigo)+"/"+idReciclador);
        Response<List<Bolsa>> response = call.execute();
        for (Bolsa bolsasYear : response.body()) {
            valor++;
            if (bolsasYear.getRecojoFecha() != null) {
                Date dia = bolsasYear.getRecojoFecha();
                Calendar cal = Calendar.getInstance();
                cal.setTime(dia);
                if (cal.get(Calendar.MONTH) == 0)
                    xDataYearBolsas[0] += 1;
                else if (cal.get(Calendar.MONTH) == 1)
                    xDataYearBolsas[1] += 1;
                else if (cal.get(Calendar.MONTH) == 2)
                    xDataYearBolsas[2] += 1;
                else if (cal.get(Calendar.MONTH) == 3)
                    xDataYearBolsas[3] += 1;
                else if (cal.get(Calendar.MONTH) == 4)
                    xDataYearBolsas[4] += 1;
                else if (cal.get(Calendar.MONTH) == 5)
                    xDataYearBolsas[5] += 1;
                else if (cal.get(Calendar.MONTH) == 6)
                    xDataYearBolsas[6] += 1;
                else if (cal.get(Calendar.MONTH) == 7)
                    xDataYearBolsas[7] += 1;
                else if (cal.get(Calendar.MONTH) == 8)
                    xDataYearBolsas[8] += 1;
                else if (cal.get(Calendar.MONTH) == 9)
                    xDataYearBolsas[9] += 1;
                else if (cal.get(Calendar.MONTH) == 10)
                    xDataYearBolsas[10] += 1;
                else if (cal.get(Calendar.MONTH) == 11)
                    xDataYearBolsas[11] += 1;
            }

        }
        if(valor>0) {
            String query = "insert into DatosAnuales(tipocondominio,enero,febrero,marzo,abril,mayo,junio,julio,agosto,setiembre,octubre,noviembre,diciembre,tipo) " +
                    "values ( "+codigo+"," + xDataYearBolsas[0] + ", " + xDataYearBolsas[1] + ", "
                    + xDataYearBolsas[2] + ", " + xDataYearBolsas[3] + ", " + xDataYearBolsas[4] + "," + xDataYearBolsas[5]  + ", "
                    + xDataYearBolsas[6] + ", " + xDataYearBolsas[7] + ","+ + xDataYearBolsas[8] + ","+xDataYearBolsas[9] + ", " + xDataYearBolsas[10] +","+ xDataYearBolsas[11] +  ",'bolsa')";
            db.execSQL(query);
            System.out.println(query);

        }
    }

    public void initialCounter(){
        countPlastico=0;
        pesoPlastico=0;
        puntosPlastico=0;
        countVidrio=0;
        pesoVidrio=0;
        puntosVidrio=0;
        countPapelCarton=0;
        pesoPapelCarton=0;
        puntosPapelCarton=0;
        countMetal=0;
        pesoMetal=0;
        puntosMetal=0;
        for(int i=0;i< 7 ; i++)
        {
            xDataMonthOrWeek[i] = 0;
            xDataMonthOrWeekBolsas[i] = 0;
            xDataMonthOrWeekPuntos[i] = 0;
        }
        for(int i = 0;i< 11; i++){
            plasticobyMes[i] = 0;
            pesosbyMes[i] = 0;
            papelbyMes[i] = 0;
        }
    }

    public void addingValues(Probolsa bolsasbydate ){
        if (bolsasbydate.getProducto().getCategoria().getNombre().equals("Plastico")) {
            pesoPlastico += bolsasbydate.getProducto().getPeso();
            puntosPlastico += bolsasbydate.getPuntuacion();
            countPlastico += bolsasbydate.getCantidad();
        }else if (bolsasbydate.getProducto().getCategoria().getNombre().equals("Vidrio")) {
            pesoVidrio += bolsasbydate.getProducto().getPeso();
            puntosVidrio += bolsasbydate.getPuntuacion();
            countVidrio+= bolsasbydate.getCantidad();
        } else if (bolsasbydate.getProducto().getCategoria().getNombre().equals("Papel/Carton")) {
            pesoPapelCarton += bolsasbydate.getProducto().getPeso();
            puntosPapelCarton += bolsasbydate.getPuntuacion();
            countPapelCarton+=bolsasbydate.getCantidad();
        } else if (bolsasbydate.getProducto().getCategoria().getNombre().equals("Metal")) {
            pesoMetal += bolsasbydate.getProducto().getPeso();
            puntosMetal += bolsasbydate.getPuntuacion();
            countMetal+=bolsasbydate.getCantidad();
        }
    }

    public void generateQuery(String tipo,String producto,int nombreCondominio,double cantidad,double peso,double puntuacion){
        String query = "insert into Contador (tendenciaTipo,productoTipo,nombrecondominio,cantidad,peso,puntuacion) " +
                "values ('" + tipo + "', '" + producto + "',"+nombreCondominio+", " + cantidad + ", "
                + peso + ", " + puntuacion + ")";
        db.execSQL(query);
        System.out.println(query);
    }

    public void InsertProductos() throws IOException {
        JsonPlaceHolderApi jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);
        Call<List<Producto>> call=jsonPlaceHolderApi.getProductos("/producto");
        Response<List<Producto>> response = call.execute();
        for (Producto p: response.body()) {
            String query = "insert into Producto (barcode , categoria , codigo , contenido , descripcion , nombre , peso , tipo_contenido , urlimagen) " +
                    "values ('" + p.getBarcode() + "', " + p.getCategoria().getCodigo() + ", " + p.getCodigo() + ", " + p.getContenido() + ", " + p.getDescripcion()
                    + ", '" + p.getNombre() + "', " + p.getPeso() + ", '" + p.getTipo_Contenido().getAbreviatura() + "', '" + p.getUrlImage() + "')";
            db.execSQL(query);
            System.out.println(query);
        }
        Log.e("TAG","onResponse:" + response.toString());
        /*call.enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                if(response.isSuccessful()) {
                    for (Producto p: response.body()) {
                        String query = "insert into Producto (barcode , categoria , codigo , contenido , descripcion , nombre , peso , tipo_contenido , urlimagen) " +
                                "values ('" + p.getBarcode() + "', " + p.getCategoria().getCodigo() + ", " + p.getCodigo() + ", " + p.getContenido() + ", " + p.getDescripcion()
                                + ", '" + p.getNombre() + "', " + p.getPeso() + ", '" + p.getTipo_Contenido().getAbreviatura() + "', " + p.getUrlimagen() + ")";
                        db.execSQL(query);
                        System.out.println(query);
                    }
                    Log.e("TAG","onResponse:" + response.toString());
                }else{
                    Log.e("TAG","onResponse:" + response.toString());
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                Log.e("TAG","onFailure:" + t.getMessage());
            }
        });*/
    }

    public void InsertBolsas() throws IOException {
        dbHelper helper = new dbHelper(context, "Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();

        Cursor fila = db.rawQuery("select codigo from Reciclador", null);

        System.out.println(fila.getColumnNames().toString());

        fila.moveToFirst();

        String codigo = fila.getString(0);

        JsonPlaceHolderApi jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);
        Call<List<Bolsa>> call=jsonPlaceHolderApi.getBolsaByReciclador("/bolsa/reciclador/" + codigo);
        Response<List<Bolsa>> response = call.execute();

        for (Bolsa p: response.body()) {
            if(p.getValidado()) {
                String query = "insert into Bolsa (codigo, usuario, condominio, qrcode, fechaCreado, validado) " +
                        "values (" + p.getCodigo() + ", " + p.getUsuario().getCodigo() + ", " + p.getUsuario().getCondominio().getCodigo() + ", '" + p.getQrCode().getQrCode() + "', '" + p.getCreadoFecha().toString() + "', 'true')";
                db.execSQL(query);
                System.out.println(query);
            }else{
                String query = "insert into Bolsa (codigo, usuario, condominio, qrcode, fechaCreado, validado) " +
                        "values (" + p.getCodigo() + ", " + p.getUsuario().getCodigo() + ", " + p.getUsuario().getCondominio().getCodigo() + ", '" + p.getQrCode().getQrCode() + "', '" + p.getCreadoFecha().toString() + "', 'false')";
                db.execSQL(query);
                System.out.println(query);
            }
        }
        Log.e("TAG","onResponse:" + response.toString());
    }

    public void InsertProbolsa() throws IOException {
        dbHelper helper = new dbHelper(context, "Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();

        Cursor fila = db.rawQuery("select codigo from Reciclador", null);

        fila.moveToFirst();

        JsonPlaceHolderApi jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);
        Call<List<Probolsa>> call=jsonPlaceHolderApi.getProbolsaByReciclador("/probolsa/reciclador/" + fila.getInt(0));
        Response<List<Probolsa>> response = call.execute();
        for (Probolsa p: response.body()) {
            String query = "insert into Probolsa (bolsa, cantidad, codigo, peso, producto, puntuacion) " +
                    "values (" + p.getBolsa().getCodigo() + ", " + p.getCantidad() + ", " + p.getCodigo() + ", "
                    + p.getPeso() + ", " + p.getProducto().getCodigo() + ", " + p.getPuntuacion() + ")";
            db.execSQL(query);
            System.out.println(query);
        }
        Log.e("TAG","onResponse:" + response.toString());
    }

    public void InsertUsuarios() throws IOException {
        JsonPlaceHolderApi jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);

        dbHelper helper = new dbHelper(context, "Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();

        Cursor fila = db.rawQuery("select usuario from Bolsa", null);

        if(fila.moveToFirst()){
                do{

                    Cursor fila2 = db.rawQuery("select codigo from Usuario where codigo = " + fila.getInt(0), null);

                    if(!fila2.moveToFirst()) {
                        Call<Usuario> call = jsonPlaceHolderApi.getUsuario("/usuario/" + fila.getInt(0));
                        Response<Usuario> response = call.execute();
                        String[] ayuda = new String[4];
                        ayuda[0] = response.body().getNombre();
                        ayuda[1] = response.body().getApellido();
                        ayuda[2] = response.body().getCondominio().getNombre();
                        ayuda[3] = response.body().getCodigo().toString();
                        String query = "insert into Usuario (nombre, apellido, condominio_name, codigo) " +
                                "values ('" + ayuda[0] + "', '" + ayuda[1] + "', '" + ayuda[2] + "', " + ayuda[3] + ")";
                        db.execSQL(query);
                        System.out.println(query);

                        Log.e("TAG", "onResponse:" + response.toString());
                    }
                }while(fila.moveToNext());
        }
    }

    public void getContenedorByCondominio()throws IOException{
        dbHelper helper = new dbHelper(context, "Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor condominioData = db.rawQuery("select codigo from Condominio",null);
        while (condominioData.moveToNext()){
            JsonPlaceHolderApi jsonPlaceHolderApi =retrofit.create(JsonPlaceHolderApi.class);
            Call<ContenedorTemp> call=jsonPlaceHolderApi.getContenedorByCondominio("bolsa/activas/condominio/"+Integer.toString(condominioData.getInt(0)));
            Response<ContenedorTemp> response = call.execute();
            System.out.println(Integer.toString(response.body().getCantBolsas()));
            String query = "insert into Contenedor (cantidad,condominiocodigo,pesoTotalBolsas) " +
                    "values ("+response.body().getCantBolsas()+"," + condominioData.getInt(0) + ", " + response.body().getCantidadTotal() + ")";
            db.execSQL(query);
            System.out.println(query);
        }
    }

  /*  public void obtenerDatosProductByBolsa() throws IOException, InterruptedException{
        initialCounter();
        dbHelper helper = new dbHelper(context,"Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor f1 = db.rawQuery("select codigo from LastBolsas ",null);
        int codigoBolsa=0;
        if(f1.moveToFirst()){
            do {
                codigoBolsa = f1.getInt(0);
                JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
                Call<List<Probolsa>> call = jsonPlaceHolderApi.getProductoByIdBolsa("probolsa/bolsa/" + codigoBolsa);
                Response<List<Probolsa>> response = call.execute();
                for (Probolsa probolsas : response.body()) {
                    if (probolsas.getProducto().getCategoria().getNombre().equals("Plastico")) {
                        plasticoCount += probolsas.getCantidad();
                        puntosPlastico += probolsas.getPuntuacion();
                        pesoPlastico += probolsas.getPeso();
                    } /*else if (probolsas.getProducto().getCategoria().getNombre().equals("Vidrio")) {
                        vidrioCount += probolsas.getCantidad();
                        puntosVidrio += probolsas.getPuntuacion();
                        pesoVidrio += probolsas.getPeso();
                    } else if (probolsas.getProducto().getCategoria().getNombre().equals("Metal")) {
                        metalCount += probolsas.getCantidad();
                        puntosMetal += probolsas.getPuntuacion();
                        pesoMetal += probolsas.getPeso();
                    } else {
                        papelCartonCount += probolsas.getCantidad();
                        puntosPapelCarton += probolsas.getPuntuacion();
                        pesoPapelCarton += probolsas.getPeso();
                    }
                    String query = "insert into LastProbolsas (codigo,bolsa) " +
                            "values (" + probolsas.getCodigo() + ","+probolsas.getBolsa().getCodigo()+")";
                    db.execSQL(query);
                    System.out.println(query);
                }
                generateQuery("LastBolsas", "Plastico", plasticoCount, pesoPlastico, puntosPlastico, codigoBolsa);
                generateQuery("LastBolsas", "Vidrio", vidrioCount, pesoVidrio, puntosVidrio, codigoBolsa);
                generateQuery("LastBolsas", "Papel", papelCartonCount, pesoPapelCarton, puntosPapelCarton,codigoBolsa);
                generateQuery("LastBolsas", "Metal", metalCount, pesoMetal, puntosMetal, codigoBolsa);
                initialCounter();

            }while(f1.moveToNext()); }
    }*/

    /*public void insertBolsasByMonthOrWeek(String urlDate) throws IOException, InterruptedException{
        initialCounter();
        dbHelper helper = new dbHelper(context, "Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();

        Cursor fila = db.rawQuery("select codigo from Usuario", null);

        fila.moveToFirst();
        int valor =0;
        String codigo = fila.getString(fila.getColumnIndex("codigo"));
        JsonPlaceHolderApi jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);
        Call<List<Probolsa>> call=jsonPlaceHolderApi.getBolsasByDate("probolsa/"+urlDate+codigo);
        Response<List<Probolsa>> response = call.execute();
        for(Probolsa bolsasbydate: response.body()){
            valor++;
            if (bolsasbydate.getBolsa().getRecojoFecha() != null) {
                if (bolsasbydate.getProducto().getCategoria().getNombre().equals("Plastico") || bolsasbydate.getProducto().getCategoria().getNombre().equals("Papel/Carton")) {
                    bolsasWeek.add(bolsasbydate.getBolsa().getCodigo());
                    if (urlDate.equals("bolsasWeek/") || urlDate.equals("bolsasMonth/")) {
                        Date dia = bolsasbydate.getBolsa().getRecojoFecha();
                        SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
                        if (simpleDateformat.format(dia).equals("Monday")) {
                            yAxisDataMonth[0] += 1;
                            xAxisDataMonthPuntos[0] += bolsasbydate.getPuntuacion();
                        } else if (simpleDateformat.format(dia).equals("Tuesday")) {
                            yAxisDataMonth[1] += 1;
                            xAxisDataMonthPuntos[1] += bolsasbydate.getPuntuacion();
                        } else if (simpleDateformat.format(dia).equals("Wednesday")) {
                            yAxisDataMonth[2] += 1;
                            xAxisDataMonthPuntos[2] += bolsasbydate.getPuntuacion();
                        } else if (simpleDateformat.format(dia).equals("Thursday")) {
                            yAxisDataMonth[3] += 1;
                            xAxisDataMonthPuntos[3] += bolsasbydate.getPuntuacion();
                        } else if (simpleDateformat.format(dia).equals("Friday")) {
                            yAxisDataMonth[4] += 1;
                            xAxisDataMonthPuntos[4] += bolsasbydate.getPuntuacion();
                        } else if (simpleDateformat.format(dia).equals("Saturday")) {
                            yAxisDataMonth[5] += 1;
                            xAxisDataMonthPuntos[5] += bolsasbydate.getPuntuacion();
                        } else {
                            yAxisDataMonth[6] += 1;
                            xAxisDataMonthPuntos[6] += bolsasbydate.getPuntuacion();
                        }
                        addingValuestoText(bolsasbydate);
                    }
                }
            }
        }
        if(valor>0) {
            generateQuery("Semana", "Plastico", plasticoCount, pesoPlastico, puntosPlastico,0);
            generateQuery("Semana", "Vidrio", vidrioCount, pesoVidrio, puntosVidrio,0);
            generateQuery("Semana", "Papel", papelCartonCount, pesoPapelCarton, puntosPapelCarton,0);
            generateQuery("Semana", "Metal", metalCount, pesoMetal, puntosMetal,0);
            String query = "insert into DatosDiarios(tipo,lunes,martes,miercoles,jueves,viernes,sabado,domingo) " +
                    "values ('Semana', " + yAxisDataMonth[0] + ", " + yAxisDataMonth[1] + ", "
                    + yAxisDataMonth[2] + ", " + yAxisDataMonth[3] + ", " + yAxisDataMonth[4] + "," + yAxisDataMonth[5] + "," + yAxisDataMonth[6] + ")";
            db.execSQL(query);
            System.out.println(query);

            String query2 = "insert into DatosDiarios(tipo,lunes,martes,miercoles,jueves,viernes,sabado,domingo) " +
                    "values ('puntos', " + xAxisDataMonthPuntos[0] + ", " + xAxisDataMonthPuntos[1] + ", "
                    + xAxisDataMonthPuntos[2] + ", " + xAxisDataMonthPuntos[3] + ", " + xAxisDataMonthPuntos[4] + "," + xAxisDataMonthPuntos[5] + "," + xAxisDataMonthPuntos[6] + ")";
            db.execSQL(query2);
            System.out.println(query2);

        }

        Log.e("TAG","onResponse:" + response.toString());

        try {
            obtenerNumberBolsasByWeek();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void obtenerNumberBolsasByWeek() throws IOException, InterruptedException, ParseException {
        initialCounter();
        dbHelper helper = new dbHelper(context, "Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        int valor = 0;
        for (Integer bolsasWeek : bolsasWeek) {
            Cursor f1 = db.rawQuery("select recojoFecha from Bolsa where codigo = "+ bolsasWeek,null);
            System.out.println(bolsasWeek);
            if(f1.moveToFirst()) {
                do {
                    valor++;
                    System.out.println("Entro");
                    if(!f1.getString(0).equals("null") || !f1.getString(0).equals(null)) {
                        String sDate1 = f1.getString(0);
                        Date dia =new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH).parse(sDate1);
                        System.out.println(dia.toString());
                        SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE", Locale.ENGLISH);

                        if (simpleDateformat.format(dia).equals("Monday")) {
                            yAxisDataMonthBoslsas[0] += 1;
                        } else if (simpleDateformat.format(dia).equals("Tuesday"))
                            yAxisDataMonthBoslsas[1] += 1;
                        else if (simpleDateformat.format(dia).equals("Wednesday"))
                            yAxisDataMonthBoslsas[2] += 1;
                        else if (simpleDateformat.format(dia).equals("Thursday")) {
                            yAxisDataMonthBoslsas[3] += 1;
                        } else if (simpleDateformat.format(dia).equals("Friday"))
                            yAxisDataMonthBoslsas[4] += 1;
                        else if (simpleDateformat.format(dia).equals("Saturday"))
                            yAxisDataMonthBoslsas[5] += 1;
                        else
                            yAxisDataMonthBoslsas[6] += 1;
                    }

                } while (f1.moveToNext()) ;
            }
        }

        if(valor>0) {
            String query = "insert into DatosDiarios(tipo,lunes,martes,miercoles,jueves,viernes,sabado,domingo) " +
                    "values ('bolsas', " + yAxisDataMonthBoslsas[0] + ", " + yAxisDataMonthBoslsas[1] + ", "
                    + yAxisDataMonthBoslsas[2] + ", " + yAxisDataMonthBoslsas[3] + ", " + yAxisDataMonthBoslsas[4] + "," + yAxisDataMonthBoslsas[5] + "," + yAxisDataMonthBoslsas[6] + ")";
            db.execSQL(query);
            System.out.println(query);

        }

    }

    public void obtenerBolsasByDay()throws IOException, InterruptedException{
        pesoCount=0;
        bolsasCount=0;
        puntosCount=0;
        dbHelper helper = new dbHelper(context, "Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor fila = db.rawQuery("select codigo from Usuario", null);
        fila.moveToFirst();
        String codigo = fila.getString(fila.getColumnIndex("codigo"));
        Set<Integer> bolsas = new HashSet<>();
        JsonPlaceHolderApi jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);
        Call<List<Probolsa>> call=jsonPlaceHolderApi.getBolsasByDate("probolsa/bolsasDay/"+codigo);
        Response<List<Probolsa>> response = call.execute();
        for(Probolsa probolsas : response.body()){
            bolsas.add(probolsas.getBolsa().getCodigo());
            pesoCount+=probolsas.getPeso();
            puntosCount+=probolsas.getPuntuacion();
        }
        String query = "insert into Contador (tendenciaTipo,cantidad ,peso ,puntuacion ) " +
                "values ('Dia', " +  bolsas.size()+ ", " + pesoCount + ", "
                + puntosCount + ")";
        db.execSQL(query);
        System.out.println(query);


        Log.e("TAG","onResponse:" + response.toString());


    }

    public void obtenerUltimasBolsas()throws IOException, InterruptedException{
        dbHelper helper = new dbHelper(context, "Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor fila = db.rawQuery("select codigo from Usuario", null);
        fila.moveToFirst();
        int valor =0;
        String codigo = fila.getString(fila.getColumnIndex("codigo"));
        JsonPlaceHolderApi jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);
        Call<List<Bolsa>> call=jsonPlaceHolderApi.getBolsasByUsuario("bolsa/last/"+codigo);
        Response<List<Bolsa>> response = call.execute();
        for(Bolsa lastBolsas : response.body()){
            String query = "insert into LastBolsas (codigo) " +
                    "values (" + lastBolsas.getCodigo() + ")";
            db.execSQL(query);
            System.out.println(query);
        }

        Log.e("TAG","onResponse:" + response.toString());
    }

    public void obtenerBolsasByYear(String urlDate)throws IOException, InterruptedException{
        initialCounter();
        dbHelper helper = new dbHelper(context, "Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        Set<Integer> bolsas = new HashSet<>();
        Cursor fila = db.rawQuery("select codigo from Usuario", null);

        fila.moveToFirst();
        int valor =0;
        String codigo = fila.getString(fila.getColumnIndex("codigo"));
        JsonPlaceHolderApi jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);
        Call<List<Probolsa>> call=jsonPlaceHolderApi.getBolsasByDate("probolsa/"+urlDate+codigo);
        Response<List<Probolsa>> response = call.execute();
        for (Probolsa bolsasbydate : response.body()) {
            valor++;
            if (bolsasbydate.getBolsa().getRecojoFecha() != null) {
                if (bolsasbydate.getProducto().getCategoria().getNombre().equals("Plastico") || bolsasbydate.getProducto().getCategoria().getNombre().equals("Papel/Carton")) {
                    System.out.println(bolsasbydate.getProducto().getCategoria().getNombre());
                    bolsasLast.add(bolsasbydate.getBolsa().getCodigo());
                    Date dia = bolsasbydate.getBolsa().getRecojoFecha();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dia);
                    if (cal.get(Calendar.MONTH) == 0) {
                        yAxisDataYear[0] += bolsasbydate.getCantidad();
                        yAxisDataYearPunto[0] += bolsasbydate.getPuntuacion();
                    } else if (cal.get(Calendar.MONTH) == 1) {
                        yAxisDataYear[1] += bolsasbydate.getCantidad();
                        yAxisDataYearPunto[1] += bolsasbydate.getPuntuacion();
                    } else if (cal.get(Calendar.MONTH) == 2) {
                        yAxisDataYear[2] += bolsasbydate.getCantidad();
                        yAxisDataYearPunto[2] += bolsasbydate.getPuntuacion();
                    } else if (cal.get(Calendar.MONTH) == 3) {
                        yAxisDataYear[3] += bolsasbydate.getCantidad();
                        yAxisDataYearPunto[3] += bolsasbydate.getPuntuacion();
                    } else if (cal.get(Calendar.MONTH) == 4) {
                        yAxisDataYear[4] += bolsasbydate.getCantidad();
                        yAxisDataYearPunto[4] += bolsasbydate.getPuntuacion();
                    } else if (cal.get(Calendar.MONTH) == 5) {
                        yAxisDataYear[5] += bolsasbydate.getCantidad();
                        yAxisDataYearPunto[5] += bolsasbydate.getPuntuacion();
                    } else if (cal.get(Calendar.MONTH) == 6) {
                        yAxisDataYear[6] += bolsasbydate.getCantidad();
                        yAxisDataYearPunto[6] += bolsasbydate.getPuntuacion();
                    } else if (cal.get(Calendar.MONTH) == 7) {
                        yAxisDataYear[7] += bolsasbydate.getCantidad();
                        yAxisDataYearPunto[7] += bolsasbydate.getPuntuacion();
                    } else if (cal.get(Calendar.MONTH) == 8) {
                        yAxisDataYear[8] += bolsasbydate.getCantidad();
                        yAxisDataYearPunto[8] += bolsasbydate.getPuntuacion();
                    } else if (cal.get(Calendar.MONTH) == 9) {
                        yAxisDataYear[9] += bolsasbydate.getCantidad();
                        yAxisDataYearPunto[9] += bolsasbydate.getPuntuacion();
                    } else if (cal.get(Calendar.MONTH) == 10) {
                        yAxisDataYear[10] += bolsasbydate.getCantidad();
                        yAxisDataYearPunto[10] += bolsasbydate.getPuntuacion();
                    } else if (cal.get(Calendar.MONTH) == 11) {
                        yAxisDataYear[11] += bolsasbydate.getCantidad();
                        yAxisDataYearPunto[11] += bolsasbydate.getPuntuacion();
                    }
                    bolsas.add(bolsasbydate.getBolsa().getCodigo());
                    addingValuestoText(bolsasbydate);
                }
            }

        }
        if(valor>0) {
            generateQuery("Year", "Plastico", plasticoCount, pesoPlastico, puntosPlastico,bolsas.size());
            generateQuery("Year", "Vidrio", vidrioCount, pesoVidrio, puntosVidrio,bolsas.size());
            generateQuery("Year", "Papel", papelCartonCount, pesoPapelCarton, puntosPapelCarton,bolsas.size());
            generateQuery("Year", "Metal", metalCount, pesoMetal, puntosMetal,bolsas.size());
            String query = "insert into DatosAnuales(enero,febrero,marzo,abril,mayo,junio,julio,agosto,setiembre,octubre,noviembre,diciembre,tipo) " +
                    "values ( " + yAxisDataYear[0] + ", " + yAxisDataYear[1] + ", "
                    + yAxisDataYear[2] + ", " + yAxisDataYear[3] + ", " + yAxisDataYear[4] + "," + yAxisDataYear[5]  + ", "
                    + yAxisDataYear[6] + ", " + yAxisDataYear[7] + ","+ + yAxisDataYear[8] + ","+yAxisDataYear[9] + ", " + yAxisDataYear[10] +","+ yAxisDataYear[11] +  ",'probolsa')";
            db.execSQL(query);
            System.out.println(query);

            String query2 = "insert into DatosAnuales(enero,febrero,marzo,abril,mayo,junio,julio,agosto,setiembre,octubre,noviembre,diciembre,tipo) " +
                    "values ( " + yAxisDataYearPunto[0] + ", " + yAxisDataYearPunto[1] + ", "
                    + yAxisDataYearPunto[2] + ", " + yAxisDataYearPunto[3] + ", " + yAxisDataYearPunto[4] + "," + yAxisDataYearPunto[5]  + ", "
                    + yAxisDataYearPunto[6] + ", " + yAxisDataYearPunto[7] + ","+ + yAxisDataYearPunto[8] + ","+yAxisDataYearPunto[9] + ", " + yAxisDataYearPunto[10] +","+ yAxisDataYearPunto[11] +  ",'puntos')";
            db.execSQL(query2);
            System.out.println(query2);

        }

        Log.e("TAG","onResponse:" + response.toString());
        System.out.println(bolsasLast.size());
        try {
            obtenerLasBolsasMonth("bolsasYear/");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void obtenerLasBolsasMonth(String urlDate) throws ParseException {
        initialCounter();
        dbHelper helper = new dbHelper(context, "Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        int valor = 0;
        for (Integer bolsasLast : bolsasLast) {
            Cursor f1 = db.rawQuery("select recojoFecha from Bolsa where codigo = "+ bolsasLast,null);
            if(f1.moveToFirst()){
                do {
                    valor++;
                    if(!f1.getString(0).equals("null") || !f1.getString(0).equals(null)) {
                        String sDate1 = f1.getString(0);
                        Date dia=new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH).parse(sDate1);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(dia);
                        if (cal.get(Calendar.MONTH) == 0)
                            yAxisDataYearBolsa[0] += 1;
                        else if (cal.get(Calendar.MONTH) == 1)
                            yAxisDataYearBolsa[1] += 1;
                        else if (cal.get(Calendar.MONTH) == 2)
                            yAxisDataYearBolsa[2] += 1;
                        else if (cal.get(Calendar.MONTH) == 3)
                            yAxisDataYearBolsa[3] += 1;
                        else if (cal.get(Calendar.MONTH) == 4)
                            yAxisDataYearBolsa[4] += 1;
                        else if (cal.get(Calendar.MONTH) == 5)
                            yAxisDataYearBolsa[5] += 1;
                        else if (cal.get(Calendar.MONTH) == 6)
                            yAxisDataYearBolsa[6] += 1;
                        else if (cal.get(Calendar.MONTH) == 7)
                            yAxisDataYearBolsa[7] += 1;
                        else if (cal.get(Calendar.MONTH) == 8)
                            yAxisDataYearBolsa[8] += 1;
                        else if (cal.get(Calendar.MONTH) == 9)
                            yAxisDataYearBolsa[9] += 1;
                        else if (cal.get(Calendar.MONTH) == 10)
                            yAxisDataYearBolsa[10] += 1;
                        else if (cal.get(Calendar.MONTH) == 11)
                            yAxisDataYearBolsa[11] += 1;
                    }

                }while(f1.moveToNext());
            }

        }
        if(valor>0) {
            String query = "insert into DatosAnuales(enero,febrero,marzo,abril,mayo,junio,julio,agosto,setiembre,octubre,noviembre,diciembre,tipo) " +
                    "values ( " + yAxisDataYearBolsa[0] + ", " + yAxisDataYearBolsa[1] + ", "
                    + yAxisDataYearBolsa[2] + ", " + yAxisDataYearBolsa[3] + ", " + yAxisDataYearBolsa[4] + "," + yAxisDataYearBolsa[5]  + ", "
                    + yAxisDataYearBolsa[6] + ", " + yAxisDataYearBolsa[7] + ","+ + yAxisDataYearBolsa[8] + ","+yAxisDataYearBolsa[9] + ", " + yAxisDataYearBolsa[10] +","+ yAxisDataYearBolsa[11] +  ",'bolsa')";
            db.execSQL(query);
            System.out.println(query);

        }
    }

    public void initialCounter(){
        plasticoCount=0;
        pesoPlastico=0;
        puntosPlastico=0;
        vidrioCount=0;
        pesoVidrio=0;
        puntosVidrio=0;
        papelCartonCount=0;
        pesoPapelCarton=0;
        puntosPapelCarton=0;
        metalCount=0;
        pesoMetal=0;
        puntosMetal=0;
        residuosTotal=0;
    }

    public void addingValuestoText(Probolsa bolsasbydate ){
        if (bolsasbydate.getProducto().getCategoria().getNombre().equals("Plastico")) {
            pesoPlastico += bolsasbydate.getProducto().getPeso();
            puntosPlastico += bolsasbydate.getPuntuacion();
            plasticoCount += bolsasbydate.getCantidad();
        }else if (bolsasbydate.getProducto().getCategoria().getNombre().equals("Vidrio")) {
            pesoVidrio += bolsasbydate.getProducto().getPeso();
            puntosVidrio += bolsasbydate.getPuntuacion();
            vidrioCount+= bolsasbydate.getCantidad();
        } else if (bolsasbydate.getProducto().getCategoria().getNombre().equals("Papel/Carton")) {
            pesoPapelCarton += bolsasbydate.getProducto().getPeso();
            puntosPapelCarton += bolsasbydate.getPuntuacion();
            papelCartonCount+=bolsasbydate.getCantidad();
        } else if (bolsasbydate.getProducto().getCategoria().getNombre().equals("Metal")) {
            pesoMetal += bolsasbydate.getProducto().getPeso();
            puntosMetal += bolsasbydate.getPuntuacion();
            metalCount+=bolsasbydate.getCantidad();
        }
    }*/

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void ordenar(){
        System.out.println("PESOOOOOOOOOOOOOOOOOO");
        System.out.println(pesoEnero);
        pesoEnero = sortByValue(pesoEnero);
        insertar_tops(pesoEnero,"enero");
        pesoFebrero = sortByValue(pesoFebrero);
        insertar_tops(pesoFebrero,"febrero");
        pesoMarzo = sortByValue(pesoMarzo);
        insertar_tops(pesoMarzo,"marzo");
        pesoAbril = sortByValue(pesoAbril);
        insertar_tops(pesoAbril,"abril");
        pesoMayo = sortByValue(pesoMayo);
        insertar_tops(pesoMayo,"mayo");
        pesoJunio = sortByValue(pesoJunio);
        insertar_tops(pesoJunio,"junio");
        pesoJulio = sortByValue(pesoJulio);
        insertar_tops(pesoJulio,"julio");
        pesoAgosto = sortByValue(pesoAgosto);
        insertar_tops(pesoAgosto,"agosto");
        pesoSetiembre = sortByValue(pesoSetiembre);
        insertar_tops(pesoSetiembre,"setiembre");
        pesoOctubre = sortByValue(pesoOctubre);
        insertar_tops(pesoOctubre,"octubre");
        pesoNoviembre = sortByValue(pesoNoviembre);
        insertar_tops(pesoNoviembre,"noviembre");
        pesoDiciembre = sortByValue(pesoDiciembre);
        insertar_tops(pesoDiciembre,"diciembre");

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void ordenarTipo(){
        plasticoEnero = sortByValue(plasticoEnero);
        insertar_tops_residuos(plasticoEnero,"enero", "Plástico");
        plasticoFebrero = sortByValue(plasticoFebrero);
        insertar_tops_residuos(plasticoFebrero,"febrero", "Plástico");
        plasticoMarzo = sortByValue(plasticoMarzo);
        insertar_tops_residuos(plasticoMarzo,"marzo", "Plástico");
        plasticoAbril = sortByValue(plasticoAbril);
        insertar_tops_residuos(plasticoAbril,"abril", "Plástico");
        plasticoMayo = sortByValue(plasticoMayo);
        insertar_tops_residuos(plasticoMayo,"mayo", "Plástico");
        plasticoJunio = sortByValue(plasticoJunio);
        insertar_tops_residuos(plasticoJunio,"junio", "Plástico");
        plasticoJulio = sortByValue(plasticoJulio);
        insertar_tops_residuos(plasticoJulio,"julio", "Plástico");
        plasticoAgosto = sortByValue(plasticoAgosto);
        insertar_tops_residuos(plasticoAgosto,"agosto", "Plástico");
        plasticoSetiembre = sortByValue(plasticoSetiembre);
        insertar_tops_residuos(plasticoSetiembre,"setiembre", "Plástico");
        plasticoOctubre = sortByValue(plasticoOctubre);
        insertar_tops_residuos(plasticoOctubre,"octubre", "Plástico");
        plasticoNoviembre = sortByValue(plasticoNoviembre);
        insertar_tops_residuos(plasticoNoviembre,"noviembre", "Plástico");
        plasticoDiciembre = sortByValue(plasticoDiciembre);
        insertar_tops_residuos(plasticoDiciembre,"diciembre", "Plástico");
        papelEnero = sortByValue(papelEnero);
        insertar_tops_residuos(papelEnero,"enero", "Papel/Cartón");
        papelFebrero = sortByValue(papelFebrero);
        insertar_tops_residuos(papelFebrero,"febrero", "Papel/Cartón");
        papelMarzo = sortByValue(papelMarzo);
        insertar_tops_residuos(papelMarzo,"marzo", "Papel/Cartón");
        papelAbril = sortByValue(papelAbril);
        insertar_tops_residuos(papelAbril,"abril", "Papel/Cartón");
        papelMayo = sortByValue(papelMayo);
        insertar_tops_residuos(papelMayo,"mayo", "Papel/Cartón");
        papelJunio = sortByValue(papelJunio);
        insertar_tops_residuos(papelJunio,"junio", "Papel/Cartón");
        papelJulio = sortByValue(papelJulio);
        insertar_tops_residuos(papelJulio,"julio", "Papel/Cartón");
        papelAgosto = sortByValue(papelAgosto);
        insertar_tops_residuos(papelAgosto,"agosto", "Papel/Cartón");
        papelSetiembre = sortByValue(papelSetiembre);
        insertar_tops_residuos(papelSetiembre,"setiembre", "Papel/Cartón");
        papelOctubre = sortByValue(papelOctubre);
        insertar_tops_residuos(papelOctubre,"octubre", "Papel/Cartón");
        papelNoviembre = sortByValue(papelNoviembre);
        insertar_tops_residuos(papelNoviembre,"noviembre", "Papel/Cartón");
        papelDiciembre = sortByValue(papelDiciembre);
        insertar_tops_residuos(papelDiciembre,"diciembre", "Papel/Cartón");
    }

    public void insertar_tops_residuos(Map<Integer,Double> hm, String mes,String tipo_producto){
        double peso1 = 0, peso2 = 0, peso3 = 0;
        int codigo1 = 0,codigo2 = 0,codigo3 = 0;
        Iterator<Map.Entry<Integer, Double>> it = hm.entrySet().iterator();
        if(cantidad_condominios> 3){
            for(int i=0;i< 3 ;i++){
                Map.Entry<Integer, Double> entry = it.next();
                if(i == 0) {
                    codigo1 = entry.getKey();
                    peso1 = entry.getValue();
                }else{
                    if( i == 1){
                        codigo2 = entry.getKey();
                        peso2 = entry.getValue();
                    }
                    else{
                        codigo3 = entry.getKey();
                        peso3 = entry.getValue();
                    }
                }
            }
        }else {
            for (int i = 0; i < cantidad_condominios; i++) {
                Map.Entry<Integer, Double> entry = it.next();
                if (i == 0) {
                    codigo1 = entry.getKey();
                    peso1 = entry.getValue();
                } else {
                    if (i == 1) {
                        codigo2 = entry.getKey();
                        peso2 = entry.getValue();
                    } else {
                        codigo3 = entry.getKey();
                        peso3 = entry.getValue();
                    }
                }
            }
        }
        generateQueryTopProductos(peso1, mes, codigo1, tipo_producto,peso2,codigo2,peso3,codigo3);
    }


    public void insertar_tops(Map<Integer,Double> hm, String mes){
        double peso1 = 0, peso2 = 0, peso3 = 0;
        int codigo1 = 0,codigo2 = 0,codigo3 = 0;
        Iterator<Map.Entry<Integer, Double>> it = hm.entrySet().iterator();
        if(cantidad_condominios> 3){
            for(int i=0;i< 3 ;i++){
                Map.Entry<Integer, Double> entry = it.next();
                if(i == 0) {
                    codigo1 = entry.getKey();
                    peso1 = entry.getValue();
                }else{
                    if( i == 1){
                        codigo2 = entry.getKey();
                        peso2 = entry.getValue();
                    }
                    else{
                        codigo3 = entry.getKey();
                        peso3 = entry.getValue();
                    }
                }
            }
        }else {
            for (int i = 0; i < cantidad_condominios; i++) {
                Map.Entry<Integer, Double> entry = it.next();
                if (i == 0) {
                    codigo1 = entry.getKey();
                    peso1 = entry.getValue();
                } else {
                    if (i == 1) {
                        codigo2 = entry.getKey();
                        peso2 = entry.getValue();
                    } else {
                        codigo3 = entry.getKey();
                        peso3 = entry.getValue();
                    }
                }
            }
        }
        generateQueryTopCondominios(peso1, mes, codigo1,peso2,codigo2,peso3,codigo3);
    }

    public void generateQuery(String tipo,String producto,double cantidad,double peso,double puntuacion,int bolsaID){
        String query = "insert into Contador (tendenciaTipo,productoTipo,cantidad,peso,puntuacion,bolsa) " +
                "values ('" + tipo + "', '" + producto + "', " + cantidad + ", "
                + peso + ", " + puntuacion + ","+bolsaID+")";
        db.execSQL(query);
        System.out.println(query);
    }

    public void generateQueryTopProductos(double Peso,String mes,int codigo_producto,String tipo_producto, double Peso2, int codigo_2, double Peso3, int codigo3){
        String query = "insert into Contador (peso_prim,mes,codigo_producto_prim,tipo_producto, peso_sec, codigo_producto_sec, peso_ter, codigo_producto_ter) " +
                "values (" + Peso + ", '" + mes + "', " + codigo_producto + ", '"
                + tipo_producto + "', " + Peso2+", " + codigo_2+" , "+Peso3+", "+codigo3+")";
        db.execSQL(query);
        System.out.println(query);
    }

    public void generateQueryTopCondominios(double Peso,String mes,int codigo_condominio1, double Peso2, int codigo_2, double Peso3, int codigo3){
        String query = "insert into Contador (peso_prim,mes,codigo_producto_prim, peso_sec, codigo_producto_sec, peso_ter, codigo_producto_ter,tipo_producto) " +
                "values (" + Peso + ", '" + mes + "', " + codigo_condominio1 + ", "
                 + Peso2+", " + codigo_2+" , "+Peso3+", "+codigo3+",'total')";
        db.execSQL(query);
        System.out.println(query);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Map<Integer,Double> sortByValue(Map<Integer, Double> hm)
    {

        hm = hm.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                        LinkedHashMap::new));
        return hm;
    }

}
