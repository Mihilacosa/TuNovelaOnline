package com.example.tunovelaonline;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tunovelaonline.pojos.Capitulo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ModificarCapitulosFragment extends Fragment {
    String equipoServidor;
    int puertoServidor = 30500;
    Socket socketCliente;
    private ArrayList<Capitulo> Lista = new ArrayList<>();

    View view;

    private String id_novela;
    private String id_capitulo;
    private String titulo_Novela;

    RecyclerView recyclerCapitulos;
    TextView titulo_novela;
    ModificarCapitulosFragment modCapitulo;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_modificar_capitulos, container, false);
        equipoServidor = getString(R.string.ip_server);
        titulo_novela = view.findViewById(R.id.titulo_novela_modificar);

        Bundle bundle = this.getArguments();
        id_novela = bundle.getString("id");
        titulo_Novela = bundle.getString("titulo");

        titulo_novela.setText(titulo_Novela);

        new Thread(new CargarCapitulos()).start();

        return view;
    }

    class CargarCapitulos implements Runnable {
        @Override
        public void run() {
            try {
                socketCliente = new Socket(equipoServidor, puertoServidor);

                OutputStream os = socketCliente.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF("mostrar capitulos modificar");

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
                        recyclerCapitulos = view.findViewById(R.id.RecyclerCapitulosMod);
                        recyclerCapitulos.setLayoutManager(new LinearLayoutManager(getContext()));

                        AdaptadorCapitulos adapter = new AdaptadorCapitulos(Lista);

                        adapter.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                id_capitulo = Lista.get(recyclerCapitulos.getChildAdapterPosition(v)).getIdCapitulo().toString();

                                final CharSequence[] options = { "Modificar capitulo", "Eliminar", "Cancelar" };
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Opciones");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int item) {
                                        if (options[item].equals("Modificar capitulo")) {

                                            Bundle bundle = new Bundle();
                                            bundle.putString("id",id_capitulo);
                                            bundle.putString("id_novela",id_novela);
                                            bundle.putString("titulo",titulo_Novela);
                                            ModificarCapituloFragment capitulo_mod = new ModificarCapituloFragment();
                                            capitulo_mod.setArguments(bundle);
                                            getFragmentManager().beginTransaction().replace(R.id.fragment_container,capitulo_mod).addToBackStack( "tag" ).commit();
                                        }
                                        else if (options[item].equals("Eliminar"))
                                        {
                                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    switch (which){
                                                        case DialogInterface.BUTTON_POSITIVE:

                                                            new Thread(new EliminarCapitulo()).start();

                                                            break;
                                                        case DialogInterface.BUTTON_NEGATIVE:
                                                            //No button clicked
                                                            break;
                                                    }
                                                }
                                            };

                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                            builder.setMessage("Estasn seguro de eliminar el capitulo?").setPositiveButton("Si", dialogClickListener)
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

                        recyclerCapitulos.setAdapter(adapter);
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

    class EliminarCapitulo implements Runnable {
        @Override
        public void run() {
            try {
                socketCliente = new Socket(equipoServidor, puertoServidor);

                OutputStream os = socketCliente.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF("eliminar capitulo");

                os = socketCliente.getOutputStream();
                dos = new DataOutputStream(os);
                dos.writeUTF(id_capitulo);

                os.close();
                dos.close();

                socketCliente.close();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Bundle bundle = new Bundle();
                        bundle.putString("id",id_novela);
                        bundle.putString("titulo",titulo_Novela);
                        modCapitulo = new ModificarCapitulosFragment();
                        modCapitulo.setArguments(bundle);
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getFragmentManager().beginTransaction().replace(R.id.fragment_container,modCapitulo).commit();
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
