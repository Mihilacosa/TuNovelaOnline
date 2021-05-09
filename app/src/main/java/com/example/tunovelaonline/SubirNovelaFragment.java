package com.example.tunovelaonline;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tunovelaonline.pojos.Capitulo;
import com.example.tunovelaonline.pojos.Novela;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.NOTIFICATION_SERVICE;

public class SubirNovelaFragment extends Fragment {
    String equipoServidor = "192.168.1.116";
    int puertoServidor = 30500;
    Socket socketCliente;
    Novela novela = new Novela();
    Capitulo capitulo = new Capitulo();

    private String usuario = "";
    String id_usuario = "";
    String id_novela = "";

    private final static String CHANNEL_ID = "NOTIFICACION";

    private static int SELECT_PICTURE = 2;

    TextView titulo;
    TextView resena;
    ImageView portada_img;
    Button portada;
    Uri selectedImageURI;
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
    TextView titulo_cap;
    RadioButton tipo0, tipo1;
    String tipo_cap = "";
    TextView contenido_cap;
    Button enviar;
    ScrollView scrollNovela;
    String extension;

    RequestQueue requestQueue;
    View v;
    Context contexto;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_subir_novela, container, false);

        contexto = container.getContext();

        titulo = v.findViewById(R.id.nuevo_titulo);
        resena = v.findViewById(R.id.nueva_resena);
        portada_img = v.findViewById(R.id.nueva_portada);
        portada = v.findViewById(R.id.btn_portada);
        nombre_alt = v.findViewById(R.id.nuevo_nombre_alt);
        autor = v.findViewById(R.id.nuevo_autor);
        artista = v.findViewById(R.id.nuevo_artista);
        traductor = v.findViewById(R.id.nuevo_traductor);
        generos_inicio();
        titulo_cap = v.findViewById(R.id.nuevo_titulo_cap);
        tipo0 = v.findViewById(R.id.nuevo_prologo);
        tipo1 = v.findViewById(R.id.nuevo_cap_1);
        contenido_cap = v.findViewById(R.id.nuevo_contenido_cap);
        enviar = v.findViewById(R.id.btnEnviar_novela);
        scrollNovela = v.findViewById(R.id.ScrollSubirNovela);

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

        tipo_cap_seleccionado();

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(titulo.getText())){
                    titulo.setError("El título es obligatorio");
                    scrollNovela.smoothScrollTo(0, 0);
                    return;
                }

                if(TextUtils.isEmpty(resena.getText())){
                    resena.setError("La reseña es obligatoria");
                    scrollNovela.smoothScrollTo(0, 0);
                    return;
                }

                if(TextUtils.isEmpty(autor.getText())){
                    autor.setError("El autor es obligatorio");
                    scrollNovela.smoothScrollTo(0, 0);
                    return;
                }

                if(TextUtils.isEmpty(titulo_cap.getText())){
                    titulo_cap.setError("El titulo de capitulo es obligatorio");
                    return;
                }

                if(TextUtils.isEmpty(contenido_cap.getText())){
                    contenido_cap.setError("El contenido es obligatorio");
                    return;
                }

                generos();

                if(TextUtils.isEmpty(genero)){
                    Toast.makeText(getContext(), "Debe de seleccionar como mínimo un genero.", Toast.LENGTH_SHORT).show();
                    scrollNovela.smoothScrollTo(0, 2000);
                    return;
                }

                new Thread(new Subir()).start();

                long start = System.currentTimeMillis();
                long end = start + 2*1000; // 60 seconds * 1000 ms/sec
                while (System.currentTimeMillis() < end) {

                    getFragmentManager().beginTransaction().replace(R.id.fragment_container,new InicioFragment()).commit();
                }
            }
        });

        return v;
    }


    public void generos_inicio() {
        checkBox1 = v.findViewById(R.id.checkBox1);
        checkBox2 = v.findViewById(R.id.checkBox2);
        checkBox3 = v.findViewById(R.id.checkBox3);
        checkBox4 = v.findViewById(R.id.checkBox4);
        checkBox5 = v.findViewById(R.id.checkBox5);
        checkBox6 = v.findViewById(R.id.checkBox6);
        checkBox7 = v.findViewById(R.id.checkBox7);
        checkBox8 = v.findViewById(R.id.checkBox8);
        checkBox9 = v.findViewById(R.id.checkBox9);
        checkBox10 = v.findViewById(R.id.checkBox10);

        checkBox11 = v.findViewById(R.id.checkBox11);
        checkBox12 = v.findViewById(R.id.checkBox12);
        checkBox13 = v.findViewById(R.id.checkBox13);
        checkBox14 = v.findViewById(R.id.checkBox14);
        checkBox15 = v.findViewById(R.id.checkBox15);
        checkBox16 = v.findViewById(R.id.checkBox16);
        checkBox18 = v.findViewById(R.id.checkBox18);
        checkBox19 = v.findViewById(R.id.checkBox19);
        checkBox20 = v.findViewById(R.id.checkBox20);

        checkBox21 = v.findViewById(R.id.checkBox21);
        checkBox22 = v.findViewById(R.id.checkBox22);
        checkBox23 = v.findViewById(R.id.checkBox23);
        checkBox24 = v.findViewById(R.id.checkBox24);
        checkBox25 = v.findViewById(R.id.checkBox25);
        checkBox26 = v.findViewById(R.id.checkBox26);
        checkBox27 = v.findViewById(R.id.checkBox27);
        checkBox28 = v.findViewById(R.id.checkBox28);
        checkBox29 = v.findViewById(R.id.checkBox29);
        checkBox30 = v.findViewById(R.id.checkBox30);

        checkBox31 = v.findViewById(R.id.checkBox31);
        checkBox32 = v.findViewById(R.id.checkBox32);
        checkBox33 = v.findViewById(R.id.checkBox33);
        checkBox34 = v.findViewById(R.id.checkBox34);
        checkBox35 = v.findViewById(R.id.checkBox35);
        checkBox36 = v.findViewById(R.id.checkBox36);
        checkBox37 = v.findViewById(R.id.checkBox37);
        checkBox38 = v.findViewById(R.id.checkBox38);
        checkBox39 = v.findViewById(R.id.checkBox39);
        checkBox40 = v.findViewById(R.id.checkBox40);
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

    public void tipo_cap_seleccionado() {
        if (tipo0.isChecked()){
            tipo_cap = "0";
        }

        if (tipo1.isChecked()){
            tipo_cap = "1";
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

                Uri returnUri = data.getData();
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
                dos.writeUTF("subir novela");

                dos.writeUTF(extension);

                novela.setIdUsuario(Integer.parseInt(id_usuario));
                novela.setTitulo(titulo.getText().toString());
                novela.setResena(resena.getText().toString());
                novela.setNombreAlternativo(nombre_alt.getText().toString());
                novela.setAutor(autor.getText().toString());
                novela.setArtista(artista.getText().toString());
                novela.setTraductor(traductor.getText().toString());
                novela.setGenero(genero);

                capitulo.setTitulo(titulo_cap.getText().toString());
                capitulo.setNumCapitulo(Integer.parseInt(tipo_cap));
                capitulo.setContenido(contenido_cap.getText().toString());

                ObjectOutputStream oos = new ObjectOutputStream(socketCliente.getOutputStream());
                oos.writeObject(novela);

                oos = new ObjectOutputStream(socketCliente.getOutputStream());
                oos.writeObject(capitulo);

                InputStream is = socketCliente.getInputStream();
                DataInputStream dis = new DataInputStream(is);
                id_novela = String.valueOf(dis.readInt());

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imagename = "id_" + id_novela;
                        SubirImagen(bitmap);
                    }
                });

                os.close();
                dos.close();
                oos.close();

                socketCliente.close();
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

}
