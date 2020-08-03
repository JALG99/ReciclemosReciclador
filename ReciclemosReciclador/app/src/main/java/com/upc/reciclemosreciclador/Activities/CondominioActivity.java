package com.upc.reciclemosreciclador.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.upc.reciclemosreciclador.Adicionales.InitialValues;
import com.upc.reciclemosreciclador.Adicionales.dbHelper;
import com.upc.reciclemosreciclador.Fragments.CondominioListaFragment;
import com.upc.reciclemosreciclador.Inicio.BolsaActivity;
import com.upc.reciclemosreciclador.Inicio.EstadosActivity;
import com.upc.reciclemosreciclador.Inicio.ExportarCSV;
import com.upc.reciclemosreciclador.Inicio.LoginActivity;
import com.upc.reciclemosreciclador.Inicio.MeInformoActivity;
import com.upc.reciclemosreciclador.Inicio.ValidacionActivity;
import com.upc.reciclemosreciclador.R;

public class CondominioActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    Fragment condominioListaFragment = CondominioListaFragment.newInstance();
    private DrawerLayout mDrawerLayout;
    private ExportarCSV exportarCSV = new ExportarCSV();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_condominio);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment, condominioListaFragment);
        transaction.commit();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = findViewById(R.id.drawer);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        dbHelper helper = new dbHelper(this, "Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor filaUsuario = db.rawQuery("select codigo, nombre from Reciclador", null);

        filaUsuario.moveToFirst();

        ((InitialValues)this.getApplication()).setIdReciclador(Integer.toString(filaUsuario.getInt(0)));

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.txtUser);
        navUsername.setText(filaUsuario.getString(1));
        TextView navViewProfile = headerView.findViewById(R.id.txtMiPerfil);
        navViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ProfileActity.class));
            }
        });

        //INICIALIZA APP BAR
        BottomNavigationView bottomNavigationView = findViewById(R.id.botton_navigation);

        //CAMBIO DE SELECCIÓN
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.miaporte:
                        startActivity(new Intent(getApplicationContext(), ValidarActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.estados:
                        startActivity(new Intent(getApplicationContext(), EstadosActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.escaner:
                        startActivity(new Intent(getApplicationContext(), BolsaActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.mibolsa:
                        startActivity(new Intent(getApplicationContext(), ValidacionActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_condominios:
                startActivity(new Intent(getApplicationContext(), CondominioActivity.class));
                break;
            case R.id.nav_info:
                startActivity(new Intent(getApplicationContext(), MeInformoActivity.class));
                break;
            case R.id.nav_message:
                Toast.makeText(this, "Proximamente 2", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;
            case R.id.nav_csv:
                exportarCSV.exportar(this);
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
