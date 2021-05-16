package com.example.tunovelaonline;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.tunovelaonline.pojos.Capitulo;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class CapituloFragment extends Fragment {
    String equipoServidor;
    int puertoServidor = 30500;
    Socket socketCliente;
    Capitulo capitulo;

    private TextView Titulo;
    private TextView Contenido;
    private String id_novela;
    private String id_capitulo;
    private ArrayList<Integer> Capitulos_id = new ArrayList<>();
    private int posicion = 0;
    private Integer id_cap;
    private ScrollView scroll;

    private  String usuario = "";
    private FirebaseAuth mAuth;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_capitulo, container, false);
        equipoServidor = getString(R.string.ip_server);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            SharedPreferences datos_usu = this.getActivity().getSharedPreferences("usuario_login", Context.MODE_PRIVATE);
            usuario = datos_usu.getString("usuario", "");
            if (!usuario.equals("")) {
                //setTitle("Hola " + usuario);
            }
        }

        Button Anterior = v.findViewById(R.id.Anterior);
        Button Indice = v.findViewById(R.id.Indice);
        Button Siguiente = v.findViewById(R.id.Siguiente);

        Button Anterior2 = v.findViewById(R.id.Anterior2);
        Button Indice2 = v.findViewById(R.id.Indice2);
        Button Siguiente2 = v.findViewById(R.id.Siguiente2);

        scroll = v.findViewById(R.id.Cap_scroll);

        Bundle bundle = this.getArguments();
        id_novela = bundle.getString("id_nov");
        id_capitulo = bundle.getString("id_cap");
        id_cap = Integer.valueOf(id_capitulo);
        Capitulos_id = (ArrayList<Integer>) bundle.getSerializable("ARRAYLIST");

        for (int i = 0; i < Capitulos_id.size(); i++){
            if(Capitulos_id.get(i).equals(id_cap)){
                posicion = i;
            }
        }

        int cap_max = Capitulos_id.size() - 1;

        if(posicion == 0){
            Anterior.setEnabled(false);
            Anterior2.setEnabled(false);
        }

        if(posicion == cap_max){
            Siguiente.setEnabled(false);
            Siguiente2.setEnabled(false);
        }

        Titulo = v.findViewById(R.id.Titulo_cap);
        Contenido = v.findViewById(R.id.Contenido_cap);


        new Thread(new CargarCapitulo()).start();

        Anterior.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if(posicion != cap_max){
                    Siguiente.setEnabled(true);
                }

                posicion = posicion - 1;
                id_cap = Capitulos_id.get(posicion);
                id_capitulo =  String.valueOf(Capitulos_id.get(posicion));
                new Thread(new CargarCapitulo()).start();

                if(posicion == 0){
                    Anterior.setEnabled(false);
                }else{
                    Anterior.setEnabled(true);
                }

                scroll.fullScroll(ScrollView.FOCUS_UP);
            }
        });

        Anterior2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if(posicion != cap_max){
                    Siguiente2.setEnabled(true);
                }

                posicion = posicion - 1;
                id_cap = Capitulos_id.get(posicion);
                id_capitulo =  String.valueOf(Capitulos_id.get(posicion));
                new Thread(new CargarCapitulo()).start();

                if(posicion == 0){
                    Anterior2.setEnabled(false);
                }else{
                    Anterior2.setEnabled(true);
                }

                scroll.fullScroll(ScrollView.FOCUS_UP);
            }
        });

        Siguiente.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                posicion = posicion + 1;
                id_cap = Capitulos_id.get(posicion);
                id_capitulo =  String.valueOf(Capitulos_id.get(posicion));
                new Thread(new CargarCapitulo()).start();

                if(posicion == cap_max){
                    Siguiente.setEnabled(false);
                }else{
                    Siguiente.setEnabled(true);
                }

                if(posicion != 0){
                    Anterior.setEnabled(true);
                }

                scroll.fullScroll(ScrollView.FOCUS_UP);
            }
        });

        Siguiente2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                posicion = posicion + 1;
                id_cap = Capitulos_id.get(posicion);
                id_capitulo =  String.valueOf(Capitulos_id.get(posicion));
                new Thread(new CargarCapitulo()).start();

                if(posicion == cap_max){
                    Siguiente2.setEnabled(false);
                }else{
                    Siguiente2.setEnabled(true);
                }

                if(posicion != 0){
                    Anterior2.setEnabled(true);
                }

                scroll.fullScroll(ScrollView.FOCUS_UP);
            }
        });

        Indice.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                VolverNovela();
            }
        });

        Indice2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                VolverNovela();
            }
        });

        return v;
    }


    //Volver a novela
    private void VolverNovela() {
        Bundle bundle = new Bundle();
        bundle.putString("id",id_novela);
        NovelaFragment novela = new NovelaFragment();
        novela.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.fragment_container,novela).commit();
    }

    class CargarCapitulo implements Runnable {
        @Override
        public void run() {
            try {
                socketCliente = new Socket(equipoServidor, puertoServidor);

                OutputStream os = socketCliente.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF("mostrar capitulo");

                os = socketCliente.getOutputStream();
                dos = new DataOutputStream(os);
                dos.writeUTF(id_capitulo);

                ObjectInputStream ois = new ObjectInputStream(socketCliente.getInputStream());
                capitulo = (Capitulo) ois.readObject();

                os.close();
                dos.close();
                ois.close();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Titulo.setText("Capítulo: " + capitulo.getNumCapitulo() + " - " + capitulo.getTitulo());
                        Contenido.setText(capitulo.getContenido());
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
}
