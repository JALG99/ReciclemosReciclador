package com.example.recicladorapp.Fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.recicladorapp.Adicionales.dbHelper;
import com.example.recicladorapp.R;
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


public class MonthCondominioFragment extends Fragment {
    int valor = 0;
    private int maximo = 0;
    TextView txtPapelCartonCount,txtPapelCartonPeso,txtResiduosCount,txtPesoResiduos;
    LineChart chartResiduos;
    private int totalResiduo,totalPeso;
    TextView txtPlasticoCount,txtPlasticoPeso;
    String[] axisDataMonth = {"Dom","Lun", "Mar", "Mie", "Jue", "Vie", "Sab"};

    public MonthCondominioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_month_condominio, container, false);
        txtPlasticoCount =view.findViewById(R.id.txtPlasticoCantidad) ;
        txtPlasticoPeso =view.findViewById(R.id.txtPlasticoPeso) ;
        txtPapelCartonCount =view.findViewById(R.id.txtPapelCartonCantidad) ;
        txtPapelCartonPeso =view.findViewById(R.id.txtPapelCartonPeso) ;
        chartResiduos = view.findViewById(R.id.chart2);
        txtResiduosCount = view.findViewById(R.id.txtCantBolsasHoy);
        txtPesoResiduos = view.findViewById(R.id.txtPesoBolsasHoy);
        totalPeso=0;
        totalResiduo=0;
        getDataWeek();
        graphicData();
        txtResiduosCount.setText(Integer.toString(totalResiduo));
        txtPesoResiduos.setText(Integer.toString(totalPeso/1000));
        return view;
    }
    public void getDataWeek() {
        dbHelper helper = new dbHelper(getActivity(),"Reciclador.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor f = db.rawQuery("select cantidad ,peso ,puntuacion from Contador where tendenciaTipo = 'bolsasMonth/' and productoTipo = 'Plastico' and nombrecondominio =  " + valor, null);

        if(f.moveToFirst()) {
            txtPlasticoCount.setText(Integer.toString(f.getInt(0)));
            txtPlasticoPeso.setText(Integer.toString((int)f.getDouble(1))+ " g");
            totalResiduo+=f.getInt(0);
            totalPeso+=f.getDouble(1);

        }
        Cursor f3 = db.rawQuery("select cantidad ,peso ,puntuacion from Contador where tendenciaTipo = 'bolsasMonth/' and productoTipo = 'Papel' and nombrecondominio =  " + valor, null);
        if(f3.moveToFirst()) {
            txtPapelCartonCount.setText(Integer.toString(f3.getInt(0)));
            txtPapelCartonPeso.setText(Integer.toString((int)f3.getDouble(1)) + " g");
            totalResiduo+=f3.getInt(0);
            totalPeso+=f3.getDouble(1);
        }
    }

    public void graphicData(){
        LineDataSet lineDataSetBolsas = new LineDataSet(dataResiduosValue(),"Data Set 1");
        lineDataSetBolsas.setColors(ColorTemplate.rgb("2e2a29"));
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSetBolsas);
        LineData data = new LineData(dataSets);

        YAxis rightAxis = chartResiduos.getAxisRight();
        YAxis leftAxis = chartResiduos.getAxisLeft();
        XAxis xAxis = chartResiduos.getXAxis();


        rightAxis.setEnabled(false);
        rightAxis.disableAxisLineDashedLine();

        leftAxis.setDrawGridLines(false);
        leftAxis.setLabelCount(5, true);

        if(maximo < 5)
            leftAxis.setAxisMaximum(5);
        else
            leftAxis.setAxisMaximum(maximo);

        leftAxis.setAxisMinimum(0);


        xAxis.setSpaceMax(0.5f);
        xAxis.setSpaceMin(0.5f);

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(8);


        chartResiduos.setDrawGridBackground(false);
        chartResiduos.setData(data);
        chartResiduos.invalidate();
        chartResiduos.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(axisDataMonth));
        chartResiduos.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) Math.floor(value));
            }
        });
        chartResiduos.getData().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) Math.floor(value));
            }
        });
        chartResiduos.getDescription().setEnabled(false);
        chartResiduos.getLegend().setEnabled(false);
    }

    private ArrayList<Entry> dataResiduosValue()
    {
        ArrayList<Entry> dataValues = new ArrayList<>();
        dbHelper helper = new dbHelper(getActivity(),"Reciclador.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
        int [] dias = {0,0,0,0,0,0,0};
        Cursor f = db.rawQuery("select lunes,martes,miercoles,jueves,viernes,sabado,domingo from DatosDiarios where tipo = 'residuos' and frecuenciatipo = 'bolsasMonth/' and tipocondominio = " + valor, null);
        if(f.moveToFirst()) {
            dias[0] += f.getInt(0);
            dias[1] += f.getInt(1);
            dias[2] += f.getInt(2);
            dias[3] += f.getInt(3);
            dias[4] += f.getInt(4);
            dias[5] += f.getInt(5);
            dias[6] += f.getInt(6);
        }
        dataValues.add(new Entry(0, dias[6]));
        for (int i = 0; i < 6; i++) {
            if (maximo <dias [i])
                maximo = dias[i];
            dataValues.add(new Entry(i + 1, dias[i]));
        }

        if (maximo < dias[6])
            maximo = dias[6];
        return dataValues;
    }
    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }


}
