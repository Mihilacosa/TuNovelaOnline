package com.example.tunovelaonline;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.tunovelaonline.pojos.Capitulo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Iterator;

public class CapituloFragment extends Fragment {
    String equipoServidor;
    int puertoServidor = 30500;
    Socket socketCliente;
    Capitulo capitulo;

    private TextView Titulo;
    private TextView Contenido;
    private String id_novela;
    private String id_capitulo;
    private ArrayList<Integer> Capitulos_id = new ArrayList<>();
    private ArrayList<Capitulo> Lista = new ArrayList<>();
    private int posicion = 0;
    private Integer id_cap;
    private ScrollView scroll;
    Button Anterior, Indice, Siguiente, Anterior2, Indice2, Siguiente2;
    int cap_max;
    Button opciones;
    TextView contenido;
    Boolean marcado = false;
    private AdView mAdView, mAdView2;
    Boolean suscrito = false;

    private String usuario = "";
    private FirebaseAuth mAuth;
    View v;
    Context contexto;

//popUp
    android.widget.Spinner spinner;
    Button menos,mas,blanco,oscuro,crema,enviar;
    TextView tamano_letra;
    ConstraintLayout content;

    String tamano,font,color,id_usuario, fecha;
    String colorFondo, fontSpinner;
    Integer tam_letra = 14;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_capitulo, container, false);
        equipoServidor = getString(R.string.ip_server);

        Bundle bundle = this.getArguments();
        id_novela = bundle.getString("id_nov");
        id_capitulo = bundle.getString("id_cap");
        id_cap = Integer.valueOf(id_capitulo);

        contenido = v.findViewById(R.id.Contenido_cap);
        content = v.findViewById(R.id.contenidoView);
        contexto = container.getContext();

        Titulo = v.findViewById(R.id.Titulo_cap);
        Contenido = v.findViewById(R.id.Contenido_cap);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            SharedPreferences datos_usu = this.getActivity().getSharedPreferences("usuario_login", Context.MODE_PRIVATE);
            id_usuario = datos_usu.getString("id", "");
            fecha = datos_usu.getString("suscripcion", "");

            new Thread(new ComprobarMarca()).start();
        }

        mAdView = v.findViewById(R.id.adViewC1);
        mAdView2 = v.findViewById(R.id.adViewC2);
        if(fecha != "true"){
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
            mAdView2.loadAd(adRequest);
        }else{
            mAdView.setVisibility(View.GONE);
            mAdView2.setVisibility(View.GONE);
        }

        SharedPreferences datos_usu = this.getActivity().getSharedPreferences("usuario_login", Context.MODE_PRIVATE);
        tamano = datos_usu.getString("tamano", "14");
        font = datos_usu.getString("font", "Open sans");
        color = datos_usu.getString("color", "blanco");

        tam_letra = Integer.valueOf(tamano);
        contenido.setTextSize(TypedValue.COMPLEX_UNIT_SP, Float.parseFloat(tamano));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            que_font(font);
        }
        colorFondo = color;
        color_fondo(color);

        opciones = v.findViewById(R.id.btnOpciones);
        Anterior = v.findViewById(R.id.Anterior);
        Indice = v.findViewById(R.id.Indice);
        Siguiente = v.findViewById(R.id.Siguiente);

        Anterior2 = v.findViewById(R.id.Anterior2);
        Indice2 = v.findViewById(R.id.Indice2);
        Siguiente2 = v.findViewById(R.id.Siguiente2);

        scroll = v.findViewById(R.id.Cap_scroll);

        Anterior.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                posicion = posicion - 1;
                if(posicion != cap_max){
                    Siguiente.setEnabled(true);
                    Siguiente.setBackgroundTintList(getResources().getColorStateList(R.color.purple_500));
                }

                id_cap = Capitulos_id.get(posicion);
                id_capitulo =  String.valueOf(Capitulos_id.get(posicion));
                new Thread(new CargarCapitulo()).start();

                if(posicion == 0){
                    Anterior.setEnabled(false);
                    Anterior.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                }else{
                    Anterior.setEnabled(true);
                    Anterior.setBackgroundTintList(getResources().getColorStateList(R.color.purple_500));
                }

                scroll.fullScroll(ScrollView.FOCUS_UP);
            }
        });

        Anterior2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                posicion = posicion - 1;
                if(posicion != cap_max){
                    Siguiente2.setEnabled(true);
                    Siguiente2.setBackgroundTintList(getResources().getColorStateList(R.color.purple_500));
                }

                id_cap = Capitulos_id.get(posicion);
                id_capitulo =  String.valueOf(Capitulos_id.get(posicion));
                new Thread(new CargarCapitulo()).start();

                if(posicion == 0){
                    Anterior2.setEnabled(false);
                    Anterior2.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                }else{
                    Anterior2.setEnabled(true);
                    Anterior2.setBackgroundTintList(getResources().getColorStateList(R.color.purple_500));
                }

                scroll.fullScroll(ScrollView.FOCUS_UP);
            }
        });

        Siguiente.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                posicion = posicion + 1;
                id_cap = Capitulos_id.get(posicion);
                id_capitulo =  String.valueOf(Capitulos_id.get(posicion));
                new Thread(new CargarCapitulo()).start();

                if(posicion == cap_max){
                    Siguiente.setEnabled(false);
                    Siguiente.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                }else{
                    Siguiente.setEnabled(true);
                    Siguiente.setBackgroundTintList(getResources().getColorStateList(R.color.purple_500));
                }

                if(posicion != 0){
                    Anterior.setEnabled(true);
                    Anterior.setBackgroundTintList(getResources().getColorStateList(R.color.purple_500));
                }

                scroll.fullScroll(ScrollView.FOCUS_UP);
            }
        });

        Siguiente2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                posicion = posicion + 1;
                id_cap = Capitulos_id.get(posicion);
                id_capitulo =  String.valueOf(Capitulos_id.get(posicion));
                new Thread(new CargarCapitulo()).start();

                if(posicion == cap_max){
                    Siguiente2.setEnabled(false);
                    Siguiente2.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                }else{
                    Siguiente2.setEnabled(true);
                    Siguiente2.setBackgroundTintList(getResources().getColorStateList(R.color.purple_500));
                }

                if(posicion != 0){
                    Anterior2.setEnabled(true);
                    Anterior2.setBackgroundTintList(getResources().getColorStateList(R.color.purple_500));
                }

                scroll.fullScroll(ScrollView.FOCUS_UP);
            }
        });

        Indice.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                VolverNovela();
            }
        });

        Indice2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                VolverNovela();
            }
        });

        opciones.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                displayPopup();
            }
        });
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new Thread(new CargarCapitulo()).start();
            }
        },200); // milliseconds: 1 seg.
        return v;
    }


    //Volver a novela
    private void VolverNovela() {
        Bundle bundle = new Bundle();
        bundle.putString("id",id_novela);
        NovelaFragment novela = new NovelaFragment();
        novela.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.fragment_container,novela).commit();
    }

    class CargarCapitulos implements Runnable {
        @Override
        public void run() {
            try {
                socketCliente = new Socket(equipoServidor, puertoServidor);

                OutputStream os = socketCliente.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF("mostrar capitulos");

                os = socketCliente.getOutputStream();
                dos = new DataOutputStream(os);
                dos.writeUTF(id_novela);

                ObjectInputStream ois = new ObjectInputStream(socketCliente.getInputStream());
                Lista = (ArrayList<Capitulo>) ois.readObject();

                os.close();
                dos.close();
                ois.close();

                getActivity().runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run() {
                        Iterator<Capitulo> caps = Lista.iterator();
                        while(caps.hasNext()){
                            Capitulos_id.add(caps.next().getIdCapitulo());
                        }

                        for (int i = 0; i < Capitulos_id.size(); i++){
                            if(Capitulos_id.get(i).equals(id_cap)){
                                posicion = i;
                            }
                        }

                        cap_max = Capitulos_id.size() - 1;

                        if(posicion == 0){
                            Anterior.setEnabled(false);
                            Anterior2.setEnabled(false);
                            Anterior.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                            Anterior2.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                        }

                        if(posicion == cap_max){
                            Siguiente.setEnabled(false);
                            Siguiente2.setEnabled(false);
                            Siguiente.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                            Siguiente2.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
                        }
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

    class CargarCapitulo implements Runnable {
        @Override
        public void run() {
            try {
                socketCliente = new Socket(equipoServidor, puertoServidor);

                OutputStream os = socketCliente.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF("mostrar capitulo");

                os = socketCliente.getOutputStream();
                dos = new DataOutputStream(os);
                dos.writeUTF(id_capitulo);

                ObjectInputStream ois = new ObjectInputStream(socketCliente.getInputStream());
                capitulo = (Capitulo) ois.readObject();

                os.close();
                dos.close();
                ois.close();

                socketCliente.close();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Titulo.setText("Capítulo: " + capitulo.getNumCapitulo() + " - " + capitulo.getTitulo());
                        Contenido.setText(capitulo.getContenido());
                        if(marcado == true){
                            new Thread(new ActualizarMarcador()).start();
                        }

                        if(Lista.isEmpty()){
                            new android.os.Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    new Thread(new CargarCapitulos()).start();
                                }
                            },500); // milliseconds: 1 seg.
                        }
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    class ComprobarMarca implements Runnable {
        @Override
        public void run() {
            try {
                socketCliente = new Socket(equipoServidor, puertoServidor);

                OutputStream os = socketCliente.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF("comprobar marcapaginas");

                dos.writeUTF(id_usuario);

                dos.writeUTF(id_novela);

                InputStream is = socketCliente.getInputStream();
                DataInputStream dis = new DataInputStream(is);
                if(dis.readUTF().equals("gg")){
                    marcado = true;
                }else{
                    marcado = false;
                }

                os.close();
                dos.close();
                is.close();
                dis.close();

                socketCliente.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class ActualizarMarcador implements Runnable {
        @Override
        public void run() {
            try {
                socketCliente = new Socket(equipoServidor, puertoServidor);

                OutputStream os = socketCliente.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF("actualizar marcapaginas");

                dos.writeUTF(id_usuario);

                dos.writeUTF(id_novela);

                dos.writeUTF(id_capitulo);

                os.close();
                dos.close();

                socketCliente.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void displayPopup(){
        View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.pop_up, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        popupWindow.showAtLocation(v.findViewById(R.id.Cap_scroll), Gravity.CENTER, 0, 0);

        tamano_letra = popupView.findViewById(R.id.text_tamano);
        tamano_letra.setText(tamano);
        spinner = popupView.findViewById(R.id.spinner);
        menos = popupView.findViewById(R.id.btn_menos);
        mas = popupView.findViewById(R.id.btn_mas);
        blanco = popupView.findViewById(R.id.btn_blanco);
        oscuro = popupView.findViewById(R.id.btn_oscuro);
        crema = popupView.findViewById(R.id.btn_crema);
        enviar = popupView.findViewById(R.id.btnEnviar_preferencia);

        spinnerSelected(font);
        menos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tam_letra > 8){
                    tam_letra = tam_letra - 1;
                    tamano_letra.setText(tam_letra.toString());
                    contenido.setTextSize(TypedValue.COMPLEX_UNIT_SP, tam_letra);
                }
            }
        });

        mas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tam_letra < 30){
                    tam_letra = tam_letra + 1;
                    tamano_letra.setText(tam_letra.toString());
                    contenido.setTextSize(TypedValue.COMPLEX_UNIT_SP, tam_letra);
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerSelected(spinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        blanco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorFondo = "blanco";
                contenido.setTextColor(Color.parseColor("#000000"));
                content.setBackgroundColor(Color.parseColor("#FFFFFF"));
                Titulo.setTextColor(Color.parseColor("#000000"));
            }
        });

        oscuro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorFondo = "oscuro";
                contenido.setTextColor(Color.parseColor("#888888"));
                content.setBackgroundColor(Color.parseColor("#262626"));
                Titulo.setTextColor(Color.parseColor("#888888"));
            }
        });

        crema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorFondo = "crema";
                contenido.setTextColor(Color.parseColor("#000000"));
                content.setBackgroundColor(Color.parseColor("#e8dfbe"));
                Titulo.setTextColor(Color.parseColor("#000000"));
            }
        });

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences datos_usu = contexto.getSharedPreferences("usuario_login", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = datos_usu.edit();

                editor.putString("tamano", tam_letra.toString());
                editor.putString("font", fontSpinner);
                editor.putString("color", colorFondo);
                editor.apply();

                popupWindow.dismiss();
            }
        });
    }

    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void que_font (String font){
        if(font.equals("Open sans")){
            fontSpinner = "Open sans";
            Typeface typeface = ResourcesCompat.getFont(getContext(),R.font.open_sans);
            contenido.setTypeface(typeface);
        }
        if(font.equals("Amaranth")){
            fontSpinner = "Amaranth";
            Typeface typeface = ResourcesCompat.getFont(getContext(),R.font.amaranth);
            contenido.setTypeface(typeface);
        }
        if(font.equals("Doppio One")){
            fontSpinner = "Doppio One";
            Typeface typeface = ResourcesCompat.getFont(getContext(),R.font.doppio_one);
            contenido.setTypeface(typeface);
        }
        if(font.equals("Lustria")){
            fontSpinner = "Lustria";
            Typeface typeface = ResourcesCompat.getFont(getContext(),R.font.lustria);
            contenido.setTypeface(typeface);
        }
        if(font.equals("Happy Monkey")){
            fontSpinner = "Happy Monkey";
            Typeface typeface = ResourcesCompat.getFont(getContext(),R.font.happy_monkey);
            contenido.setTypeface(typeface);
        }
    }

    public void spinnerSelected (String font){
        if(font.equals("Open sans")){
            fontSpinner = "Open sans";
            Typeface typeface = ResourcesCompat.getFont(getContext(),R.font.open_sans);
            contenido.setTypeface(typeface);
            spinner.setSelection(0);
        }
        if(font.equals("Amaranth")){
            fontSpinner = "Amaranth";
            Typeface typeface = ResourcesCompat.getFont(getContext(),R.font.amaranth);
            contenido.setTypeface(typeface);
            spinner.setSelection(1);
        }
        if(font.equals("Doppio One")){
            fontSpinner = "Doppio One";
            Typeface typeface = ResourcesCompat.getFont(getContext(),R.font.doppio_one);
            contenido.setTypeface(typeface);
            spinner.setSelection(2);
        }
        if(font.equals("Lustria")){
            fontSpinner = "Lustria";
            Typeface typeface = ResourcesCompat.getFont(getContext(),R.font.lustria);
            contenido.setTypeface(typeface);
            spinner.setSelection(3);
        }
        if(font.equals("Happy Monkey")){
            fontSpinner = "Happy Monkey";
            Typeface typeface = ResourcesCompat.getFont(getContext(),R.font.happy_monkey);
            contenido.setTypeface(typeface);
            spinner.setSelection(4);
        }
    }

    public void color_fondo (String color){
        if(color.equals("blanco")){
            contenido.setTextColor(Color.parseColor("#000000"));
            content.setBackgroundColor(Color.parseColor("#FFFFFF"));
            Titulo.setTextColor(Color.parseColor("#000000"));
        }

        if(color.equals("oscuro")){
            contenido.setTextColor(Color.parseColor("#888888"));
            content.setBackgroundColor(Color.parseColor("#262626"));
            Titulo.setTextColor(Color.parseColor("#888888"));
        }

        if(color.equals("crema")){
            contenido.setTextColor(Color.parseColor("#000000"));
            content.setBackgroundColor(Color.parseColor("#e8dfbe"));
            Titulo.setTextColor(Color.parseColor("#000000"));
        }
    }

}
