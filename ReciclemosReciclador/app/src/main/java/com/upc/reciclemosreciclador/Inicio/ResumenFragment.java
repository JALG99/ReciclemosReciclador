package com.upc.reciclemosreciclador.Inicio;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.upc.reciclemosreciclador.Adicionales.dbHelper;
import com.upc.reciclemosreciclador.Entities.Bolsalist;
import com.upc.reciclemosreciclador.Entities.Resumen;
import com.upc.reciclemosreciclador.R;

import java.util.ArrayList;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ResumenFragment extends Fragment {

    ArrayList<Resumen> lisResumen = new ArrayList<>();
    RecyclerView rclResumen;
    AdapterResumen adapter;
    Retrofit retrofit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_resumen, container, false);

        rclResumen = v.findViewById(R.id.rclDetalle);
        rclResumen.setLayoutManager(new LinearLayoutManager(getActivity()));

        int cantPlastico, cantPapelCarton, cantVidrio, cantMetal, pesoPlastico, pesoPapelCarton, pesoVidrio, pesoMetal;
        cantPlastico = 0;
        cantPapelCarton = 0;
        cantVidrio = 0;
        cantMetal = 0;
        pesoPlastico = 0;
        pesoPapelCarton = 0;
        pesoVidrio = 0;
        pesoMetal = 0;

        dbHelper helper = new dbHelper(getActivity(),"Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor fila = db.rawQuery("select id_lite, codigo from Bolsa", null);

        fila.moveToLast();

        Cursor f = db.rawQuery("select nombre, codigo from Categoria", null);

        f.moveToFirst();

        Cursor fila2 = db.rawQuery("select id_lite, codigo, producto, cantidad, peso  from Probolsa where bolsa = " + fila.getInt(1), null);

        fila2.moveToFirst();

        do{
            Cursor fila3 = db.rawQuery("select categoria  from Producto where codigo = " + fila2.getInt(2), null);

            fila3.moveToFirst();
            switch (fila3.getInt(0)){
                case 1:
                    cantPlastico = cantPlastico + fila2.getInt(3);
                    pesoPlastico = pesoPlastico + fila2.getInt(4);
                    break;
                case 2:
                    cantVidrio = cantVidrio + fila2.getInt(3);
                    pesoVidrio = pesoVidrio + fila2.getInt(4);
                    break;
                case 3:
                    cantPapelCarton = cantPapelCarton + fila2.getInt(3);
                    pesoPapelCarton = pesoPapelCarton + fila2.getInt(4);
                    break;
                case 4:
                    cantMetal = cantMetal + fila2.getInt(3);
                    pesoMetal = pesoMetal + fila2.getInt(4);
                    break;
            }
        }while(fila2.moveToNext());

        if(cantPlastico != 0){
            Resumen ayuda = new Resumen();

            ayuda.setCodigo(1);
            ayuda.setNombre("Plástico");
            ayuda.setCantidad(cantPlastico);
            ayuda.setPeso(pesoPlastico);

            lisResumen.add(ayuda);
        }
        if(cantPapelCarton != 0){
            Resumen ayuda = new Resumen();

            ayuda.setCodigo(3);
            ayuda.setNombre("Papel/Cartón");
            ayuda.setCantidad(cantPapelCarton);
            ayuda.setPeso(pesoPapelCarton);

            lisResumen.add(ayuda);
        }
        if(cantMetal != 0){
            Resumen ayuda = new Resumen();

            ayuda.setCodigo(4);
            ayuda.setNombre("Metal");
            ayuda.setCantidad(cantMetal);
            ayuda.setPeso(pesoMetal);

            lisResumen.add(ayuda);
        }
        if(cantVidrio != 0){
            Resumen ayuda = new Resumen();

            ayuda.setCodigo(2);
            ayuda.setNombre("Vidrio");
            ayuda.setCantidad(cantVidrio);
            ayuda.setPeso(pesoVidrio);

            lisResumen.add(ayuda);
        }

        adapter = new AdapterResumen(lisResumen, getActivity());
        rclResumen.setAdapter(adapter);

        db.close();
        return v;
    }

}
