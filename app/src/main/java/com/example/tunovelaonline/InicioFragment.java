package com.example.tunovelaonline;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tunovelaonline.pojos.Novela;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class InicioFragment extends Fragment {
    String equipoServidor,id_usuario,fecha = "";
    int puertoServidor = 30500;
    Socket socketCliente;
    Date date;
    private  String usuario = "", id_N, id_capU, id_capP;
    int id_select, id_novela;

    ArrayList<Novela> listaNovelas = new ArrayList();
    RecyclerView recyclerNovelas;
    AdaptadorNovelas adapter;

    View view;
    NovelaFragment novela;
    CapituloFragment capitulo;
    TextView name;
    Context contexto;
    private InterstitialAd interstitialAd;
    FirebaseAuth mAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_inicio, container, false);
        equipoServidor = getString(R.string.ip_server);
        contexto = container.getContext();
        new Thread(new Cargar()).start();
        loadInterstitialAd();
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            SharedPreferences datos_usu = this.getActivity().getSharedPreferences("usuario_login", Context.MODE_PRIVATE);
            usuario = datos_usu.getString("usuario", "");
            id_usuario = datos_usu.getString("id", "");
            fecha = datos_usu.getString("suscripcion", "");
        }

        return view;
    }

    private void loadInterstitialAd() {
        interstitialAd = new InterstitialAd(getActivity());
        interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/3419835294");
        interstitialAd.loadAd(new AdRequest.Builder().build());
    }

    class Cargar implements Runnable {
        @Override
        public void run() {
            try {
                socketCliente = new Socket(equipoServidor, puertoServidor);

                OutputStream os = socketCliente.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF("mostrar ultimas actualizaciones");


                ObjectInputStream ois = new ObjectInputStream(socketCliente.getInputStream());
                listaNovelas = (ArrayList<Novela>) ois.readObject();

                os.close();
                dos.close();
                ois.close();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (fecha.length() < 4 && mAuth.getCurrentUser() != null) {
                            new Thread(new FechaSuscripcion()).start();
                        }
                        recyclerNovelas = view.findViewById(R.id.ReyclerId);
                        recyclerNovelas.setLayoutManager(new LinearLayoutManager(getContext()));

                        adapter = new AdaptadorNovelas(listaNovelas, getActivity().getApplicationContext());
                        adapter.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(fecha.length() == 4){
                                    id_N = listaNovelas.get(recyclerNovelas.getChildAdapterPosition(v)).getIdNovela().toString();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("id",id_N);
                                    novela = new NovelaFragment();
                                    novela.setArguments(bundle);
                                    getFragmentManager().beginTransaction().replace(R.id.fragment_container,novela).addToBackStack( "tag" ).commit();
                                }else{
                                    interstitialAd.show();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            id_N = listaNovelas.get(recyclerNovelas.getChildAdapterPosition(v)).getIdNovela().toString();
                                            Bundle bundle = new Bundle();
                                            bundle.putString("id",id_N);
                                            novela = new NovelaFragment();
                                            novela.setArguments(bundle);
                                            getFragmentManager().beginTransaction().replace(R.id.fragment_container,novela).addToBackStack( "tag" ).commit();
                                        }
                                    }, 200);
                                }
                            }
                        });
                        recyclerNovelas.setAdapter(adapter);
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

    class FechaSuscripcion implements Runnable {
        @Override
        public void run() {
            try {
                socketCliente = new Socket(equipoServidor, puertoServidor);

                OutputStream os = socketCliente.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF("fecha suscripcion");

                dos.writeUTF(id_usuario);

                InputStream is2 = socketCliente.getInputStream();
                DataInputStream dis2 = new DataInputStream(is2);
                fecha = dis2.readUTF();

                date = new SimpleDateFormat("yyyy-MM-dd").parse(fecha);

                os.close();
                dos.close();
                is2.close();
                dis2.close();

                socketCliente.close();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences datos_usu = contexto.getSharedPreferences("usuario_login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = datos_usu.edit();

                        if(fecha.length() > 2){
                            try {
                                date = new SimpleDateFormat("yyyy-MM-dd").parse(fecha);

                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                String hoy = simpleDateFormat.format(new Date());
                                Date hoyFecha = new SimpleDateFormat("yyyy-MM-dd").parse(hoy);

                                if(hoyFecha.before(date)){
                                    editor.putString("suscripcion", "true");
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        editor.putString("fecha_sus", fecha);
                        editor.apply();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case 120:
                String resena2 = adapter.mostrarResena(item.getGroupId());
                String titulo2 = adapter.mostrarTitulo(item.getGroupId());
                AlertDialog.Builder resenya = new AlertDialog.Builder(getContext());
                resenya.setMessage("Titulo: " + titulo2 + "\n\nReseña \n\n" + resena2);
                resenya.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                resenya.show();
                return true;
            case 121:
                if(fecha.length() == 4){
                    id_N = adapter.mostrarId(item.getGroupId());
                    id_capU = adapter.mostrarId_U(item.getGroupId());

                    Bundle bundle = new Bundle();
                    bundle.putString("id_nov",id_N);
                    bundle.putString("id_cap",id_capU);
                    capitulo = new CapituloFragment();
                    capitulo.setArguments(bundle);
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container,capitulo).addToBackStack( "tag" ).commit();
                }else{
                    interstitialAd.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            id_N = adapter.mostrarId(item.getGroupId());
                            id_capU = adapter.mostrarId_U(item.getGroupId());

                            Bundle bundle = new Bundle();
                            bundle.putString("id_nov",id_N);
                            bundle.putString("id_cap",id_capU);
                            capitulo = new CapituloFragment();
                            capitulo.setArguments(bundle);
                            getFragmentManager().beginTransaction().replace(R.id.fragment_container,capitulo).addToBackStack( "tag" ).commit();
                        }
                    }, 200);
                }
                return true;
            case 122:
                if(adapter.tamano(item.getGroupId()) == 1){
                    Toast.makeText(getContext(), "Esta novela solo tiene un capítulo.", Toast.LENGTH_SHORT).show();
                }else{
                    if(fecha.length() == 4){
                        id_N = adapter.mostrarId(item.getGroupId());
                        id_capP = adapter.mostrarId_P(item.getGroupId());

                        Bundle bundle2 = new Bundle();
                        bundle2.putString("id_nov",id_N);
                        bundle2.putString("id_cap",id_capP);
                        capitulo = new CapituloFragment();
                        capitulo.setArguments(bundle2);
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container,capitulo).addToBackStack( "tag" ).commit();
                    }else{
                        interstitialAd.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                id_N = adapter.mostrarId(item.getGroupId());
                                id_capP = adapter.mostrarId_P(item.getGroupId());

                                Bundle bundle2 = new Bundle();
                                bundle2.putString("id_nov",id_N);
                                bundle2.putString("id_cap",id_capP);
                                capitulo = new CapituloFragment();
                                capitulo.setArguments(bundle2);
                                getFragmentManager().beginTransaction().replace(R.id.fragment_container,capitulo).addToBackStack( "tag" ).commit();
                            }
                        }, 200);
                    }
                }
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }
}
