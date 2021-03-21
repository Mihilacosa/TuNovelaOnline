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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class InicioFragment extends Fragment {

    private  String usuario = "";
    String titulo, id, imagen, resena;

    ArrayList<ListaNovelas> listaNovelas = new ArrayList<>();
    RecyclerView recyclerNovelas;
    AdaptadorNovelas adapter;

    View view;
    NovelaFragment novela;
    TextView name;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_inicio, container, false);
/*
        final TextView textView = view.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
*/
        //View v = inflater.inflate(R.layout.nav_header, container, false);
        //name = v.findViewById(R.id.usuName);

        Cargar("https://tnowebservice.000webhostapp.com/UltimasActualizaciones.php");

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

    private void Cargar(String URL) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++){
                    try {
                        jsonObject = response.getJSONObject(i);
                        titulo = new String(jsonObject.getString("titulo").getBytes("ISO-8859-1"), "UTF-8");
                        id = jsonObject.getString("id_novela");
                        imagen = jsonObject.getString("portada");
                        resena = new String(jsonObject.getString("resena").getBytes("ISO-8859-1"), "UTF-8");
                        listaNovelas.add(new ListaNovelas(titulo, id, imagen, resena));

                    } catch (JSONException | UnsupportedEncodingException e){
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                recyclerNovelas = view.findViewById(R.id.ReyclerId);
                recyclerNovelas.setLayoutManager(new LinearLayoutManager(getContext()));

                adapter = new AdaptadorNovelas(listaNovelas, getActivity().getApplicationContext());
                adapter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id_N = listaNovelas.get(recyclerNovelas.getChildAdapterPosition(v)).getId();

                        Bundle bundle = new Bundle();
                        bundle.putString("id",id_N);
                        novela = new NovelaFragment();
                        novela.setArguments(bundle);
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container,novela).commit();
                    }
                });
                recyclerNovelas.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "ERROR DE CARGA DE NOVELA", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case 120:
                String resena2 = adapter.mostrarResena(item.getGroupId());
                AlertDialog.Builder resenya = new AlertDialog.Builder(getContext());
                resenya.setMessage("Reseña \n\n" + resena2);
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
