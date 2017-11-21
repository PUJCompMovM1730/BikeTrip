package com.example.carlos.biketrip;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import entidades.ComentarioEnt;
import entidades.PuntoEnt;

public class Comentar extends AppCompatActivity {

    TextView txCom;
    EditText txComentario;
    RatingBar rtbar;
    Button btnComentar;
    Button btnCancelar;

    private FirebaseAuth mAuth;




    private FirebaseDatabase database1;
    private DatabaseReference myRef1;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    public	static	final	String	PATH_COMENTARIOS="comentariosU/";

    public	static	final	String	PATH_PUNTOS="puntos/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentar);
        txCom = (TextView) findViewById(R.id.textView);
        txComentario = (EditText) findViewById(R.id.edtComentarioCC);
        rtbar = (RatingBar) findViewById(R.id.ratingBarCC);
        btnComentar = (Button) findViewById(R.id.btnHacerComentario);
        btnCancelar = (Button) findViewById(R.id.btnCancelarCC);
        mAuth =	FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        database1 = FirebaseDatabase.getInstance();
        btnComentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comentario =  txComentario.getText().toString();
                float puntaje = rtbar.getRating();

                int can = getIntent().getIntExtra("CantidadU",0);
                float puntajeAc = getIntent().getFloatExtra("PuntajeA",0);

                float puntaF;
                if(can==0)
                {
                    puntaF=puntaje;
                }else{
                    puntaF = puntajeAc*(can/(can+1))+puntaje*(1/(can+1));

                }
                String idP = getIntent().getStringExtra("IDP");
                Date d = new Date();
                d.setTime(d.getTime());
                ComentarioEnt c = new ComentarioEnt();
                c.setCalificacion(puntaje);
                c.setIdUsuarioC(mAuth.getCurrentUser().getUid());
                c.setComentario(comentario);
                c.setfCalificacion(d);
                c.setIdPunto(idP);
                myRef= FirebaseDatabase.getInstance().getReferenceFromUrl("https://ejerciciostorage.firebaseio.com/");

                String key = myRef.push().getKey();
                c.setIdComentario(key);

                myRef = database.getReference(PATH_COMENTARIOS+ key);
                myRef.setValue(c);
                PuntoEnt p = (PuntoEnt) getIntent().getSerializableExtra("Punto");
                p.setPuntaje(puntaF);
                p.setCanUsuarios(p.getCanUsuarios()+1);
                myRef1= FirebaseDatabase.getInstance().getReferenceFromUrl("https://ejerciciostorage.firebaseio.com/");
                myRef1 = database1.getReference(PATH_PUNTOS+ p.getIdPunto());
                myRef1.setValue(p);

                finish();
                Toast.makeText(getBaseContext(),"Comentario Creado",Toast.LENGTH_LONG).show();


            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
