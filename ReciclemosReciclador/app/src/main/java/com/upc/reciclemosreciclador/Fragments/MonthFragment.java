package com.upc.reciclemosreciclador.Fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.upc.reciclemosreciclador.Adicionales.dbHelper;
import com.upc.reciclemosreciclador.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 */
public class MonthFragment extends Fragment {
    TextView txtPapelCartonCount,txtResiduosCount;
    private int totalResiduo;
    TextView txtPlasticoCount;
    TextView title;
    TextView txtBolsasCount;

    public MonthFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_month, container, false);
        title = view.findViewById(R.id.txtCuenta);
        txtPlasticoCount =view.findViewById(R.id.txtInputPlastico) ;
        txtPapelCartonCount =view.findViewById(R.id.txtInputPapelCarton) ;
        txtResiduosCount = view.findViewById(R.id.txtInputResiduos);
        txtBolsasCount = view.findViewById(R.id.txtInputBolsas);
        totalResiduo=0;
        getDataWeek();
        calcularBolsasTotales();
        txtResiduosCount.setText(Integer.toString(totalResiduo));
        Date date = new Date();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        title.setText("Resumen "+Integer.toString(year));
        return view;
    }

    public void getDataWeek() {
        dbHelper helper = new dbHelper(getActivity(),"Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
        int plasticoCount = 0,papelCartonCount = 0;
        double plasticoPeso = 0 ,papelCartonPeso = 0;

        Cursor CondominiosDB = db.rawQuery("select codigo from Condominio",null);
        while (CondominiosDB.moveToNext()) {
            Cursor f = db.rawQuery("select cantidad ,peso  from Contador where tendenciaTipo = 'bolsasMonth/' and productoTipo = 'Plastico' and nombrecondominio = " + CondominiosDB.getInt(0), null);

            if (f.moveToFirst()) {
                plasticoCount += (f.getInt(0));
                totalResiduo += f.getInt(0);

            }
            Cursor f3 = db.rawQuery("select cantidad ,peso  from Contador where tendenciaTipo = 'bolsasMonth/' and productoTipo = 'Papel' and nombrecondominio =  " + CondominiosDB.getInt(0), null);
            if (f3.moveToFirst()) {

                papelCartonCount += (f3.getInt(0));
                totalResiduo += f3.getInt(0);
            }
        }
        txtPlasticoCount.setText(Integer.toString(plasticoCount));
        txtPapelCartonCount.setText(Integer.toString(papelCartonCount));
    }


    private void calcularBolsasTotales()
    {
        dbHelper helper = new dbHelper(getActivity(),"Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
        int [] meses = {0,0,0,0,0,0,0,0,0,0,0,0};
        Cursor CondominiosDB = db.rawQuery("select codigo from Condominio",null);
        while (CondominiosDB.moveToNext()) {
            Cursor f = db.rawQuery("select enero,febrero,marzo,abril,mayo,junio,julio,agosto,setiembre,octubre,noviembre,diciembre from DatosAnuales where tipo = 'bolsa' and tipocondominio = " +CondominiosDB.getInt(0), null);
            if (f.moveToFirst()) {
                meses[0] += f.getInt(0);
                meses[1] += f.getInt(1);
                meses[2] += f.getInt(2);
                meses[3] += f.getInt(3);
                meses[4] += f.getInt(4);
                meses[5] += f.getInt(5);
                meses[6] += f.getInt(6);
                meses[7] += f.getInt(7);
                meses[8] += f.getInt(8);
                meses[9] += f.getInt(9);
                meses[10] += f.getInt(10);
                meses[11] += f.getInt(11);
            }
        }
        int bolsasTotales = 0;

        for(int i = 0;i<12 ; i++)
            bolsasTotales += meses[i];

        txtBolsasCount.setText(Integer.toString(bolsasTotales));




    }


}
