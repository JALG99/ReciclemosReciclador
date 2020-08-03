package com.upc.reciclemosreciclador.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.upc.reciclemosreciclador.Activities.CondominioActivity;
import com.upc.reciclemosreciclador.Entities.Condominio;
import com.upc.reciclemosreciclador.Fragments.CondominioSelectedFragment;
import com.upc.reciclemosreciclador.R;

import java.util.ArrayList;

public class ListaCondominioAdapter extends RecyclerView.Adapter<ListaCondominioAdapter.ViewHolder> {

    private ArrayList<Condominio> listCondominio;
    private Context context;
    private int position;

    public ListaCondominioAdapter(Context context){
        this.context = context;
        listCondominio = new ArrayList<>();
        position = 0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.condominolist,parent,false);
        final ViewHolder viewHolder = new ViewHolder(view);
       viewHolder.txt_MasInfo.setOnClickListener(v -> {
           CondominioSelectedFragment detalleCondominioFragment = new CondominioSelectedFragment(listCondominio.get(viewHolder.getAdapterPosition()).getCodigo());
           FragmentTransaction transaction= ((CondominioActivity)context).getSupportFragmentManager().beginTransaction();
           transaction.replace(R.id.fragment, detalleCondominioFragment);
           transaction.commit();
       });
        return viewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Condominio c = listCondominio.get(position);
        holder.txt_NombreCondominio.setText(c.getNombre());
        holder.txt_NombreUrbanizacion.setText(c.getUrbanizacion());
        holder.txt_NombreDistrito.setText(c.getDistrito().getNombre());
    }

    @Override
    public int getItemCount() {
        return listCondominio.size();
    }

    public  void adicionarCondominios(ArrayList<Condominio> listCondominio){
        this.listCondominio.addAll(listCondominio);
        notifyDataSetChanged();
    }

    public int getPos() {
        return position;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        private ConstraintLayout item_Condominios;
        private TextView txt_NombreCondominio;
        private TextView txt_NombreUrbanizacion;
        private TextView txt_NombreDistrito;
        private TextView txt_MasInfo;
        OnNoteListener onNoteListener;

        public ViewHolder(View itemView  ){
            super(itemView);
            item_Condominios = itemView.findViewById(R.id.item_CondominiosList);
            txt_NombreCondominio = itemView.findViewById(R.id.txt_CondominioName);
            txt_NombreUrbanizacion = itemView.findViewById(R.id.txt_CondominioUrbanizacion);
            txt_NombreDistrito = itemView.findViewById(R.id.txt_CondominioDistrito);
            txt_MasInfo = itemView.findViewById(R.id.txtMasInfo);
        }

    }

    public interface OnNoteListener{
        void oneNoteClick(int position);
    }

    public void filterList(ArrayList<Condominio> filteredList){
        listCondominio = filteredList;
        notifyDataSetChanged();
    }
}
