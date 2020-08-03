package com.upc.reciclemosreciclador.Fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.upc.reciclemosreciclador.Adicionales.dbHelper;
import com.upc.reciclemosreciclador.Entities.Condominio;
import com.upc.reciclemosreciclador.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class MesFragment extends Fragment {
    private int maximo = 0;
    private int codigo_condominio = 0;
    private int maximoPuntos = 0;
    Map<Integer, Double> topResiduosTotal = new HashMap<>();
    Map<Integer, Double> topResiduosTotalPlastico = new HashMap<>();
    Map<Integer, Double> topResiduosTotalPapel = new HashMap<>();
    Map<Integer, Double> topResiduosMonthTotal = new HashMap<>();
    Map<Integer, Double> topResiduosMonthByTipo = new HashMap<>();
    LineChart chartResiduos;
    private int index = 0;
    PieChart chartPiePesos;
    String[] axisDataMonth = {"","En", "Febr", "Mar", "Abr", "May", "Jun", "Jul", "Ag", "Set", "Oct", "Nov", "Dic"};
    String[] axisDataByMonth = {" "," "," "};
    private ArrayList<String> CondominioList;
    private ArrayList<String> MonthList;
    private ArrayList<String> TipoList;
    private Spinner spinnerMonth;
    private Spinner spinnerTipo;
    private String valueMonth="Todos";
    private String valueTipo="Todos";
    private String valueResiduo="Todos";
    private ArrayList<Condominio> listaCondominios;
    private TextView txtKilosTotal;
    private TextView txtNombreResiduo1, txtNombreResiduo2, txtNombreResiduo3,txCondominios,txtUsuarios;
    private TextView txtPesoResiduo1, txtPesoResiduo2, txtPesoResiduo3;
    private TextView txtCantidad, txtPuntos;
    private double pesos;
    private long removalCounter = 0;
    private double kiloTotal=0;
    private int [] colorClassArray;
    private String [] nombreMesTotal_TipoTotal = {" "," "," "};
    private double [] pesoMesTotal_TipoTotal = {0,0,0};
    private String [] nombreMesTotal_Plastico = {" "," "," "};
    private double [] pesoMesTotal_Plastico = {0,0,0};
    private String [] nombreMesTotal_Papel = {" "," "," "};
    private double [] pesoMesTotal_Papel = {0,0,0};
    private String [] nombreMes_TipoTotal = {" "," "," "};
    private double [] pesoMes_TipoTotal = {0,0,0};
    private String [] nombreMes_Tipo = {" "," "," "};
    private double [] pesoMes_Tipo = {0,0,0};
    Map<Integer,Double> plasticos_pesos = new HashMap<>();
    dbHelper helper;
    Map<Integer,Double> papelcarton_pesos = new HashMap<>();
    private int [] colorList = {Color.GRAY, Color.rgb(45,52,151)};
    private String [] titleList ={"Plastico", "Papel/Carton"};
    private static final int VISIBLE_COUNT = 3;
    private Spinner spinnerCondominio;
    public String ValorCondominio = "";
    private int cantidad_condominios = 0;
    private int cantidad_usuarios = 0;
    public MesFragment() {
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_mes, container, false);
        spinnerCondominio = view.findViewById(R.id.spnCondominio);
        spinnerMonth = view.findViewById(R.id.spnMes);
        spinnerTipo = view.findViewById(R.id.spnResiduo);
        chartResiduos = view.findViewById(R.id.chartResiduo);
        chartPiePesos = view.findViewById(R.id.piechartResiduos);
        txtCantidad = view.findViewById(R.id.txtInputCantidad);
        txtKilosTotal = view.findViewById(R.id.txtInputKilos);
        txCondominios = view.findViewById(R.id.txtInputCondominios);
        txtUsuarios = view.findViewById(R.id.txtInputGeneradores);
        txtNombreResiduo1 = view.findViewById(R.id.txtNombreCondominio1);
        txtNombreResiduo2 = view.findViewById(R.id.txtNombreCondominio2);
        txtNombreResiduo3 = view.findViewById(R.id.txtNombreCondominio3);
        txtPesoResiduo1  = view.findViewById(R.id.txtPesoCondominio1);
        txtPesoResiduo2 = view.findViewById(R.id.txtPesoCondominio2);
        txtPesoResiduo3 = view.findViewById(R.id.txtPesoCondominio3);
        helper = new dbHelper(getActivity(),"Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor condominiosDB = db.rawQuery("select codigo, nombre from Condominio", null);
        while(condominiosDB.moveToNext()){
            cantidad_condominios += 1;
            topResiduosTotal.put(condominiosDB.getInt(0), 0.0);
            topResiduosMonthTotal.put(condominiosDB.getInt(0), 0.0);
            topResiduosTotalPapel.put(condominiosDB.getInt(0), 0.0);
            topResiduosTotalPlastico.put(condominiosDB.getInt(0), 0.0);
            topResiduosMonthByTipo.put(condominiosDB.getInt(0), 0.0);
            plasticos_pesos.put(condominiosDB.getInt(0), 0.0);
            papelcarton_pesos.put(condominiosDB.getInt(0), 0.0);
        }
        Cursor usuarioDB = db.rawQuery("select codigo, nombre from Usuario", null);
        while(usuarioDB.moveToNext()){
            cantidad_usuarios += 1;
        }
        txCondominios.setText(Integer.toString(cantidad_condominios));
        txtUsuarios.setText(Integer.toString(cantidad_usuarios));

        colorClassArray = new int[]{Color.GRAY, Color.rgb(45,52,151)};
        agregarMesesLista();
        agregarTiposLista();
        consultarListaCondominios();
        topCondominiosTotal();
        TopResiduosTotalByPapel();
        TopCondominiosTotalByPlastico();
        ArrayAdapter<CharSequence> adaptador = new ArrayAdapter(getActivity(),R.layout.custom_spinner,CondominioList);
        ArrayAdapter<CharSequence> adaptador2 = new ArrayAdapter(getActivity(),R.layout.custom_spinner,MonthList);
        ArrayAdapter<CharSequence> adaptador3 = new ArrayAdapter(getActivity(),R.layout.custom_spinner,TipoList);

        System.out.println(CondominioList);
        spinnerCondominio.setAdapter(adaptador);
        spinnerMonth.setAdapter(adaptador2);
        spinnerTipo.setAdapter(adaptador3);
        spinnerCondominio.setSelection(0);
        spinnerTipo.setSelection(0);
        spinnerMonth.setSelection(12);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                valueMonth = spinnerMonth.getSelectedItem().toString().toLowerCase();
                valueTipo = spinnerCondominio.getSelectedItem().toString();
                valueResiduo = spinnerTipo.getSelectedItem().toString();
                txtCantidad.setText(Integer.toString(getBolsasByMonth(valueMonth.toLowerCase())));
                pesos = datosPesoByMonth(valueMonth.toLowerCase());
                kiloTotal = pesos;
                txtKilosTotal.setText(Double.toString( kiloTotal/1000));
                graphicPieChart();
                graphicData();
                setTopCondominios();
            }



            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                valueMonth = spinnerMonth.getSelectedItem().toString().toLowerCase();
                valueTipo = spinnerCondominio.getSelectedItem().toString();
                valueResiduo = spinnerTipo.getSelectedItem().toString();
                txtCantidad.setText(Integer.toString(getBolsasByMonth(valueMonth.toLowerCase())));
                pesos = datosPesoByMonth(valueMonth.toLowerCase());
                kiloTotal = pesos;
                txtKilosTotal.setText(Double.toString( kiloTotal/1000));
                graphicPieChart();
                graphicData();
                setTopCondominios();
            }



            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerCondominio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                valueTipo = spinnerCondominio.getSelectedItem().toString();
                valueResiduo = spinnerTipo.getSelectedItem().toString();
                if(!valueTipo.equals("Todos"))
                    codigo_condominio = buscarCodigo(valueTipo);
                System.out.println("SELECIONADOOOOOOO NOMBREEE Y CODIGOOOO");
                System.out.println(valueTipo);
                System.out.println(buscarCodigo(valueTipo));
                txtCantidad.setText(Integer.toString(getBolsasByMonth(valueMonth.toLowerCase())));
                pesos = datosPesoByMonth(valueMonth.toLowerCase());
                kiloTotal = pesos;
                txtKilosTotal.setText(Double.toString( kiloTotal/1000));


                graphicPieChart();
                graphicData();
                setTopCondominios();

            }



            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void graphicPieChart(){
        PieDataSet pieDataSet;
        if( (valueMonth.equals("Todos") ||valueMonth.equals("todos")) && valueTipo.equals("Todos") && valueResiduo.equals("Todos") ) {
            pieDataSet = new PieDataSet(dataPesosValuePieChart(), "Data Set 1");
        }
        else {
            if(  valueTipo.equals("Todos") && valueResiduo.equals("Todos") ) {
                pieDataSet = new PieDataSet(dataPesosValueByMonthPieChart(valueMonth), "Data Set 1");
            }else{
                if(  (valueMonth.equals("Todos") || valueMonth.equals("todos")) && valueResiduo.equals("Todos")){
                    pieDataSet = new PieDataSet(dataPesosValuePieChartbyTipo(), "Data Set 1");

                }else{
                    if( valueResiduo.equals("Todos")) {
                        pieDataSet = new PieDataSet(dataPesosValueByMonthPieChartandTipo(valueMonth, valueTipo), "Data Set 1");
                    }else{
                        if((valueMonth.equals("Todos") ||valueMonth.equals("todos")) && valueTipo.equals("Todos")){
                            pieDataSet = new PieDataSet(dataPesosValuePieChartbyResiduo(), "Data Set 1");
                        }
                        else{
                            if(valueTipo.equals("Todos")){
                                pieDataSet = new PieDataSet(dataPesosValueByMonthPieChartandResiduo(valueMonth), "Data Set 1");
                            }else{
                                if(valueMonth.equals("Todos") || valueMonth.equals("todos")){
                                    pieDataSet = new PieDataSet(dataPesosValuePieChartbyTipoAndResiduo(), "Data Set 1");

                                }else{
                                    pieDataSet = new PieDataSet(dataPesosValueByMonthPieChartandTipoandResiduo(valueMonth, valueTipo), "Data Set 1");

                                }
                            }
                        }
                    }

                }
            }
        }
        pieDataSet.setColors(colorClassArray);
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(20f);
        pieData.setValueTextColor(Color.WHITE);
        chartPiePesos.setData(pieData);
        Legend legend = chartPiePesos.getLegend();
        List<LegendEntry> entries = new ArrayList<>();
        for (int i = 0; i < titleList.length; i++) {
            LegendEntry entry = new LegendEntry();
            entry.formColor = colorList[i];
            entry.label = titleList[i];
            entries.add(entry);
        }
        legend.setCustom(entries);
        chartPiePesos.getData().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                DecimalFormat f = new DecimalFormat("##.0");
                return String.valueOf(f.format(value));
            }
        });
        chartPiePesos.getDescription().setEnabled(false);
        chartPiePesos.notifyDataSetChanged();
        chartPiePesos.invalidate();

    }

    public void graphicData(){
        LineDataSet lineDataSetBolsas;
        if( (valueMonth.equals("Todos") ||valueMonth.equals("todos")) && valueTipo.equals("Todos") && valueResiduo.equals("Todos") ) {
            lineDataSetBolsas = new LineDataSet(dataResiduosValue(), "Data Set 1");
        }
        else {
            if(  valueTipo.equals("Todos") && valueResiduo.equals("Todos") ) {
                lineDataSetBolsas = new LineDataSet(dataResiduosValueByMonth(valueMonth),"Data Set 1");

            }else{
                if(  (valueMonth.equals("Todos") || valueMonth.equals("todos")) && valueResiduo.equals("Todos")){
                    lineDataSetBolsas = new LineDataSet(dataResiduosValueByCondominio(),"Data Set 1");

                }else{
                    if( valueResiduo.equals("Todos")) {
                        lineDataSetBolsas = new LineDataSet(dataResiduosValueByMonthAndTipo(valueMonth),"Data Set 1");
                    }else{
                        if((valueMonth.equals("Todos") ||valueMonth.equals("todos")) && valueTipo.equals("Todos")){
                            lineDataSetBolsas = new LineDataSet(dataResiduosValueandResiduo(), "Data Set 1");
                        }
                        else{
                            if(valueTipo.equals("Todos")){
                                lineDataSetBolsas = new LineDataSet(dataResiduosValueByMonthandResiduo(valueMonth), "Data Set 1");
                            }else{
                                if(valueMonth.equals("Todos") || valueMonth.equals("todos")){
                                    lineDataSetBolsas = new LineDataSet(dataResiduosValueByCondominioandResiduo(), "Data Set 1");

                                }else{
                                    lineDataSetBolsas = new LineDataSet(dataResiduosValueByMonthAndTipoandResiduo(valueMonth), "Data Set 1");

                                }
                            }
                        }
                    }

                }
            }
        }
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
        leftAxis.setLabelCount(10, true);
        if(maximo < 5)
            leftAxis.setAxisMaximum(5);
        else {
            if( maximo % 5 != 0)
                leftAxis.setAxisMaximum(maximo + (100 - (maximo % 5)));
            else
                leftAxis.setAxisMaximum(maximo+50);
        }

        leftAxis.setAxisMinimum(0);

        if(valueMonth.equals("Todos") || valueMonth.equals("todos")){
            xAxis.setSpaceMax(0.5f);
            xAxis.setSpaceMin(0.5f);
            xAxis.setTextSize(8);
            xAxis.setLabelCount(12);
        }
        else{
            xAxis.setSpaceMax(0);
            xAxis.setSpaceMin(0);
            xAxis.setTextSize(13);
            xAxis.setLabelCount(1);
        }

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        chartResiduos.setExtraBottomOffset(10);
        chartResiduos.setDrawGridBackground(false);
        chartResiduos.setData(data);
        chartResiduos.invalidate();
        chartResiduos.setScaleEnabled(false);
        if(valueMonth.equals("Todos") || valueMonth.equals("todos"))
            chartResiduos.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(axisDataMonth));
        else {

            axisDataByMonth[1] = valueMonth;
            chartResiduos.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(axisDataByMonth));
        }
        chartResiduos.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) Math.floor(value));
            }
        });
        chartResiduos.getData().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if(value > 0)
                    return String.valueOf((int) Math.floor(value));
                else
                    return "";
            }
        });
        chartResiduos.getDescription().setEnabled(false);
        chartResiduos.getLegend().setEnabled(false);
        chartResiduos.getData().setValueTextSize(10);
    }


    private ArrayList<Entry> dataResiduosValue()
    {
        int [] residuos= {0,0,0,0,0,0,0,0,0,0,0,0};
        ArrayList<Entry> dataValues = new ArrayList<>();
        dbHelper helper = new dbHelper(getActivity(),"Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor f = db.rawQuery("select enero,febrero,marzo,abril,mayo,junio,julio,agosto,setiembre,octubre,noviembre,diciembre from DatosAnuales where tipo = 'pesos'", null);
        while(f.moveToNext()) {
            for (int i = 0; i <= 11; i++) {
                residuos[i] += f.getInt(i);
            }
        }
        for (int i = 0; i <= 11; i++) {
            if(maximo< residuos[i]/1000)
                maximo = residuos[i]/1000;
            dataValues.add(new Entry(i+1, residuos[i]/1000));
        }
        return dataValues;
    }

    private ArrayList<Entry> dataResiduosValueandResiduo()
    {
        int [] residuos= {0,0,0,0,0,0,0,0,0,0,0,0};
        ArrayList<Entry> dataValues = new ArrayList<>();
        dbHelper helper = new dbHelper(getActivity(),"Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor f = db.rawQuery("select enero,febrero,marzo,abril,mayo,junio,julio,agosto,setiembre,octubre,noviembre,diciembre from DatosAnuales where tipo = '"+valueResiduo+"'", null);
        while(f.moveToNext()) {
            for (int i = 0; i <= 11; i++) {
                residuos[i] += f.getInt(i);
            }
        }
        for (int i = 0; i <= 11; i++) {
            if(maximo< residuos[i]/1000)
                maximo = residuos[i]/1000;
            dataValues.add(new Entry(i+1, residuos[i]/1000));
        }
        return dataValues;
    }

    private ArrayList<Entry> dataResiduosValueByCondominio()
    {
        ArrayList<Entry> dataValues = new ArrayList<>();
        dbHelper helper = new dbHelper(getActivity(),"Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor f = db.rawQuery("select enero,febrero,marzo,abril,mayo,junio,julio,agosto,setiembre,octubre,noviembre,diciembre from DatosAnuales where tipo = 'pesos' and tipocondominio = "+ codigo_condominio, null);
        if(f.moveToFirst()) {
            for (int i = 0; i <= 11; i++) {
                if(maximo< f.getInt(i)/1000)
                    maximo = f.getInt(i)/1000;
                dataValues.add(new Entry(i+1,f.getInt(i)/1000));
            }
        }
        return dataValues;
    }

    private ArrayList<Entry> dataResiduosValueByCondominioandResiduo()
    {
        ArrayList<Entry> dataValues = new ArrayList<>();
        dbHelper helper = new dbHelper(getActivity(),"Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor f = db.rawQuery("select enero,febrero,marzo,abril,mayo,junio,julio,agosto,setiembre,octubre,noviembre,diciembre from DatosAnuales where tipo = '"+valueResiduo+"' and tipocondominio = "+ codigo_condominio, null);
        if(f.moveToFirst()) {
            for (int i = 0; i <= 11; i++) {
                if(maximo< f.getInt(i)/1000)
                    maximo = f.getInt(i)/1000;
                dataValues.add(new Entry(i+1,f.getInt(i)/1000));
            }
        }
        return dataValues;
    }


    private ArrayList<Entry> dataResiduosValueByMonth(String month)
    {
        int total = 0;
        ArrayList<Entry> dataValuesAdd = new ArrayList<>();
        dbHelper helper = new dbHelper(getActivity(),"Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor f = db.rawQuery("select " + month + " from DatosAnuales where tipo = 'pesos'", null);
        while(f.moveToNext()) {
            total += f.getInt(0);
        }

        if(maximo< total/1000)
            maximo = total/1000;
        dataValuesAdd.add(new Entry(0,0));
        dataValuesAdd.add(new Entry(1,total/1000));
        dataValuesAdd.add(new Entry(2,0));
        return dataValuesAdd;
    }

    private ArrayList<Entry> dataResiduosValueByMonthandResiduo(String month)
    {
        int total = 0;
        ArrayList<Entry> dataValuesAdd = new ArrayList<>();
        dbHelper helper = new dbHelper(getActivity(),"Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor f = db.rawQuery("select " + month + " from DatosAnuales where tipo = '"+valueResiduo+"'", null);
        while(f.moveToNext()) {
            total += f.getInt(0);
        }

        if(maximo< total/1000)
            maximo = total/1000;
        dataValuesAdd.add(new Entry(0,0));
        dataValuesAdd.add(new Entry(1,total/1000));
        dataValuesAdd.add(new Entry(2,0));
        return dataValuesAdd;
    }

    private ArrayList<Entry> dataResiduosValueByMonthAndTipo(String month)
    {
        ArrayList<Entry> dataValuesAdd = new ArrayList<>();
        dbHelper helper = new dbHelper(getActivity(),"Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor f = db.rawQuery("select " + month + " from DatosAnuales where  tipo = 'pesos' and tipocondominio = "+ codigo_condominio, null);
        if(f.moveToFirst()) {
            if(maximo< f.getInt(0)/1000)
                maximo = f.getInt(0)/1000;
            dataValuesAdd.add(new Entry(0,0));
            dataValuesAdd.add(new Entry(1,f.getInt(0)/1000));
            dataValuesAdd.add(new Entry(2,0));
        }
        return dataValuesAdd;
    }

    private ArrayList<Entry> dataResiduosValueByMonthAndTipoandResiduo(String month)
    {
        ArrayList<Entry> dataValuesAdd = new ArrayList<>();
        dbHelper helper = new dbHelper(getActivity(),"Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor f = db.rawQuery("select " + month + " from DatosAnuales where  tipo = '"+valueResiduo+"' and tipocondominio = "+ codigo_condominio, null);
        if(f.moveToFirst()) {
            if(maximo< f.getInt(0)/1000)
                maximo = f.getInt(0)/1000;
            dataValuesAdd.add(new Entry(0,0));
            dataValuesAdd.add(new Entry(1,f.getInt(0)/1000));
            dataValuesAdd.add(new Entry(2,0));
        }
        return dataValuesAdd;
    }

    private ArrayList<PieEntry> dataPesosValuePieChart()
    {
        float plasticos_pesos= 0;
        float papelcarton_pesos = 0;
        ArrayList<PieEntry> dataValues = new ArrayList<>();
        dbHelper helper = new dbHelper(getActivity(),"Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor f = db.rawQuery("select enero,febrero,marzo,abril,mayo,junio,julio,agosto,setiembre,octubre,noviembre,diciembre,tipocondominio from DatosAnuales where tipo = 'Plástico'", null);
        while(f.moveToNext()) {
            for (int i = 0; i <= 11; i++) {
                plasticos_pesos += f.getInt(i);
            }
        }

        Cursor f2 = db.rawQuery("select enero,febrero,marzo,abril,mayo,junio,julio,agosto,setiembre,octubre,noviembre,diciembre from DatosAnuales where tipo = 'Papel/Cartón'", null);
        while(f2.moveToNext()) {
            for (int i = 0; i <= 11; i++) {
                papelcarton_pesos += f2.getInt(i);
            }
        }
        plasticos_pesos = plasticos_pesos/1000;
        papelcarton_pesos = papelcarton_pesos/1000;

        float porcentaje_plastico = plasticos_pesos*100/(plasticos_pesos + papelcarton_pesos);
        float porcentaje_papel = papelcarton_pesos*100/(plasticos_pesos + papelcarton_pesos);
        if(plasticos_pesos + papelcarton_pesos > 0 ) {
            dataValues.add(new PieEntry(plasticos_pesos, (float) (Math.round(porcentaje_plastico * 10d) / 10d) + "%"));
            dataValues.add(new PieEntry(papelcarton_pesos,(float) (Math.round(porcentaje_papel * 10d) / 10d) + "%"));
        }else{
            dataValues.add(new PieEntry(plasticos_pesos, Integer.toString(0) + "%"));
            dataValues.add(new PieEntry(papelcarton_pesos, Integer.toString(0) + "%"));
        }
        return dataValues;
    }

    private ArrayList<PieEntry> dataPesosValuePieChartbyResiduo()
    {
        float plasticos_pesos= 0;
        float papelcarton_pesos = 0;
        ArrayList<PieEntry> dataValues = new ArrayList<>();
        dbHelper helper = new dbHelper(getActivity(),"Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
        if(valueResiduo.equals("Plástico")) {

            Cursor f = db.rawQuery("select enero,febrero,marzo,abril,mayo,junio,julio,agosto,setiembre,octubre,noviembre,diciembre,tipocondominio from DatosAnuales where tipo = 'Plástico'", null);
            while (f.moveToNext()) {
                for (int i = 0; i <= 11; i++) {
                    plasticos_pesos += f.getInt(i);
                }
            }
        }

        if(valueResiduo.equals("Papel/Cartón")) {
            Cursor f2 = db.rawQuery("select enero,febrero,marzo,abril,mayo,junio,julio,agosto,setiembre,octubre,noviembre,diciembre from DatosAnuales where tipo = 'Papel/Cartón'", null);
            while (f2.moveToNext()) {
                for (int i = 0; i <= 11; i++) {
                    papelcarton_pesos += f2.getInt(i);
                }
            }
        }
        plasticos_pesos = plasticos_pesos/1000;
        papelcarton_pesos = papelcarton_pesos/1000;

        float porcentaje_plastico = plasticos_pesos*100/(plasticos_pesos + papelcarton_pesos);
        float porcentaje_papel = papelcarton_pesos*100/(plasticos_pesos + papelcarton_pesos);
        if(plasticos_pesos + papelcarton_pesos > 0 ) {
            if(valueResiduo.equals("Plástico"))
                dataValues.add(new PieEntry(plasticos_pesos, (float) (Math.round(porcentaje_plastico * 10d) / 10d) + "%"));
            if(valueResiduo.equals("Papel/Cartón"))
                dataValues.add(new PieEntry(papelcarton_pesos,(float) (Math.round(porcentaje_papel * 10d) / 10d) + "%"));
        }else{
            if(valueResiduo.equals("Plástico"))
                dataValues.add(new PieEntry(plasticos_pesos, Integer.toString(0) + "%"));
            if(valueResiduo.equals("Papel/Cartón"))
                dataValues.add(new PieEntry(papelcarton_pesos, Integer.toString(0) + "%"));
        }
        return dataValues;
    }

    private ArrayList<PieEntry> dataPesosValuePieChartbyTipo()
    {
        float plasticos_pesos= 0;
        float papelcarton_pesos = 0;
        ArrayList<PieEntry> dataValues = new ArrayList<>();
        dbHelper helper = new dbHelper(getActivity(),"Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();

            Cursor f = db.rawQuery("select enero,febrero,marzo,abril,mayo,junio,julio,agosto,setiembre,octubre,noviembre,diciembre from DatosAnuales where tipo = 'Plástico' and tipocondominio = "+ codigo_condominio, null);
            if (f.moveToFirst()) {
                for (int i = 0; i <= 11; i++) {
                    plasticos_pesos += f.getInt(i);
                }
            }
            Cursor f2 = db.rawQuery("select enero,febrero,marzo,abril,mayo,junio,julio,agosto,setiembre,octubre,noviembre,diciembre from DatosAnuales where tipo = 'Papel/Cartón' and tipocondominio = "+ codigo_condominio, null);
            if (f2.moveToFirst()) {
                for (int i = 0; i <= 11; i++) {
                    papelcarton_pesos += f2.getInt(i);
                }
            }

            System.out.println(plasticos_pesos);
            System.out.println(papelcarton_pesos);
        plasticos_pesos = plasticos_pesos/1000;
        papelcarton_pesos = papelcarton_pesos/1000;

        float porcentaje_plastico = plasticos_pesos*100/(plasticos_pesos + papelcarton_pesos);
        float porcentaje_papel = papelcarton_pesos*100/(plasticos_pesos + papelcarton_pesos);
        if(plasticos_pesos + papelcarton_pesos > 0 ) {
            dataValues.add(new PieEntry(plasticos_pesos, (float) (Math.round(porcentaje_plastico * 10d) / 10d) + "%"));
            dataValues.add(new PieEntry(papelcarton_pesos,(float) (Math.round(porcentaje_papel * 10d) / 10d) + "%"));
        }else{
            dataValues.add(new PieEntry(plasticos_pesos, Integer.toString(0) + "%"));
            dataValues.add(new PieEntry(papelcarton_pesos, Integer.toString(0) + "%"));
        }
        return dataValues;
    }

    private ArrayList<PieEntry> dataPesosValuePieChartbyTipoAndResiduo()
    {
        float plasticos_pesos= 0;
        float papelcarton_pesos = 0;
        ArrayList<PieEntry> dataValues = new ArrayList<>();
        dbHelper helper = new dbHelper(getActivity(),"Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
        if(valueResiduo.equals("Plástico")) {
            Cursor f = db.rawQuery("select enero,febrero,marzo,abril,mayo,junio,julio,agosto,setiembre,octubre,noviembre,diciembre from DatosAnuales where tipo = 'Plástico' and tipocondominio = " + codigo_condominio, null);
            if (f.moveToFirst()) {
                for (int i = 0; i <= 11; i++) {
                    plasticos_pesos += f.getInt(i);
                }
            }
        }

        if(valueResiduo.equals("Papel/Cartón")) {
            Cursor f2 = db.rawQuery("select enero,febrero,marzo,abril,mayo,junio,julio,agosto,setiembre,octubre,noviembre,diciembre from DatosAnuales where tipo = 'Papel/Cartón' and tipocondominio = " + codigo_condominio, null);
            if (f2.moveToFirst()) {
                for (int i = 0; i <= 11; i++) {
                    papelcarton_pesos += f2.getInt(i);
                }
            }
        }

        System.out.println(plasticos_pesos);
        System.out.println(papelcarton_pesos);
        plasticos_pesos = plasticos_pesos/1000;
        papelcarton_pesos = papelcarton_pesos/1000;

        float porcentaje_plastico = plasticos_pesos*100/(plasticos_pesos + papelcarton_pesos);
        float porcentaje_papel = papelcarton_pesos*100/(plasticos_pesos + papelcarton_pesos);
        if(plasticos_pesos + papelcarton_pesos > 0 ) {
            if(valueResiduo.equals("Plástico"))
                dataValues.add(new PieEntry(plasticos_pesos, (float) (Math.round(porcentaje_plastico * 10d) / 10d) + "%"));
            if(valueResiduo.equals("Papel/Cartón"))
                dataValues.add(new PieEntry(papelcarton_pesos,(float) (Math.round(porcentaje_papel * 10d) / 10d) + "%"));
        }else{
            if(valueResiduo.equals("Plástico"))
                dataValues.add(new PieEntry(plasticos_pesos, Integer.toString(0) + "%"));
            if(valueResiduo.equals("Papel/Cartón"))
                dataValues.add(new PieEntry(papelcarton_pesos, Integer.toString(0) + "%"));
        }
        return dataValues;
    }

    private ArrayList<PieEntry> dataPesosValueByMonthPieChart(String month)
    {
        float plasticos_pesos= 0;
        float papelcarton_pesos = 0;
        ArrayList<PieEntry> dataValuesAdd = new ArrayList<>();
        dbHelper helper = new dbHelper(getActivity(),"Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor f = db.rawQuery("select " + month + " from DatosAnuales where tipo = 'Plástico'", null);
        while(f.moveToNext()) {
            plasticos_pesos += f.getInt(0);
        }

        Cursor f2 = db.rawQuery("select " + month + " from DatosAnuales where tipo = 'Papel/Cartón'", null);
        while (f2.moveToNext()) {
            papelcarton_pesos += f2.getInt(0);
        }

        plasticos_pesos = plasticos_pesos/1000;
        papelcarton_pesos = papelcarton_pesos/1000;

        float porcentaje_plastico = plasticos_pesos*100/(plasticos_pesos + papelcarton_pesos);
        float porcentaje_papel = papelcarton_pesos*100/(plasticos_pesos + papelcarton_pesos);
        if(plasticos_pesos + papelcarton_pesos > 0 ) {
            dataValuesAdd.add(new PieEntry(plasticos_pesos, (float) (Math.round(porcentaje_plastico * 10d) / 10d) + "%"));
            dataValuesAdd.add(new PieEntry(papelcarton_pesos,(float) (Math.round(porcentaje_papel * 10d) / 10d) + "%"));
        }else{
            dataValuesAdd.add(new PieEntry(plasticos_pesos, Integer.toString(0) + "%"));
            dataValuesAdd.add(new PieEntry(papelcarton_pesos, Integer.toString(0) + "%"));
        }
        return dataValuesAdd;
    }

    private ArrayList<PieEntry> dataPesosValueByMonthPieChartandResiduo(String month)
    {
        float plasticos_pesos= 0;
        float papelcarton_pesos = 0;
        ArrayList<PieEntry> dataValuesAdd = new ArrayList<>();
        dbHelper helper = new dbHelper(getActivity(),"Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();

        if(valueResiduo.equals("Plástico")) {
            Cursor f = db.rawQuery("select " + month + " from DatosAnuales where tipo = 'Plástico'", null);
            while (f.moveToNext()) {
                plasticos_pesos += f.getInt(0);
            }
        }

        if(valueResiduo.equals("Papel/Cartón")) {
            Cursor f2 = db.rawQuery("select " + month + " from DatosAnuales where tipo = 'Papel/Cartón'", null);
            while (f2.moveToNext()) {
                papelcarton_pesos += f2.getInt(0);
            }
        }

        plasticos_pesos = plasticos_pesos/1000;
        papelcarton_pesos = papelcarton_pesos/1000;

        float porcentaje_plastico = plasticos_pesos*100/(plasticos_pesos + papelcarton_pesos);
        float porcentaje_papel = papelcarton_pesos*100/(plasticos_pesos + papelcarton_pesos);
        if(plasticos_pesos + papelcarton_pesos > 0 ) {
            if(valueResiduo.equals("Plástico"))
                dataValuesAdd.add(new PieEntry(plasticos_pesos, (float) (Math.round(porcentaje_plastico * 10d) / 10d) + "%"));
            if(valueResiduo.equals("Papel/Cartón"))
                dataValuesAdd.add(new PieEntry(papelcarton_pesos,(float) (Math.round(porcentaje_papel * 10d) / 10d) + "%"));
        }else{
            if(valueResiduo.equals("Plástico"))
                dataValuesAdd.add(new PieEntry(plasticos_pesos, Integer.toString(0) + "%"));
            if(valueResiduo.equals("Papel/Cartón"))
                dataValuesAdd.add(new PieEntry(papelcarton_pesos, Integer.toString(0) + "%"));
        }
        return dataValuesAdd;
    }

    private ArrayList<PieEntry> dataPesosValueByMonthPieChartandTipo(String month , String tipo)
    {
        float plasticos_pesos= 0;
        float papelcarton_pesos = 0;
        ArrayList<PieEntry> dataValuesAdd = new ArrayList<>();
        dbHelper helper = new dbHelper(getActivity(),"Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
            Cursor f = db.rawQuery("select " + month + " from DatosAnuales where tipo = 'Plástico' and tipocondominio = "+ codigo_condominio, null);
            if (f.moveToFirst()) {
                plasticos_pesos += f.getInt(0);
            }

            Cursor f2 = db.rawQuery("select " + month + " from DatosAnuales where tipo = 'Papel/Cartón' and tipocondominio = "+ codigo_condominio, null);
            if (f2.moveToFirst()) {
                papelcarton_pesos += f2.getInt(0);
            }

        plasticos_pesos = plasticos_pesos/1000;
        papelcarton_pesos = papelcarton_pesos/1000;

        float porcentaje_plastico = plasticos_pesos*100/(plasticos_pesos + papelcarton_pesos);
        float porcentaje_papel = papelcarton_pesos*100/(plasticos_pesos + papelcarton_pesos);
        if(plasticos_pesos + papelcarton_pesos > 0 ) {
            dataValuesAdd.add(new PieEntry(plasticos_pesos, (float) (Math.round(porcentaje_plastico * 10d) / 10d) + "%"));
            dataValuesAdd.add(new PieEntry(papelcarton_pesos,(float) (Math.round(porcentaje_papel * 10d) / 10d) + "%"));
        }else{
            dataValuesAdd.add(new PieEntry(plasticos_pesos, Integer.toString(0) + "%"));
            dataValuesAdd.add(new PieEntry(papelcarton_pesos, Integer.toString(0) + "%"));
        }
        return dataValuesAdd;
    }

    private ArrayList<PieEntry> dataPesosValueByMonthPieChartandTipoandResiduo(String month , String tipo)
    {
        float plasticos_pesos= 0;
        float papelcarton_pesos = 0;
        ArrayList<PieEntry> dataValuesAdd = new ArrayList<>();
        dbHelper helper = new dbHelper(getActivity(),"Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();

        if(valueResiduo.equals("Plástico")) {
            Cursor f = db.rawQuery("select " + month + " from DatosAnuales where tipo = 'Plástico' and tipocondominio = " + codigo_condominio, null);
            if (f.moveToFirst()) {
                plasticos_pesos += f.getInt(0);
            }
        }
        if(valueResiduo.equals("Papel/Cartón")) {
            Cursor f2 = db.rawQuery("select " + month + " from DatosAnuales where tipo = 'Papel/Cartón' and tipocondominio = " + codigo_condominio, null);
            if (f2.moveToFirst()) {
                papelcarton_pesos += f2.getInt(0);
            }
        }

        plasticos_pesos = plasticos_pesos/1000;
        papelcarton_pesos = papelcarton_pesos/1000;

        float porcentaje_plastico = plasticos_pesos*100/(plasticos_pesos + papelcarton_pesos);
        float porcentaje_papel = papelcarton_pesos*100/(plasticos_pesos + papelcarton_pesos);
        if(plasticos_pesos + papelcarton_pesos > 0 ) {
            if(valueResiduo.equals("Plástico"))
                dataValuesAdd.add(new PieEntry(plasticos_pesos, (float) (Math.round(porcentaje_plastico * 10d) / 10d) + "%"));
            if(valueResiduo.equals("Papel/Cartón"))
                dataValuesAdd.add(new PieEntry(papelcarton_pesos,(float) (Math.round(porcentaje_papel * 10d) / 10d) + "%"));
        }else{
            if(valueResiduo.equals("Plástico"))
                dataValuesAdd.add(new PieEntry(plasticos_pesos, Integer.toString(0) + "%"));
            if(valueResiduo.equals("Papel/Cartón"))
                dataValuesAdd.add(new PieEntry(papelcarton_pesos, Integer.toString(0) + "%"));
        }
        return dataValuesAdd;
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

    private void obtenerLista(){
        CondominioList = new ArrayList<String>();
        CondominioList.add("Todos");
        for (int i = 0; i< listaCondominios.size() ; i++)
        {
            CondominioList.add(listaCondominios.get(i).getNombre());
        }
    }

    private int buscarCodigo(String nombre){
        for (int i=0;i< listaCondominios.size();i++){
            if(listaCondominios.get(i).getNombre().equals(nombre))
                return listaCondominios.get(i).getCodigo();
        }
        return 0;
    }


    private void agregarMesesLista(){
        MonthList =  new ArrayList<String>();
        MonthList.add("Enero");
        MonthList.add("Febrero");
        MonthList.add("Marzo");
        MonthList.add("Abril");
        MonthList.add("Mayo");
        MonthList.add("Junio");
        MonthList.add("Julio");
        MonthList.add("Agosto");
        MonthList.add("Setiembre");
        MonthList.add("Octubre");
        MonthList.add("Noviembre");
        MonthList.add("Diciembre");
        MonthList.add("Todos");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void topCondominiosTotal(){
        dbHelper helper = new dbHelper(getActivity(), "Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor f = db.rawQuery("select peso_prim,mes,codigo_producto_prim, peso_sec, codigo_producto_sec, peso_ter, codigo_producto_ter from Contador  where tipo_producto = 'total' ", null);
        while (f.moveToNext()) {
            System.out.println(f.getInt(2));
            System.out.println(f.getDouble(0));
            System.out.println(f.getInt(4));
            System.out.println(f.getDouble(3));
            System.out.println(f.getInt(6));
            System.out.println(f.getDouble(5));
            if(f.getInt(2) != 0)
                topResiduosTotal.put(f.getInt(2), topResiduosTotal.get(f.getInt(2)) + f.getInt(0) );
            if(f.getInt(4) != 0)
                topResiduosTotal.put(f.getInt(4), topResiduosTotal.get(f.getInt(4)) + f.getInt(3) );
            if(f.getInt(6) != 0)
                topResiduosTotal.put(f.getInt(6), topResiduosTotal.get(f.getInt(6)) + f.getInt(5) );
        }
        topResiduosTotal = sortByValue(topResiduosTotal);
        System.out.println(topResiduosTotal);
        ArrayList<Integer> datos = insertar_tops(topResiduosTotal);
        System.out.println(datos);
        if(cantidad_condominios > 3) {
            for (int i = 0; i < 3; i++) {
                if(datos.get(i) != 0) {
                    Cursor f1 = db.rawQuery("select nombre from Condominio where codigo = " + datos.get(i), null);
                    if (f1.moveToFirst()) {
                        nombreMesTotal_TipoTotal[i] = f1.getString(0);
                        pesoMesTotal_TipoTotal[i] = topResiduosTotal.get(datos.get(i));
                    }
                }
            }
        }else{
            for (int i = 0; i < cantidad_condominios; i++) {
                if(datos.get(i) != 0) {
                    Cursor f1 = db.rawQuery("select nombre from Condominio where codigo = " + datos.get(i), null);
                    if (f1.moveToFirst()) {
                        nombreMesTotal_TipoTotal[i] = f1.getString(0);
                        pesoMesTotal_TipoTotal[i] = topResiduosTotal.get(datos.get(i));
                    }
                }
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void TopCondominiosTotalByPlastico(){
        dbHelper helper = new dbHelper(getActivity(), "Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor f = db.rawQuery("select peso_prim,mes,codigo_producto_prim,tipo_producto, peso_sec, codigo_producto_sec, peso_ter, codigo_producto_ter from Contador where tipo_producto = 'Plástico'", null);
        while (f.moveToNext()) {
            if(f.getInt(2) != 0)
                topResiduosTotalPlastico.put(f.getInt(2), topResiduosTotalPlastico.get(f.getInt(2)) + f.getInt(0) );
            if(f.getInt(5) != 0)
                topResiduosTotalPlastico.put(f.getInt(5), topResiduosTotalPlastico.get(f.getInt(5)) + f.getInt(4) );
            if(f.getInt(7) != 0)
                topResiduosTotalPlastico.put(f.getInt(7), topResiduosTotalPlastico.get(f.getInt(7)) + f.getInt(6) );
        }
        topResiduosTotalPlastico = sortByValue(topResiduosTotalPlastico);
        ArrayList<Integer> datos = insertar_tops(topResiduosTotalPlastico);
        if(cantidad_condominios > 3) {
            for (int i = 0; i < 3; i++) {
                if(datos.get(i) != 0) {
                    Cursor f1 = db.rawQuery("select nombre from Condominio where codigo = " + datos.get(i), null);
                    if (f1.moveToFirst()) {
                        nombreMesTotal_Plastico[i] = f1.getString(0);
                        pesoMesTotal_Plastico[i] = topResiduosTotalPlastico.get(datos.get(i));
                    }
                }
            }
        }else{
            for (int i = 0; i < cantidad_condominios; i++) {
                if(datos.get(i) != 0) {
                    Cursor f1 = db.rawQuery("select nombre from Condominio where codigo = " + datos.get(i), null);
                    if (f1.moveToFirst()) {
                        nombreMesTotal_Plastico[i] = f1.getString(0);
                        pesoMesTotal_Plastico[i] = topResiduosTotalPlastico.get(datos.get(i));
                    }
                }
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void TopResiduosTotalByPapel(){
        dbHelper helper = new dbHelper(getActivity(), "Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor f = db.rawQuery("select peso_prim,mes,codigo_producto_prim,tipo_producto, peso_sec, codigo_producto_sec, peso_ter, codigo_producto_ter from Contador where tipo_producto = 'Papel/Cartón'", null);
        while (f.moveToNext()) {
            if(f.getInt(2) != 0)
                topResiduosTotalPapel.put(f.getInt(2), topResiduosTotalPapel.get(f.getInt(2)) + f.getInt(0) );
            if(f.getInt(5) != 0)
                topResiduosTotalPapel.put(f.getInt(5), topResiduosTotalPapel.get(f.getInt(5)) + f.getInt(4) );
            if(f.getInt(7) != 0)
                topResiduosTotalPapel.put(f.getInt(7), topResiduosTotalPapel.get(f.getInt(7)) + f.getInt(6) );
        }
        topResiduosTotalPapel = sortByValue(topResiduosTotalPapel);
        ArrayList<Integer> datos = insertar_tops(topResiduosTotalPapel);
        if(cantidad_condominios > 3) {
            for (int i = 0; i < 3; i++) {
                if(datos.get(i) != 0) {
                    Cursor f1 = db.rawQuery("select nombre from Condominio where codigo = " + datos.get(i), null);
                    if (f1.moveToFirst()) {
                        nombreMesTotal_Plastico[i] = f1.getString(0);
                        pesoMesTotal_Papel[i] = topResiduosTotalPapel.get(datos.get(i));
                    }
                }
            }
        }else{
            for (int i = 0; i < cantidad_condominios; i++) {
                if(datos.get(i) != 0) {
                    Cursor f1 = db.rawQuery("select nombre from Condominio where codigo = " + datos.get(i), null);
                    if (f1.moveToFirst()) {
                        nombreMesTotal_Papel[i] = f1.getString(0);
                        pesoMesTotal_Papel[i] = topResiduosTotalPapel.get(datos.get(i));
                    }
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void TopResiduosMonth(){
        for (Map.Entry<Integer, Double> entry : topResiduosMonthTotal.entrySet()) {
            entry.setValue(entry.getValue() - entry.getValue());
        }
        dbHelper helper = new dbHelper(getActivity(), "Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
        String query = "select peso_prim,mes,codigo_producto_prim, peso_sec, codigo_producto_sec, peso_ter, codigo_producto_ter from Contador where mes = '"+valueMonth+"' and tipo_producto = 'total' ";
        Cursor f = db.rawQuery(query, null);
        while (f.moveToNext()) {
            if(f.getInt(2) != 0)
                topResiduosMonthTotal.put(f.getInt(2), topResiduosMonthTotal.get(f.getInt(2)) + f.getDouble(0) );
            if(f.getInt(4) != 0)
                topResiduosMonthTotal.put(f.getInt(4), topResiduosMonthTotal.get(f.getInt(4)) + f.getDouble(3) );
            if(f.getInt(6) != 0)
                topResiduosMonthTotal.put(f.getInt(6), topResiduosMonthTotal.get(f.getInt(6)) + f.getDouble(5) );
        }
        System.out.println("QUERYYY");
        System.out.println(query);
        topResiduosMonthTotal = sortByValue(topResiduosMonthTotal);
        System.out.println(topResiduosMonthTotal);
        ArrayList<Integer> datos = insertar_tops(topResiduosMonthTotal);
        System.out.println(datos);
        if(cantidad_condominios > 3) {
            for (int i = 0; i < 3; i++) {
                if(datos.get(i) != 0) {
                    Cursor f1 = db.rawQuery("select nombre from Condominio where codigo = " + datos.get(i), null);
                    if (f1.moveToFirst()) {
                        nombreMes_TipoTotal[i] = f1.getString(0);
                        pesoMes_TipoTotal[i] = topResiduosMonthTotal.get(datos.get(i));
                    }
                }
            }
        }else{
            for (int i = 0; i < cantidad_condominios; i++) {
                if(datos.get(i) != 0) {
                    Cursor f1 = db.rawQuery("select nombre from Condominio where codigo = " + datos.get(i), null);
                    if (f1.moveToFirst()) {
                        nombreMes_TipoTotal[i] = f1.getString(0);
                        pesoMes_TipoTotal[i] = topResiduosMonthTotal.get(datos.get(i));
                    }
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void TopResiduosMonthAndTipo(){
        for (Map.Entry<Integer, Double> entry : topResiduosMonthByTipo.entrySet()) {
            entry.setValue(entry.getValue() - entry.getValue());
        }
        dbHelper helper = new dbHelper(getActivity(), "Usuario.sqlite", null, 1);
        String query2 = "select peso_prim,mes,codigo_producto_prim,tipo_producto, peso_sec, codigo_producto_sec, peso_ter, codigo_producto_ter from Contador where mes = '"+valueMonth+"' and tipo_producto = '"+valueResiduo+"'";
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor f = db.rawQuery(query2, null);
        while (f.moveToNext()) {
            if(f.getInt(2) != 0)
            topResiduosMonthByTipo.put(f.getInt(2), topResiduosMonthByTipo.get(f.getInt(2)) + f.getInt(0) );
            if(f.getInt(5) != 0)
                topResiduosMonthByTipo.put(f.getInt(5), topResiduosMonthByTipo.get(f.getInt(5)) + f.getInt(4) );
            if(f.getInt(7) != 0)
                topResiduosMonthByTipo.put(f.getInt(7), topResiduosMonthByTipo.get(f.getInt(7)) + f.getInt(6) );
        }
        System.out.println(query2);
        System.out.println(topResiduosMonthByTipo);
        topResiduosMonthByTipo = sortByValue(topResiduosMonthByTipo);
        ArrayList<Integer> datos = insertar_tops(topResiduosMonthByTipo);
        if(cantidad_condominios > 3) {
            for (int i = 0; i < 3; i++) {
                if(datos.get(i) != 0) {
                    Cursor f1 = db.rawQuery("select nombre from Condominio where codigo = " + datos.get(i), null);
                    if (f1.moveToFirst()) {
                        nombreMes_Tipo[i] = f1.getString(0);
                        pesoMes_Tipo[i] = topResiduosMonthByTipo.get(datos.get(i));
                    }
                }
            }
        }else{
            for (int i = 0; i < cantidad_condominios; i++) {
                if(datos.get(i) != 0) {
                    Cursor f1 = db.rawQuery("select nombre from Condominio where codigo = " + datos.get(i), null);
                    if (f1.moveToFirst()) {
                        nombreMes_Tipo[i] = f1.getString(0);
                        pesoMes_Tipo[i] = topResiduosMonthByTipo.get(datos.get(i));
                    }
                }
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Map<Integer,Double> sortByValue(Map<Integer, Double> hm)
    {

        hm = hm.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                        LinkedHashMap::new));
        return hm;
    }

    public ArrayList<Integer>  insertar_tops(Map<Integer,Double> hm){

        int codigo1 = 0,codigo2 = 0,codigo3 = 0;
        ArrayList<Integer> codigos = new ArrayList<>();
        Iterator<Map.Entry<Integer, Double>> it = hm.entrySet().iterator();
        if(cantidad_condominios> 3){
            for(int i=0;i< 3 ;i++){
                Map.Entry<Integer, Double> entry = it.next();
                if(i == 0) {
                    codigo1 = entry.getKey();
                }else{
                    if( i == 1){
                        codigo2 = entry.getKey();
                    }
                    else{
                        codigo3 = entry.getKey();
                    }
                }
            }
        }else {
            for (int i = 0; i < cantidad_condominios; i++) {
                Map.Entry<Integer, Double> entry = it.next();
                if (i == 0) {
                    codigo1 = entry.getKey();
                } else {
                    if (i == 1) {
                        codigo2 = entry.getKey();
                    } else {
                        codigo3 = entry.getKey();
                    }
                }
            }
        }

        codigos.add(codigo1);
        codigos.add(codigo2);
        codigos.add(codigo3);

        return codigos;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setTopCondominios(){
        if( (valueMonth.equals("Todos") ||valueMonth.equals("todos")) && valueResiduo.equals("Todos") ) {
            txtNombreResiduo1.setText(nombreMesTotal_TipoTotal[0]);
            if(nombreMesTotal_TipoTotal[0].length() > 1)
                txtPesoResiduo1.setText(Double.toString(pesoMesTotal_TipoTotal[0] /1000));
            txtNombreResiduo2.setText(nombreMesTotal_TipoTotal[1]);
            if(nombreMesTotal_TipoTotal[1].length() > 1)
                txtPesoResiduo2.setText(Double.toString(pesoMesTotal_TipoTotal[1] /1000));
            txtNombreResiduo3.setText(nombreMesTotal_TipoTotal[2]);
            if(nombreMesTotal_TipoTotal[2].length() > 1) {
                txtPesoResiduo3.setText(Double.toString(pesoMesTotal_TipoTotal[2] / 1000));
            }else
            {
                txtPesoResiduo3.setText("");

            }
        }
        else {
            if(  valueResiduo.equals("Todos") ) {
                TopResiduosMonth();
                for (int i = 0; i < pesoMes_TipoTotal.length; i++)
                    System.out.println(pesoMes_TipoTotal[i]);
                txtNombreResiduo1.setText(nombreMes_TipoTotal[0]);
                if(nombreMes_TipoTotal[0].length() > 1)
                    txtPesoResiduo1.setText(Double.toString(pesoMes_TipoTotal[0] / 1000));
                txtNombreResiduo2.setText(nombreMes_TipoTotal[1]);
                if(nombreMes_TipoTotal[1].length() > 1)
                    txtPesoResiduo2.setText(Double.toString(pesoMes_TipoTotal[1] / 1000));
                txtNombreResiduo3.setText(nombreMes_TipoTotal[2]);
                if(nombreMes_TipoTotal[2].length() > 1 )
                    txtPesoResiduo3.setText(Double.toString(pesoMes_TipoTotal[2] / 1000));
            }else{
                if(  valueMonth.equals("Todos") ||valueMonth.equals("todos")){
                    if(valueResiduo.equals("Plástico")) {
                        txtNombreResiduo1.setText(nombreMesTotal_Plastico[0]);
                        if(nombreMesTotal_Plastico[0].length() > 1)
                            txtPesoResiduo1.setText(Double.toString(pesoMesTotal_Plastico[0] /1000));
                        txtNombreResiduo2.setText(nombreMesTotal_Plastico[1]);
                        if(nombreMesTotal_Plastico[1].length() > 1)
                            txtPesoResiduo2.setText(Double.toString(pesoMesTotal_Plastico[1] /1000));
                        txtNombreResiduo3.setText(nombreMesTotal_Plastico[2]);
                        if(nombreMesTotal_Plastico[2].length() > 1)
                            txtPesoResiduo3.setText(Double.toString(pesoMesTotal_Plastico[2] /1000));
                    }else{
                        txtNombreResiduo1.setText(nombreMesTotal_Papel[0]);
                        if(nombreMesTotal_Papel[0].length() > 1)
                        txtPesoResiduo1.setText(Double.toString(pesoMesTotal_Papel[0] /1000));
                        txtNombreResiduo2.setText(nombreMesTotal_Papel[1]);
                        if(nombreMesTotal_Papel[1].length() > 1)
                        txtPesoResiduo2.setText(Double.toString(pesoMesTotal_Papel[1] /1000));
                        txtNombreResiduo3.setText(nombreMesTotal_Papel[2]);
                        if(nombreMesTotal_Papel[2].length() > 1)
                        txtPesoResiduo3.setText(Double.toString(pesoMesTotal_Papel[2] /1000));
                    }

                }else{
                    TopResiduosMonthAndTipo();
                    txtNombreResiduo1.setText(nombreMes_Tipo[0]);
                    if(nombreMes_Tipo[0].length() > 1)
                        txtPesoResiduo1.setText(Double.toString(pesoMes_Tipo[0] /1000));
                    txtNombreResiduo2.setText(nombreMes_Tipo[1]);
                    if(nombreMes_Tipo[1].length() > 1)
                        txtPesoResiduo2.setText(Double.toString(pesoMes_Tipo[1] /1000));
                    txtNombreResiduo3.setText(nombreMes_Tipo[2]);
                    if(nombreMes_Tipo[2].length() > 1)
                        txtPesoResiduo3.setText(Double.toString(pesoMes_Tipo[2] /1000));
                }
            }
        }
    }

    private int getBolsasByMonth(String month) {
        int cantidadTotal = 0;
        dbHelper helper = new dbHelper(getActivity(), "Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
        if( (valueMonth.equals("Todos") ||valueMonth.equals("todos")) && valueTipo.equals("Todos") ) {
            Cursor f = db.rawQuery("select enero,febrero,marzo,abril,mayo,junio,julio,agosto,setiembre,octubre,noviembre,diciembre from DatosAnuales where tipo = 'bolsa'", null);
            while (f.moveToNext()) {
                for (int i = 0; i <= 11; i++) {
                    cantidadTotal += f.getInt(i);
                }
            }
        }
        else {
            if(  valueTipo.equals("Todos") ) {
                Cursor f = db.rawQuery("select " + month + "  from DatosAnuales where tipo = 'bolsa'", null);
                while (f.moveToNext()) {
                    cantidadTotal += f.getInt(0);
                }
            }else{
                if(  valueMonth.equals("Todos") || valueMonth.equals("todos")){
                    Cursor f = db.rawQuery("select enero,febrero,marzo,abril,mayo,junio,julio,agosto,setiembre,octubre,noviembre,diciembre from DatosAnuales where tipo = 'bolsa' and tipocondominio = "+ codigo_condominio, null);
                    if (f.moveToFirst()) {
                        for (int i = 0; i <= 11; i++) {
                            cantidadTotal += f.getInt(i);
                        }
                    }


                }else{
                    Cursor f = db.rawQuery("select " + month + "  from DatosAnuales where tipo = 'bolsa'  and tipocondominio = "+ codigo_condominio, null);
                    if (f.moveToFirst()) {
                        cantidadTotal += f.getInt(0);
                    }

                }
            }
        }
        return cantidadTotal;
    }

    private double datosPesoByMonth(String month)
    {
        double dataPesos;
        dbHelper helper = new dbHelper(getActivity(),"Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
        double pesosTotal = 0;

        if( (valueMonth.equals("Todos") ||valueMonth.equals("todos")) && valueTipo.equals("Todos") && valueResiduo.equals("Todos") ) {
            Cursor f = db.rawQuery("select enero,febrero,marzo,abril,mayo,junio,julio,agosto,setiembre,octubre,noviembre,diciembre from DatosAnuales where tipo = 'pesos'", null);
            while (f.moveToNext()) {
                for (int i = 0; i <= 11; i++) {
                    pesosTotal += f.getInt(i);
                }
            }
        }
        else {
            if(  valueTipo.equals("Todos") && valueResiduo.equals("Todos") ) {

                Cursor f = db.rawQuery("select " + month + "  from DatosAnuales where tipo = 'pesos'", null);
                while (f.moveToNext()) {
                    pesosTotal += f.getInt(0);
                }

            }else{
                if(  (valueMonth.equals("Todos") || valueMonth.equals("todos")) && valueResiduo.equals("Todos")){
                    System.out.println("Entrooooooooooo");
                    Cursor f = db.rawQuery("select enero,febrero,marzo,abril,mayo,junio,julio,agosto,setiembre,octubre,noviembre,diciembre from DatosAnuales where tipo = 'pesos' and tipocondominio = "+ codigo_condominio, null);
                    if(f.moveToFirst()) {
                        for (int i = 0; i <= 11; i++) {
                            pesosTotal += f.getInt(i);
                        }
                    }

                }else{
                    if( valueResiduo.equals("Todos")) {
                        Cursor f = db.rawQuery("select " + month + " from DatosAnuales where tipo = 'pesos' and tipocondominio = "+ codigo_condominio, null);
                        if(f.moveToFirst()) {
                            pesosTotal += f.getInt(0);
                        }
                    }else{
                        if((valueMonth.equals("Todos") ||valueMonth.equals("todos")) && valueTipo.equals("Todos")){
                            Cursor f = db.rawQuery("select enero,febrero,marzo,abril,mayo,junio,julio,agosto,setiembre,octubre,noviembre,diciembre from DatosAnuales where tipo = '"+valueResiduo+"'", null);
                            while (f.moveToNext()) {
                                for (int i = 0; i <= 11; i++) {
                                    pesosTotal += f.getInt(i);
                                }
                            }
                        }
                        else{
                            if(valueTipo.equals("Todos")){
                                Cursor f = db.rawQuery("select " + month + "  from DatosAnuales where tipo = '"+valueResiduo+"'", null);
                                while (f.moveToNext()) {
                                    pesosTotal += f.getInt(0);
                                }
                            }else{
                                if(valueMonth.equals("Todos") || valueMonth.equals("todos")){
                                    System.out.println("Entrooooooooooo");
                                    Cursor f = db.rawQuery("select enero,febrero,marzo,abril,mayo,junio,julio,agosto,setiembre,octubre,noviembre,diciembre from DatosAnuales where tipo = '"+valueResiduo+"' and tipocondominio = "+ codigo_condominio, null);
                                    if(f.moveToFirst()) {
                                        for (int i = 0; i <= 11; i++) {
                                            pesosTotal += f.getInt(i);
                                        }
                                    }
                                }else{
                                    Cursor f = db.rawQuery("select " + month + " from DatosAnuales where tipo = '\"+valueResiduo+\"' and tipocondominio = "+ codigo_condominio, null);
                                    if(f.moveToFirst()) {
                                        pesosTotal += f.getInt(0);
                                    }

                                }
                            }
                        }
                    }


                }
            }
        }

        dataPesos = pesosTotal;
        return dataPesos;
    }


    private void agregarTiposLista(){
        TipoList =  new ArrayList<String>();
        TipoList.add("Todos");
        TipoList.add("Plástico");
        TipoList.add("Papel/Cartón");

    }







}
