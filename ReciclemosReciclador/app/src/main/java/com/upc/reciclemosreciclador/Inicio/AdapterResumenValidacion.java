package com.upc.reciclemosreciclador.Inicio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.upc.reciclemosreciclador.Entities.ResumenValidacion;
import com.upc.reciclemosreciclador.R;

import java.util.ArrayList;

public class AdapterResumenValidacion extends RecyclerView.Adapter<AdapterResumenValidacion.ViewHolderValidacion> {
    ArrayList<ResumenValidacion> listResumenValidacion;
    private Context context;

    public AdapterResumenValidacion(ArrayList<ResumenValidacion> listResumenValidacion, Context context){
        this.listResumenValidacion = listResumenValidacion;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderValidacion onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.resumen_validacion,null,false);
        return new ViewHolderValidacion(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderValidacion holder, int position) {
        holder.asignarResumen(listResumenValidacion.get(position), position);
    }

    @Override
    public int getItemCount() {
        return listResumenValidacion.size();
    }

    public class ViewHolderValidacion extends RecyclerView.ViewHolder {

        TextView txtNumero, txtNombre, txtUrbanizacion, txtDistrito, txtPeso, txtPorcentaje;

        public ViewHolderValidacion(@NonNull View itemView) {
            super(itemView);
            txtNumero = itemView.findViewById(R.id.txtNumero);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtUrbanizacion = itemView.findViewById(R.id.txtUrbanizacion);
            txtDistrito = itemView.findViewById(R.id.txtDistrito);
            txtPeso = itemView.findViewById(R.id.txtPeso);
            txtPorcentaje = itemView.findViewById(R.id.txtPorcentaje);
        }

        public void asignarResumen(ResumenValidacion resumenValidacion, int position) {
            txtNumero.setText((position + 1) + "");
            txtNombre.setText(resumenValidacion.getNombre());
            txtUrbanizacion.setText(resumenValidacion.getUrbanizacion());
            txtDistrito.setText(resumenValidacion.getDistrito());
            txtPeso.setText(resumenValidacion.getPesoActual() + " Kg/" + resumenValidacion.getBolsas() + " bolsas");

            int porcentaje = (resumenValidacion.getPesoActual() * 100)/resumenValidacion.getMax();

            txtPorcentaje.setText(porcentaje + "% lleno");

            if(porcentaje > 100)
                txtPorcentaje.setText("100% lleno");

            if(porcentaje <= 35)
                txtPorcentaje.setTextColor(ContextCompat.getColor(context, R.color.rojo));
            else
                if(porcentaje <= 70)
                    txtPorcentaje.setTextColor(ContextCompat.getColor(context, R.color.amarillo));
                else
                    txtPorcentaje.setTextColor(ContextCompat.getColor(context, R.color.verde));
        }

    }
}
