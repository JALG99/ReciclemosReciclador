package com.upc.reciclemosreciclador.Fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.upc.reciclemosreciclador.Adapters.ListaCondominioAdapter;
import com.upc.reciclemosreciclador.Adicionales.dbHelper;
import com.upc.reciclemosreciclador.Entities.Condominio;
import com.upc.reciclemosreciclador.Entities.Distrito;
import com.upc.reciclemosreciclador.R;

import java.util.ArrayList;

public class CondominioListaFragment extends Fragment {

    View view;
    private RecyclerView recyclerView;
    private ListaCondominioAdapter listaCondominioAdapter;
    private EditText editTextCondominio;
    private ArrayList<Condominio> listCondominios;
    public CondominioListaFragment() {
        // Required empty public constructor
    }

    public static CondominioListaFragment newInstance() {
        return new CondominioListaFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_condominio_lista, container, false);
        editTextCondominio = view.findViewById(R.id.txtMisCondominios);
        recyclerView = view.findViewById(R.id.recycler);
        listaCondominioAdapter = new ListaCondominioAdapter(getContext());
        recyclerView.setAdapter(listaCondominioAdapter);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),1);
        recyclerView.setLayoutManager(layoutManager);
        obtenerDatos();
        editTextCondominio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
        return view;
    }

   private void filter(String text){
        ArrayList<Condominio> filteredList = new ArrayList<>();
        for(Condominio item: listCondominios ){
            if(item.getNombre().toLowerCase().contains(text.toLowerCase())  || item.getUrbanizacion().toLowerCase().contains(text.toLowerCase())  || item.getDistrito().getNombre().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }
       listaCondominioAdapter.filterList(filteredList);
   }
    private void obtenerDatos(){
        Condominio condominio;
        Distrito distrito;
        dbHelper helper = new dbHelper(getActivity(),"Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
        listCondominios = new ArrayList<>();
        Cursor condominiosData = db.rawQuery("select codigo, nombre,urbanizacion,distrito from Condominio",null);
        while (condominiosData.moveToNext()){
            condominio = new Condominio();
            distrito = new Distrito();
            condominio.setCodigo(condominiosData.getInt(0));
            condominio.setNombre(condominiosData.getString(1));
            condominio.setUrbanizacion(condominiosData.getString(2));
            distrito.setNombre(condominiosData.getString(3));
            condominio.setDistrito(distrito);
            listCondominios.add(0,condominio);
        }
        listaCondominioAdapter.adicionarCondominios(listCondominios);
    }
}
