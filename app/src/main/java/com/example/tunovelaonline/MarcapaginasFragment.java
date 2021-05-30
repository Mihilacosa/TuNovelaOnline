package com.example.tunovelaonline;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tunovelaonline.pojos.Novela;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MarcapaginasFragment extends Fragment {
    String equipoServidor;
    int puertoServidor = 30500;
    Socket socketCliente;

    Novela novela;
    NovelaFragment fragNovela;
    ArrayList<Novela> listaNovelas = new ArrayList();
    String id_N, id_capU, id_capP;

    RecyclerView recyclerNovelas;
    AdaptadorMarcapaginas adapter;
    CapituloFragment capitulo;

    String id_usuario, id_novela;
    private FirebaseAuth mAuth;
    View view;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_marcapaginas, container, false);
        equipoServidor = getString(R.string.ip_server);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            SharedPreferences datos_usu = this.getActivity().getSharedPreferences("usuario_login", Context.MODE_PRIVATE);
            id_usuario = datos_usu.getString("id", "");
        }

        new Thread(new Marcapaginas()).start();
        return view;
    }

    class Marcapaginas implements Runnable {
        @Override
        public void run() {
            try {
                socketCliente = new Socket(equipoServidor, puertoServidor);

                OutputStream os = socketCliente.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF("marcapaginas");

                dos.writeUTF(id_usuario);

                ObjectInputStream ois = new ObjectInputStream(socketCliente.getInputStream());
                listaNovelas = (ArrayList<Novela>) ois.readObject();

                if(listaNovelas.isEmpty()){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "No tiene novelas marcadas", Toast.LENGTH_SHORT).show();
                        }
                    });

                    getFragmentManager().beginTransaction().replace(R.id.fragment_container,new InicioFragment()).commit();
                }else{

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerNovelas = view.findViewById(R.id.RecyclerMarcapaginas);
                            recyclerNovelas.setLayoutManager(new LinearLayoutManager(getContext()));

                            adapter = new AdaptadorMarcapaginas(listaNovelas, getActivity().getApplicationContext());
                            adapter.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    id_N = listaNovelas.get(recyclerNovelas.getChildAdapterPosition(v)).getIdNovela().toString();

                                    Bundle bundle = new Bundle();
                                    bundle.putString("id",id_N);
                                    fragNovela = new NovelaFragment();
                                    fragNovela.setArguments(bundle);
                                    getFragmentManager().beginTransaction().replace(R.id.fragment_container,fragNovela).addToBackStack( "tag" ).commit();
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


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case 120:
                id_novela = adapter.mostrarId(item.getGroupId());

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:

                                new Thread(new EliminarMarca()).start();

                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Estasn seguro de eliminar el marcapaginas?").setPositiveButton("Si", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

                return true;
            case 121:
                id_N = adapter.mostrarId(item.getGroupId());
                id_capU = adapter.mostrarId_U(item.getGroupId());

                Bundle bundle = new Bundle();
                bundle.putString("id_nov",id_N);
                bundle.putString("id_cap",id_capU);
                capitulo = new CapituloFragment();
                capitulo.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,capitulo).addToBackStack( "tag" ).commit();
                return true;
            case 122:
                id_N = adapter.mostrarId(item.getGroupId());
                id_capP = adapter.mostrarId_P(item.getGroupId());

                Bundle bundle2 = new Bundle();
                bundle2.putString("id_nov",id_N);
                bundle2.putString("id_cap",id_capP);
                capitulo = new CapituloFragment();
                capitulo.setArguments(bundle2);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,capitulo).addToBackStack( "tag" ).commit();

                return true;

            default:
                return super.onContextItemSelected(item);
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

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getFragmentManager().beginTransaction().replace(R.id.fragment_container,new MarcapaginasFragment()).commit();
                            }
                        },500); // milliseconds: 1 seg.
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
