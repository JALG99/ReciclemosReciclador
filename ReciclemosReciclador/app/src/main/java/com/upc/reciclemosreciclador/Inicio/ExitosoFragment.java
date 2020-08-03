package com.upc.reciclemosreciclador.Inicio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.upc.reciclemosreciclador.Entities.Bolsa;
import com.upc.reciclemosreciclador.Entities.JsonPlaceHolderApi;
import com.upc.reciclemosreciclador.Entities.Probolsa;
import com.upc.reciclemosreciclador.R;
import com.upc.reciclemosreciclador.Adicionales.dbHelper;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ExitosoFragment extends Fragment {

    /*TextView txtTotalPuntos, txtTotalCantidad;
    TextView btnTxtVincular;
    int TotalPuntos;
    int TotalCantidad;*/
    Button btnAceptar, btnRechazar;
    private Retrofit retrofit;
    private ProgressDialog progressDialog;
    int bolsa;

    ExitosoFragment(int bolsa){
        this.bolsa = bolsa;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message message) {
            switch (message.what){
                case 1:
                    progressDialog.show();
                    progressDialog.setCancelable(false);
                    break;
                case 2:
                    progressDialog.cancel();
                    ((BolsaActivity) getActivity()).aceptada(bolsa);
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.exitoso, container, false);

        /*txtTotalPuntos = v.findViewById(R.id.txtTotalPuntos);
        txtTotalCantidad = v.findViewById(R.id.txtTotalCantidad);
        btnTxtVincular = v.findViewById(R.id.btnTxtVincular);

        btnTxtVincular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regisrtarbolsaactivity = new Intent(getContext(), RegistrarBolsaActivity.class);
                startActivity(regisrtarbolsaactivity);
            }
        });
        calcular();*/

        btnAceptar = v.findViewById(R.id.btnAceptar);
        btnRechazar = v.findViewById(R.id.btnRechazar);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Cargando, por favor espere...");

        retrofit = new Retrofit.Builder()
                .baseUrl("https://recyclerapiresttdp.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BackgroundJob().execute();
            }
        });
        btnRechazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BolsaActivity) getActivity()).rechazada(bolsa);
            }
        });

        return v;
    }

    public void recogerBolsa() throws IOException {
        dbHelper helper = new dbHelper(getActivity(),"Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();

        Cursor f2 = db.rawQuery("select codigo from Reciclador", null);

        f2.moveToFirst();

        JsonPlaceHolderApi jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);
        Call<Bolsa> call2=jsonPlaceHolderApi.bolsaRecogida("bolsa/recogida/" + bolsa + "/" + f2.getInt(0));
        Response<Bolsa> response2 = call2.execute();
        Log.e("TAG","onResponse:" + response2.toString());
    }

    private class BackgroundJob extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                recogerBolsa();
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
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

    /*public void calcular(){
        TotalPuntos = 0;
        TotalCantidad = 0;
        dbHelper helper = new dbHelper(getActivity(),"Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor fila2 = db.rawQuery("select codigo from Bolsa where activa = 'true'", null);

        fila2.moveToFirst();

        Cursor fila = db.rawQuery("select cantidad, puntuacion, producto from Probolsa where bolsa = " + fila2.getInt(0), null);

        if(fila.moveToFirst()){
            do {
                Cursor fila3 = db.rawQuery("select categoria from Producto where codigo = " + fila.getInt(2), null);
                fila3.moveToFirst();
                if(fila3.getInt(0) != 2 && fila3.getInt(0) != 4) {
                    TotalPuntos += fila.getInt(1);
                    TotalCantidad += fila.getInt(0);
                }
            } while (fila.moveToNext());
            System.out.println(TotalPuntos);
            System.out.println(TotalCantidad);
            db.close();

            txtTotalPuntos.setText(TotalPuntos + "");
            txtTotalCantidad.setText(TotalCantidad + "");
        }else{
            ((BolsaActivity) getActivity()).setEscaneaAlgoFragment();
        }

    }*/
}