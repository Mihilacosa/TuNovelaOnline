package com.example.tunovelaonline;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class InicioFragment extends Fragment {
    String equipoServidor;
    int puertoServidor = 30500;
    Socket socketCliente;

    private  String usuario = "";

    ArrayList<Novela> listaNovelas = new ArrayList();
    RecyclerView recyclerNovelas;
    AdaptadorNovelas adapter;

    View view;
    NovelaFragment novela;
    TextView name;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_inicio, container, false);
        equipoServidor = getString(R.string.ip_server);
        new Thread(new Cargar()).start();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            SharedPreferences datos_usu = this.getActivity().getSharedPreferences("usuario_login", Context.MODE_PRIVATE);
            usuario = datos_usu.getString("usuario", "");
            if (!usuario.equals("")) {
                //name.setText(usuario);
            }
        }

        return view;
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
                        recyclerNovelas = view.findViewById(R.id.ReyclerId);
                        recyclerNovelas.setLayoutManager(new LinearLayoutManager(getContext()));

                        adapter = new AdaptadorNovelas(listaNovelas, getActivity().getApplicationContext());
                        adapter.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String id_N = listaNovelas.get(recyclerNovelas.getChildAdapterPosition(v)).getIdNovela().toString();

                                Bundle bundle = new Bundle();
                                bundle.putString("id",id_N);
                                novela = new NovelaFragment();
                                novela.setArguments(bundle);
                                getFragmentManager().beginTransaction().replace(R.id.fragment_container,novela).addToBackStack( "tag" ).commit();
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
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }
}
