package com.upc.reciclemosreciclador.Fragments;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.upc.reciclemosreciclador.Adicionales.dbHelper;
import com.upc.reciclemosreciclador.Inicio.LoginActivity;
import com.upc.reciclemosreciclador.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private TextView txtNombre,txtApellido,txtCorreo,txtPassword,txtDni;
    private TextView txtDepartamento,txtDistrito,txtAsociacion,txtCodigoReciclador,txtFechaNacimiento,txtCelular;
    private Button btnSolicitud,btnCerrarSesion;


    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }


    public ProfileFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        txtNombre = view.findViewById(R.id.txtInputNombre);
        txtApellido = view.findViewById(R.id.txtInputApellidos);
        txtCorreo = view.findViewById(R.id.txtInputCorreo);
        txtPassword = view.findViewById(R.id.txtInputPassword);
        txtCelular = view.findViewById(R.id.txtInputCelular);
        txtDepartamento = view.findViewById(R.id.txtInputDepartamento);
        txtDistrito = view.findViewById(R.id.txtInputDistrito);
        txtAsociacion = view.findViewById(R.id.txtInputAsociacion);
        txtCodigoReciclador = view.findViewById(R.id.txtInputCodFormalizador);
        txtFechaNacimiento = view.findViewById(R.id.txtInputFechaNacimiento);
        txtDni = view.findViewById(R.id.txtInputDNI);
        btnSolicitud = view.findViewById(R.id.btnModificarDatos);
        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);
        obtenerDatos();
        btnSolicitud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SolicitudFragment profileFragment = new SolicitudFragment();
                FragmentTransaction transaction= (getActivity()).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, profileFragment);
                transaction.commit();
            }
        });
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });
        return view;
    }

    public void obtenerDatos(){
        dbHelper helper = new dbHelper(getContext(),"Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor fila2 = db.rawQuery("select  nombre , apellido , direccion , dni , sexo , email , fecha_Nacimiento , celular , distrito_name , departamento_name, asociacion_name , codFormalizado  from Reciclador", null);

        fila2.moveToFirst();
        txtNombre.setText(fila2.getString(0));
        txtApellido.setText(fila2.getString(1));
        txtCorreo.setText(fila2.getString(5));
        txtDepartamento.setText(fila2.getString(9));
        txtDistrito.setText(fila2.getString(8));
        txtDni.setText(fila2.getString(3));
        String[] parts = fila2.getString(6).split("-");
        String year = parts[0];
        String month = parts[1];
        String dateAux = parts[2];
        String[] partsDate = dateAux.split("T");
        String date = partsDate[0];
        txtFechaNacimiento.setText(date+"/"+month+"/"+year);
        txtAsociacion.setText(fila2.getString(10));
        if(fila2.getString(7).equals("null"))
        txtCelular.setText("No ingresado");
        else
            txtCelular.setText(fila2.getString(7));
        txtCodigoReciclador.setText(fila2.getString(11));
    }
}
