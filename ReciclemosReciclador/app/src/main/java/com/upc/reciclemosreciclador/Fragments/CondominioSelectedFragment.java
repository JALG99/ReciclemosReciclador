package com.upc.reciclemosreciclador.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.upc.reciclemosreciclador.Adapters.ViewCondominioAdapter;
import com.upc.reciclemosreciclador.R;


public class CondominioSelectedFragment extends Fragment {

    private int codigo;
    private ViewPager viewPager;
    private ViewCondominioAdapter adapter;
    private TabLayout tabLayout;

    public CondominioSelectedFragment(int codigo) {
        this.codigo = codigo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_condominio_selected, container, false);
        tabLayout = view.findViewById(R.id.tabLayoutCondominioSelected);
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        viewPager = view.findViewById(R.id.viewPagerCondominioSelected);
        adapter = new ViewCondominioAdapter(getFragmentManager(),tabLayout.getTabCount(), codigo);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }
}
