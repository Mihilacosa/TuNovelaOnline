package com.example.tunovelaonline;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tunovelaonline.pojos.Capitulo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SubirCapituloFragment extends Fragment {
    String equipoServidor;
    int puertoServidor = 30500;
    Socket socketCliente;

    EditText titulo, num_cap, contenido;
    Button enviar;
    private String id_novela, fecha;
    Capitulo capitulo;
    private AdView mAdView;

    View view;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_subir_capitulo, container, false);
        equipoServidor = getString(R.string.ip_server);
        titulo = view.findViewById(R.id.subir_titulo_capitulo);
        num_cap = view.findViewById(R.id.subir_num_capitulo);
        contenido = view.findViewById(R.id.subir_contenido_cap);
        enviar = view.findViewById(R.id.btnSubir_capitulo);

        Bundle bundle = this.getArguments();
        id_novela = bundle.getString("id");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            SharedPreferences datos_usu = this.getActivity().getSharedPreferences("usuario_login", Context.MODE_PRIVATE);
            fecha = datos_usu.getString("suscripcion", "");
        }

        mAdView = view.findViewById(R.id.adViewSC1);
        if(fecha.length() < 4){
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }else{
            mAdView.setVisibility(View.GONE);
        }

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new EnviarCapitulo()).start();

                getFragmentManager().beginTransaction().replace(R.id.fragment_container,new ModificarNovelasFragment()).commit();
            }
        });

        return view;
    }

    class EnviarCapitulo implements Runnable {
        @Override
        public void run() {
            try {
                socketCliente = new Socket(equipoServidor, puertoServidor);

                OutputStream os = socketCliente.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF("subir capitulo");

                capitulo = new Capitulo();
                capitulo.setIdNovela(Integer.valueOf(id_novela));
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
