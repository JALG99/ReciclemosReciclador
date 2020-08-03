package com.example.recicladorapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.recicladorapp.Adapters.ViewTrendingAdapter;
import com.example.recicladorapp.R;
import com.google.android.material.tabs.TabLayout;


public class TotalBolsasValidarFragment extends Fragment implements  TabLayout.OnTabSelectedListener {
    private ViewPager viewPager;
    private ViewTrendingAdapter adapter;
    private TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_total_bolsas_validar,container,false);
        tabLayout = view.findViewById(R.id.tabLayoutFrecuenciaTotal);
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        viewPager = view.findViewById(R.id.viewPagerFrecuenciaTotal);
        adapter = new ViewTrendingAdapter(getFragmentManager(),tabLayout.getTabCount(),"total",0);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
}
