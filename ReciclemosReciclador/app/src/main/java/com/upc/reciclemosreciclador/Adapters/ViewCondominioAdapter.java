package com.upc.reciclemosreciclador.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.upc.reciclemosreciclador.Fragments.DetalleCondominioFragment;
import com.upc.reciclemosreciclador.Fragments.VecinosCondominioFragment;

public class ViewCondominioAdapter extends FragmentStatePagerAdapter {
    int tabCount;
    private String[] tabTitles = new String[]{"Detalle", "Vecinos"};
    private int codigoCondominio;

    public ViewCondominioAdapter(@NonNull FragmentManager fm, int tabCount, int codigoCondominio) {
        super(fm);
        this.tabCount=tabCount;
        this.codigoCondominio = codigoCondominio;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    DetalleCondominioFragment tab1 = new DetalleCondominioFragment(codigoCondominio);
                    return tab1;
                case 1:
                    VecinosCondominioFragment tab2 = new VecinosCondominioFragment(codigoCondominio);
                    return tab2;
                default:
                    return null;
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

}
