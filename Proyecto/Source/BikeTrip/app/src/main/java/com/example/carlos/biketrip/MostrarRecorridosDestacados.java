package com.example.carlos.biketrip;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import entidades.CustomAdapterRD;
import entidades.RutaEnt;

public class MostrarRecorridosDestacados extends Fragment {

    View v;
    ListView LVRecorridos;

    private FirebaseAuth mAuth;
    FirebaseUser user;

    FirebaseDatabase database;
    DatabaseReference myRef;
    StorageReference mStorageRef;

    private ArrayList<RutaEnt> data = new ArrayList<>();
    ArrayAdapter adapter;

    public	static	final	String	PATH_RUTAS_DEST="recorridosDest/";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.activity_mostrar_recorridos_destacados, container, true);

        mAuth =	FirebaseAuth.getInstance();
        user	=	mAuth.getCurrentUser();
        database=	FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        LVRecorridos = v.findViewById(R.id.listRecorridosDestacados);


        adapter = new CustomAdapterRD(getActivity(), R.layout.layout_recorridosdestacados,
                data, mStorageRef,database,
                myRef);
        LVRecorridos.setAdapter(adapter);
        loadRutasDestacadas();



        return super.onCreateView(inflater, container, savedInstanceState);
    }


    public void loadRutasDestacadas(){

        myRef =	database.getReference(PATH_RUTAS_DEST);
        myRef.addValueEventListener(new	ValueEventListener()	{
            @Override
            public	void	onDataChange(DataSnapshot dataSnapshot)	 {

                adapter.clear();

                for	(DataSnapshot singleSnapshot :	dataSnapshot.getChildren())	{
                    RutaEnt r=new RutaEnt();
                    r =	singleSnapshot.getValue(RutaEnt.class);
                    data.add(r);

                    Log.i("DATOS_RUTAS_DEST_", r.getNombre() + " "+r.getDescripcion()+" "
                    +r.getDistancia()+" ("+r.getLatInicio()+","+r.getLonInicio()+")"
                            +" ("+r.getLatFinal()+","+r.getLonFinal()+")");
                }

                adapter.notifyDataSetChanged();
            }
            @Override
            public	void	onCancelled(DatabaseError databaseError)	{
                Log.w("ERROR_RECORRIDO_DEST",
                        "error	en	la	consulta",	databaseError.toException());
            }
        });


    }
}
