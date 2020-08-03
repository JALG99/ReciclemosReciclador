package com.upc.reciclemosreciclador.Inicio;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.upc.reciclemosreciclador.Adicionales.dbHelper;
import com.upc.reciclemosreciclador.R;

public class DetallesFragment extends Fragment {

    int bolsa;
    TextView txtNombre, txtCondominio, txtFecha;
    Button btnValidar;

    public DetallesFragment(int bolsa) {
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
        View v = inflater.inflate(R.layout.fragment_detalles, container, false);

        txtNombre = v.findViewById(R.id.txtNombre);
        txtCondominio = v.findViewById(R.id.txtCondominio);
        txtFecha = v.findViewById(R.id.txtFecha);

        dbHelper helper = new dbHelper(getActivity(),"Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor fila = db.rawQuery("select usuario, fechaCreado from Bolsa where codigo = " + bolsa, null);

        fila.moveToFirst();

        Cursor fila2 = db.rawQuery("select nombre, condominio_name from Usuario where codigo = " + fila.getInt(0), null);

        fila2.moveToFirst();

        txtNombre.setText(fila2.getString(0));
        txtCondominio.setText(fila2.getString(1));
        txtFecha.setText(fila.getString(1));

        btnValidar = v.findViewById(R.id.btnValidar);
        btnValidar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ValidacionActivity) getActivity()).validar(bolsa);
            }
        });

        return v;
    }
}
