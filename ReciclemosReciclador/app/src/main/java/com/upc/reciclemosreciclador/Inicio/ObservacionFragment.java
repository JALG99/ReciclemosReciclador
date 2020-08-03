package com.upc.reciclemosreciclador.Inicio;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.upc.reciclemosreciclador.Adicionales.dbHelper;
import com.upc.reciclemosreciclador.Entities.Bolsa;
import com.upc.reciclemosreciclador.Entities.JsonPlaceHolderApi;
import com.upc.reciclemosreciclador.R;

import org.w3c.dom.Text;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ObservacionFragment extends Fragment {

    int bolsa;
    Boolean validado;
    TextView txtMensaje;
    EditText txtObservacion;
    Button btnValidar;
    private Retrofit retrofit;
    private ProgressDialog progressDialog;

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
                    ((ValidacionActivity) getActivity()).finalizado();
                    break;
            }
        }
    };

    public ObservacionFragment(int bolsa, Boolean validado) {
        this.bolsa = bolsa;
        this.validado = validado;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_observacion, container, false);

        txtObservacion = v.findViewById(R.id.txtObservacion);
        txtMensaje = v.findViewById(R.id.txtMensaje);
        btnValidar = v.findViewById(R.id.btnValidar);

        retrofit = new Retrofit.Builder()
                .baseUrl("https://recyclerapiresttdp.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Registrando observación...");

        if(validado)
            txtMensaje.setText("Al procesar el contenido revisado todo coincide correctamente, si desea dejar un comentario adicional puede hacerlo en la parte inferior.");
        else
            txtMensaje.setText("Al parecer el contenido revisado no coincide con lo esperado. Si desea puede dejar un comentario al respecto en la parte inferior.");

        btnValidar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtObservacion.getText().toString().compareTo("") != 0)
                    new BackgroundJob(txtObservacion.getText().toString(), 1).execute();
                else
                    new BackgroundJob(txtObservacion.getText().toString(), 0).execute();
            }
        });

        return v;
    }


    public void enviar(String observacion, int c) throws IOException {
        dbHelper helper = new dbHelper(getActivity(),"Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        if(c == 1) {
            Call<Bolsa> call2 = jsonPlaceHolderApi.agregarObservacion("bolsa/observacion/" + bolsa + "/" + observacion);
            Response<Bolsa> response2 = call2.execute();
            Log.e("TAG", "onResponse:" + response2.toString());
        }
        Call<Bolsa> call=jsonPlaceHolderApi.bolsaValidada("bolsa/validado/" + bolsa);
        Response<Bolsa> response = call.execute();
        Log.e("TAG","onResponse:" + response.toString());
    }

    private class BackgroundJob extends AsyncTask<Void,Void,Void> {

        String obs;
        int c;

        BackgroundJob(String obs, int c){
            this.obs = obs;
            this.c = c;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                enviar(obs, c);
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
}
