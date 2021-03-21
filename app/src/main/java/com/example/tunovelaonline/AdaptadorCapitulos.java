package com.example.tunovelaonline;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdaptadorCapitulos extends RecyclerView.Adapter<AdaptadorCapitulos.ViewHolderCapitulos> implements View.OnClickListener {

    ArrayList<ListaCapitulos> listaCapitulos;
    private View.OnClickListener listener;

    public AdaptadorCapitulos(ArrayList<ListaCapitulos> listaCapitulos) {
        this.listaCapitulos = listaCapitulos;
    }

    @NonNull
    @Override
    public ViewHolderCapitulos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_capitulos, parent, false);

        view.setOnClickListener(this);
        return new ViewHolderCapitulos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderCapitulos holder, int position) {
        holder.capitulos.setText(listaCapitulos.get(position).getCapitulo());
        holder.capitulos.setId(Integer.parseInt(listaCapitulos.get(position).getId()));
    }

    @Override
    public int getItemCount() {
        return listaCapitulos.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if(listener != null){
            listener.onClick(v);
        }
    }

    public class ViewHolderCapitulos extends RecyclerView.ViewHolder {

        TextView capitulos;

        public ViewHolderCapitulos(@NonNull View itemView) {
            super(itemView);
            capitulos = itemView.findViewById(R.id.capitulox);
        }
    }
}
