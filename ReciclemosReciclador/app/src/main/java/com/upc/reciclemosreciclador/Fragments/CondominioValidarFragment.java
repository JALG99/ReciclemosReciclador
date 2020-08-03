package com.upc.reciclemosreciclador.Fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.upc.reciclemosreciclador.Adicionales.dbHelper;
import com.upc.reciclemosreciclador.R;
import com.google.android.material.tabs.TabLayout;
import com.upc.reciclemosreciclador.Adapters.ViewTrendingAdapter;
import com.upc.reciclemosreciclador.Entities.Condominio;

import java.util.ArrayList;

public class CondominioValidarFragment extends Fragment implements  TabLayout.OnTabSelectedListener {
   private ViewPager viewPager;
   private ViewTrendingAdapter adapter;
   private TabLayout tabLayout;
   private Spinner spinnerCondominio;
   private ArrayList<String> CondominioList;
   private ArrayList<Condominio> listaCondominios;
   dbHelper helper;
   public String ValorCondominio = "";

   @Nullable
   @Override
   public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.fragment_condominio_validar,container,false);
      spinnerCondominio = view.findViewById(R.id.spnCondominio);
      helper = new dbHelper(getActivity(),"Usuario.sqlite", null, 1);
      consultarListaCondominios();
      ArrayAdapter<CharSequence> adaptador = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,CondominioList);
      spinnerCondominio.setAdapter(adaptador);
      tabLayout = view.findViewById(R.id.tabLayoutFrecuenciaByCondominio);
      tabLayout.addTab(tabLayout.newTab());
      tabLayout.addTab(tabLayout.newTab());
      tabLayout.addTab(tabLayout.newTab());
      tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
      viewPager = view.findViewById(R.id.viewPagerFrecuenciaByCondominio);
      adapter = new ViewTrendingAdapter(getFragmentManager(),tabLayout.getTabCount(),"condominio",0);
      viewPager.setAdapter(adapter);
      tabLayout.setupWithViewPager(viewPager);
      return view;
   }
   @Override
   public void onActivityCreated(@Nullable Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);
      spinnerCondominio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
         @Override
         public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            ValorCondominio = spinnerCondominio.getSelectedItem().toString();
            ViewTrendingAdapter saAdapter = (ViewTrendingAdapter) viewPager.getAdapter();
            int codigo = buscarCodigo(ValorCondominio);
            saAdapter.setValor(codigo);
            saAdapter.notifyDataSetChanged();
            viewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(viewPager);
         }

         @Override
         public void onNothingSelected(AdapterView<?> parentView) {
            // your code here
         }

      });
   }
   @Override
   public void onTabSelected(TabLayout.Tab tab) {
      viewPager.setCurrentItem(tab.getPosition());
   }

   @Override
   public void onTabUnselected(TabLayout.Tab tab) {

   }

   @Override
   public void onTabReselected(TabLayout.Tab tab) {

   }

   private void consultarListaCondominios()
   {
      SQLiteDatabase db = helper.getReadableDatabase();

      Condominio condominio = null;
      listaCondominios = new ArrayList<Condominio>();
      Cursor cursor = db.rawQuery("select codigo,nombre,reciclador from Condominio",null);
      while (cursor.moveToNext())
      {
         condominio = new Condominio();
         condominio.setCodigo(cursor.getInt(0));
         condominio.setNombre(cursor.getString(1));
         listaCondominios.add(condominio);
         System.out.println(condominio.getNombre());
      }
      obtenerLista();
   }
   private int buscarCodigo(String nombre){
      for (int i=0;i< listaCondominios.size();i++){
         if(listaCondominios.get(i).getNombre().equals(nombre))
            return listaCondominios.get(i).getCodigo();
      }
      return 0;
   }


   private void obtenerLista(){
      CondominioList = new ArrayList<String>();
      CondominioList.add("Seleccionar el condominio");
      for (int i = 0; i< listaCondominios.size() ; i++)
      {
         CondominioList.add(listaCondominios.get(i).getNombre());
      }
   }

   public String getValorCondominio() {
      return ValorCondominio;
   }

   public void setValorCondominio(String valorCondominio) {
      ValorCondominio = valorCondominio;
   }
}
