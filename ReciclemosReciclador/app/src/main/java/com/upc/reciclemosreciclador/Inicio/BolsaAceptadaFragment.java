package com.upc.reciclemosreciclador.Inicio;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.upc.reciclemosreciclador.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BolsaAceptadaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BolsaAceptadaFragment extends Fragment {

    Button btnAceptar;
    int bolsa;

    public BolsaAceptadaFragment(int bolsa){
        this.bolsa = bolsa;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bolsa_aceptada, container, false);

        btnAceptar = v.findViewById(R.id.btnAceptar);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidacionActivity validacion = new ValidacionActivity();
                Intent validacionActivity = new Intent(getContext(), validacion.getClass());
                startActivity(validacionActivity);
                validacion.detallito(bolsa);
            }
        });

        return v;
    }
}
