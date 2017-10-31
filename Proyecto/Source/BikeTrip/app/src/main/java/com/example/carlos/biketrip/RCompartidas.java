package com.example.carlos.biketrip;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import entidades.AdapterItem;
import entidades.RutaEnt;

public class RCompartidas extends AppCompatActivity {

    private ListView list;
    private TextView txInfo;
    private ArrayList<RutaEnt> rutas;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

    public	static	final	String	PATH_RUTAS="rutasP/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rcompartidas);
        list = (ListView) findViewById(R.id.listRC);
        txInfo = (TextView)findViewById(R.id.infoRC);
        rutas = new ArrayList<RutaEnt>();
        database=	FirebaseDatabase.getInstance();
        mAuth =	FirebaseAuth.getInstance();
        Intent i = getIntent();
        int a = i.getIntExtra("Actividad",0);
        if(a == 0){
            leerDatos();
        }else{
            leerDatos2();
        }

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                RutaEnt sel = new RutaEnt();
                sel = rutas.get(pos);
                sel.setIdUsuario(mAuth.getCurrentUser().getUid());
                myRef =FirebaseDatabase.getInstance().getReferenceFromUrl("https://ejerciciostorage.firebaseio.com/");

                String	key	= myRef.child("rutasP").push().getKey();
                //myRef.push().getKey();
                myRef=database.getReference(PATH_RUTAS+key);
                myRef.setValue(sel);
                Toast.makeText(getBaseContext(),"Ruta Guardada Exitosamente",Toast.LENGTH_LONG).show();

                startActivity(new Intent(getBaseContext(),MenuPrincipal.class));
            }
        });
    }

    private void leerDatos2() {
        myRef =FirebaseDatabase.getInstance().getReferenceFromUrl("https://ejerciciostorage.firebaseio.com/");
        myRef.child("rutasP");
        myRef = database.getReference(PATH_RUTAS);

       final String id = getIntent().getStringExtra("Usuario");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    RutaEnt rut = singleSnapshot.getValue(RutaEnt.class);
                    Log.i("Ruta", "Encontró ruta:	" + rut.getNombre());

                    Date today = new Date();
                    today.setTime(today.getTime());
                    Date daR = rut.getTiempo();
                    Boolean b1 = today.before(daR);
                    Boolean b2 = today.equals(daR);
                    if(!rut.isPrivada() && (b1 || b2) &&id.equals(rut.getIdUsuario()))
                    {
                        rutas.add(rut);
                    }
                }
                Log.i("CANTIDAD:", "rutas:	" + rutas.size());
                txInfo.setText("----------Su rutas son:"+ rutas.size());

                AdapterItem adapter = new AdapterItem(getBaseContext(),R.layout.item_layout, rutas);
                list.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Consulta", "error	en	la	consulta", databaseError.toException());
            }
        });
    }

    public	void	leerDatos() {
        myRef =FirebaseDatabase.getInstance().getReferenceFromUrl("https://ejerciciostorage.firebaseio.com/");
        myRef.child("rutas");
        myRef = database.getReference(PATH_RUTAS);
        final String uId = mAuth.getCurrentUser().getUid();


        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    RutaEnt rut = singleSnapshot.getValue(RutaEnt.class);
                    Log.i("Ruta", "Encontró ruta:	" + rut.getNombre());

                    Date today = new Date();
                    today.setTime(today.getTime());
                    Date daR = rut.getTiempo();
                    Boolean b1 = today.before(daR);
                    Boolean b2 = today.equals(daR);
                    if(!rut.isPrivada() && (b1 || b2))
                    {
                        rutas.add(rut);
                    }
                }
                Log.i("CANTIDAD:", "rutas:	" + rutas.size());
                txInfo.setText("----------Su rutas son:"+ rutas.size());

                AdapterItem adapter = new AdapterItem(getBaseContext(),R.layout.item_layout, rutas);
                list.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Consulta", "error	en	la	consulta", databaseError.toException());
            }
        });
    }


}
