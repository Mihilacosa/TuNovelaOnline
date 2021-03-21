package com.example.tunovelaonline;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class NovelaFragment extends Fragment {
    public static final String ID_CAPITULO = "com.com.example.tarea_1.ID_CAPITULO";
    public static final String ID_NOVELA = "com.com.example.tarea_1.ID_NOVELA";
    private TextView Titulo_novela;
    private TextView resena;
    private ImageView portada, imgDespliegue;
    private ListView Lista_caps;
    private ArrayList<ListaCapitulos> Lista = new ArrayList<>();
    private ArrayList<Integer> Capitulos_id = new ArrayList<>();
    private String id_capitulo;
    private String id_novela;

    View v;

    RecyclerView recyclerCapitulos;
    LinearLayout desplegable, Adesplegar;
    TextView tit_alt, autor, artista, traductor, genero, fecha;
    private AdView mAdView;

    private  String usuario = "";
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_novela, container, false);

        tit_alt = v.findViewById(R.id.Novela_titulo_alternativo);
        autor = v.findViewById(R.id.Novela_autor);
        artista = v.findViewById(R.id.Novela_artista);
        traductor = v.findViewById(R.id.Novela_traductor);
        genero = v.findViewById(R.id.Novela_genero);
        fecha = v.findViewById(R.id.Novela_fecha);

        desplegable = v.findViewById(R.id.desplegable);
        Adesplegar = v.findViewById(R.id.Adesplegar);
        Adesplegar.setVisibility(View.GONE);
        imgDespliegue = v.findViewById(R.id.imgDesplegable);

        tit_alt.setVisibility(View.GONE);
        artista.setVisibility(View.GONE);
        traductor.setVisibility(View.GONE);

        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            SharedPreferences datos_usu = this.getActivity().getSharedPreferences("usuario_login", Context.MODE_PRIVATE);
            usuario = datos_usu.getString("usuario", "");
            if (!usuario.equals("")) {
                //setTitle("Hola " + usuario);
            }
        }

/*
        Intent i = getIntent();
        onNewIntent(getIntent());

        if(i.getStringExtra(MainActivity.ID_NOVELA).equals("")){
            id_novela = i.getStringExtra(Capitulo.ID_NOVELA);
        }
        if(!i.getStringExtra(MainActivity.ID_NOVELA).equals("")){
            id_novela = i.getStringExtra(MainActivity.ID_NOVELA);
        }
*/

        Bundle bundle = this.getArguments();
        id_novela = bundle.getString("id");

        CargarNovela("https://tnowebservice.000webhostapp.com/Novela_seleccionada.php?id_novela=" + id_novela);
        CargarCapitulos("https://tnowebservice.000webhostapp.com/Lista_caps.php?id_novela=" + id_novela);

        Titulo_novela = v.findViewById(R.id.Titulo_dif);
        resena = v.findViewById(R.id.Resena_novela_selec);
        portada = v.findViewById(R.id.Portada_novela_selec);

        desplegable.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if(Adesplegar.getVisibility() == View.GONE){
                    Adesplegar.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide));
                    imgDespliegue.setImageResource(R.drawable.ic_arriba);
                    Adesplegar.setVisibility(View.VISIBLE);
                }else{
                    imgDespliegue.setImageResource(R.drawable.ic_abajo);
                    Adesplegar.setVisibility(View.GONE);
                }

            }
        });

        return v;
    }
/*
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("ID_NOVELA")) {
                id_novela = extras.getString("ID_NOVELA");
            }
        }


    }
*/
    //carga novela y capitulos

    private void CargarNovela(String URL) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++){
                    try {
                        jsonObject = response.getJSONObject(i);
                        Titulo_novela.setText(new String(jsonObject.getString("titulo").getBytes("ISO-8859-1"), "UTF-8"));
                        resena.setText(new String(jsonObject.getString("resena").getBytes("ISO-8859-1"), "UTF-8"));
                        Picasso.get().load(jsonObject.getString("portada")).into(portada);

                        if(jsonObject.getString("nombre_alternativo").isEmpty()){

                        }else {
                            tit_alt.setVisibility(View.VISIBLE);
                            tit_alt.setText(tit_alt.getText() + new String(jsonObject.getString("nombre_alternativo").getBytes("ISO-8859-1"), "UTF-8"));
                        }

                        autor.setText(autor.getText() + new String(jsonObject.getString("autor").getBytes("ISO-8859-1"), "UTF-8"));

                        if(jsonObject.getString("artista").isEmpty()){

                        }else {
                            artista.setVisibility(View.VISIBLE);
                            artista.setText(artista.getText() + new String(jsonObject.getString("artista").getBytes("ISO-8859-1"), "UTF-8"));
                        }


                        if(jsonObject.getString("traductor").isEmpty()){

                        }else {
                            traductor.setVisibility(View.VISIBLE);
                            traductor.setText(traductor.getText() + new String(jsonObject.getString("traductor").getBytes("ISO-8859-1"), "UTF-8"));
                        }

                        genero.setText(genero.getText() + new String(jsonObject.getString("genero").getBytes("ISO-8859-1"), "UTF-8"));
                        fecha.setText(fecha.getText() + new String(jsonObject.getString("fecha_subida").getBytes("ISO-8859-1"), "UTF-8"));

                    } catch (JSONException | UnsupportedEncodingException e){
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "ERROR DE CARGA DE NOVELA SELECCIONADA", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);
    }

    private void CargarCapitulos(String URL) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++){
                    try {
                        jsonObject = response.getJSONObject(i);
                        String capitulo = "Capitulo: " + jsonObject.getString("num_capitulo") + " - " + new String(jsonObject.getString("titulo").getBytes("ISO-8859-1"), "UTF-8");
                        String id_cap = jsonObject.getString("id_capitulo");
                        Integer id_cap_int = Integer.valueOf(jsonObject.getString("id_capitulo"));
                        Capitulos_id.add(id_cap_int);
                        Lista.add(new ListaCapitulos(capitulo, id_cap));
                    } catch (JSONException | UnsupportedEncodingException e){
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                recyclerCapitulos = v.findViewById(R.id.RecyclerCapitulos);
                recyclerCapitulos.setLayoutManager(new LinearLayoutManager(getContext()));

                AdaptadorCapitulos adapter = new AdaptadorCapitulos(Lista);

                adapter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        id_capitulo = Lista.get(recyclerCapitulos.getChildAdapterPosition(v)).getId();

                        Bundle bundle = new Bundle();
                        bundle.putString("id_nov",id_novela);
                        bundle.putString("id_cap",id_capitulo);
                        bundle.putSerializable("ARRAYLIST", Capitulos_id);
                        CapituloFragment capitulo = new CapituloFragment();
                        capitulo.setArguments(bundle);
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container,capitulo).commit();
                    }
                });

                recyclerCapitulos.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "ERROR DE CARGA DE NOVELA SELECCIONADA", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);
    }
}
