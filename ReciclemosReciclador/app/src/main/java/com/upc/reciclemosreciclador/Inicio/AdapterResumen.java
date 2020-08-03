package com.upc.reciclemosreciclador.Inicio;

import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.upc.reciclemosreciclador.Entities.Resumen;
import com.upc.reciclemosreciclador.R;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class AdapterResumen extends RecyclerView.Adapter<AdapterResumen.ViewHolderDetalle> {

    ArrayList<Resumen> listResumen;
    private Context context;

    public AdapterResumen(ArrayList<Resumen> listResumen, Context context){
        this.listResumen = listResumen;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderDetalle onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.resumen,null,false);
        return new ViewHolderDetalle(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDetalle holder, int position) {
        holder.asignarDetalle(listResumen.get(position));
    }

    @Override
    public int getItemCount() {
        return listResumen.size();
    }

    public class ViewHolderDetalle extends RecyclerView.ViewHolder {

        ImageView imgCategoria;
        TextView txtNombre, txtPeso, txtCantidad;
        int cod;

        public ViewHolderDetalle(@NonNull View itemView) {
            super(itemView);
            imgCategoria = itemView.findViewById(R.id.imgCategoria);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtPeso = itemView.findViewById(R.id.txtPeso);
            txtCantidad = itemView.findViewById(R.id.txtCantidad);

        }

        public void asignarDetalle(Resumen resumen) {
            cod = resumen.getCodigo();
            txtNombre.setText(resumen.getNombre());
            txtCantidad.setText(resumen.getCantidad()+"");
            switch (resumen.getNombre()){
                case "Plástico":
                    imgCategoria.setImageResource(R.drawable.ic_plastico);
                    imgCategoria.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPlastico));
                    txtNombre.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPlastico));
                    break;
                case "Papel/Cartón":
                    imgCategoria.setImageResource(R.drawable.ic_papel_carton);
                    imgCategoria.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPapelCarton));
                    txtNombre.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPapelCarton));
                    break;
                case "Vidrio":
                    imgCategoria.setImageResource(R.drawable.ic_vidrio);
                    imgCategoria.setBackgroundColor(ContextCompat.getColor(context, R.color.colorVidrio));
                    txtNombre.setBackgroundColor(ContextCompat.getColor(context, R.color.colorVidrio));
                    break;
                case "Metal":
                    imgCategoria.setImageResource(R.drawable.ic_metal);
                    imgCategoria.setBackgroundColor(ContextCompat.getColor(context, R.color.colorMetal));
                    txtNombre.setBackgroundColor(ContextCompat.getColor(context, R.color.colorMetal));
                    break;
            }
            if(resumen.getPeso() <= 500.0) {
                txtPeso.setText(resumen.getPeso() + " gr");
            }else{
                txtPeso.setText(resumen.getPeso()/1000.0 + " kg");
            }
        }

    }
}
