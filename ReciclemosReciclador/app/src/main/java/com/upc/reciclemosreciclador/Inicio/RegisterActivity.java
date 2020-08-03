package com.upc.reciclemosreciclador.Inicio;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.upc.reciclemosreciclador.Entities.Condominio;
import com.upc.reciclemosreciclador.Entities.Departamento;
import com.upc.reciclemosreciclador.Entities.Distrito;
import com.upc.reciclemosreciclador.Entities.JsonPlaceHolderApi;
import com.upc.reciclemosreciclador.Entities.Reciclador;
import com.upc.reciclemosreciclador.Entities.Sexo;
import com.upc.reciclemosreciclador.Entities.Usuario;
import com.upc.reciclemosreciclador.R;

import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {
    private static Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //AL MENOS UN DIGITO
                    "(?=.*[a-z])" +         //AL MENOS UNA MINUSCULA
                    "(?=.*[A-Z])" +         //AL MENOS UNA MAYUSCULA
                    "(?=\\S+$)" +           //SIN ESPACIOS EN BLANCO
                    ".{6,}" +               //MINIMO 6 CARACTERES
                    "$");

    private EditText txtEmail,txtCodFormalizado, txtPassword, txtRepetirPassword;
    private TextView txtNombres, txtApellidos, txtDNI, txtAsociacion;
    private Button btnBuscar, btnRegistrar, btnLogin;
    private int codigo = 0;

    private Retrofit retrofit;

    private ProgressDialog progressDialog, progressDialog1, progressDialog2;
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            switch (message.what){
                case 0:
                    progressDialog.show();
                    progressDialog.setCancelable(false);
                    break;
                case 1:
                    progressDialog.cancel();
                    break;
                case 2:
                    progressDialog1.show();
                    progressDialog1.setCancelable(false);
                    break;
                case 3:
                    txtCodFormalizado.setError("Ese código no existe");
                    break;
                case 4:
                    txtCodFormalizado.setError("Ese código ya fue registrado");
                    break;
                case 5:
                    txtEmail.setError("Ese email ya está registrado");
                    break;
                case 6:
                    progressDialog1.cancel();
                    break;
                case 7:
                    progressDialog2.show();
                    progressDialog2.setCancelable(false);
                    break;
                case 8:
                    progressDialog2.cancel();
                    Toast.makeText(getApplicationContext(), "Registro exitoso", Toast.LENGTH_LONG).show();
                    Intent loginactivity = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(loginactivity);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        retrofit = new Retrofit.Builder()
                .baseUrl("https://recyclerapiresttdp.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnLogin = findViewById(R.id.btnLogin);
        btnBuscar = findViewById(R.id.btnBuscar);

        txtCodFormalizado = findViewById(R.id.txtCodFormalizado);
        txtNombres = findViewById(R.id.txtNombres);
        txtApellidos = findViewById(R.id.txtApellidos);
        txtDNI = findViewById(R.id.txtDNI);
        txtAsociacion = findViewById(R.id.txtAsociacion);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        txtRepetirPassword = findViewById(R.id.txtRepetirPassword);

        btnLogin.setOnClickListener(mOnClickListener);
        btnBuscar.setOnClickListener(nOnClickListener);
        btnRegistrar.setOnClickListener(oOnClickListener);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Buscando código...");

        progressDialog1 = new ProgressDialog(this);
        progressDialog1.setMessage("Validando, por favor espere...");

        progressDialog2 = new ProgressDialog(this);
        progressDialog2.setMessage("Registrando...");

        bloqueo(1);
    }

    void bloqueo(int i) {
        bloquear(txtEmail, i);
        bloquear(txtPassword, i);
        bloquear(txtRepetirPassword, i);
        if(i == 1)
            btnRegistrar.setClickable(false);
        else
            btnRegistrar.setClickable(true);
    }
    void bloquear(EditText editText, int i){
        if(i == 1){
            editText.setClickable(false);
            editText.setCursorVisible(false);
            editText.setFocusable(false);
            editText.setFocusableInTouchMode(false);
        }else{
            editText.setClickable(true);
            editText.setCursorVisible(true);
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent loginactivity = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(loginactivity);
        }
    };

    private View.OnClickListener nOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buscarReciclador(txtCodFormalizado.getText().toString());
        }
    };

    private View.OnClickListener oOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            validacionSimple();
        }
    };

    //INICIO ALGORTIMO PARA HASH DE CONTRASEÑA
    private static String[] generateStorngPasswordHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        byte[] salt = getSalt();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        String[] respuesta = new String[2];
        String saltext = Arrays.toString(salt);
        respuesta[0] = iterations + ":" + toHex(salt) + ":" + toHex(hash);
        respuesta[1] = saltext;

        return respuesta;
    }

    private static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
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

    //VALIDACION DE CODIGO FORMALIZADO
    private boolean validarCodFormalizado(){
        String codInput = txtCodFormalizado.getText().toString().trim();

        if(codInput.isEmpty()){
            txtCodFormalizado.setError("Debe llenar el campo");
            return false;
        } else{
            txtCodFormalizado.setError(null);
            return true;
        }
    }

    //VALIDACION DE EMAIL
    private boolean validarEmail(){
        String emailInput = txtEmail.getText().toString().trim();

        if(emailInput.isEmpty()){
            txtEmail.setError("Debe llenar el campo");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
            txtEmail.setError("Por favor ingrese un email válido");
            return false;
        } else{
            txtEmail.setError(null);
            return true;
        }
    }

    //VALIDACION DE PASSWORD
    private boolean validarPassword(){
        String passwordInput = txtPassword.getText().toString().trim();
        String passwordRepetido = txtRepetirPassword.getText().toString().trim();

        if(passwordInput.isEmpty()){
            txtPassword.setError("Debe llenar el campo");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()){
            txtPassword.setError("Contraseña débil");
            return false;
        } else if(passwordInput.compareTo(passwordRepetido) != 0) {
            txtPassword.setError("Las contraseñas no coinciden");
            return false;
        }else {
            txtPassword.setError(null);
            return true;
        }
    }

    //VALIDAR EMAIL REPETIDO
    public boolean BuscarEmail(String email) throws IOException {
        JsonPlaceHolderApi jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);
        Call<Integer> call=jsonPlaceHolderApi.getEmail("reciclador/correo/activo/" + email);
        Response<Integer> response = call.execute();
        Log.e("TAG","onResponse:" + response.toString());
        if(response.body() == 1){
            return false;
        }else{
            Message msg = new Message();
            msg.what = 6;
            mHandler.sendMessage(msg);
            return true;
        }
    }

    //VALIDAR EMAIL CODIGO FORMALIZADO
    public boolean BuscarCodFormalizado(String codFormalizado) throws IOException {
        JsonPlaceHolderApi jsonPlaceHolderApi=retrofit.create(JsonPlaceHolderApi.class);
        Call<Reciclador> call=jsonPlaceHolderApi.getCodFormalizado("reciclador/codFormalizado/activo/" + codFormalizado);
        Response<Reciclador> response = call.execute();
        Log.e("TAG","onResponse:" + response.toString());
        if(response.body().getCodigo() == 0){
            Message msg = new Message();
            msg.what = 3;
            mHandler.sendMessage(msg);
            codigo = 0;
            bloqueo(1);
            return false;
        }else if(response.body().getCodigo() == -1){
            Message msg = new Message();
            msg.what = 4;
            mHandler.sendMessage(msg);
            codigo = 0;
            bloqueo(1);
            return false;
        } else{
            txtNombres.setText(response.body().getNombre());
            txtApellidos.setText(response.body().getApellido());
            txtDNI.setText(response.body().getDni());
            txtAsociacion.setText(response.body().getAsociacion().getNombre());
            codigo = response.body().getCodigo();
            bloqueo(0);
            return true;
        }
    }

    public void buscarReciclador(String codFormalizado) {
        if(validarCodFormalizado()){
            new Buscar(codFormalizado).execute();
        } else{
            codigo = 0;
            bloqueo(1);
        }
    }

    public void validacionSimple() {
        String valor_email = txtEmail.getText().toString().toLowerCase();
        String password = txtPassword.getText().toString();

        if(validarEmail() & validarPassword()) {
            new BackgroundJob(valor_email, password).execute();
        }
    }

    public boolean Registrar(String valor_email, String password) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {

        if(BuscarEmail(valor_email)) {
            Message msg2 = new Message();
            msg2.what = 7;
            mHandler.sendMessage(msg2);
            String[] valor_password = generateStorngPasswordHash(password);
            Reciclador user = new Reciclador();
            user.setCodigo(codigo);
            user.setEmail(valor_email);
            user.setSalt(valor_password[1]);
            user.setPassword(valor_password[0]);
            JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
            Call<Reciclador> call = jsonPlaceHolderApi.createReciclador(user);
            call.execute();
            return true;
        }else{
            return false;
        }
    }

    private class BackgroundJob extends AsyncTask<Void,Void,Void> {

        boolean resultado = false;
        String valor_email, password;

        public BackgroundJob(String valor_email, String password){
            this.valor_email = valor_email;
            this.password = password;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Message msg = new Message();
                msg.what = 2;
                mHandler.sendMessage(msg);
                resultado = Registrar(valor_email, password);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void avoid){
            if(resultado){
                Message msg2 = new Message();
                msg2.what = 8;
                mHandler.sendMessage(msg2);
            } else{
                Message msg = new Message();
                msg.what = 6;
                mHandler.sendMessage(msg);
            }
        }
    }

    private class Buscar extends AsyncTask<Void,Void,Void> {

        boolean resultado = false;
        String codFormalizado;

        public Buscar(String codFormalizado){
            this.codFormalizado = codFormalizado;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Message msg = new Message();
                msg.what = 0;
                mHandler.sendMessage(msg);
                resultado = BuscarCodFormalizado(codFormalizado);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void avoid){
            Message msg2 = new Message();
            msg2.what = 1;
            mHandler.sendMessage(msg2);
        }
    }
}
