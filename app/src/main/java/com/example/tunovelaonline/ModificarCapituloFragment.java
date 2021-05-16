package com.example.tunovelaonline;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tunovelaonline.pojos.Capitulo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ModificarCapituloFragment extends Fragment {
    String equipoServidor;
    int puertoServidor = 30500;
    Socket socketCliente;

    private String id_cap;
    private String id_novela;
    private String titulo_Novela;

    Capitulo capitulo;

    EditText titulo, num_cap, contenido;
    Button enviar;
    ModificarCapitulosFragment modCapitulo;

    View view;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_modificar_capitulo, container, false);
        equipoServidor = getString(R.string.ip_server);
        titulo = view.findViewById(R.id.modi_nuevo_titulo_capitulo);
        num_cap = view.findViewById(R.id.modi_nuevo_num_capitulo);
        contenido = view.findViewById(R.id.modi_nuevo_contenido_cap);
        enviar = view.findViewById(R.id.btnEnviar_mod_capitulo);

        Bundle bundle = this.getArguments();
        id_cap = bundle.getString("id");
        id_novela = bundle.getString("id_novela");
        titulo_Novela = bundle.getString("titulo");

        new Thread(new CargarCapitulosMod()).start();

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new EnviarCapitulosMod()).start();

                Bundle bundle = new Bundle();
                bundle.putString("id",id_novela);
                bundle.putString("titulo",titulo_Novela);
                modCapitulo = new ModificarCapitulosFragment();
                modCapitulo.setArguments(bundle);

                long start = System.currentTimeMillis();
                long end = start + 1000; // 60 seconds * 1000 ms/sec
                while (System.currentTimeMillis() < end) {
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container,modCapitulo).commit();
                }
            }
        });

        return view;
    }

    class CargarCapitulosMod implements Runnable {
        @Override
        public void run() {
            try {
                socketCliente = new Socket(equipoServidor, puertoServidor);

                OutputStream os = socketCliente.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF("mostrar capitulo");

                dos.writeUTF(id_cap);

                ObjectInputStream ois = new ObjectInputStream(socketCliente.getInputStream());
                capitulo = (Capitulo) ois.readObject();

                os.close();
                dos.close();
                ois.close();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        titulo.setText(capitulo.getTitulo());
                        num_cap.setText(String.valueOf(capitulo.getNumCapitulo()));
                        contenido.setText(capitulo.getContenido());
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

    class EnviarCapitulosMod implements Runnable {
        @Override
        public void run() {
            try {
                socketCliente = new Socket(equipoServidor, puertoServidor);

                OutputStream os = socketCliente.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF("enviar capitulo modificar");

                capitulo = new Capitulo();
                capitulo.setIdCapitulo(Integer.parseInt(id_cap));
                capitulo.setTitulo(String.valueOf(titulo.getText()));
                capitulo.setNumCapitulo(Integer.parseInt(String.valueOf(num_cap.getText())));
                capitulo.setContenido(String.valueOf(contenido.getText()));

                ObjectOutputStream oos = new ObjectOutputStream(socketCliente.getOutputStream());
                oos.writeObject(capitulo);

                os.close();
                dos.close();
                oos.close();

                socketCliente.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
