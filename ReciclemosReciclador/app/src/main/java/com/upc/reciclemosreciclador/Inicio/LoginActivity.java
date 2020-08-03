package com.upc.reciclemosreciclador.Inicio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.upc.reciclemosreciclador.Entities.JsonPlaceHolderApi;
import com.upc.reciclemosreciclador.R;

import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private TextView textView, textView2, txtForget;
    private ImageView imageView;
    private EditText txtEmail, txtPassword;
    private Button btnIngresar, btnRegistrar;
    private Retrofit retrofit;
    private String[] arrpassword = null;
    private APIToSQLite sqlitedb;
    private CheckBox checkBox;
    private ProgressDialog progressDialog, progressDialog2;
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            switch (message.what){
                case 1:
                    progressDialog.cancel();
                    progressDialog2.show();
                    progressDialog2.setCancelable(false);
                    break;
                case 2:
                    progressDialog2.cancel();
                    Toast.makeText(getApplicationContext(), "Información cargada", Toast.LENGTH_LONG).show();
                    Intent lectoractivity = new Intent(getApplicationContext(), BolsaActivity.class);
                    startActivity(lectoractivity);
                    break;
                case 3:
                    progressDialog.cancel();
                    txtEmail.setError(null);
                    break;
                case 4:
                    new BackgroundJob().execute();
                    break;
                case 5:
                    progressDialog.cancel();
                    txtEmail.setError("No esta registrado");
                    break;

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        txtForget = findViewById(R.id.txtForget);
        imageView = findViewById(R.id.imageView);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnIngresar = findViewById(R.id.btnIngresar);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        checkBox = findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    txtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else {
                    txtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        btnRegistrar.setOnClickListener(nOnClickListener);
        retrofit = new Retrofit.Builder()
                .baseUrl("https://recyclerapiresttdp.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Validando, por favor espere...");

        progressDialog2 = new ProgressDialog(this);
        progressDialog2.setMessage("Cargando información, por favor espere...");

        sqlitedb = new APIToSQLite(this, "");
    }

    //INTERFACE ENTITY
    public interface EntityCallBack<T> {

        void onSuccess(@NonNull T value);

        void onError(@NonNull Throwable throwable);
    }

    private View.OnClickListener nOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent registerActivity = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(registerActivity);
        }
    };

    //BUSCAR EMAIL
    public void BuscarEmail(@Nullable final EntityCallBack entityCallBack) throws InterruptedException, IOException {
        JsonPlaceHolderApi jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);
        Call<String[]> call=jsonPlaceHolderApi.getRecicladorByEmail("reciclador/correo/" + txtEmail.getText().toString().toLowerCase());
        Response<String[]> response = call.execute();
        /*call.enqueue(new Callback<String[]>() {
            @Override
            public void onResponse(Call<String[]> call, Response<String[]> response) {
                if(response.isSuccessful()){*/

                    Log.e("TAG","onResponse:" + response.toString());
                    entityCallBack.onSuccess(response.body());
                /*}else{
                    Log.e("TAG","onFailure:" + response.toString());
                }
            }

            @Override
            public void onFailure(Call<String[]> call, Throwable t) {
                Log.e("TAG","onFailure:" + t.getMessage());
            }
        });*/
    }

    //VALIDACION DE EMAIL
    public void validarEmail() throws IOException, InterruptedException {
        String emailInput = txtEmail.getText().toString().trim();

        if(emailInput.isEmpty()){
            txtEmail.setError("Debe llenar el campo");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
            txtEmail.setError("Por favor ingrese un email válido");
        } else {
            progressDialog.show();
            progressDialog.setCancelable(false);
            new Validacion().execute();
        }
    }

    //INICIO ALGORTIMO PARA HASH DE CONTRASEÑA
    private static String generateStorngPasswordHash(String password, String saltpass) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 1000;
        int i = 0;
        char[] chars = password.toCharArray();
        saltpass = saltpass.replaceAll(",",", ");
        int l = saltpass.length();
        saltpass = saltpass.substring(1, l-1);
        saltpass = saltpass.replaceAll(",","");
        Scanner scanner = new Scanner(saltpass);
        byte[] salt = new byte[16];
        while (scanner.hasNextByte()) {
            salt[i] = scanner.nextByte();
            i++;
        }

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return iterations + ":" + toHex(salt) + ":" + toHex(hash);
    }

    private static String toHex(byte[] array) throws NoSuchAlgorithmException {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0)
        {
            return String.format("%0"  +paddingLength + "d", 0) + hex;
        }else{
            return hex;
        }
    }
    //FIN DE ALGORITMO PARA HASH DE CONTRASEÑA

    //VALIDACION DE PASSWORD
    private boolean validarPassword() throws InvalidKeySpecException, NoSuchAlgorithmException {
        String passwordInput = txtPassword.getText().toString().trim();

        if(passwordInput.isEmpty()){
            arrpassword = null;
            txtPassword.setError("Debe llenar el campo");
            return false;
        } else {
            String[] password = new String[2];
            password[0] = arrpassword[0];
            System.out.println(password[0]);
            password[1] = arrpassword[1];
            String valor_password = generateStorngPasswordHash(passwordInput, password[1]);
            System.out.println(valor_password);

            if (password[0].compareTo(valor_password) != 0) {
                arrpassword = null;
                txtPassword.setError("Constraseña incorrecta");
                return false;
            }else return true;
        }
    }

    public void Autenticar(View view) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException, InterruptedException {
        validarEmail();
    }

    private class BackgroundJob extends AsyncTask<Void,Void,Void> {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                sqlitedb.InsertReciclador(txtEmail.getText().toString().toLowerCase(), arrpassword[0]);
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
                sqlitedb.InsertProductos();
                sqlitedb.InsertBolsas();
                sqlitedb.InsertProbolsa();
                sqlitedb.InsertUsuarios();
                sqlitedb.getCondominiosByReciclador();
                sqlitedb.getResiduosByWeekOrMonth("bolsasWeek/");
                sqlitedb.getResiduosByWeekOrMonth("bolsasMonth/");
                sqlitedb.obtenerBolsasByYear("bolsasYear/");
                sqlitedb.getContenedorByCondominio();
            } catch (IOException e) {
                e.printStackTrace();
            }catch (InterruptedException e) {
            e.printStackTrace();
        }
            return null;
        }

        @Override
        protected void onPostExecute(Void avoid){
            Message msg = new Message();
            msg.what = 2;
            mHandler.sendMessage(msg);
        }
    }

    private class Validacion extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                BuscarEmail(new EntityCallBack<String[]>() {
                    @Override
                    public void onSuccess(@NonNull String[] value) {
                        arrpassword = value;
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {

                    }
                });
            }catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void avoid){
            Message msg = new Message();
            if (arrpassword != null) {
                msg.what = 3;
                mHandler.sendMessage(msg);
                try {
                    if(validarPassword()){
                        Message msg2 = new Message();
                        msg2.what = 4;
                        mHandler.sendMessage(msg2);
                    }
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            } else {
                msg.what = 5;
                mHandler.sendMessage(msg);
            }
        }
    }
}
