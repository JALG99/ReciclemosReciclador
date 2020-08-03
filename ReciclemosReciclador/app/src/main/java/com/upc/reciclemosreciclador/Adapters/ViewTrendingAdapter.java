package com.upc.reciclemosreciclador.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.upc.reciclemosreciclador.Fragments.MonthCondominioFragment;
import com.upc.reciclemosreciclador.Fragments.MonthFragment;
import com.upc.reciclemosreciclador.Fragments.WeekCondominioFragment;
import com.upc.reciclemosreciclador.Fragments.WeekFragment;
import com.upc.reciclemosreciclador.Fragments.YearCondominioFragment;
import com.upc.reciclemosreciclador.Fragments.YearFragment;

public class ViewTrendingAdapter extends FragmentStatePagerAdapter {

    int tabCount;
    String tipo;
    int valor;
    private String[] tabTitles = new String[]{"Semana", "Mes","Record"};

    public ViewTrendingAdapter(@NonNull FragmentManager fm, int tabCount, String tipo, int valor) {
        super(fm);
        this.tabCount=tabCount;
        this.tipo = tipo;
        this.valor = valor;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(tipo.equals("condominio")) {
            switch (position) {
                case 0:
                    WeekCondominioFragment tab1 = new WeekCondominioFragment();
                    tab1.setValor(valor);
                    return tab1;
                case 1:
                    YearCondominioFragment tab3 = new YearCondominioFragment();
                    tab3.setValor(valor);
                    return tab3;
                case 2:
                    MonthCondominioFragment tab2 = new MonthCondominioFragment();
                    tab2.setValor(valor);
                    return tab2;
                default:
                    return null;
            }
        }else{
            switch (position) {
                case 0:
                    WeekFragment tab1 = new WeekFragment();

                    return tab1;
                case 1:
                    YearFragment tab3 = new YearFragment();
                    return tab3;
                case 2:
                    MonthFragment tab2 = new MonthFragment();
                    return tab2;
                default:
                    return null;
            }
        }
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public int getCount() {
        return tabCount;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }
}