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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.upc.reciclemosreciclador.Adicionales.PermissionUtil;
import com.upc.reciclemosreciclador.Adicionales.dbHelper;
import com.upc.reciclemosreciclador.Entities.Bolsa;
import com.upc.reciclemosreciclador.Entities.JsonPlaceHolderApi;
import com.upc.reciclemosreciclador.Entities.Probolsa;
import com.upc.reciclemosreciclador.Entities.QrCode;
import com.upc.reciclemosreciclador.Entities.Usuario;
import com.upc.reciclemosreciclador.R;
import com.google.zxing.ResultPoint;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QrFragment extends Fragment {

    private Button btnQr;
    private Retrofit retrofit;
    private ProgressDialog progressDialog;
    DecoratedBarcodeView decoratedBarcodeView;
    Context context;
    int bolsa;

    public static QrFragment newInstance() {
        return new QrFragment();
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
                    Toast.makeText(getActivity(), "No existe código QR", Toast.LENGTH_LONG).show();
                    scan();
                    break;
                case 3:
                    progressDialog.cancel();
                    ((BolsaActivity) getActivity()).registroExitoso(bolsa);
                    break;
                case 4:
                    progressDialog.cancel();
                    Toast.makeText(getActivity(), "La bolsa ya fue recogida", Toast.LENGTH_LONG).show();
                    scan();
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_qr, container, false);

        retrofit = new Retrofit.Builder()
                .baseUrl("https://recyclerapiresttdp.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Buscando bolsa...");

        decoratedBarcodeView = v.findViewById(R.id.zxing_barcode_scanner);
        decoratedBarcodeView.setStatusText("");

        if(PermissionUtil.on().requestPermission(this, Manifest.permission.CAMERA))
            scan();

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
                    new Background(result.getText()).execute();
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

    public void GetQr(String qr) throws IOException {
        JsonPlaceHolderApi jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);
        Call<Bolsa> call=jsonPlaceHolderApi.getBolsaByQRCode("bolsa/qrCode/" + qr);
        Response<Bolsa> response = call.execute();
        Log.e("TAG","onResponse:" + response.toString());

        try{
            if(response.body().getQrCode().getQrCode().compareTo(qr) == 0){

                if(response.body().getRecojoFecha() == null){

                    dbHelper helper = new dbHelper(getActivity(),"Usuario.sqlite", null, 1);
                    SQLiteDatabase db = helper.getWritableDatabase();

                    Bolsa b = response.body();
                    String query = "insert into Bolsa (codigo, usuario, condominio, qrcode, fechaCreado, validado) " +
                            "values (" + b.getCodigo() + ", '" + b.getUsuario().getCodigo() + "', '" +
                            b.getUsuario().getCondominio().getCodigo() + "', '" + b.getQrCode().getQrCode() + "', '" + b.getCreadoFecha().toString() + "', 'false')";

                    bolsa = b.getCodigo();

                    db.execSQL(query);
                    System.out.println(query);

                    Call<List<Probolsa>> call2=jsonPlaceHolderApi.getProductoByIdBolsa("probolsa/bolsa/" + b.getCodigo());
                    Response<List<Probolsa>> response2 = call2.execute();
                    Log.e("TAG","onResponse:" + response.toString());

                    for (Probolsa p: response2.body()) {
                        String query2 = "insert into Probolsa (bolsa, cantidad, codigo, peso, producto, puntuacion) " +
                                "values (" + p.getBolsa().getCodigo() + ", " + p.getCantidad() + ", " + p.getCodigo() + ", "
                                + p.getPeso() + ", " + p.getProducto().getCodigo() + ", " + p.getPuntuacion() + ")";
                        db.execSQL(query2);
                        System.out.println(query2);
                    }

                    Message msg = new Message();
                    msg.what = 3;
                    mHandler.sendMessage(msg);
                } else{
                    Message msg = new Message();
                    msg.what = 4;
                    mHandler.sendMessage(msg);
                }
            }else{
                Message msg = new Message();
                msg.what = 2;
                mHandler.sendMessage(msg);
            }
        }catch (Exception e){
            Message msg = new Message();
            msg.what = 2;
            mHandler.sendMessage(msg);
        }

    }

    public class Background extends AsyncTask<Void,Void,Void> {

        String qr;

        public Background(String qr){
            this.qr = qr;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
                GetQr(qr);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void avoid){
        }
    }
}
