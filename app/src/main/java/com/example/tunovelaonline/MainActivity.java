package com.example.tunovelaonline;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawer;
    NovelaFragment novela;
    private String usuario = "";
    String id_usuario = "";
    String usu_email = "";
    String imagen_fire = "";

    Menu nav_Menu;
    NavigationView navigationView;
    View v;
    TextView name, email;
    boolean log;
    ImageView imagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        log = false;

        onNewIntent(getIntent());

        navigationView = findViewById(R.id.nav_view);

        v = navigationView.getHeaderView(0);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            SharedPreferences datos_usu = getSharedPreferences("usuario_login", Context.MODE_PRIVATE);
            usuario = datos_usu.getString("usuario", "");
            id_usuario = datos_usu.getString("id", "");
            usu_email = datos_usu.getString("email", "");
            imagen_fire = datos_usu.getString("imagen", "");
            if (!usuario.equals("")) {
                log = true;
                v.findViewById(R.id.nav_title).setVisibility(View.GONE);
                v.findViewById(R.id.usuInfo).setVisibility(View.VISIBLE);

                name = v.findViewById(R.id.usuName);
                email = v.findViewById(R.id.usuEmail);
                imagen= v.findViewById(R.id.usuIcono);

                name.setText(usuario);
                email.setText(usu_email);
                if(imagen_fire.equals("") || imagen_fire.equals(" ")){

                }else{
                    Picasso.get().load(imagen_fire).noPlaceholder().centerCrop().fit().into(imagen);
                }

                nav_Menu = navigationView.getMenu();

                nav_Menu.findItem(R.id.login).setVisible(false);
                nav_Menu.findItem(R.id.registro).setVisible(false);
                nav_Menu.findItem(R.id.logout).setVisible(true);
                nav_Menu.findItem(R.id.subir_novela).setVisible(true);
                nav_Menu.findItem(R.id.confUsuario).setVisible(true);
                nav_Menu.findItem(R.id.modificar).setVisible(true);
                nav_Menu.findItem(R.id.confpreferencias).setVisible(true);
            }
        }

        if(!log){
            v.findViewById(R.id.usuInfo).setVisibility(View.GONE);
            v.findViewById(R.id.nav_title).setVisibility(View.VISIBLE);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new InicioFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_inicio);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("ID_NOVELA")) {
                String id_novela = extras.getString("ID_NOVELA");

                Bundle bundle = new Bundle();
                bundle.putString("id",id_novela);
                novela = new NovelaFragment();
                novela.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,novela).commit();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_inicio:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new InicioFragment()).commit();
                break;
            case R.id.busqueda:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new BusquedaFragment()).addToBackStack( "tag" ).commit();
                break;
            case R.id.login:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new LoginFragment()).addToBackStack( "tag" ).commit();
                break;
            case R.id.registro:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new RegistroFragment()).addToBackStack( "tag" ).commit();
                break;
            case R.id.subir_novela:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SubirNovelaFragment()).addToBackStack( "tag" ).commit();
                break;
            case R.id.modificar:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ModificarNovelasFragment()).addToBackStack( "tag" ).commit();
                break;
            case R.id.confUsuario:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new UsuarioFragment()).addToBackStack( "tag" ).commit();
                break;
            case R.id.confpreferencias:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PreferenciaFragment()).addToBackStack( "tag" ).commit();
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();

                nav_Menu.findItem(R.id.login).setVisible(true);
                nav_Menu.findItem(R.id.registro).setVisible(true);
                nav_Menu.findItem(R.id.logout).setVisible(false);
                nav_Menu.findItem(R.id.subir_novela).setVisible(false);
                nav_Menu.findItem(R.id.confUsuario).setVisible(false);
                nav_Menu.findItem(R.id.modificar).setVisible(false);

                Intent i = new Intent(this, MainActivity.class);

                startActivity(i);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}