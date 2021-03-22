package com.example.tunovelaonline;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tunovelaonline.pojos.Novela;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdaptadorNovelas extends RecyclerView.Adapter<AdaptadorNovelas.ViewHolderNovelas> implements View.OnClickListener{

    ArrayList<Novela> listaNovelas;
    private View.OnClickListener listener;
    private LayoutInflater mInflater;
    private Context context;

    public AdaptadorNovelas(ArrayList<Novela> listaNovelas, Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.listaNovelas = listaNovelas;
    }

    @Override
    public ViewHolderNovelas onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.from(parent.getContext()).inflate(R.layout.lista_novelas, parent,false);
        view.setOnClickListener(this);
        return new ViewHolderNovelas(view);
    }

    @Override
    public void onBindViewHolder(ViewHolderNovelas holder, int position) {
        holder.card.setAnimation(AnimationUtils.loadAnimation(context, R.anim.slide));
        holder.Titulo.setText(listaNovelas.get(position).getTitulo());
        holder.id.setText(listaNovelas.get(position).getIdNovela().toString());
        //holder.imagen.setImageURI(Uri.parse(listaNovelas.get(position).getImagen()));
        Picasso.get().load(listaNovelas.get(position).getPortada()).into(holder.imagen);
    }

    @Override
    public int getItemCount() {
        return listaNovelas.size();
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

    public class ViewHolderNovelas extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{

        TextView Titulo, id;
        ImageView imagen;
        CardView card;

        public ViewHolderNovelas(View itemView) {
            super(itemView);
            Titulo = itemView.findViewById(R.id.titulox);
            id = itemView.findViewById(R.id.id_novelax);
            imagen = itemView.findViewById(R.id.portadax);
            card = itemView.findViewById(R.id.cardx);
            card.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Seleccione una opción");
            menu.add(this.getAdapterPosition(), 120, 0, "Reseña");
        }
    }

    public String mostrarResena (int position){
        String resena = listaNovelas.get(position).getResena();
        return resena;
    }
}
