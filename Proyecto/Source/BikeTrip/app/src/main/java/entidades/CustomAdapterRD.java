package entidades;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.carlos.biketrip.Maps;
import com.example.carlos.biketrip.Perfil;
import com.example.carlos.biketrip.R;
import com.example.carlos.biketrip.RCompartidas;
import com.example.carlos.biketrip.Ruta;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.maps.model.LatLng;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Camila on 11/20/2017.
 */

public class CustomAdapterRD extends ArrayAdapter<RutaEnt> {

    Context context;
    int layoutResourceId;
    ArrayList<RutaEnt> data = null;
    FirebaseDatabase database;
    DatabaseReference myRef;
    StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    FirebaseUser user;

    private RutaEnt item;
    public static final String PATH_RUTASRD = "recorridosDest/";

    public CustomAdapterRD(Context context, int resource, List<RutaEnt> objects,
                         StorageReference mStorageRef, FirebaseDatabase d,
                         DatabaseReference dr) {
        super(context, resource, objects);
        this.layoutResourceId = resource;
        this.context = context;
        this.data = (ArrayList) objects;
        this.mStorageRef = mStorageRef;
        this.database = d;
        this.myRef=dr;
        mAuth =	FirebaseAuth.getInstance();
        user	=	mAuth.getCurrentUser();
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        CustomAdapterRD.InfoRuta infoRD = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();


            row = inflater.inflate(layoutResourceId, parent, false);

            infoRD = new CustomAdapterRD.InfoRuta();
            infoRD.RDnom = row.findViewById(R.id.tvNombreRuta);
            infoRD.RDDistancia = row.findViewById(R.id.tvDistancia);
            infoRD.RDInicio = row.findViewById(R.id.tvInicio);
            infoRD.RDFin = row.findViewById(R.id.tvFin);
            infoRD.RDRuta = row.findViewById(R.id.ibtnMapaRD);

            row.setTag(infoRD);

        }
        else
        {
            infoRD = (CustomAdapterRD.InfoRuta) row.getTag();
        }

        item= data.get(position);
        infoRD.RDnom.setText(item.getNombre());
        infoRD.RDDistancia.setText(String.valueOf(item.getDistancia())+" km");
        infoRD.RDInicio.setText(item.getInicio());
        infoRD.RDFin.setText(item.getFin());

        final int k = position;
        infoRD.RDRuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                item = data.get(position);
                Intent i = new Intent(getContext(),Maps.class);
                i.putExtra("Actividad",1);
                i.putExtra("LatF",item.getLatFinal());
                i.putExtra("LonF",item.getLonFinal());
                Log.i("CustomAdapter+loadRutas",k+ "    "+item.getNombre()+" "
                        +item.getLatFinal()+ " "+item.getLonFinal());
                getContext().startActivity(i);

            }
        });
        return row;
    }

    private class InfoRuta {
        public TextView RDnom;
        public TextView RDDistancia;
        public TextView RDInicio;
        public TextView RDFin;
        public ImageButton RDRuta;
    }


    /*
    public RutaEnt cargarRuta(final RutaEnt user){
        myRef =FirebaseDatabase.getInstance().getReferenceFromUrl("https://ejerciciostorage.firebaseio.com/");
        myRef.child("recorridosDest");
        final RutaEnt rutaMAx = new RutaEnt();
        Date d = new Date();
        d.setTime(0);
        rutaMAx.setTiempo(d);
        myRef = database.getReference(PATH_RUTASRD);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    RutaEnt rut = singleSnapshot.getValue(RutaEnt.class);
                    Log.i("Ruta", "Encontró ruta:	" + rut.getNombre()+" ");
                    Date today = new Date();
                    today.setTime(today.getTime());
                    Date daR = rut.getTiempo();
                    Boolean b1 = today.before(daR);
                    Boolean b2 = today.equals(daR);

                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Consulta", "error	en	la	consulta", databaseError.toException());
            }
        });
        return rutaMAx;
    };*/




    }
