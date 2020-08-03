package com.upc.reciclemosreciclador.Inicio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.upc.reciclemosreciclador.Entities.Validar;
import com.upc.reciclemosreciclador.R;

import java.util.ArrayList;

public class AdapterValidacion extends RecyclerView.Adapter<AdapterValidacion.ViewHolderValidacion>{
    ArrayList<Validar> listValidar;
    Context context;

    public AdapterValidacion(ArrayList<Validar> listValidar, Context context){
        this.listValidar = listValidar;
        this.context = context;
    }

    public void filterList(ArrayList<Validar> filteredList){
        listValidar = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdapterValidacion.ViewHolderValidacion onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.validacion,null,false);
        return new AdapterValidacion.ViewHolderValidacion(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterValidacion.ViewHolderValidacion holder, int position) {
        holder.asignarValidacion(listValidar.get(position));
    }

    @Override
    public int getItemCount() {
        return listValidar.size();
    }

    public class ViewHolderValidacion extends RecyclerView.ViewHolder {

        ImageView imgProducto, imgCategoria;
        TextView txtNombre, txtContenido, txtCategoria, txtCantidadV;
        CheckBox checkBox;

        public ViewHolderValidacion(@NonNull View itemView) {
            super(itemView);
            imgProducto = itemView.findViewById(R.id.imgProducto);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtContenido = itemView.findViewById(R.id.txtContenido);
            txtCategoria = itemView.findViewById(R.id.txtCategoria);
            txtCantidadV = itemView.findViewById(R.id.txtCantidadV);
            imgCategoria = itemView.findViewById(R.id.imgCategoria);
            checkBox = itemView.findViewById(R.id.checkBox);
        }

        public void asignarValidacion(final Validar validar) {
            txtNombre.setText(validar.getNombre());
            switch (validar.getCategoria()){
                case "Plástico":
                    imgCategoria.setColorFilter(ContextCompat.getColor(context,  R.color.colorPlastico), android.graphics.PorterDuff.Mode.MULTIPLY);
                    txtCategoria.setText(validar.getCategoria());
                    break;
                case "Papel/Cartón":
                    imgCategoria.setColorFilter(ContextCompat.getColor(context,  R.color.colorPapelCarton), android.graphics.PorterDuff.Mode.MULTIPLY);
                    txtCategoria.setText(validar.getCategoria());
                    break;
                case "Vidrio":
                    imgCategoria.setColorFilter(ContextCompat.getColor(context,  R.color.colorVidrio), android.graphics.PorterDuff.Mode.MULTIPLY);
                    txtCategoria.setText(validar.getCategoria());
                    break;
                case "Metal":
                    imgCategoria.setColorFilter(ContextCompat.getColor(context,  R.color.colorMetal), android.graphics.PorterDuff.Mode.MULTIPLY);
                    txtCategoria.setText(validar.getCategoria());
                    break;
            }
            Picasso.get()
                    .load(validar.getUtlimagen())
                    .error(R.drawable.login)
                    .into(imgProducto);
            txtCantidadV.setText("Cant: " + validar.getCantidad());
            txtContenido.setText(validar.getContenido() + validar.getAbreviatura());
            checkBox.setChecked(validar.getValidado());
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    validar.setValidado(isChecked);
                }
            });

        }

    }
}
