package com.example.tunovelaonline;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tunovelaonline.pojos.Novela;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AdaptadorMarcapaginas extends RecyclerView.Adapter<AdaptadorMarcapaginas.ViewHolderNovelas> implements View.OnClickListener{

    ArrayList<Novela> listaNovelas;
    private View.OnClickListener listener;
    private LayoutInflater mInflater;
    private Context context;

    public AdaptadorMarcapaginas(ArrayList<Novela> listaNovelas, Context context) {
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
        Picasso.get().load(listaNovelas.get(position).getPortada()).into(holder.imagen);

        holder.ultimo_id.setText(listaNovelas.get(position).getListaCapitulos().get(0).getIdCapitulo().toString());
        holder.U_contenido.setText("Capítulo " + listaNovelas.get(position).getListaCapitulos().get(0).getNumCapitulo().toString() + " - " + listaNovelas.get(position).getListaCapitulos().get(0).getTitulo());
        holder.U_hace.setText(Hace(listaNovelas.get(position).getListaCapitulos().get(0).getFechaSubida()));

        if(listaNovelas.get(position).getListaCapitulos().size() == 1){
            holder.penultimo.setVisibility(View.GONE);
        }else{
            holder.penultimo_id.setText(listaNovelas.get(position).getListaCapitulos().get(1).getIdCapitulo().toString());
            holder.P_contenido.setText("Capítulo " + listaNovelas.get(position).getListaCapitulos().get(1).getNumCapitulo().toString() + " - " + listaNovelas.get(position).getListaCapitulos().get(1).getTitulo());
            holder.P_hace.setText(Hace(listaNovelas.get(position).getListaCapitulos().get(1).getFechaSubida()));
        }

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
        //novela
        TextView Titulo, id;
        ImageView imagen;
        CardView card;

        //cap1
        LinearLayout ultimo;
        TextView ultimo_id, U_contenido, U_hace;

        //cap2
        LinearLayout penultimo;
        TextView penultimo_id, P_contenido, P_hace;


        public ViewHolderNovelas(View itemView) {
            super(itemView);
            Titulo = itemView.findViewById(R.id.titulox);
            id = itemView.findViewById(R.id.id_novelax);
            imagen = itemView.findViewById(R.id.portadax);
            card = itemView.findViewById(R.id.cardx);
            card.setOnCreateContextMenuListener(this);

            ultimo = itemView.findViewById(R.id.ultimoCapitulo);
            ultimo_id = itemView.findViewById(R.id.ultimoId);
            U_contenido = itemView.findViewById(R.id.capituloUltima);
            U_hace = itemView.findViewById(R.id.tiempoUltimo);

            penultimo = itemView.findViewById(R.id.penultimoCapitulo);
            penultimo_id = itemView.findViewById(R.id.penultimoId);
            P_contenido = itemView.findViewById(R.id.capituloPenultimo);
            P_hace = itemView.findViewById(R.id.tiempoPenultimo);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Seleccione una opción");
            menu.add(this.getAdapterPosition(), 120, 0, "Reseña");
            menu.add(this.getAdapterPosition(), 121, 0, "Ultimo capítulo visto");
            menu.add(this.getAdapterPosition(), 122, 0, "Ultimo capítulo");
        }
    }

    public String mostrarResena (int position){
        String resena = listaNovelas.get(position).getResena();
        return resena;
    }

    public String mostrarTitulo (int position){
        String titulo = listaNovelas.get(position).getTitulo();
        return titulo;
    }

    public String mostrarId (int position) {
        String id = String.valueOf(listaNovelas.get(position).getIdNovela());
        return id;
    }

    public String mostrarId_U (int position) {
        String id = String.valueOf(listaNovelas.get(position).getListaCapitulos().get(0).getIdCapitulo());
        return id;
    }

    public String mostrarId_P (int position) {
        String id = String.valueOf(listaNovelas.get(position).getListaCapitulos().get(1).getIdCapitulo());
        return id;
    }

    public int tamano (int position) {
        int ta = listaNovelas.get(position).getListaCapitulos().size();
        return ta;
    }

    public String Hace(String fecha){
        long time = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            time = sdf.parse(fecha).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date currentTime = Calendar.getInstance().getTime();

        PrettyTime prettyTime = new PrettyTime(new Locale("ES"));
        return prettyTime.format(new Date(time));
    }
}
