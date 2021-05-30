package com.example.tunovelaonline;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tunovelaonline.pojos.Novela;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class BusquedaFragment extends Fragment {
    String equipoServidor;
    int puertoServidor = 30500;
    Socket socketCliente;

    ArrayList<Novela> listaNovelas = new ArrayList();
    ArrayList<Novela> listaNovelasAux = new ArrayList();
    RecyclerView recyclerNovelas;
    AdaptadorNovelas adapter;
    EditText barra;
    CapituloFragment capitulo;
    String id_N,id_capU, id_capP;

    NovelaFragment novela;
    View view;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_busqueda, container, false);
        equipoServidor = getString(R.string.ip_server);
        new Thread(new Cargar()).start();
        recyclerNovelas = view.findViewById(R.id.ReyclerBusqueda);
        barra = view.findViewById(R.id.BarraBusqueda);

        barra.addTextChangedListener(textWatcher);

        barra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barra.setText("");
            }
        });


        return view;
    }

    private TextWatcher textWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            listaNovelasAux = new ArrayList();
            if(s == ""){
                barra.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_busqueda, 0);
            }else{
                barra.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clean, 0);
            }

            for (Novela novela :  listaNovelas) {
                String titulo = novela.getTitulo().toLowerCase();
                String contenido = s.toString().toLowerCase();
                if(titulo.contains(contenido)){
                    listaNovelasAux.add(novela);
                }
            }

            if(listaNovelasAux.size() != 0){
                Adaptador(listaNovelasAux);
            }else{
                if(s != ""){
                    barra.setError("No hay novelas");
                }

                Adaptador(listaNovelas);
            }


            recyclerNovelas.smoothScrollToPosition(0);
        }
    };

    public void Adaptador (ArrayList<Novela> lista){
        recyclerNovelas.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new AdaptadorNovelas(lista, getActivity().getApplicationContext());
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id_N = lista.get(recyclerNovelas.getChildAdapterPosition(v)).getIdNovela().toString();

                Bundle bundle = new Bundle();
                bundle.putString("id",id_N);
                novela = new NovelaFragment();
                novela.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container,novela).commit();
            }
        });
        recyclerNovelas.setAdapter(adapter);
    }

    class Cargar implements Runnable {
        @Override
        public void run() {
            try {
                socketCliente = new Socket(equipoServidor, puertoServidor);

                OutputStream os = socketCliente.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF("mostrar todo");


                ObjectInputStream ois = new ObjectInputStream(socketCliente.getInputStream());
                listaNovelas = (ArrayList<Novela>) ois.readObject();

                os.close();
                dos.close();
                ois.close();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
                                getFragmentManager().beginTransaction().replace(R.id.fragment_container,novela).commit();
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
                if(adapter.tamano(item.getGroupId()) == 1){
                    Toast.makeText(getContext(), "Esta novela solo tiene un capítulo.", Toast.LENGTH_SHORT).show();
                }else{
                    id_N = adapter.mostrarId(item.getGroupId());
                    id_capP = adapter.mostrarId_P(item.getGroupId());

                    Bundle bundle2 = new Bundle();
                    bundle2.putString("id_nov",id_N);
                    bundle2.putString("id_cap",id_capP);
                    capitulo = new CapituloFragment();
                    capitulo.setArguments(bundle2);
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container,capitulo).addToBackStack( "tag" ).commit();
                }
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }
}
