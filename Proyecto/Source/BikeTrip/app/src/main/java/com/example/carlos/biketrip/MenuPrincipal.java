package com.example.carlos.biketrip;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import entidades.RutaEnt;
import entidades.Usuario;

public class MenuPrincipal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{


    ImageView imageView;
    TextView nombreU;
    TextView kilometrosRec;
    TextView cantViajesTot;
    private int intActividad;

    private FirebaseAuth mAuth;
    FirebaseUser user;
    private Usuario actual;

    FirebaseDatabase database;
    DatabaseReference myRef;

    StorageReference mStorageRef;

    public	static	final	String	PATH_USERS="users/";
    public	static	final	String	PATH_RUTAS="rutas/";
    public	static	final	String	PATH_IMAGENES="images/";
    public static final String TAG="TAG";

    Fragment fragment = null;
    boolean fragementoSeleccionado=false;
    NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        intActividad = 0;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        Intent i = getIntent();
        int ed = i.getIntExtra("intActividad",0);
        if (ed!=0){
            switch (ed){
                case 1:
                {
                    fragment = new MapaRuta();
                }
                break;
                case 2:
                {
                    fragment = new Historial();
                }
                break;
                case 3:
                {
                    fragment = new Amigos();
                }
                break;
                case 4:
                {
                    fragment = new RecorridosDestacados();
                }
                break;
                case 5:
                {
                    fragment = new CrearRecorridosDestacados();
                }
                break;
                case 6:
                {
                    //fragment = new CrearPuntoEmpresa();

                }
                break;
                case 7:
                {
                    fragment = new Notificaciones();
                }
                break;

            }

            FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, fragment);
            transaction.commit();
            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

        }else{
            FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, new MapaRuta());
            transaction.commit();
            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
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
                        actual = u;
                        nombreU.setText(u.getNombre() + " " +u.getApellido());

                        if(!u.getImagen().equals("")){
                            mStorageRef = FirebaseStorage.getInstance().getReference(PATH_IMAGENES
                                    +user.getUid()+"/"+u.getImagen());
                            Glide.with(getBaseContext())
                                    .using(new FirebaseImageLoader())
                                    .load(mStorageRef)
                                    .into(imageView);
                        }

                    }

                    Log.i("DATOS ALMACENADOS USER",u.toString());
                }
                if(actual.getTipo()==1){
                    Menu menuNav=navigationView.getMenu();

                    MenuItem nav_item2 = menuNav.findItem(R.id.nav_CrearRecorridosDestacados);
                    MenuItem nav_item3 = menuNav.findItem(R.id.nav_CrearPunto);

                    nav_item2.setEnabled(false);
                    nav_item2.setVisible(false);

                    nav_item3.setEnabled(false);
                    nav_item3.setVisible(false);
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


        int id = item.getItemId();

        if (id == R.id.nav_IniciarRuta) {
            fragment = new MapaRuta();
            intActividad=1;
            fragementoSeleccionado = true;

        } else if (id == R.id.nav_Historial) {
            fragment = new Historial();
            intActividad=2;
            fragementoSeleccionado = true;
        } else if (id == R.id.nav_Amigos) {
            fragment = new Amigos();
            intActividad=3;
            fragementoSeleccionado = true;

        } else if (id == R.id.nav_RecorridosDestacados) {
            fragment = new RecorridosDestacados();
            intActividad=4;
            fragementoSeleccionado = true;

        }else if (id == R.id.nav_CrearRecorridosDestacados) {
            fragment = new CrearRecorridosDestacados();
            intActividad=5;
            fragementoSeleccionado = true;

        }else if (id == R.id.nav_CrearPunto) {
            //fragment = new CrearPuntoEmpresa();
            intActividad=6;
            //fragementoSeleccionado = true;
            Intent intent = new Intent(getBaseContext(),CrearPuntoEmpresa.class);
            startActivity(intent);


        }else if (id == R.id.nav_Notificaciones) {
            fragment = new Notificaciones();
            fragementoSeleccionado = true;
            intActividad=7;
        } else if (id == R.id.nav_Configuracion) {

        } else if (id == R.id.nav_CerrarSesion) {
            mAuth.signOut();
            LoginManager.getInstance().logOut();
            Intent intent= new Intent(getBaseContext(), MainActivity.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
            }
            startActivity(intent);
        }

        if(fragementoSeleccionado){
            startActivity(new Intent(getBaseContext(),MenuPrincipal.class)
                    .putExtra("intActividad",intActividad));
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
