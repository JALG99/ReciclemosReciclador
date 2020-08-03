package com.upc.reciclemosreciclador.Fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.upc.reciclemosreciclador.Adicionales.dbHelper;
import com.upc.reciclemosreciclador.Entities.Condominio;
import com.upc.reciclemosreciclador.Entities.ContenedorTemp;
import com.upc.reciclemosreciclador.Entities.Distrito;
import com.upc.reciclemosreciclador.Entities.GeoLocation;
import com.upc.reciclemosreciclador.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetalleCondominioFragment extends Fragment implements OnMapReadyCallback {
    int position;
    GoogleMap map;
    private double latitude,longitude;
    private Condominio condominio;
    private ContenedorTemp contenedor;
    private  Distrito distrito;
    private TextView txtNombre,txtDireccion,txtUrbanizacion,txtDistrito;
    private TextView txtNombreContacto, txtNumeroContacto;
    private TextView txtCantidad, txtPeso;
    public DetalleCondominioFragment(int position) {
        // Required empty public constructor
        this.position = position;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_detalle_condominio, container, false);
        obtenerDatos();
        txtNombre = view.findViewById(R.id.txtTitleCondominio);
        txtDireccion = view.findViewById(R.id.txtDireccionCondominio);
        txtUrbanizacion = view.findViewById(R.id.txtUrbanizacionCondominio);
        txtDistrito = view.findViewById(R.id.txtDistritoCondominio);
        txtNombreContacto = view.findViewById(R.id.txtNombreContactoCondominio);
        txtNumeroContacto = view.findViewById(R.id.txtNumeroContactoCondominio);
        txtCantidad = view.findViewById(R.id.txtUnidadesContenedorCondominio);
        txtPeso = view.findViewById(R.id.txtCapacidadContenedorCondominio);

        txtNombre.setText("Condominio "+ condominio.getNombre());
        txtDireccion.setText(condominio.getDireccion());
        txtUrbanizacion.setText(condominio.getUrbanizacion());
        txtDistrito.setText(condominio.getDistrito().getNombre());
        txtNombreContacto.setText("Nombre: " + condominio.getNombreContacto());
        txtNumeroContacto.setText("Numero: "+condominio.getNumeroContacto());
        txtCantidad.setText("Unidades: "+ Integer.toString(contenedor.getCantBolsas()));
        // txtPeso.setText("Capacidad: "+Double.toString(contenedor.getCantidadTotal()/1000));
        txtPeso.setText("Capacidad maxima por unidad: 1100L" );
        String address = condominio.getDireccion() + " "+condominio.getDistrito().getNombre();
        SupportMapFragment mapFragment =  (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.imgRutaCondominio);
        GeoLocation geoLocation = new GeoLocation();
        geoLocation.getAddress(address + " Lima Peru",getContext(),new GeoHandler(mapFragment,this));
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng ciudad = new LatLng(latitude,longitude);
        map.addMarker(new MarkerOptions().position(ciudad).title("Maharashtra"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(ciudad,13));
    }

    public void obtenerDatos(){
        dbHelper helper = new dbHelper(getActivity(),"Usuario.sqlite", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
        condominio = new Condominio();
        distrito = new Distrito();
        contenedor = new ContenedorTemp();
        Cursor condominioData = db.rawQuery("select nombre,direccion,urbanizacion, distrito,nombrecontacto,numerocontacto from Condominio where codigo = "+ position,null);
        condominioData.moveToFirst();
        condominio.setNombre(condominioData.getString(0));
        condominio.setDireccion(condominioData.getString(1));
        condominio.setUrbanizacion(condominioData.getString(2));
        distrito.setNombre(condominioData.getString(3));
        condominio.setDistrito(distrito);
        condominio.setNombreContacto(condominioData.getString(4));
        condominio.setNumeroContacto(condominioData.getString(5));

        Cursor contenedorData = db.rawQuery("select cantidad,pesoTotalBolsas  from Contenedor where condominiocodigo = "+ position,null);
        contenedorData.moveToFirst();
        contenedor.setCantBolsas(contenedorData.getInt(0));
        contenedor.setCantidadTotal(contenedorData.getDouble(1));

    }

    private class GeoHandler extends Handler{
        SupportMapFragment mapFragment;
        OnMapReadyCallback onMapReadyCallback;
        public GeoHandler(SupportMapFragment mapFragment, OnMapReadyCallback onMapReadyCallback){
            this.mapFragment = mapFragment;
            this.onMapReadyCallback = onMapReadyCallback;
        }
        @Override
        public void handleMessage(Message msg){
            String address;
            switch (msg.what){
                case 1:
                    Bundle bundle = msg.getData();
                    address = bundle.getString("address");
                    break;
                default:
                    address = "0.0,0.0";
            }
            String[] parts = address.split(",");
            latitude = Double.parseDouble(parts[0]);
            longitude = Double.parseDouble(parts[1]);
            mapFragment.getMapAsync( onMapReadyCallback);
        }
    }
}
