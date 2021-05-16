package com.example.tunovelaonline;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tunovelaonline.pojos.Novela;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class ModificarNovelaFragment extends Fragment {
    String equipoServidor;
    int puertoServidor = 30500;
    Socket socketCliente;
    Novela novela;
    Novela novelaModi;

    private String usuario = "";
    String id_usuario = "";
    String id_novela = "";

    private static int SELECT_PICTURE = 2;

    String titulo_modi, resena_modi, nombre_alt_modi, autor_modi, artista_modi, traductor_modi, genero_modi;

    TextView titulo;
    TextView resena;
    ImageView portada_img;
    Button portada;
    Uri selectedImageURI, returnUri;
    Bitmap bitmap = null;
    String imagename;
    TextView nombre_alt;
    TextView autor;
    TextView artista;
    TextView traductor;
    String genero = "";
    CheckBox checkBox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6, checkBox7, checkBox8, checkBox9, checkBox10,
            checkBox11, checkBox12, checkBox13, checkBox14, checkBox15, checkBox16, checkBox18, checkBox19, checkBox20,
            checkBox21, checkBox22, checkBox23, checkBox24, checkBox25, checkBox26, checkBox27, checkBox28, checkBox29, checkBox30,
            checkBox31, checkBox32, checkBox33, checkBox34, checkBox35, checkBox36, checkBox37, checkBox38, checkBox39, checkBox40;
    Button enviar;
    TextView modi_generos;
    String extension;

    RequestQueue requestQueue;
    View v;
    Context contexto;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_modificar_novela, container, false);
        equipoServidor = getString(R.string.ip_server);
        contexto = container.getContext();

        titulo = v.findViewById(R.id.modi_nuevo_titulo);
        resena = v.findViewById(R.id.modi_nueva_resena);
        portada_img = v.findViewById(R.id.modi_nueva_portada);
        portada = v.findViewById(R.id.modi_btn_portada);
        nombre_alt = v.findViewById(R.id.modi_nuevo_nombre_alt);
        autor = v.findViewById(R.id.modi_nuevo_autor);
        artista = v.findViewById(R.id.modi_nuevo_artista);
        traductor = v.findViewById(R.id.modi_nuevo_traductor);
        generos_inicio();
        enviar = v.findViewById(R.id.modi_btnEnviar_novela);
        modi_generos = v.findViewById(R.id.modi_generos);


        Bundle bundle = this.getArguments();
        id_novela = bundle.getString("id");

        imagename = "id_" + id_novela;

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            SharedPreferences datos_usu = this.getActivity().getSharedPreferences("usuario_login", Context.MODE_PRIVATE);
            usuario = datos_usu.getString("usuario", "");
            id_usuario = datos_usu.getString("id", "");
            if (!usuario.equals("")) {
                //setTitle("Hola " + usuario);
            }
        }

        //registerForContextMenu(findViewById(R.id.main));
        portada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchImage();
            }
        });

        new Thread(new CargarNovela()).start();

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generos();

                new Thread(new Subir()).start();

                if(!(bitmap == null)){
                    SubirImagen(bitmap);
                    new Thread(new UpdateURL()).start();
                }

                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container,new ModificarNovelasFragment()).commit();
                    }
                },1000); // milliseconds: 1 seg.


            }
        });

        return v;
    }

    class CargarNovela implements Runnable {
        @Override
        public void run() {
            try {
                socketCliente = new Socket(equipoServidor, puertoServidor);

                OutputStream os = socketCliente.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF("mostrar novela");

                os = socketCliente.getOutputStream();
                dos = new DataOutputStream(os);
                dos.writeUTF(id_novela);

                ObjectInputStream ois = new ObjectInputStream(socketCliente.getInputStream());
                novela = (Novela) ois.readObject();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Picasso.get().load(novela.getPortada()).into(portada_img);
                        titulo_modi = novela.getTitulo();
                        resena_modi = novela.getResena();
                        nombre_alt_modi = novela.getNombreAlternativo();
                        autor_modi = novela.getAutor();
                        artista_modi = novela.getArtista();
                        traductor_modi = novela.getTraductor();
                        genero_modi = novela.getGenero();

                        titulo.setText(titulo_modi);
                        resena.setText(resena_modi);
                        nombre_alt.setText(nombre_alt_modi);
                        autor.setText(autor_modi);
                        artista.setText(artista_modi);
                        traductor.setText(traductor_modi);
                        modi_generos.setText("Genero: " + genero_modi);
                    }
                });

                os.close();
                dos.close();
                ois.close();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    public void generos_inicio() {
        checkBox1 = v.findViewById(R.id.modi_checkBox1);
        checkBox2 = v.findViewById(R.id.modi_checkBox2);
        checkBox3 = v.findViewById(R.id.modi_checkBox3);
        checkBox4 = v.findViewById(R.id.modi_checkBox4);
        checkBox5 = v.findViewById(R.id.modi_checkBox5);
        checkBox6 = v.findViewById(R.id.modi_checkBox6);
        checkBox7 = v.findViewById(R.id.modi_checkBox7);
        checkBox8 = v.findViewById(R.id.modi_checkBox8);
        checkBox9 = v.findViewById(R.id.modi_checkBox9);
        checkBox10 = v.findViewById(R.id.modi_checkBox10);

        checkBox11 = v.findViewById(R.id.modi_checkBox11);
        checkBox12 = v.findViewById(R.id.modi_checkBox12);
        checkBox13 = v.findViewById(R.id.modi_checkBox13);
        checkBox14 = v.findViewById(R.id.modi_checkBox14);
        checkBox15 = v.findViewById(R.id.modi_checkBox15);
        checkBox16 = v.findViewById(R.id.modi_checkBox16);
        checkBox18 = v.findViewById(R.id.modi_checkBox18);
        checkBox19 = v.findViewById(R.id.modi_checkBox19);
        checkBox20 = v.findViewById(R.id.modi_checkBox20);

        checkBox21 = v.findViewById(R.id.modi_checkBox21);
        checkBox22 = v.findViewById(R.id.modi_checkBox22);
        checkBox23 = v.findViewById(R.id.modi_checkBox23);
        checkBox24 = v.findViewById(R.id.modi_checkBox24);
        checkBox25 = v.findViewById(R.id.modi_checkBox25);
        checkBox26 = v.findViewById(R.id.modi_checkBox26);
        checkBox27 = v.findViewById(R.id.modi_checkBox27);
        checkBox28 = v.findViewById(R.id.modi_checkBox28);
        checkBox29 = v.findViewById(R.id.modi_checkBox29);
        checkBox30 = v.findViewById(R.id.modi_checkBox30);

        checkBox31 = v.findViewById(R.id.modi_checkBox31);
        checkBox32 = v.findViewById(R.id.modi_checkBox32);
        checkBox33 = v.findViewById(R.id.modi_checkBox33);
        checkBox34 = v.findViewById(R.id.modi_checkBox34);
        checkBox35 = v.findViewById(R.id.modi_checkBox35);
        checkBox36 = v.findViewById(R.id.modi_checkBox36);
        checkBox37 = v.findViewById(R.id.modi_checkBox37);
        checkBox38 = v.findViewById(R.id.modi_checkBox38);
        checkBox39 = v.findViewById(R.id.modi_checkBox39);
        checkBox40 = v.findViewById(R.id.modi_checkBox40);
    }

    public void generos() {
        if (checkBox1.isChecked()){
            genero += checkBox1.getText() + ", ";
        }
        if (checkBox2.isChecked()){
            genero += checkBox2.getText() + ", ";
        }
        if (checkBox3.isChecked()){
            genero += checkBox3.getText() + ", ";
        }
        if (checkBox4.isChecked()){
            genero += checkBox4.getText() + ", ";
        }
        if (checkBox5.isChecked()){
            genero += checkBox5.getText() + ", ";
        }
        if (checkBox6.isChecked()){
            genero += checkBox6.getText() + ", ";
        }
        if (checkBox7.isChecked()){
            genero += checkBox7.getText() + ", ";
        }
        if (checkBox8.isChecked()){
            genero += checkBox8.getText() + ", ";
        }
        if (checkBox9.isChecked()){
            genero += checkBox9.getText() + ", ";
        }
        if (checkBox10.isChecked()){
            genero += checkBox10.getText() + ", ";
        }

        if (checkBox11.isChecked()){
            genero += checkBox11.getText() + ", ";
        }
        if (checkBox12.isChecked()){
            genero += checkBox12.getText() + ", ";
        }
        if (checkBox13.isChecked()){
            genero += checkBox13.getText() + ", ";
        }
        if (checkBox14.isChecked()){
            genero += checkBox14.getText() + ", ";
        }
        if (checkBox15.isChecked()){
            genero += checkBox15.getText() + ", ";
        }
        if (checkBox16.isChecked()){
            genero += checkBox16.getText() + ", ";
        }
        if (checkBox18.isChecked()){
            genero += checkBox18.getText() + ", ";
        }
        if (checkBox19.isChecked()){
            genero += checkBox19.getText() + ", ";
        }
        if (checkBox20.isChecked()){
            genero += checkBox20.getText() + ", ";
        }

        if (checkBox21.isChecked()){
            genero += checkBox21.getText() + ", ";
        }
        if (checkBox22.isChecked()){
            genero += checkBox22.getText() + ", ";
        }
        if (checkBox23.isChecked()){
            genero += checkBox23.getText() + ", ";
        }
        if (checkBox24.isChecked()){
            genero += checkBox24.getText() + ", ";
        }
        if (checkBox25.isChecked()){
            genero += checkBox25.getText() + ", ";
        }
        if (checkBox26.isChecked()){
            genero += checkBox26.getText() + ", ";
        }
        if (checkBox27.isChecked()){
            genero += checkBox27.getText() + ", ";
        }
        if (checkBox28.isChecked()){
            genero += checkBox28.getText() + ", ";
        }
        if (checkBox29.isChecked()){
            genero += checkBox29.getText() + ", ";
        }
        if (checkBox30.isChecked()){
            genero += checkBox30.getText() + ", ";
        }

        if (checkBox31.isChecked()){
            genero += checkBox31.getText() + ", ";
        }
        if (checkBox32.isChecked()){
            genero += checkBox32.getText() + ", ";
        }
        if (checkBox33.isChecked()){
            genero += checkBox33.getText() + ", ";
        }
        if (checkBox34.isChecked()){
            genero += checkBox34.getText() + ", ";
        }
        if (checkBox35.isChecked()){
            genero += checkBox35.getText() + ", ";
        }
        if (checkBox36.isChecked()){
            genero += checkBox36.getText() + ", ";
        }
        if (checkBox37.isChecked()){
            genero += checkBox37.getText() + ", ";
        }
        if (checkBox38.isChecked()){
            genero += checkBox38.getText() + ", ";
        }
        if (checkBox39.isChecked()){
            genero += checkBox39.getText() + ", ";
        }
        if (checkBox40.isChecked()){
            genero += checkBox40.getText() + ", ";
        }

        if(!genero.equals("")) {
            genero = genero.substring(0, genero.length() - 2);
        }
    }

    private void fetchImage(){
        final CharSequence[] options = { "Sacar foto", "Elegir de galeria","Cancelar" };
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Sacar foto"))
                {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    try {
                        startActivityForResult(takePictureIntent, 1);
                    } catch (ActivityNotFoundException e) {
                        // display error state to the user
                    }
                }
                else if (options[item].equals("Elegir de galeria"))
                {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
                }
                else if (options[item].equals("Cancelar")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            portada_img.setImageBitmap(null);
            bitmap = null;
            if (requestCode == 1) {
                bitmap = (Bitmap) data.getExtras().get("data");
                extension = ".png";
                portada_img.setImageBitmap(bitmap);
            } else {

                selectedImageURI = data.getData();
                //rutaImagen = getPath(selectedImageURI);
                //rutaImagen = selectedImageURI.getPath();

                if (requestCode == SELECT_PICTURE && data != null && data.getData() != null) {
                    try {
                        Picasso.get().load(selectedImageURI).noPlaceholder().centerCrop().fit().into(portada_img);
                        //Log.d("filePath", String.valueOf(rutaImagen));
                        bitmap = MediaStore.Images.Media.getBitmap(contexto.getContentResolver(), selectedImageURI);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                returnUri = data.getData();
                Cursor returnCursor = contexto.getContentResolver().query(returnUri, null, null, null, null);

                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                returnCursor.moveToFirst();

                extension = returnCursor.getString(nameIndex);

                extension = extension.substring(extension.lastIndexOf("."));

            }
        }
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    class Subir implements Runnable {
        @Override
        public void run() {
            try {
                socketCliente = new Socket(equipoServidor, puertoServidor);

                OutputStream os = socketCliente.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF("novela para modificar");

                novelaModi = new Novela();
                novelaModi.setIdNovela(Integer.parseInt(id_novela));
                novelaModi.setTitulo(titulo.getText().toString());
                novelaModi.setResena(resena.getText().toString());
                novelaModi.setNombreAlternativo(nombre_alt.getText().toString());
                novelaModi.setAutor(autor.getText().toString());
                novelaModi.setArtista(artista.getText().toString());
                novelaModi.setTraductor(traductor.getText().toString());
                if(genero == ""){
                    genero = genero_modi;
                }
                novelaModi.setGenero(genero);

                ObjectOutputStream oos = new ObjectOutputStream(socketCliente.getOutputStream());
                oos.writeObject(novelaModi);

                os.close();
                dos.close();
                oos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void SubirImagen(final Bitmap bitmap) {
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, "https://tnowebservice.000webhostapp.com/Subir_portada.php", new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                //Toast.makeText(SubirNovela.this, "Imagen subida", Toast.LENGTH_LONG).show();
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("GotError","" + error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("imagen", new DataPart(imagename + extension, getFileDataFromDrawable(bitmap)));
                return params;
            }
        };
        //adding the request to volley
        Volley.newRequestQueue(getContext()).add(volleyMultipartRequest);
    }

    class UpdateURL implements Runnable {
        @Override
        public void run() {
            try {
                socketCliente = new Socket(equipoServidor, puertoServidor);

                OutputStream os = socketCliente.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF("novela para modificar url");

                dos.writeUTF(id_novela);
                dos.writeUTF(extension);

                os.close();
                dos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
