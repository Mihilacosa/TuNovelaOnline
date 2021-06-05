package com.example.tunovelaonline;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.tunovelaonline.pojos.Usuario;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

public class UsuarioFragment extends Fragment {
    String equipoServidor;
    int puertoServidor = 30500;
    Socket socketCliente;
    private static int SELECT_PICTURE = 2;

    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    private static final String CONFIG_CLIENT_ID = "AQfu_Oe87euQAs0XUtuYA8NgILJ9VF_0Ac_6agq4zVE6sHmZ7MIxcYaWxo8PY3eBf1G3Co4_-8qyhFaO";

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            .acceptCreditCards(true)
            .languageOrLocale("ES")
            .rememberUser(true)
            .merchantName("Tu Novela Online");

    String usuario = "";
    String id_usuario, usu_email, imagen_fire, fecha, fecha_sus;
    View view;
    EditText usuarioNuevo, emailNuevo, cont_act, cont_nueva, cont_rep;
    ImageView paypal,imagen_usu;
    Button enviar,imagen;
    String usu,email,contAct = "",cont,cont2;
    Boolean usuDif = false, emailDif = false, boton_imagen = false;
    TextView textSuscripcion;
    Date date;

    Uri selectedImageURI;
    Bitmap bitmap = null, bitmap_old;
    String imagename;
    String extension = ".png";
    Context contexto;
    private AdView mAdView;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_usuario, container, false);
        equipoServidor = getString(R.string.ip_server);
        contexto = container.getContext();
        textSuscripcion = view.findViewById(R.id.TextSuscricion);
        usuarioNuevo = view.findViewById(R.id.nuevoUsuario);
        emailNuevo = view.findViewById(R.id.nuevoEmail);
        cont_act = view.findViewById(R.id.contrasena_actual);
        cont_nueva = view.findViewById(R.id.nueva_contrasena);
        cont_rep = view.findViewById(R.id.nueva_contrasena_seg);
        paypal = view.findViewById(R.id.PayPal);
        imagen = view.findViewById(R.id.btn_imagen);
        imagen_usu = view.findViewById(R.id.nueva_imagen);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            SharedPreferences datos_usu = this.getActivity().getSharedPreferences("usuario_login", Context.MODE_PRIVATE);
            usuario = datos_usu.getString("usuario", "");
            id_usuario = datos_usu.getString("id", "");
            usu_email = datos_usu.getString("email", "");
            imagen_fire = datos_usu.getString("imagen", "");
            fecha_sus = datos_usu.getString("fecha_sus", "");
            fecha = datos_usu.getString("suscripcion", "");

            if(fecha.length() == 4){
                paypal.setVisibility(View.GONE);
                textSuscripcion.setText("Suscripción hasta: " + fecha_sus);
            }

            usuarioNuevo.setText(usuario);
            emailNuevo.setText(usu_email);
            if(imagen_fire.length() < 5){
                bitmap =((BitmapDrawable)getResources().getDrawable(R.drawable.no_img)).getBitmap();
            }else{
                Picasso.get().load(imagen_fire).noPlaceholder().centerCrop().fit().into(imagen_usu);
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bitmap_old = ((BitmapDrawable)imagen_usu.getDrawable()).getBitmap();
                        bitmap = ((BitmapDrawable)imagen_usu.getDrawable()).getBitmap();
                    }
                },500); // milliseconds: 1 seg.
            }

        }

        mAdView = view.findViewById(R.id.adViewU1);
        if(fecha.length() < 2){
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }else{
            mAdView.setVisibility(View.GONE);
        }

        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchImage();
            }
        });

        paypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBuyPressed(v);
            }
        });

        initPaymentService();

        enviar = view.findViewById(R.id.btnEnviar_usuario);

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usu = String.valueOf(usuarioNuevo.getText());
                email = String.valueOf(emailNuevo.getText());
                contAct = String.valueOf(cont_act.getText());
                cont = String.valueOf(cont_nueva.getText());
                cont2 = String.valueOf(cont_rep.getText());

                if(usu.equals(usuario) || usu.isEmpty()){

                }else{
                    usuario= usu;
                    usuDif = true;
                }

                if(email.equals(usu_email) || email.isEmpty()){

                }else{
                    usu_email = email;
                    emailDif = true;
                    CambioEmail();
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

                if(emailDif == false && usuDif == false && contAct.length() == 0 && boton_imagen == false){
                    Toast.makeText(getContext(), "No ha proporcionado información nueva", Toast.LENGTH_SHORT).show();
                }else{
                    imagename = "id_usu_" + id_usuario + extension;

                    SharedPreferences datos_usu = contexto.getSharedPreferences("usuario_login", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = datos_usu.edit();

                    editor.putString("usuario", usu);
                    editor.putString("id", id_usuario);
                    editor.putString("email", email);
                    editor.putString("imagen", "https://tnowebservice.000webhostapp.com/img/" + imagename);
                    editor.apply();

                    if(boton_imagen == true){
                        SubirImagen(bitmap);
                    }
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
                                Toast.makeText(getActivity(), "Contraseña modificada", Toast.LENGTH_SHORT).show();
                                new Thread(new CambioDatosUsuario()).start();
                            } else {
                                Log.d(TAG, "Error password not updated");
                                Toast.makeText(getActivity(), "Contraseña no modificada", Toast.LENGTH_SHORT).show();
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

    public void CambioEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AuthCredential credential = EmailAuthProvider.getCredential(usu_email, contAct);

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    user.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email updated");
                                Toast.makeText(getActivity(), "Email modificado", Toast.LENGTH_SHORT).show();
                                new Thread(new CambioDatosUsuario()).start();
                            } else {
                                Log.d(TAG, "Error Email not updated");
                                Toast.makeText(getActivity(), "Email no modificado", Toast.LENGTH_SHORT).show();
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
                usuario.setImagen("https://tnowebservice.000webhostapp.com/img/" + imagename);
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
                                SharedPreferences datos_usu = contexto.getSharedPreferences("usuario_login", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = datos_usu.edit();

                                editor.putString("usuario", usu);
                                editor.putString("email", email);
                                editor.putString("imagen", "https://tnowebservice.000webhostapp.com/img/" + imagename);
                                editor.apply();

                                Intent i = new Intent(getContext(), MainActivity.class);
                                startActivity(i);
                            }
                        }, 1000);
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class CambioFecha implements Runnable {
        @Override
        public void run() {
            try {
                socketCliente = new Socket(equipoServidor, puertoServidor);

                OutputStream os = socketCliente.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF("cambio fecha");

                dos.writeUTF(id_usuario);

                InputStream is2 = socketCliente.getInputStream();
                DataInputStream dis2 = new DataInputStream(is2);
                fecha = dis2.readUTF();

                os.close();
                dos.close();

                socketCliente.close();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        paypal.setVisibility(View.GONE);
                        textSuscripcion.setText("Suscripción hasta: " + fecha);
                        SharedPreferences datos_usu = contexto.getSharedPreferences("usuario_login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = datos_usu.edit();

                        editor.putString("suscripcion", fecha);
                        editor.apply();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void initPaymentService() {
        try {
            Intent intent = new Intent(getActivity(), PayPalService.class);
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

            getActivity().startService(intent);
        } catch (Exception e) {
            Log.i("PayPal Exception", e.getMessage());
        }
    }

    public void onBuyPressed(View pressed) {

        PayPalPayment thingToBuy = new PayPalPayment(new BigDecimal(3), "EUR", "Suscripción de 30 días", PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(getActivity(), PaymentActivity.class);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data
                        .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.i("paymentExample", confirm.toJSONObject().toString(4));

                        Toast.makeText(getActivity(), "Pago completado", Toast.LENGTH_LONG).show();

                        new Thread(new CambioFecha()).start();
                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }

                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
                Toast.makeText(getActivity(), "Pago cancelado", Toast.LENGTH_LONG).show();
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment was submitted. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth = data
                        .getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.i("FuturePaymentExample", auth.toJSONObject().toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.i("FuturePaymentExample", authorization_code);

                        sendAuthorizationToServer(auth);
                        Toast.makeText(getActivity().getApplicationContext(), "Future Payment code received from PayPal",
                                Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        Log.e("FuturePaymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("FuturePaymentExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("FuturePaymentExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            imagen_usu.setImageBitmap(null);
            bitmap = null;
            if (requestCode == 1) {
                bitmap = (Bitmap) data.getExtras().get("data");
                extension = ".png";
                imagen_usu.setImageBitmap(bitmap);
                boton_imagen = true;
            } else {

                selectedImageURI = data.getData();
                //rutaImagen = getPath(selectedImageURI);
                //rutaImagen = selectedImageURI.getPath();

                if (requestCode == SELECT_PICTURE && data != null && data.getData() != null) {
                    try {
                        Picasso.get().load(selectedImageURI).noPlaceholder().centerCrop().fit().into(imagen_usu);
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
                boton_imagen = true;
            }
        }
    }

    private void sendAuthorizationToServer(PayPalAuthorization authorization) {

    }

    @Override
    public void onDestroy() {
        // Stop service when done
        getActivity().stopService(new Intent(getActivity(), PayPalService.class));
        super.onDestroy();
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
                    startActivityForResult(Intent.createChooser(intent, "Elegir imagen"), SELECT_PICTURE);
                }
                else if (options[item].equals("Cancelar")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
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
                params.put("imagen", new DataPart(imagename, getFileDataFromDrawable(bitmap)));
                return params;
            }
        };
        //adding the request to volley
        Volley.newRequestQueue(getContext()).add(volleyMultipartRequest);
    }
}
