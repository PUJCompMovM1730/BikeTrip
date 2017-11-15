package com.example.carlos.biketrip;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Date;

import entidades.Mensaje;

public class EnviarMensaje extends AppCompatActivity {

    TextView tvDestinoMensaje;
    EditText etMensaje;
    Button enviarMensaje;

    String origen;
    String destino;
    String nombreOrigen;
    String nombreDestino;



    FirebaseDatabase database;
    DatabaseReference myRef;
    public	static	final	String	PATH_MENSAJES="mensajes/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enviar_mensaje);

        tvDestinoMensaje = (TextView)findViewById(R.id.nombreUsuarioDestino);
        etMensaje = (EditText) findViewById(R.id.edMensajeAEnviar);
        enviarMensaje = (Button)findViewById(R.id.btnEnviarMensaje);


        origen = getIntent().getStringExtra("idOrigenMensaje");
        destino = getIntent().getStringExtra("idDestinoMensaje");
        nombreOrigen = getIntent().getStringExtra("nombreOrigen");
        nombreDestino = getIntent().getStringExtra("nombreDestino");
        database=	FirebaseDatabase.getInstance();

        tvDestinoMensaje.setText(nombreDestino);

        enviarMensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarMensaje();
            }
        });

        /*etMensaje.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i== EditorInfo.IME_ACTION_DONE){
                    enviarMensaje();
                    return true;
                }
                return false;
            }
        });*/

    }

    public void enviarMensaje(){
        Mensaje nuevoMensaje= new Mensaje();
        nuevoMensaje.setDestino(destino);
        nuevoMensaje.setOrigen(origen);
        nuevoMensaje.setNombreOrigen(nombreOrigen);
        nuevoMensaje.setFechaMensaje(new Date());
        nuevoMensaje.setMensaje(etMensaje.getText().toString());

        if(!nuevoMensaje.getMensaje().isEmpty()){
            myRef=database.getReference();
            String	key	=	myRef.push().getKey();
            myRef=database.getReference(PATH_MENSAJES+key);
            myRef.setValue(nuevoMensaje);
            Toast.makeText(getBaseContext(),"Mensaje enviado",
                    Toast.LENGTH_LONG).show();

            finish();
        }


    }
}
