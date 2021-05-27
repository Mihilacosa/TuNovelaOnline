package com.example.tunovelaonline;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.tunovelaonline.pojos.Usuario;
import com.google.firebase.auth.FirebaseAuth;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class PreferenciaFragment extends Fragment {
    String equipoServidor;
    int puertoServidor = 30500;
    Socket socketCliente;

    String tamano,font,color,id_usuario;
    String colorFondo, fontSpinner;
    Integer tam_letra;

    LinearLayout layout_ejemplo;
    TextView tamano_letra,text_ejemplo;
    android.widget.Spinner spinner;
    Button menos,mas,blanco,oscuro,crema,enviar;

    View view;
    Context contexto;
    @RequiresApi(api = Build.VERSION_CODES.M)
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
            colorFondo = color;
            color_fondo(color);
        }

        menos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tam_letra > 8){
                    tam_letra = tam_letra - 1;
                    tamano_letra.setText(tam_letra.toString());
                    text_ejemplo.setTextSize(TypedValue.COMPLEX_UNIT_SP, tam_letra);
                }
            }
        });

        mas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tam_letra < 30){
                    tam_letra = tam_letra + 1;
                    tamano_letra.setText(tam_letra.toString());
                    text_ejemplo.setTextSize(TypedValue.COMPLEX_UNIT_SP, tam_letra);
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                que_font(spinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        blanco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorFondo = "blanco";
                text_ejemplo.setTextColor(Color.parseColor("#000000"));
                layout_ejemplo.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        });

        oscuro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorFondo = "oscuro";
                text_ejemplo.setTextColor(Color.parseColor("#888888"));
                layout_ejemplo.setBackgroundColor(Color.parseColor("#262626"));
            }
        });

        crema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorFondo = "crema";
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

    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void que_font (String font){
        if(font.equals("Open sans")){
            fontSpinner = "Open sans";
            Typeface typeface = ResourcesCompat.getFont(getContext(),R.font.open_sans);
            text_ejemplo.setTypeface(typeface);
            spinner.setSelection(0);
        }
        if(font.equals("Amaranth")){
            fontSpinner = "Amaranth";
            Typeface typeface = ResourcesCompat.getFont(getContext(),R.font.amaranth);
            text_ejemplo.setTypeface(typeface);
            spinner.setSelection(1);
        }
        if(font.equals("Doppio One")){
            fontSpinner = "Doppio One";
            Typeface typeface = ResourcesCompat.getFont(getContext(),R.font.doppio_one);
            text_ejemplo.setTypeface(typeface);
            spinner.setSelection(2);
        }
        if(font.equals("Lustria")){
            fontSpinner = "Lustria";
            Typeface typeface = ResourcesCompat.getFont(getContext(),R.font.lustria);
            text_ejemplo.setTypeface(typeface);
            spinner.setSelection(3);
        }
        if(font.equals("Happy Monkey")){
            fontSpinner = "Happy Monkey";
            Typeface typeface = ResourcesCompat.getFont(getContext(),R.font.happy_monkey);
            text_ejemplo.setTypeface(typeface);
            spinner.setSelection(4);
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
            try {
                socketCliente = new Socket(equipoServidor, puertoServidor);

                OutputStream os = socketCliente.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF("preferencias");

                Usuario usuario = new Usuario();
                usuario.setIdUsuario(Integer.valueOf(id_usuario));
                usuario.setTamanoLetra(tam_letra);
                usuario.setFontLetra(spinner.getSelectedItem().toString());
                usuario.setTema(colorFondo);

                ObjectOutputStream oos = new ObjectOutputStream(socketCliente.getOutputStream());
                oos.writeObject(usuario);


                os.close();
                dos.close();
                oos.close();

                socketCliente.close();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences datos_usu = contexto.getSharedPreferences("usuario_login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = datos_usu.edit();

                        editor.putString("tamano", tam_letra.toString());
                        editor.putString("font", fontSpinner);
                        editor.putString("color", colorFondo);
                        editor.apply();

                        getFragmentManager().beginTransaction().replace(R.id.fragment_container,new InicioFragment()).addToBackStack( "tag" ).commit();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
