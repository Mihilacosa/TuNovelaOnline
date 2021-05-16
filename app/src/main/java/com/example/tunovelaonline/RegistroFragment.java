package com.example.tunovelaonline;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tunovelaonline.pojos.Novela;
import com.example.tunovelaonline.pojos.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class RegistroFragment extends Fragment {
    String equipoServidor;
    int puertoServidor = 30500;
    Socket socketCliente;
    Usuario usuario2;

    private EditText NuevoUsuario;
    private EditText NuevoEmail;
    private EditText NuevaContrasena;
    private EditText NuevaContrasena2;
    private FirebaseAuth mAuth;
    String email;
    Context contexto;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_registro, container, false);
        mAuth = FirebaseAuth.getInstance();
        equipoServidor = getString(R.string.ip_server);
        contexto = container.getContext();

        Button InicioSesionRegisatro = v.findViewById(R.id.InicioRegistro);
        InicioSesionRegisatro.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                InicioSesionReg();
            }
        });


        NuevoUsuario = v.findViewById(R.id.CrearUsuario);
        NuevoEmail = v.findViewById(R.id.CrearEmail);
        NuevaContrasena = v.findViewById(R.id.CrearContrasena1);
        NuevaContrasena2 = v.findViewById(R.id.ConbContrasena1);

        Button CrearCuenta = v.findViewById(R.id.bCrearCuenta);
        CrearCuenta.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String usuario =  NuevoUsuario.getText().toString();
                email = NuevoEmail.getText().toString();
                String Contrasena = NuevaContrasena.getText().toString();
                String Contrasena2 = NuevaContrasena2.getText().toString();

                if(TextUtils.isEmpty(usuario)){
                    NuevoUsuario.setError("El usuario debe contener minimo un carácter");
                    return;
                }

                if(TextUtils.isEmpty(email)){
                    NuevoEmail.setError("El email es obligatorio");
                    return;
                }

                if(TextUtils.isEmpty(Contrasena)){
                    NuevaContrasena.setError("la contraseña no puede estar vacia");
                    return;
                }

                if(Contrasena.length() < 6){
                    NuevaContrasena.setError("la contraseña debe tener como minimo 6 caracteres");
                    return;
                }

                if(!Contrasena.equals(Contrasena2)) {
                    NuevaContrasena2.setError("No coincide con la contraseña");
                    return;
                }

                if(Contrasena.equals(Contrasena2)) {

                    mAuth.createUserWithEmailAndPassword(email, Contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                new Thread(new EnvioInfoCrearCuenta()).start();
                            } else {
                                Toast.makeText(getContext(), "El email esta siendo usado", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        return v;
    }

    //Moverse a iniciar sesión

    public void InicioSesionReg(){
        getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new LoginFragment()).commit();
    }

    //Enviar datos para crear cuenta

    class EnvioInfoCrearCuenta implements Runnable {
        @Override
        public void run() {
            try {
                socketCliente = new Socket(equipoServidor, puertoServidor);

                OutputStream os = socketCliente.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF("nuevo usuario");

                Usuario usuario = new Usuario();
                usuario.setUsuario(NuevoUsuario.getText().toString());
                usuario.setContrasena(NuevaContrasena.getText().toString());
                usuario.setEmail(email);

                ObjectOutputStream oos = new ObjectOutputStream(socketCliente.getOutputStream());
                oos.writeObject(usuario);


                os.close();
                dos.close();
                oos.close();

                socketCliente.close();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                new Thread(new EnvioLogin()).start();
                            }
                        }, 1000);
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class EnvioLogin implements Runnable {
        @Override
        public void run() {
            try {
                socketCliente = new Socket(equipoServidor, puertoServidor);

                OutputStream os = socketCliente.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF("login usuario");

                os = socketCliente.getOutputStream();
                dos = new DataOutputStream(os);
                dos.writeUTF(email);


                ObjectInputStream ois = new ObjectInputStream(socketCliente.getInputStream());
                usuario2 = (Usuario) ois.readObject();

                os.close();
                dos.close();
                ois.close();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences datos_usu = contexto.getSharedPreferences("usuario_login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = datos_usu.edit();

                        editor.putString("usuario", usuario2.getUsuario());
                        editor.putString("id", usuario2.getIdUsuario().toString());
                        editor.putString("email", usuario2.getEmail());
                        editor.apply();

                        Intent i = new Intent(getContext(), MainActivity.class);

                        startActivity(i);
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

}
