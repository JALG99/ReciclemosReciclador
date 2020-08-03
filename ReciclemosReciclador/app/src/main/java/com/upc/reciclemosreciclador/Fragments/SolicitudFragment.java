package com.upc.reciclemosreciclador.Fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.upc.reciclemosreciclador.Adicionales.dbHelper;
import com.upc.reciclemosreciclador.Entities.JsonPlaceHolderApi;
import com.upc.reciclemosreciclador.Entities.Reciclador;
import com.upc.reciclemosreciclador.Entities.Solicitud;
import com.upc.reciclemosreciclador.R;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class SolicitudFragment extends Fragment {

    private Retrofit retrofit;
    private Button btnEnviar;
    private EditText editTextSustento,editTextDatos;

    public SolicitudFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_solicitud, container, false);
        retrofit = new Retrofit.Builder()
                .baseUrl("https://recyclerapiresttdp.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        editTextSustento = view.findViewById(R.id.editTextSolicitudSustento);
        editTextDatos = view.findViewById(R.id.editTextActualizaDatos);
        btnEnviar = view.findViewById(R.id.btnSolicitudSend);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    RegistrarSolicitud();
                } catch (InvalidKeySpecException | NoSuchAlgorithmException | ParseException e) {
                    e.printStackTrace();
                }

            }
        });
        return view;
    }

    public void RegistrarSolicitud() throws InvalidKeySpecException, NoSuchAlgorithmException, ParseException {
        if(editTextDatos.getText().toString().trim().isEmpty()){
            editTextDatos.setError("Por favor ingrese los datos que desea actualizar");
        }else if(editTextSustento.getText().toString().trim().isEmpty()){
            editTextSustento.setError("Por favor ingrese el sustento o razón por la cual cambiará los datos");
        }else{
            dbHelper helper = new dbHelper(getContext(),"Usuario.sqlite", null, 1);
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor fila2 = db.rawQuery("select codigo from Reciclador", null);
            fila2.moveToFirst();

            int codigoReciclador = fila2.getInt(0);
            Reciclador reciclador = new Reciclador();
            reciclador.setCodigo(codigoReciclador);
            System.out.println(codigoReciclador);
            String valor_sustento = editTextSustento.getText().toString();
            String valor_datosActualizar = editTextDatos.getText().toString();

            Solicitud solicitud = new Solicitud();
            solicitud.setActiva(true);
            solicitud.setSustento(valor_sustento);
            solicitud.setDatosActualizar(valor_datosActualizar);
            solicitud.setReciclador(reciclador);


            JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
            Call<Solicitud> call = jsonPlaceHolderApi.createSolicitud(solicitud);
            System.out.println(solicitud.toString());
            call.enqueue(new Callback<Solicitud>() {
                @Override
                public void onResponse(Call<Solicitud> call, Response<Solicitud> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Registro Exitoso", Toast.LENGTH_LONG).show();
                        ProfileFragment profileFragment = new ProfileFragment();
                        FragmentTransaction transaction= (getActivity()).getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment, profileFragment);
                        transaction.commit();
                        Log.e("TAG", "post submitted to API.:" + response.toString());
                    } else {
                        Log.e("TAG", "onResponse:" + response.toString());
                        ProfileFragment profileFragment = new ProfileFragment();
                        FragmentTransaction transaction= (getActivity()).getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment, profileFragment);
                        transaction.commit();
                    }
                }

                @Override
                public void onFailure(Call<Solicitud> call, Throwable t) {
                    Log.e("TAG", "onFailure:" + t.getMessage());
                }
            });
        }
    }
}
