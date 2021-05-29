package com.example.tunovelaonline;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.tunovelaonline.pojos.Capitulo;
import com.example.tunovelaonline.pojos.Novela;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class NovelaFragment extends Fragment {
    String equipoServidor,fecha_sus;
    int puertoServidor = 30500;
    Socket socketCliente;
    Novela novela;
    Date date;
    private TextView Titulo_novela;
    private TextView resena;
    private ImageView portada, imgDespliegue;
    private ListView Lista_caps;
    private ArrayList<Capitulo> Lista = new ArrayList<>();
    private ArrayList<Integer> Capitulos_id = new ArrayList<>();
    private String id_capitulo;
    private String id_novela;
    Boolean suscrito = false;
    ImageView marc;
    Integer cambio_marc = 0;
    View v;
    String id_usuario;
    Boolean marcado = false;

    RecyclerView recyclerCapitulos;
    LinearLayout desplegable, Adesplegar;
    TextView tit_alt, autor, artista, traductor, genero, fecha;
    private AdView mAdView;
    FirebaseAuth mAuth;

    private  String usuario = "";
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_novela, container, false);
        equipoServidor = getString(R.string.ip_server);
        tit_alt = v.findViewById(R.id.Novela_titulo_alternativo);
        autor = v.findViewById(R.id.Novela_autor);
        artista = v.findViewById(R.id.Novela_artista);
        traductor = v.findViewById(R.id.Novela_traductor);
        genero = v.findViewById(R.id.Novela_genero);
        fecha = v.findViewById(R.id.Novela_fecha);

        desplegable = v.findViewById(R.id.desplegable);
        Adesplegar = v.findViewById(R.id.Adesplegar);
        Adesplegar.setVisibility(View.GONE);
        imgDespliegue = v.findViewById(R.id.imgDesplegable);

        marc = v.findViewById(R.id.btn_marc);
        marc.setImageResource(R.drawable.ic_marc_mas);

        tit_alt.setVisibility(View.GONE);
        artista.setVisibility(View.GONE);
        traductor.setVisibility(View.GONE);

        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            SharedPreferences datos_usu = this.getActivity().getSharedPreferences("usuario_login", Context.MODE_PRIVATE);
            id_usuario = datos_usu.getString("id", "");
            usuario = datos_usu.getString("usuario", "");
            fecha_sus = datos_usu.getString("suscripcion", "");

            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(fecha_sus);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String hoy = simpleDateFormat.format(new Date());
                Date hoyFecha = new SimpleDateFormat("yyyy-MM-dd").parse(hoy);

                if(hoyFecha.before(date)){
                    suscrito = true;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            marc.setVisibility(View.GONE);
        }

        mAdView = v.findViewById(R.id.adView);
        if(suscrito == false){
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }else{
            mAdView.setVisibility(View.GONE);
        }

        Bundle bundle = this.getArguments();
        id_novela = bundle.getString("id");

        new Thread(new CargarNovela()).start();

        Titulo_novela = v.findViewById(R.id.Titulo_dif);
        resena = v.findViewById(R.id.Resena_novela_selec);
        portada = v.findViewById(R.id.Portada_novela_selec);

        desplegable.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if(Adesplegar.getVisibility() == View.GONE){
                    Adesplegar.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide));
                    imgDespliegue.setImageResource(R.drawable.ic_arriba);
                    Adesplegar.setVisibility(View.VISIBLE);
                }else{
                    imgDespliegue.setImageResource(R.drawable.ic_abajo);
                    Adesplegar.setVisibility(View.GONE);
                }

            }
        });

        marc.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if(cambio_marc == 0){
                    marc.setImageResource(R.drawable.ic_marc_mas);
                    cambio_marc = 1;
                    new Thread(new EliminarMarca()).start();
                }else{
                    marc.setImageResource(R.drawable.ic_marc_menos);
                    cambio_marc = 0;
                    new Thread(new CrearMarca()).start();
                }
            }
        });

        return v;
    }

    //carga novela y capitulos

    class CargarNovela implements Runnable {
        @Override
        public void run() {
            try {
                socketCliente = new Socket(equipoServidor, puertoServidor);

                OutputStream os = socketCliente.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF("mostrar novela");

                os = socketCliente.getOutputStream();
                dos = new DataOutputStream(os);
                dos.writeUTF(id_novela);


                ObjectInputStream ois = new ObjectInputStream(socketCliente.getInputStream());
                novela = (Novela) ois.readObject();

                os.close();
                dos.close();
                ois.close();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Titulo_novela.setText(novela.getTitulo());
                        resena.setText(novela.getResena());
                        Picasso.get().load(novela.getPortada()).into(portada);

                        if(novela.getNombreAlternativo() == null || novela.getNombreAlternativo().isEmpty()){

                        }else {
                            tit_alt.setVisibility(View.VISIBLE);
                            tit_alt.setText(tit_alt.getText() + novela.getNombreAlternativo());
                        }

                        autor.setText(autor.getText() + novela.getAutor());

                        if(novela.getArtista() == null || novela.getArtista().isEmpty()){

                        }else {
                            artista.setVisibility(View.VISIBLE);
                            artista.setText(artista.getText() + novela.getArtista());
                        }


                        if(novela.getTraductor() == null || novela.getTraductor().isEmpty()){

                        }else {
                            traductor.setVisibility(View.VISIBLE);
                            traductor.setText(traductor.getText() + novela.getTraductor());
                        }

                        genero.setText(genero.getText() + novela.getGenero());
                        fecha.setText(fecha.getText() + novela.getFechaSubida().toString());
                    }
                });

                socketCliente.close();

                new Thread(new CargarCapitulos()).start();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    class CargarCapitulos implements Runnable {
        @Override
        public void run() {
            try {
                socketCliente = new Socket(equipoServidor, puertoServidor);

                OutputStream os = socketCliente.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF("mostrar capitulos");

                os = socketCliente.getOutputStream();
                dos = new DataOutputStream(os);
                dos.writeUTF(id_novela);

                ObjectInputStream ois = new ObjectInputStream(socketCliente.getInputStream());
                Lista = (ArrayList<Capitulo>) ois.readObject();

                os.close();
                dos.close();
                ois.close();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Iterator<Capitulo> caps = Lista.iterator();
                        while(caps.hasNext()){
                            Capitulos_id.add(caps.next().getIdCapitulo());
                        }

                        recyclerCapitulos = v.findViewById(R.id.RecyclerCapitulos);
                        recyclerCapitulos.setLayoutManager(new LinearLayoutManager(getContext()));

                        AdaptadorCapitulos adapter = new AdaptadorCapitulos(Lista);

                        adapter.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                id_capitulo = Lista.get(recyclerCapitulos.getChildAdapterPosition(v)).getIdCapitulo().toString();

                                Bundle bundle = new Bundle();
                                bundle.putString("id_nov",id_novela);
                                bundle.putString("id_cap",id_capitulo);
                                bundle.putSerializable("ARRAYLIST", Capitulos_id);
                                CapituloFragment capitulo = new CapituloFragment();
                                capitulo.setArguments(bundle);
                                getFragmentManager().beginTransaction().replace(R.id.fragment_container,capitulo).addToBackStack( "tag" ).commit();
                            }
                        });

                        recyclerCapitulos.setAdapter(adapter);

                        if(mAuth.getCurrentUser() != null){
                            new Thread(new ComprobarMarca()).start();
                        }
                    }
                });

                socketCliente.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    class ComprobarMarca implements Runnable {
        @Override
        public void run() {
            try {
                socketCliente = new Socket(equipoServidor, puertoServidor);

                OutputStream os = socketCliente.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF("comprobar marcapaginas");

                dos.writeUTF(id_usuario);

                dos.writeUTF(id_novela);

                InputStream is = socketCliente.getInputStream();
                DataInputStream dis = new DataInputStream(is);
                if(dis.readUTF().equals("gg")){
                    marcado = true;
                }else{
                    marcado = false;
                }

                os.close();
                dos.close();
                is.close();
                dis.close();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(marcado == true){
                            marc.setImageResource(R.drawable.ic_marc_menos);
                            cambio_marc = 0;
                        }else{
                            marc.setImageResource(R.drawable.ic_marc_mas);
                            cambio_marc = 1;
                        }
                    }
                });

                socketCliente.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class EliminarMarca implements Runnable {
        @Override
        public void run() {
            try {
                socketCliente = new Socket(equipoServidor, puertoServidor);

                OutputStream os = socketCliente.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF("eliminar marcapaginas");

                dos.writeUTF(id_usuario);

                dos.writeUTF(id_novela);

                os.close();
                dos.close();

                socketCliente.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class CrearMarca implements Runnable {
        @Override
        public void run() {
            try {
                socketCliente = new Socket(equipoServidor, puertoServidor);

                OutputStream os = socketCliente.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF("crear marcapaginas");

                dos.writeUTF(id_usuario);
                dos.writeUTF(id_novela);
                dos.writeUTF(Lista.get(0).getIdCapitulo().toString());

                os.close();
                dos.close();

                socketCliente.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
