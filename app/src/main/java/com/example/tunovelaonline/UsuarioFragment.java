package com.example.tunovelaonline;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tunovelaonline.pojos.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

public class UsuarioFragment extends Fragment {
    String equipoServidor;
    int puertoServidor = 30500;
    Socket socketCliente;

    String usuario = "";
    String id_usuario, usu_email;
    View view;
    EditText usuarioNuevo, emailNuevo, cont_act, cont_nueva, cont_rep;
    Button enviar;
    String usu,email,contAct = "",cont,cont2;
    Boolean usuDif = true, emailDif = true;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_usuario, container, false);
        equipoServidor = getString(R.string.ip_server);

        usuarioNuevo = view.findViewById(R.id.nuevoUsuario);
        emailNuevo = view.findViewById(R.id.nuevoEmail);
        cont_act = view.findViewById(R.id.contrasena_actual);
        cont_nueva = view.findViewById(R.id.nueva_contrasena);
        cont_rep = view.findViewById(R.id.nueva_contrasena_seg);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            SharedPreferences datos_usu = this.getActivity().getSharedPreferences("usuario_login", Context.MODE_PRIVATE);
            usuario = datos_usu.getString("usuario", "");
            id_usuario = datos_usu.getString("id", "");
            usu_email = datos_usu.getString("email", "");

            usuarioNuevo.setText(usuario);
            emailNuevo.setText(usu_email);

        }

        enviar = view.findViewById(R.id.btnEnviar_usuario);

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usu = usuarioNuevo.getText().toString();
                email = emailNuevo.getText().toString();
                contAct = cont_act.getText().toString();
                cont = cont_nueva.getText().toString();
                cont2 = cont_rep.getText().toString();

                if(!usu.equals(usuario) || TextUtils.isEmpty(usu)){
                    usu = usuario;
                    usuDif = false;
                }

                if(!email.equals(usu_email) || TextUtils.isEmpty(email)){
                    email = usu_email;
                    emailDif = false;
                }

                if(TextUtils.isEmpty(contAct)){

                }else{
                    if(cont.length() < 6 && cont.length() > 0 ){
                        cont_nueva.setError("la contraseña debe tener como minimo 6 caracteres");
                        return;
                    }

                    if(!cont.equals(cont2)) {
                        cont_rep.setError("No coincide con la contraseña");
                        return;
                    }

                    CambioContrasena();
                }


                if(emailDif == false && usuDif == false && TextUtils.isEmpty(contAct)){
                    Toast.makeText(getContext(), "No ha proporcionado información nueva", Toast.LENGTH_SHORT).show();
                }else{
                    new Thread(new CambioDatosUsuario()).start();
                }
            }
        });

        return view;
    }

    public void CambioContrasena(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AuthCredential credential = EmailAuthProvider.getCredential(usu_email, contAct);

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    user.updatePassword(cont).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Password updated");
                                Toast.makeText(getContext(), "Contraseña modificada", Toast.LENGTH_SHORT).show();
                                new Thread(new CambioDatosUsuario()).start();
                            } else {
                                Log.d(TAG, "Error password not updated");
                                Toast.makeText(getContext(), "Contraseña no modificada", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Log.d(TAG, "Error auth failed");
                    Toast.makeText(getContext(), "Contraseña erronea", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    class CambioDatosUsuario implements Runnable {
        @Override
        public void run() {
            try {
                socketCliente = new Socket(equipoServidor, puertoServidor);

                OutputStream os = socketCliente.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF("cambio usuario");

                Usuario usuario = new Usuario();
                usuario.setIdUsuario(Integer.parseInt(id_usuario));
                usuario.setUsuario(usu);
                //Comprobar en server si hay o no contraseña
                usuario.setContrasena(contAct);
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
                                getFragmentManager().beginTransaction().replace(R.id.fragment_container,new InicioFragment()).commit();
                            }
                        }, 1000);
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
