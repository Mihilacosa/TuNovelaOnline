package com.example.tunovelaonline;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tunovelaonline.pojos.Usuario;
import com.google.firebase.auth.FirebaseAuth;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;

public class PreferenciaFragment extends Fragment {
    String equipoServidor;
    int puertoServidor = 30500;
    Socket socketCliente;

    String tamano,font,color,id_usuario;

    Integer tam_letra;

    LinearLayout layout_ejemplo;
    TextView tamano_letra,text_ejemplo;
    android.widget.Spinner spinner;
    Button menos,mas,blanco,oscuro,crema,enviar;

    View view;
    Context contexto;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_preferencia, container, false);
        equipoServidor = getString(R.string.ip_server);
        contexto = container.getContext();

        layout_ejemplo = view.findViewById(R.id.layout_ejemplo);
        tamano_letra = view.findViewById(R.id.text_tamano);
        text_ejemplo = view.findViewById(R.id.text_ejemplo);
        spinner = view.findViewById(R.id.spinner);
        menos = view.findViewById(R.id.btn_menos);
        mas = view.findViewById(R.id.btn_mas);
        blanco = view.findViewById(R.id.btn_blanco);
        oscuro = view.findViewById(R.id.btn_oscuro);
        crema = view.findViewById(R.id.btn_crema);
        enviar = view.findViewById(R.id.btnEnviar_preferencia);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            SharedPreferences datos_usu = this.getActivity().getSharedPreferences("usuario_login", Context.MODE_PRIVATE);
            id_usuario = datos_usu.getString("id", "");
            tamano = datos_usu.getString("tamano", "");
            font = datos_usu.getString("font", "");
            color = datos_usu.getString("color", "");

            tam_letra = Integer.valueOf(tamano);
            tamano_letra.setText(tamano);
            text_ejemplo.setTextSize(TypedValue.COMPLEX_UNIT_SP, Float.parseFloat(tamano));
            que_font(font);
            color_fondo(color);
        }

        menos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tam_letra > 8){
                    tam_letra = tam_letra - 1;
                    text_ejemplo.setTextSize(TypedValue.COMPLEX_UNIT_SP, tam_letra);
                }
            }
        });

        mas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tam_letra < 30){
                    tam_letra = tam_letra + 1;
                    text_ejemplo.setTextSize(TypedValue.COMPLEX_UNIT_SP, tam_letra);
                }
            }
        });

        spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    text_ejemplo.setTextAppearance((Integer) spinner.getSelectedItem());
                }
            }
        });

        blanco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_ejemplo.setTextColor(Color.parseColor("#000000"));
                layout_ejemplo.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        });

        oscuro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_ejemplo.setTextColor(Color.parseColor("#888888"));
                layout_ejemplo.setBackgroundColor(Color.parseColor("#262626"));
            }
        });

        crema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_ejemplo.setTextColor(Color.parseColor("#000000"));
                layout_ejemplo.setBackgroundColor(Color.parseColor("#e8dfbe"));
            }
        });

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new EnvioPreferencias()).start();
            }
        });

        return view;
    }

    public void que_font (String font){
        if(color.equals("Open sans")){
            spinner.setSelection(1);
        }
        if(color.equals("Arial")){
            spinner.setSelection(2);
        }
        if(color.equals("Times New Roman")){
            spinner.setSelection(3);
        }
        if(color.equals("Century Gothic")){
            spinner.setSelection(4);
        }
        if(color.equals("Lucida Sans")){
            spinner.setSelection(5);
        }
        if(color.equals("Tahoma")){
            spinner.setSelection(6);
        }
        if(color.equals("Verdana")){
            spinner.setSelection(7);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            text_ejemplo.setTextAppearance((Integer) spinner.getSelectedItem());
        }
    }

    public void color_fondo (String color){
        if(color.equals("blanco")){
            text_ejemplo.setTextColor(Color.parseColor("#000000"));
            layout_ejemplo.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        if(color.equals("oscuro")){
            text_ejemplo.setTextColor(Color.parseColor("#888888"));
            layout_ejemplo.setBackgroundColor(Color.parseColor("#262626"));
        }

        if(color.equals("crema")){
            text_ejemplo.setTextColor(Color.parseColor("#000000"));
            layout_ejemplo.setBackgroundColor(Color.parseColor("#e8dfbe"));
        }
    }

    class EnvioPreferencias implements Runnable {
        @Override
        public void run() {

        }
    }
}
