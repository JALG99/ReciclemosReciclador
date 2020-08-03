package com.upc.reciclemosreciclador.Inicio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.upc.reciclemosreciclador.Adicionales.dbHelper;
import com.upc.reciclemosreciclador.Entities.Bolsa;
import com.upc.reciclemosreciclador.Entities.JsonPlaceHolderApi;
import com.upc.reciclemosreciclador.Entities.Probolsa;
import com.upc.reciclemosreciclador.Entities.Resumen;
import com.upc.reciclemosreciclador.Entities.ResumenRecoleccion;
import com.upc.reciclemosreciclador.Entities.ResumenValidacion;
import com.upc.reciclemosreciclador.R;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ResumenValidacionFragment extends Fragment {

    ArrayList<ResumenValidacion> listResumenValidacion = new ArrayList<>();
    RecyclerView rclResumenValidacion;
    AdapterResumenValidacion adapter;
    Retrofit retrofit;
    ArrayList<Integer> Condominios;
    ArrayList<Integer> Valores;


    private ProgressDialog progressDialog;
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            switch (message.what){
                case 1:
                    progressDialog.show();
                    progressDialog.setCancelable(false);
                    break;
                case 2:
                    progressDialog.cancel();
                    asignarData(Valores, Condominios);
                    break;
            }
        }
    };

    public ResumenValidacionFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_resumen_validacion, container, false);

        retrofit = new Retrofit.Builder()
                .baseUrl("https://recyclerapiresttdp.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        rclResumenValidacion = v.findViewById(R.id.rclResumenValidacion);
        rclResumenValidacion.setLayoutManager(new LinearLayoutManager(getActivity()));

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Cargando data...");

        dbHelper helper = new dbHelper(getActivity(),"Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor fila = db.rawQuery("select codigo from Condominio", null);

        fila.moveToFirst();

        Condominios = new ArrayList<>();

        do{
            Condominios.add(fila.getInt(0));
        }while(fila.moveToNext());

        db.close();

        new BackgroundJob(Condominios).execute();

        return v;
    }

    ArrayList<Bolsa> getBolsas(ArrayList<Integer> Condominios) throws IOException {
        ArrayList<Bolsa> ayuda = new ArrayList<>();

        JsonPlaceHolderApi jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);
        Call<List<Bolsa>> call=jsonPlaceHolderApi.getBolsas("/bolsa");
        Response<List<Bolsa>> response = call.execute();

        for (Bolsa p: response.body()) {
            try{
                if(Condominios.contains(p.getUsuario().getCondominio().getCodigo()) && p.getRecojoFecha() != null){
                    ayuda.add(p);
                }
            }catch (Exception e){}
        }
        Log.e("TAG","onResponse:" + response.toString());

        return ayuda;
    }

    void getProbolsas(ArrayList<Bolsa> Bolsas, ArrayList<Integer> Condominios) throws IOException {
        ArrayList<Integer> ayuda = new ArrayList<>();

        for (Integer i: Condominios) {
            ayuda.add(0); //pesototal
            ayuda.add(0); //cantbolsas
        }

        for (Bolsa p: Bolsas) {
            JsonPlaceHolderApi jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);
            Call<List<Probolsa>> call=jsonPlaceHolderApi.getProbolsaByBolsa("probolsa/bolsa/" + p.getCodigo());
            Response<List<Probolsa>> response = call.execute();

            int cond = Condominios.indexOf(p.getUsuario().getCondominio().getCodigo());

            for (Probolsa q: response.body()) {
                ayuda.set((cond * 2), (int) (ayuda.get((cond * 2)) + (q.getProducto().getPeso() * q.getCantidad())));
            }
            ayuda.set((cond * 2) + 1, ayuda.get((cond * 2) + 1) + 1);

            Log.e("TAG","onResponse:" + response.toString());
        }

        Valores = ayuda;
    }

    void asignarData(ArrayList<Integer> Valores, ArrayList<Integer> Condominios){
        dbHelper helper = new dbHelper(getActivity(),"Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor fila = db.rawQuery("select nombre, urbanizacion, distrito, codigo from Condominio", null);

        fila.moveToFirst();

        do{
            ResumenValidacion ayuda = new ResumenValidacion();
            if(Condominios.contains(fila.getInt(3))){
                ayuda.setNombre(fila.getString(0));
                ayuda.setUrbanizacion(fila.getString(1));
                ayuda.setDistrito(fila.getString(2));

                int cond = Condominios.indexOf(fila.getInt(3));

                ayuda.setPesoActual((Valores.get(cond * 2))/1000);
                ayuda.setBolsas(Valores.get((cond * 2) + 1));
                ayuda.setMax(1000);

                listResumenValidacion.add(ayuda);
            }
        }while(fila.moveToNext());

        adapter = new AdapterResumenValidacion(listResumenValidacion, getActivity());
        rclResumenValidacion.setAdapter(adapter);
    }

    private class BackgroundJob extends AsyncTask<Void,Void,Void> {

        ArrayList<Integer> Condominios;
        ArrayList<Bolsa> Bolsas;

        public BackgroundJob(ArrayList<Integer> Condominios){
            this.Condominios = Condominios;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
                Bolsas = getBolsas(Condominios);
                getProbolsas(Bolsas, Condominios);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void avoid){
            Message msg = new Message();
            msg.what = 2;
            mHandler.sendMessage(msg);
        }
    }
}
