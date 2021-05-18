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

import com.google.firebase.auth.FirebaseAuth;

import java.net.Socket;

public class UsuarioFragment extends Fragment {
    String equipoServidor;
    int puertoServidor = 30500;
    Socket socketCliente;

    String usuario = "";
    String id_usuario, usu_email;
    View view;
    EditText usuarioNueva, emailNueva, cont_act, cont_nueva, cont_rep;
    Button enviar;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_usuario, container, false);
        equipoServidor = getString(R.string.ip_server);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            SharedPreferences datos_usu = this.getActivity().getSharedPreferences("usuario_login", Context.MODE_PRIVATE);
            usuario = datos_usu.getString("usuario", "");
            id_usuario = datos_usu.getString("id", "");
            usu_email = datos_usu.getString("email", "");
            if (!usuario.equals("")) {
                //setTitle("Hola " + usuario);
            }
        }

        usuarioNueva = view.findViewById(R.id.nuevoUsuario);
        emailNueva = view.findViewById(R.id.nuevoEmail);
        cont_act = view.findViewById(R.id.contrasena_actual);
        cont_nueva = view.findViewById(R.id.nueva_contrasena);
        cont_rep = view.findViewById(R.id.nueva_contrasena_seg);

        enviar = view.findViewById(R.id.btnEnviar_usuario);

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }
}
