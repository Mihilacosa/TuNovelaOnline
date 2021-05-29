package com.example.tunovelaonline;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tunovelaonline.pojos.Novela;
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
import java.util.HashMap;
import java.util.Map;

public class ModificarNovelasFragment extends Fragment {
    String equipoServidor;
    int puertoServidor = 30500;
    Socket socketCliente;
    public static final String ID_NOVELA = "com.com.example.tarea_1.ID_NOVELA";

    ArrayList<Novela> listaNovelas = new ArrayList();
    private  String usuario = "";
    String titulo, id, imagen, resena, id_usuario;
    String id_N;

    RecyclerView recyclerNovelas;
    AdaptadorNovelas adapter;

    View v;
    RequestQueue requestQueue;

    ModificarNovelaFragment novela;
    ModificarCapitulosFragment modCapitulo;
    SubirCapituloFragment subirCapitulo;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_modificar_novelas, container, false);
        equipoServidor = getString(R.string.ip_server);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            SharedPreferences datos_usu = this.getActivity().getSharedPreferences("usuario_login", Context.MODE_PRIVATE);
            usuario = datos_usu.getString("usuario", "");
            id_usuario = datos_usu.getString("id", "");
            if (!usuario.equals("")) {
                //setTitle("Hola " + usuario);
            }
        }

        new Thread(new misNovelas()).start();
        return v;
    }


    class misNovelas implements Runnable {
        @Override
        public void run() {
            try {
                socketCliente = new Socket(equipoServidor, puertoServidor);

                OutputStream os = socketCliente.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF("mis novelas");

                dos.writeUTF(id_usuario);

                ObjectInputStream ois = new ObjectInputStream(socketCliente.getInputStream());
                listaNovelas = (ArrayList<Novela>) ois.readObject();

                if(listaNovelas.isEmpty()){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "No tiene novelas subidas", Toast.LENGTH_SHORT).show();
                        }
                    });

                    getFragmentManager().beginTransaction().replace(R.id.fragment_container,new InicioFragment()).commit();
                }else{

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerNovelas = v.findViewById(R.id.RecyclerModificar);
                            recyclerNovelas.setLayoutManager(new LinearLayoutManager(getContext()));

                            adapter = new AdaptadorNovelas(listaNovelas, getActivity().getApplicationContext());
                            adapter.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    id_N = listaNovelas.get(recyclerNovelas.getChildAdapterPosition(v)).getIdNovela().toString();
                                    String titulo = listaNovelas.get(recyclerNovelas.getChildAdapterPosition(v)).getTitulo();

                                    final CharSequence[] options = { "Modificar novela", "Añadir capitulo", "Modificar capitulo", "Eliminar", "Cancelar" };
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setTitle("Opciones");
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int item) {
                                            if (options[item].equals("Modificar novela"))
                                            {
                                                Bundle bundle = new Bundle();
                                                bundle.putString("id",id_N);
                                                novela = new ModificarNovelaFragment();
                                                novela.setArguments(bundle);
                                                getFragmentManager().beginTransaction().replace(R.id.fragment_container,novela).addToBackStack( "tag" ).commit();
                                            }
                                            else if (options[item].equals("Añadir capitulo"))
                                            {
                                                Bundle bundle = new Bundle();
                                                bundle.putString("id",id_N);
                                                subirCapitulo = new SubirCapituloFragment();
                                                subirCapitulo.setArguments(bundle);
                                                getFragmentManager().beginTransaction().replace(R.id.fragment_container,subirCapitulo).addToBackStack( "tag" ).commit();
                                            }
                                            else if (options[item].equals("Modificar capitulo"))
                                            {
                                                Bundle bundle = new Bundle();
                                                bundle.putString("id",id_N);
                                                bundle.putString("titulo",titulo);
                                                modCapitulo = new ModificarCapitulosFragment();
                                                modCapitulo.setArguments(bundle);
                                                getFragmentManager().beginTransaction().replace(R.id.fragment_container,modCapitulo).addToBackStack( "tag" ).commit();
                                            }
                                            else if (options[item].equals("Eliminar"))
                                            {
                                                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        switch (which){
                                                            case DialogInterface.BUTTON_POSITIVE:

                                                                new Thread(new EliminarNovela()).start();
                                                                new android.os.Handler().postDelayed(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        getFragmentManager().beginTransaction().replace(R.id.fragment_container,new ModificarNovelasFragment()).commit();
                                                                    }
                                                                },1000); // milliseconds: 1 seg.

                                                                break;
                                                            case DialogInterface.BUTTON_NEGATIVE:
                                                                //No button clicked
                                                                break;
                                                        }
                                                    }
                                                };

                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setMessage("Estasn seguro de eliminar la novela?").setPositiveButton("Si", dialogClickListener)
                                                        .setNegativeButton("No", dialogClickListener).show();
                                            }
                                            else if (options[item].equals("Cancelar")) {
                                                dialog.dismiss();
                                            }
                                        }
                                    });
                                    builder.show();

                                }
                            });
                            recyclerNovelas.setAdapter(adapter);
                        }
                    });
                }

                os.close();
                dos.close();
                ois.close();


                socketCliente.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    class EliminarNovela implements Runnable {
        @Override
        public void run() {
            try {
                socketCliente = new Socket(equipoServidor, puertoServidor);

                OutputStream os = socketCliente.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF("eliminar novela");

                os = socketCliente.getOutputStream();
                dos = new DataOutputStream(os);
                dos.writeUTF(id_N);

                os.close();
                dos.close();

                socketCliente.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
