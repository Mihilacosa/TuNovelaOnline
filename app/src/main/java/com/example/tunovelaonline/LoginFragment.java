package com.example.tunovelaonline;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.tunovelaonline.pojos.Novela;
import com.example.tunovelaonline.pojos.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class LoginFragment extends Fragment {
    String equipoServidor;
    int puertoServidor = 30500;
    Socket socketCliente;
    Usuario usuario = null;

    TextView olvidada;
    private EditText email;
    private EditText contrasena;
    private Button Login;
    String Email;
    private FirebaseAuth mAuth;
    CheckBox humano;
    Context contexto;
    Date date;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        equipoServidor = getString(R.string.ip_server);
        contexto = container.getContext();
        olvidada = v.findViewById(R.id.contOlvidada);
        humano = v.findViewById(R.id.humano);

        mAuth = FirebaseAuth.getInstance();

        Button Registrarse = v.findViewById(R.id.RegistroInicio);
        Registrarse.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Registrarse();
            }
        });

        email = v.findViewById(R.id.Email_login);
        contrasena = v.findViewById(R.id.Contrasena_login);
        Login = v.findViewById(R.id.bLogin);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(humano.isChecked()) {
                    Email = email.getText().toString();
                    String Contrasena = contrasena.getText().toString();

                    mAuth.signInWithEmailAndPassword(Email, Contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                new Thread(new EnvioLogin()).start();

                            } else {
                                Toast.makeText(getActivity(), "Email o contrase??a incorrectos", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(getContext(), "Debe de ser humano", Toast.LENGTH_SHORT).show();
                }


            }
        });

        olvidada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecuperacion();
            }
        });

        return v;
    }

    public void showRecuperacion(){
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setTitle("Recuperaci??n de contrase??a");
        LinearLayout linearLayout=new LinearLayout(getContext());
        final EditText emailet= new EditText(getContext());

        // write the email using which you registered
        emailet.setText("Email");
        emailet.setMinEms(16);
        emailet.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        linearLayout.addView(emailet);
        linearLayout.setPadding(10,10,10,10);
        builder.setView(linearLayout);

        // Click on Recover and a email will be sent to your registered email id
        builder.setPositiveButton("Recuperar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String emaill=emailet.getText().toString().trim();
                beginRecovery(emaill);
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    void beginRecovery(String emaill) {
        mAuth.sendPasswordResetEmail(emaill).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(getContext(),"Email mandado.",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getContext(),"Ha ocurido un error.",Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Error Failed",Toast.LENGTH_LONG).show();
            }
        });
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
                dos.writeUTF(Email);


                ObjectInputStream ois = new ObjectInputStream(socketCliente.getInputStream());
                usuario = (Usuario) ois.readObject();

                os.close();
                dos.close();
                ois.close();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences datos_usu = contexto.getSharedPreferences("usuario_login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = datos_usu.edit();

                        editor.putString("usuario", usuario.getUsuario());
                        editor.putString("id", usuario.getIdUsuario().toString());
                        editor.putString("email", usuario.getEmail());
                        editor.putString("imagen", usuario.getImagen());
                        if(String.valueOf(usuario.getSuscripcion()).length() > 2){
                            try {
                                date = new SimpleDateFormat("yyyy-MM-dd").parse(String.valueOf(usuario.getSuscripcion()));

                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                String hoy = simpleDateFormat.format(new Date());
                                Date hoyFecha = new SimpleDateFormat("yyyy-MM-dd").parse(hoy);

                                if(hoyFecha.before(date)){
                                    editor.putString("suscripcion", "true");
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        editor.putString("fecha_sus", String.valueOf(usuario.getSuscripcion()));
                        editor.putString("tamano", String.valueOf(usuario.getTamanoLetra()));
                        editor.putString("font", usuario.getFontLetra());
                        editor.putString("color", usuario.getTema());
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

    public void Registrarse(){
        getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new RegistroFragment()).addToBackStack( "tag" ).commit();
    }

}
