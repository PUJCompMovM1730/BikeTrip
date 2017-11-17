package com.example.carlos.biketrip;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
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

import entidades.AdapterItem;
import entidades.RutaEnt;

/**
 * Created by sala-bd on 29/08/2017.
 */

public class Historial extends Fragment {

    private View v;
    private ListView list;
    private TextView txInfo;
    private List<RutaEnt> rutas;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;


    public	static	final	String	PATH_RUTAS="rutas/";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_historial, container, true);
        Toast.makeText(v.getContext(),"Entre a la actividad",Toast.LENGTH_LONG).show();
        list = (ListView) v.findViewById(R.id.list);
        txInfo = (TextView)v.findViewById(R.id.info);
        rutas = new ArrayList<RutaEnt>();
        database=	FirebaseDatabase.getInstance();
        mAuth =	FirebaseAuth.getInstance();
        leerDatos();

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public	void	leerDatos() {
        myRef =FirebaseDatabase.getInstance().getReferenceFromUrl("https://ejerciciostorage.firebaseio.com/");
        myRef.child("rutas");
        myRef = database.getReference(PATH_RUTAS);
        final String uId = mAuth.getCurrentUser().getUid();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Toast.makeText(v.getContext(),"Voy a buscar",Toast.LENGTH_LONG).show();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    RutaEnt rut = singleSnapshot.getValue(RutaEnt.class);
                    Log.i("Ruta", "Encontró ruta:	" + rut.getNombre());
                    String idR = rut.getIdUsuario();

                    if(idR.equals(uId))
                    {
                        rutas.add(rut);

                    }
                }
                Log.i("CANTIDAD:", "rutas:	" + rutas.size());
                txInfo.setText("----------Su rutas son:"+ rutas.size());

                AdapterItem adapter = new AdapterItem(v.getContext(),R.layout.item_layout, rutas);
                list.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Consulta", "error	en	la	consulta", databaseError.toException());
            }
        });
    }


}
