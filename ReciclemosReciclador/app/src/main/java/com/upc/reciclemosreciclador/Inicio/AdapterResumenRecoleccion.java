package com.upc.reciclemosreciclador.Inicio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.upc.reciclemosreciclador.Entities.ResumenRecoleccion;
import com.upc.reciclemosreciclador.R;

import java.util.ArrayList;

public class AdapterResumenRecoleccion extends RecyclerView.Adapter<AdapterResumenRecoleccion.ViewHolderRecoleccion> {
    ArrayList<ResumenRecoleccion> listResumenRecoleccion;
    private Context context;

    public AdapterResumenRecoleccion(ArrayList<ResumenRecoleccion> listResumenRecoleccion, Context context){
        this.listResumenRecoleccion = listResumenRecoleccion;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderRecoleccion onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.resumen_recoleccion,null,false);
        return new ViewHolderRecoleccion(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderRecoleccion holder, int position) {
        holder.asignarResumen(listResumenRecoleccion.get(position));
    }

    @Override
    public int getItemCount() {
        return listResumenRecoleccion.size();
    }

    public class ViewHolderRecoleccion extends RecyclerView.ViewHolder {

        ImageView imgCategoria;
        TextView txtNombre, txtUrbanizacion, txtDistrito, txtPendiente;

        public ViewHolderRecoleccion(@NonNull View itemView) {
            super(itemView);
            imgCategoria = itemView.findViewById(R.id.imgCategoria);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtUrbanizacion = itemView.findViewById(R.id.txtUrbanizacion);
            txtDistrito = itemView.findViewById(R.id.txtDistrito);
            txtPendiente = itemView.findViewById(R.id.txtPendiente);

        }

        public void asignarResumen(ResumenRecoleccion resumenRecoleccion) {
            txtNombre.setText(resumenRecoleccion.getNombre());
            txtUrbanizacion.setText(resumenRecoleccion.getUrbanizacion());
            txtDistrito.setText(resumenRecoleccion.getDistrito());
            txtPendiente.setText("Pendiente " + resumenRecoleccion.getPendientes() + "/" + resumenRecoleccion.getRecogidas());
        }

    }
}
