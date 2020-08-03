package com.upc.reciclemosreciclador.Inicio;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.upc.reciclemosreciclador.Adicionales.dbHelper;
import com.upc.reciclemosreciclador.Entities.Resumen;
import com.upc.reciclemosreciclador.Entities.ResumenRecoleccion;
import com.upc.reciclemosreciclador.Entities.ResumenValidacion;
import com.upc.reciclemosreciclador.R;

import java.util.ArrayList;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ResumenRecoleccionFragment extends Fragment {

    ArrayList<ResumenRecoleccion> listResumenRecoleccion = new ArrayList<>();
    RecyclerView rclResumenRecoleccion;
    AdapterResumenRecoleccion adapter;
    TextView txtRecogidas, txtValidadas, txtPendientes;


    public ResumenRecoleccionFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_resumen_recoleccion, container, false);

        txtRecogidas = v.findViewById(R.id.txtRecogidas);
        txtValidadas = v.findViewById(R.id.txtValidadas);
        txtPendientes = v.findViewById(R.id.txtPendientes);

        rclResumenRecoleccion = v.findViewById(R.id.rclResumenRecoleccion);
        rclResumenRecoleccion.setLayoutManager(new LinearLayoutManager(getActivity()));

        dbHelper helper = new dbHelper(getActivity(),"Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor fila = db.rawQuery("select nombre, urbanizacion, distrito, codigo from Condominio", null);

        fila.moveToFirst();

        int pendientestotales = 0;
        int recogidastotales = 0;

        do{
            int recogidas = 0;
            int pendientes = 0;

            Cursor fila2 = db.rawQuery("select codigo, validado from Bolsa where condominio =" + fila.getInt(3), null);

            fila2.moveToFirst();

            do{
                if(fila2.getString(1).compareTo("false") == 0){
                    pendientes = pendientes + 1;
                    pendientestotales = pendientestotales + 1;
                }
                recogidas = recogidas + 1;
                recogidastotales = recogidastotales + 1;
            }while(fila2.moveToNext());

            ResumenRecoleccion ayuda = new ResumenRecoleccion();
            ayuda.setNombre(fila.getString(0));
            ayuda.setUrbanizacion(fila.getString(1));
            ayuda.setDistrito(fila.getString(2));
            ayuda.setRecogidas(recogidas);
            ayuda.setPendientes(pendientes);
            listResumenRecoleccion.add(ayuda);
        }while (fila.moveToNext());

        adapter = new AdapterResumenRecoleccion(listResumenRecoleccion, getActivity());
        rclResumenRecoleccion.setAdapter(adapter);

        txtPendientes.setText(pendientestotales + "");
        txtRecogidas.setText(recogidastotales + "");
        txtValidadas.setText((recogidastotales - pendientestotales) + "");

        db.close();

        return v;
    }
}
