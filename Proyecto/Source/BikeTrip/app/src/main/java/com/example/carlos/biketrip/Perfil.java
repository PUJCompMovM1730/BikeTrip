package com.example.carlos.biketrip;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

import java.util.HashMap;
import java.util.Map;

import entidades.Mensaje;
import entidades.Usuario;

public class Perfil extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private	FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser user;
    Usuario actual;
    String idUsuarioPerfil;
    String nombreUsuario;


    FirebaseDatabase database;
    DatabaseReference myRef;

    StorageReference mStorageRef;

    public	static	final	String	PATH_USERS="users/";
    public	static	final	String	PATH_IMAGENES="images/";

    public static final String TAG="TAG";

    ImageButton ibAgregar;
    ImageButton ibEliminar;
    ImageButton ibMsj;
    ImageView ivPerfilAmigo;
    ImageView ivPortadaAmigo;
    TextView tvNombreAmigo;
    TextView tvDescripcionAmigo;
    TextView tvEdadAmigo;
    TextView tvCorreoAmigo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);


        idUsuarioPerfil = getIntent().getStringExtra("idPerilAmigoDesdeCustom");
        nombreUsuario = getIntent().getStringExtra("nombreUsuarioDesdeCustom");
        mAuth =	FirebaseAuth.getInstance();
        user	=	mAuth.getCurrentUser();
        database=	FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();


        ivPerfilAmigo = (ImageView)findViewById(R.id.PerfilFoto);
        ivPortadaAmigo = (ImageView)findViewById(R.id.PerfilCoverImage);
        tvNombreAmigo = (TextView)findViewById(R.id.PerfilNombre);
        tvDescripcionAmigo = (TextView)findViewById(R.id.PerfilBio);
        tvEdadAmigo = (TextView)findViewById(R.id.PerfilFecha);
        tvCorreoAmigo = (TextView)findViewById(R.id.PerfilCorreo);

        ibEliminar = (ImageButton) findViewById(R.id.ibtnDelF);
        ibAgregar = (ImageButton)findViewById(R.id.ibtnAddF);
        ibMsj = (ImageButton)findViewById(R.id.ibtnMsg);

        loadDatosAmigo();

        ibAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String,String>map = new HashMap<>();
                map.put(idUsuarioPerfil,"");
                if(!idUsuarioPerfil.equals(user.getUid())){

                    if(actual!=null){
                        map = actual.getAmigos();
                        map.put(idUsuarioPerfil,"");
                        myRef=database.getReference(PATH_USERS+user.getUid()+"/amigos/");
                        myRef.setValue(map);
                        Toast.makeText(getBaseContext(),"Amigo agregado",
                                Toast.LENGTH_LONG).show();
                        actualizarBotones();
                    }

                }


            }
        });

        ibEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String,String>map = new HashMap<>();
                map.put(idUsuarioPerfil,"");
                if(!idUsuarioPerfil.equals(user.getUid())){

                    if(actual!=null){
                        map = actual.getAmigos();
                        map.remove(idUsuarioPerfil);
                        myRef=database.getReference(PATH_USERS+user.getUid()+"/amigos/");
                        myRef.setValue(map);
                        Toast.makeText(getBaseContext(),"Amigo eliminado",
                                Toast.LENGTH_LONG).show();
                        actualizarBotones();
                    }

                }

            }
        });

        ibMsj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getBaseContext(),EnviarMensaje.class);
                intent.putExtra("idOrigenMensaje",actual.getID());
                intent.putExtra("idDestinoMensaje",idUsuarioPerfil);
                intent.putExtra("nombreOrigen",actual.getNombre() +  " " + actual.getApellido());
                intent.putExtra("nombreDestino",nombreUsuario);


                startActivity(intent);
                /*
                Mensaje nuevoMensaje= new Mensaje();
                nuevoMensaje.setDestino(idUsuarioPerfil);
                nuevoMensaje.setOrigen(actual.getID());
                nuevoMensaje.setMensaje("Por ahora es asi");


                String	key	=	myRef.push().getKey();
                myRef=database.getReference(PATH_MENSAJES+key);
                myRef.setValue(nuevoMensaje);
                Toast.makeText(getBaseContext(),"Mensaje enviado",
                        Toast.LENGTH_LONG).show();*/
            }
        });

    }

    public  void actualizarBotones(){
        if(actual!=null){

            //Ocultar todos
            if (idUsuarioPerfil.equals(user.getUid())){
                ibAgregar.setVisibility(View.GONE);
                ibEliminar.setVisibility(View.GONE);
                ibMsj.setVisibility(View.GONE);
            }
            //Ocultar agregar
            else if(actual.getAmigos().containsKey(idUsuarioPerfil)){
                ibAgregar.setVisibility(View.GONE);
                ibEliminar.setVisibility(View.VISIBLE);
                ibMsj.setVisibility(View.VISIBLE);
            }
            //Ocultar eliminar y msj
            else if (!actual.getAmigos().containsKey(idUsuarioPerfil) ) {
                ibAgregar.setVisibility(View.VISIBLE);
                ibEliminar.setVisibility(View.GONE);
                ibMsj.setVisibility(View.GONE);
            }

        }

    }




    public	void	loadDatosAmigo()	{

        myRef =	database.getReference(PATH_USERS);
        myRef.addValueEventListener(new	ValueEventListener()	{
            @Override
            public	void	onDataChange(DataSnapshot dataSnapshot)	 {

                for	(DataSnapshot singleSnapshot :	dataSnapshot.getChildren())	{

                    Usuario u=new Usuario();
                    u=	singleSnapshot.getValue(Usuario.class);
                    u.setID(singleSnapshot.getKey());

                    if(u.getID().equals(user.getUid()))actual=u;

                    if(u.getID().equals(idUsuarioPerfil)){

                        tvNombreAmigo.setText(u.getNombre()+" "+u.getApellido());
                        tvDescripcionAmigo.setText(u.getDescripcion());
                        tvCorreoAmigo.setText(u.getCorreo());
                        tvEdadAmigo.setText(""+u.getEdad());


                        if(!u.getImagen().equals("")){
                            mStorageRef = FirebaseStorage.getInstance().getReference(PATH_IMAGENES
                                    +u.getID()+"/"+u.getImagen());
                            Glide.with(getBaseContext())
                                    .using(new FirebaseImageLoader())
                                    .load(mStorageRef)
                                    .into(ivPerfilAmigo);
                        }

                        if(!u.getPortada().equals("")){
                            mStorageRef = FirebaseStorage.getInstance().getReference(PATH_IMAGENES
                                    +u.getID()+"/"+u.getPortada());
                            Glide.with(getBaseContext())
                                    .using(new FirebaseImageLoader())
                                    .load(mStorageRef)
                                    .into(ivPortadaAmigo);
                        }


                    }

                    actualizarBotones();
                    Log.i("DATOS ALMACENADOS USER",u.toString());
                }
            }
            @Override
            public	void	onCancelled(DatabaseError databaseError)	{
                Log.w(TAG,	"error	en	la	consulta",	databaseError.toException());
            }
        });

    }

    @Override
    protected void onResume() {
        actualizarBotones();
        super.onResume();
    }
}
