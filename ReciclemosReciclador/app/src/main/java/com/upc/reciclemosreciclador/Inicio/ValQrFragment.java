package com.upc.reciclemosreciclador.Inicio;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.zxing.ResultPoint;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.upc.reciclemosreciclador.Adicionales.PermissionUtil;
import com.upc.reciclemosreciclador.Adicionales.dbHelper;
import com.upc.reciclemosreciclador.Entities.Bolsa;
import com.upc.reciclemosreciclador.Entities.JsonPlaceHolderApi;
import com.upc.reciclemosreciclador.Entities.Probolsa;
import com.upc.reciclemosreciclador.R;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ValQrFragment extends Fragment {

    private Button btnQr;
    private TextView txtRecogidas, txtValidadas, txtPendientes;
    private Retrofit retrofit;
    private ProgressDialog progressDialog;
    DecoratedBarcodeView decoratedBarcodeView;
    Context context;
    private int bolsa;
    int recogidas, validadas, pendientes;

    public static ValQrFragment newInstance() {
        return new ValQrFragment();
    }

    public void pauseScan(){
        decoratedBarcodeView.pause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message message) {
            switch (message.what){
                case 1:
                    progressDialog.show();
                    progressDialog.setCancelable(false);
                    break;
                case 2:
                    progressDialog.cancel();
                    Toast.makeText(getActivity(), "No existe bolsa", Toast.LENGTH_LONG).show();
                    scan();
                    break;
                case 3:
                    progressDialog.cancel();
                    ((ValidacionActivity) getActivity()).detalles(bolsa);
                    break;
                case 4:
                    progressDialog.cancel();
                    Toast.makeText(getActivity(), "La bolsa ya fue validada", Toast.LENGTH_LONG).show();
                    scan();
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_val_qr, container, false);

        retrofit = new Retrofit.Builder()
                .baseUrl("https://recyclerapiresttdp.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Un segundo... \n\n Estamos buscando coincidencias entre sus bolsas...");

        decoratedBarcodeView = v.findViewById(R.id.zxing_barcode_scanner);
        decoratedBarcodeView.setStatusText("");

        if(PermissionUtil.on().requestPermission(this, Manifest.permission.CAMERA))
            scan();

        txtRecogidas = v.findViewById(R.id.txtRecogidas);
        txtValidadas = v.findViewById(R.id.txtValidadas);
        txtPendientes = v.findViewById(R.id.txtPendientes);

        dbHelper helper = new dbHelper(getActivity(),"Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();

        Cursor f = db.rawQuery("select validado from Bolsa", null);

        if(f.moveToFirst()){
            recogidas = 0;
            validadas = 0;
            pendientes = 0;
            do{
                if(f.getString(0).compareTo("true") == 0)
                    validadas = validadas + 1;
                else
                    pendientes = pendientes + 1;
                recogidas = recogidas + 1;
            }while(f.moveToNext());
        }

        txtRecogidas.setText(recogidas + "");
        txtValidadas.setText(validadas + "");
        txtPendientes.setText(pendientes + "");

        return v;
    }

    void scan(){
        decoratedBarcodeView.resume();
        decoratedBarcodeView.decodeSingle(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                decoratedBarcodeView.pause();

                if (result != null) {
                    decoratedBarcodeView.pause();
                    GetQr(result.getText());
                } else {
                    decoratedBarcodeView.resume();
                }
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {

            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null)
            if (result.getContents() != null) {

            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Error al escanear", Toast.LENGTH_LONG).show();
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PermissionUtil.REQUEST_CODE_PERMISSION_DEFAULT) {
            boolean isAllowed = true;

            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    isAllowed = false;
                }
            }

            if (isAllowed) {
                scan();
            }
        }
    }

    public void GetQr(String qr) {
        dbHelper helper = new dbHelper(getActivity(),"Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();

        Cursor f = db.rawQuery("select validado, codigo from Bolsa where qrcode = '" + qr + "'", null);

        if(f.moveToFirst()){
            if(f.getString(0).compareTo("false") == 0){
                bolsa = f.getInt(1);
                Message msg = new Message();
                msg.what = 3;
                mHandler.sendMessage(msg);
            }else{
                Message msg = new Message();
                msg.what = 4;
                mHandler.sendMessage(msg);
            }
        }else{
            Message msg = new Message();
            msg.what = 2;
            mHandler.sendMessage(msg);
        }

    }
}
