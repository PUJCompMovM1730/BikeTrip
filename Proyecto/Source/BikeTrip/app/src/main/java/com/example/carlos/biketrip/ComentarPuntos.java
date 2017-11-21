package com.example.carlos.biketrip;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import entidades.ComentarioEnt;
import entidades.PuntoEnt;
import entidades.Usuario;

public class ComentarPuntos extends AppCompatActivity {


    TextView nombreP;
    TextView descripcionP;
    ImageView imagen;
    RatingBar rb;
    ListView lisv;
    Button comentar;
    Button volver;
    List<ComentarioEnt> comentarios;
    PuntoEnt punto;
    ArrayAdapter<String> adaptador;
    List<Usuario> usuarios;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseDatabase database1;
    private DatabaseReference myRef1;
    private FirebaseAuth mAuth;


    public	static	final	String	PATH_COMENTARIOS="comentariosU/";
    public	static	final	String	PATH_USUARIOS="users/";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentar_puntos);
        nombreP = (TextView) findViewById(R.id.txNombrePuntoC);
        descripcionP= (TextView) findViewById(R.id.txComentarioPuntoC);
        imagen = (ImageView) findViewById(R.id.imaPuntoC);
        rb =(RatingBar) findViewById(R.id.ratingBar);
        lisv = (ListView) findViewById(R.id.listVComentarios);
        comentar = (Button) findViewById(R.id.btnComentarC);
        volver = (Button) findViewById(R.id.btnCancelarC);
        comentarios = new ArrayList<ComentarioEnt>();
        database = FirebaseDatabase.getInstance();
        database1 = FirebaseDatabase.getInstance();
        usuarios = new ArrayList<Usuario>();
        mAuth =	FirebaseAuth.getInstance();

        adaptador = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);

        punto = (PuntoEnt) getIntent().getSerializableExtra("Punto");

        Log.w("HOLA",punto.getIdPunto());
        myRef= FirebaseDatabase.getInstance().getReferenceFromUrl("https://ejerciciostorage.firebaseio.com/");

        myRef1= FirebaseDatabase.getInstance().getReferenceFromUrl("https://ejerciciostorage.firebaseio.com/");
        myRef = database.getReference(PATH_COMENTARIOS);
        nombreP.setText(punto.getNombre());
        descripcionP.setText(punto.getComentario());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    ComentarioEnt myComent =	singleSnapshot.getValue(ComentarioEnt.class);
                    Log.i("Punto: ", "Encontró punto:	");

                    if(myComent.getIdPunto().equalsIgnoreCase(punto.getIdPunto()))
                    {
                        ComentarioEnt p = new ComentarioEnt();
                        p.setIdComentario(myComent.getIdComentario());
                        p.setIdPunto(myComent.getIdPunto());
                        p.setfCalificacion(myComent.getfCalificacion());
                        p.setComentario(myComent.getComentario());
                        p.setCalificacion(myComent.getCalificacion());
                        p.setIdUsuarioC(myComent.getIdUsuarioC());
                        Log.i("Añadi",p.getIdPunto());

                        comentarios.add(p);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Error: ", "error	en	la	consulta", databaseError.toException());
            }
        });
        rb.setNumStars(5);
        rb.setIsIndicator(true);
        if(punto.getCanUsuarios()==0)
        {

            rb.setRating((float)0);

        }else{
            rb.setRating(punto.getPuntaje());
        }
        myRef = database.getReference(PATH_USUARIOS);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Usuario myUsuario =	singleSnapshot.getValue(Usuario.class);
                    Log.i("Usuario: ", "Encontró Usuario:	");
                    usuarios.add(myUsuario);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Error: ", "error	en	la	consulta", databaseError.toException());
            }
        });

        Log.w(".:","TAMAÑO2 "+comentarios.size());

        Log.w(".:","TAMAÑO "+usuarios.size());
        for(ComentarioEnt e: comentarios)
        {
            Log.w("ALLI","Econtre "+e.getIdUsuarioC());
            for(Usuario u: usuarios)
            {
                if(e.getIdUsuarioC().compareTo(u.getID())==0)
                {
                    Toast.makeText(getBaseContext(),"Baje USuario",Toast.LENGTH_SHORT).show();
                    String com = u.getNombre()+", dijo: "+e.getComentario();
                    adaptador.add(com);
                }
            }
        }
        lisv.setAdapter(adaptador);

        comentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getBaseContext(),Comentar.class);
                i.putExtra("IDP",punto.getIdPunto());
                i.putExtra("CantidadU",punto.getCanUsuarios());
                i.putExtra("PuntajeA",punto.getPuntaje());
                i.putExtra("Punto",punto);
                startActivity(i);
            }
        });

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(),Maps.class));
            }
        });


    }
}
