package com.example.tunovelaonline;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;

public class LoginFragment extends Fragment {

    private EditText email;
    private EditText contrasena;
    private Button Login;
    private FirebaseAuth mAuth;
    CheckBox humano;
    Context contexto;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        contexto = container.getContext();

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
                    String Email = email.getText().toString();
                    String Contrasena = contrasena.getText().toString();

                    mAuth.signInWithEmailAndPassword(Email, Contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                EnvioLogin("https://tnowebservice.000webhostapp.com/Login.php?email=" + Email);

                                Intent i = new Intent(getContext(), MainActivity.class);

                                startActivity(i);

                            } else {
                                Toast.makeText(getActivity(), "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(getContext(), "Debe de ser humano", Toast.LENGTH_SHORT).show();
                }


            }
        });

        return v;
    }

    private void EnvioLogin(String URL) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;

                SharedPreferences datos_usu = contexto.getSharedPreferences("usuario_login", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = datos_usu.edit();

                for (int i = 0; i < response.length(); i++){
                    try {
                        jsonObject = response.getJSONObject(i);
                        editor.putString("usuario", jsonObject.getString("usuario"));
                        editor.putString("id", jsonObject.getString("id_usuario"));
                        editor.putString("email", jsonObject.getString("email"));
                        editor.apply();
                    } catch (JSONException e){
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "ERROR AL COMPROBAR DATOS", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonArrayRequest);
    }

    public void Registrarse(){
        getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new RegistroFragment()).commit();
    }

}
