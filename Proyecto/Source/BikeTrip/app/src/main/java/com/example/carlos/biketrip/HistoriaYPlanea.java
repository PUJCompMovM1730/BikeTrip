package com.example.carlos.biketrip;

import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import entidades.AdapterItem;
import entidades.RutaEnt;

public class HistoriaYPlanea extends AppCompatActivity {


    TextView txHistorial;
    TextView txPlaneadas;
    ListView listHistorial;
    ListView listPlaneadas;
    Button btnVolver;
    ArrayList<RutaEnt> rutasH;
    ArrayList<RutaEnt> rutasP;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

    public	static	final	String	PATH_RUTAS="rutas/";

    private FirebaseDatabase database1;
    private DatabaseReference myRef1;


    public	static	final	String	PATH_RUTASP="rutasP/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_historia_yplanea);

        txHistorial = (TextView) findViewById(R.id.txtPlaneadas);
        txPlaneadas = (TextView) findViewById(R.id.txtHistorial);
        listHistorial = (ListView) findViewById(R.id.listHis);
        listPlaneadas = (ListView) findViewById(R.id.listPlan);
        btnVolver = (Button) findViewById(R.id.btnvolver);
        rutasH = new ArrayList<RutaEnt>();
        rutasP = new ArrayList<RutaEnt>();
        database=	FirebaseDatabase.getInstance();
        database1 = FirebaseDatabase.getInstance();
        mAuth =	FirebaseAuth.getInstance();
        leerDatos();
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                        new MapaRuta()).commit();*/
                        startActivity(new Intent(getBaseContext(),MenuPrincipal.class));

             /*  Fragment fragment= new MapaRuta();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, fragment); // fragment container id in first parameter is the  container(Main layout id) of Activity
                transaction.addToBackStack(null);  // this will manage backstack
                transaction.commit();*/
            }
        });
        listHistorial.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                RutaEnt sel = new RutaEnt();
                sel = rutasH.get(pos);
                double latI = sel.getLatInicio();
                double lonI = sel.getLonInicio();
                double latF = sel.getLatFinal();
                double lonF = sel.getLonFinal();
                // Recogemos el intent que ha llamado a esta actividad.
                Intent i = getIntent();
                // Le metemos el resultado que queremos mandar a la
                // actividad principal.
                i.putExtra("LatI", latI);
                i.putExtra("LonI", lonI);
                i.putExtra("LatF", latF);
                i.putExtra("LonF", lonF);
                i.putExtra("Ruta", sel);

                // Establecemos el resultado, y volvemos a la actividad
                // principal. La variable que introducimos en primer lugar
                // "RESULT_OK" es de la propia actividad, no tenemos que
                // declararla nosotros.
                setResult(RESULT_OK, i);

                // Finalizamos la Activity para volver a la anterior
                finish();

            }
        });
        listPlaneadas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                RutaEnt sel = new RutaEnt();
                sel = rutasP.get(pos);
                double latI = sel.getLatInicio();
                double lonI = sel.getLonInicio();
                double latF = sel.getLatFinal();
                double lonF = sel.getLonFinal();
                // Recogemos el intent que ha llamado a esta actividad.
                Intent i = getIntent();
                int de;
                de = i.getIntExtra("Actividad",0);
                // Le metemos el resultado que queremos mandar a la

                // actividad principal.
                if(de ==0)
                {
                    i.putExtra("LatI", latI);
                    i.putExtra("LonI", lonI);
                    i.putExtra("LatF", latF);
                    i.putExtra("LonF", lonF);

                    i.putExtra("Ruta", sel);
                    // Establecemos el resultado, y volvemos a la actividad
                    // principal. La variable que introducimos en primer lugar
                    // "RESULT_OK" es de la propia actividad, no tenemos que
                    // declararla nosotros.
                    setResult(RESULT_OK, i);

                    // Finalizamos la Activity para volver a la anterior
                    finish();

                }else{

                    Intent a = new Intent(getBaseContext(),PlanearRuta.class);
                    a.putExtra("Actividad",1);
                    a.putExtra("Ruta",sel);
                    startActivity(a);

                }


            }
        });
    }
    public	void	leerDatos() {
        myRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://ejerciciostorage.firebaseio.com/");
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

                    if (idR.equals(uId)) {
                        rutasH.add(rut);

                    }
                }
                Log.i("CANTIDAD:", "rutas:	" + rutasH.size());
                txHistorial.setText("----------Rutas anteriores:" + rutasH.size());

                AdapterItem adapter = new AdapterItem(getBaseContext(), R.layout.item_layout, rutasH);
                listHistorial.setAdapter(adapter);
                myRef1 = FirebaseDatabase.getInstance().getReferenceFromUrl("https://ejerciciostorage.firebaseio.com/");
                myRef1.child("rutasP");
                myRef1 = database1.getReference(PATH_RUTASP);
                myRef1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Toast.makeText(v.getContext(),"Voy a buscar",Toast.LENGTH_LONG).show();
                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                            RutaEnt rut = singleSnapshot.getValue(RutaEnt.class);
                            Log.i("Ruta", "Encontró rutaP:	" + rut.getNombre());
                            String idR = rut.getIdUsuario();

                            if (idR.equals(uId)) {
                                rutasP.add(rut);

                            }
                        }
                        Log.i("CANTIDAD:", "rutas:	" + rutasP.size());
                        txHistorial.setText("----------Rutas planeadas:" + rutasP.size());

                        AdapterItem adapter1 = new AdapterItem(getBaseContext(), R.layout.item_layout, rutasP);
                        listPlaneadas.setAdapter(adapter1);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("Consulta", "error	en	la	consulta", databaseError.toException());
                    };
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Consulta", "error	en	la	consulta", databaseError.toException());

            }
        });
    }

}
