package com.example.carlos.biketrip;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import entidades.RutaEnt;
import entidades.Usuario;

public class MenuPrincipal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{


    ImageView imageView;
    TextView nombreU;
    TextView kilometrosRec;
    TextView cantViajesTot;

    private FirebaseAuth mAuth;
    private	FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser user;

    FirebaseDatabase database;
    DatabaseReference myRef;

    StorageReference mStorageRef;

    public	static	final	String	PATH_USERS="users/";
    public	static	final	String	PATH_RUTAS="rutas/";
    public	static	final	String	PATH_IMAGENES="images/";
    public static final String TAG="TAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAuth =	FirebaseAuth.getInstance();
        user	=	mAuth.getCurrentUser();
        database=	FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        View headerview = navigationView.getHeaderView(0);

        imageView = (ImageView)headerview.findViewById(R.id.ibtnPerfilPersonal);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), PerfilPersonal.class);
                startActivity(intent);
            }
        });

        nombreU = (TextView)headerview.findViewById(R.id.nombreUNavBar);
        kilometrosRec = (TextView)headerview.findViewById(R.id.kiloUNavBar);
        cantViajesTot = (TextView)headerview.findViewById(R.id.cantidadViajesNavBar);

        if(user!=null){
            loadUsers();
            loadRutas();


        }

    }



    public	void	loadUsers()	{

        myRef =	database.getReference(PATH_USERS);
        myRef.addValueEventListener(new	ValueEventListener()	{
            @Override
            public	void	onDataChange(DataSnapshot dataSnapshot)	 {

                for	(DataSnapshot singleSnapshot :	dataSnapshot.getChildren())	{

                    Usuario u=new Usuario();
                    u=	singleSnapshot.getValue(Usuario.class);
                    u.setID(singleSnapshot.getKey());

                    if(u.getID().equals(user.getUid())){
                        nombreU.setText(u.getNombre() + " " +u.getApellido());

                        if(!u.getImagen().equals("")){
                            mStorageRef = FirebaseStorage.getInstance().getReference(PATH_IMAGENES+u.getImagen());
                            Glide.with(getBaseContext())
                                    .using(new FirebaseImageLoader())
                                    .load(mStorageRef)
                                    .into(imageView);
                        }

                    }

                    Log.i("DATOS ALMACENADOS USER",u.toString());
                }
            }
            @Override
            public	void	onCancelled(DatabaseError databaseError)	{
                Log.w(TAG,	"error	en	la	consulta",	databaseError.toException());
            }
        });

    }

    public	void	loadRutas()	{

        myRef =	database.getReference(PATH_RUTAS);
        myRef.addValueEventListener(new	ValueEventListener()	{
            @Override
            public	void	onDataChange(DataSnapshot dataSnapshot)	 {

                int cantidad = 0;
                double km =0;
                for	(DataSnapshot singleSnapshot :	dataSnapshot.getChildren())	{

                    RutaEnt r=new RutaEnt();
                    r=	singleSnapshot.getValue(RutaEnt.class);
                   if(r.getIdUsuario().equals(user.getUid())){
                        cantidad+=1;
                        km+=r.getDistancia();

                   }
                    Log.i("DATOS ALMACENADOS RUTA",r.toString());
                }
                cantViajesTot.append(" "+cantidad);
                kilometrosRec.append(" "+km);
            }
            @Override
            public	void	onCancelled(DatabaseError databaseError)	{
                Log.w(TAG,	"error	en	la	consulta",	databaseError.toException());
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        Fragment fragment = null;
        boolean fragementoSeleccionado=false;

        int id = item.getItemId();

        if (id == R.id.nav_IniciarRuta) {
            fragment = new MapaRuta();
            fragementoSeleccionado = true;

        } else if (id == R.id.nav_Historial) {
            fragment = new Historial();
            fragementoSeleccionado = true;


        } else if (id == R.id.nav_Amigos) {
            fragment = new Amigos();
            fragementoSeleccionado = true;

        } else if (id == R.id.nav_Notificaciones) {
            fragment = new Notificaciones();
            fragementoSeleccionado = true;
        } else if (id == R.id.nav_Configuracion) {

        } else if (id == R.id.nav_CerrarSesion) {
            mAuth.signOut();
            Intent intent= new Intent(getBaseContext(), MainActivity.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
            }
            startActivity(intent);
        }

        if(fragementoSeleccionado){
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                    fragment).commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
