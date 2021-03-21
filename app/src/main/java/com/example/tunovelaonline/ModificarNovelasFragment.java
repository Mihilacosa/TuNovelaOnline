package com.example.tunovelaonline;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModificarNovelasFragment extends Fragment {
    public static final String ID_NOVELA = "com.com.example.tarea_1.ID_NOVELA";

    private  String usuario = "";
    String titulo, id, imagen, resena, id_usuario;

    MenuItem itemlt;
    MenuItem itemln;
    MenuItem itemr;
    MenuItem subir_novelas;

    ArrayList<ListaNovelas> listaNovelas = new ArrayList<>();
    RecyclerView recyclerNovelas;
    AdaptadorNovelasModificar adapter;

    View v;
    RequestQueue requestQueue;

    ModificarNovelaFragment novela;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_modificar_novelas, container, false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            SharedPreferences datos_usu = this.getActivity().getSharedPreferences("usuario_login", Context.MODE_PRIVATE);
            usuario = datos_usu.getString("usuario", "");
            id_usuario = datos_usu.getString("id", "");
            if (!usuario.equals("")) {
                //setTitle("Hola " + usuario);
            }
        }

        Cargar("https://tnowebservice.000webhostapp.com/NovelasModificar.php?id_usuario=" + id_usuario);

        return v;
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
                recyclerNovelas = v.findViewById(R.id.RecyclerModificar);
                recyclerNovelas.setLayoutManager(new LinearLayoutManager(getContext()));

                adapter = new AdaptadorNovelasModificar(listaNovelas, getContext());
                adapter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id_N = listaNovelas.get(recyclerNovelas.getChildAdapterPosition(v)).getId();

                        Bundle bundle = new Bundle();
                        bundle.putString("id",id_N);
                        novela = new ModificarNovelaFragment();
                        novela.setArguments(bundle);
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container,novela).commit();
                    }
                });


                recyclerNovelas.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getContext(), "No tiene novelas subidas", Toast.LENGTH_SHORT).show();

                getFragmentManager().beginTransaction().replace(R.id.fragment_container,new InicioFragment()).commit();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);
    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case 120:
                String id_novela_eliminar = adapter.mostrarId(item.getGroupId());

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:

                                StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://tnowebservice.000webhostapp.com/Eliminar_novela.php", new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }) {
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String, String> parametros = new HashMap<String, String>();
                                        parametros.put("id_novela", id_novela_eliminar);
                                        return parametros;
                                    }
                                };

                                requestQueue = Volley.newRequestQueue(getContext());
                                requestQueue.add(stringRequest);

                                getFragmentManager().beginTransaction().replace(R.id.fragment_container,new ModificarNovelasFragment()).commit();

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
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }
}
