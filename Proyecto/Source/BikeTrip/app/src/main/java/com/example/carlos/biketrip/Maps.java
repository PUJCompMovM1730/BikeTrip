package com.example.carlos.biketrip;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;


import android.support.v4.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import entidades.*;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class Maps extends FragmentActivity implements OnMapReadyCallback {

    int cantidadActualizaciones;
    private Marker puntoActual;
    private Marker puntoFinal;
    private GoogleMap mMap;
    Polyline line;
    Context context;
    public	final	static	double	RADIUS_OF_EARTH_KM	 =	6371;

    private FusedLocationProviderClient mFusedLocationClient;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 4;
    public static final int REQUEST_CHECK_SETTINGS = 5;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private double lat = 0, lon=0;
    EditText txtDireccion;
    TextView txInfo;
    TextView txttiempo;
    TextView txtduracion;
    public static final double lowerLeftLatitude = 4.475113;
    public static final double lowerLeftLongitude= -74.216308;
    public static final double upperRightLatitude= 4.815938;
    public static final double upperRigthLongitude= -73.997955;
    private ImageButton ibtnRegis ;
    private ImageButton ibtnFin ;
    private FirebaseAuth mAuth;
    private boolean reiniciar;
    private final static int RESULTADOH = 0;

    private Button mRutas;
    private LatLng startLatLng;
    private LatLng endLatLng;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    public	static	final	String	PATH_RUTAS="rutas/";
    public	static	final	String	PATH_EVENTOS="eventos/";
    public	static	final	String	PATH_PUNTOS="puntos/";
    public	static	final	String	PATH_PUNTOS_EMPRESAS="puntosEmpresa/";
    public	static	final	String	PATH_IMAGENES="images/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        //Inicialización en	onCreate()
        mAuth =	FirebaseAuth.getInstance();
       // endLatLng=null;
        cantidadActualizaciones = 0;
        puntoActual =null;
        puntoFinal = null;
        reiniciar=true;
        database=	FirebaseDatabase.getInstance();
        txtduracion = (TextView) findViewById(R.id.duracionREC);
        txttiempo = (TextView) findViewById(R.id.tiempoREC);
        mRutas = (Button) findViewById(R.id.btnMasrutas);
        mRutas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reiniciar=true;
                startActivityForResult(new Intent(getBaseContext(), HistoriaYPlanea.class),RESULTADOH);
            }
        });
        if(getIntent().getIntExtra("Actividad",0)==1) {
            Intent data = getIntent();
            final double latF = data.getExtras().getDouble("LatF");
            final double lonF = data.getExtras().getDouble("LonF");
            obtenerLocSubs();
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Location location = locationResult.getLastLocation();
                    Log.i("LOCATION", "Location	update	in	the	callback:	" + location);
                    if (location != null) {
                        // mMap.clear();
                        lat = location.getLatitude();
                        lon = location.getLongitude();
                        startLatLng = new LatLng(lat, lon);
                        endLatLng = new LatLng(latF, lonF);


                        if(puntoFinal!=null) puntoFinal.remove();

                        puntoFinal = mMap.addMarker(new MarkerOptions().position(endLatLng).
                                icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                        puntoFinal.setVisible(true);

                        /*mMap.addMarker(new MarkerOptions().position(endLatLng).
                                icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));*/
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(endLatLng));
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                        double d = distance(lat, lon, latF, lonF);
                        txtduracion.setText(String.valueOf(d));
                        double tiemp = d / 30 * 60;
                        loadTodo();

                        if(puntoActual!=null) puntoActual.remove();
                        puntoActual = mMap.addMarker(new MarkerOptions().position(startLatLng).
                                icon(BitmapDescriptorFactory.
                                        fromResource(R.drawable.bike2)));
                        puntoActual.setVisible(true);



                        /*mMap.addMarker(new MarkerOptions().position(startLatLng).
                                icon(BitmapDescriptorFactory.
                                fromResource(R.drawable.bike2)));*/
                        txttiempo.setText(String.valueOf(tiemp));

                        String urlTopass = makeURL(lat, lon, latF,
                                lonF);
                        new connectAsyncTask(urlTopass).execute();
                    }
                }
            };
        }
        ibtnFin = (ImageButton)findViewById(R.id.parar);
        ibtnRegis = (ImageButton)findViewById(R.id.registrar);
        ibtnFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RutaEnt r1 = new RutaEnt();

                // myRef=database.getReference(PATH_RUTAS+user.getUid());

                if(endLatLng==null ||startLatLng==null){
                    Toast.makeText(getBaseContext(),"Por favor indique un punto de destino", Toast.LENGTH_SHORT).show();
                }else{
                    r1.setLatInicio(startLatLng.latitude);
                    r1.setLonInicio(startLatLng.longitude);
                    r1.setLatFinal(endLatLng.latitude);
                    r1.setLonFinal( endLatLng.longitude);
                    r1.setIdUsuario(mAuth.getCurrentUser().getUid());
                    Date d = new Date();
                    long lnMilisegundos = d.getTime();
                    d.setTime(d.getTime());
                    r1.setTiempo(d);
                    java.sql.Date sqlDate = new java.sql.Date(lnMilisegundos);
                    java.sql.Time sqlTime = new java.sql.Time(lnMilisegundos);

                    r1.setNombre(sqlDate.toString()+": "+sqlTime.toString());
                    r1.setDistancia(distance(startLatLng.latitude,startLatLng.longitude,
                            endLatLng.latitude,endLatLng.longitude));
                    r1.setDescripcion(sqlDate.toString()+": Distancia"+distance(startLatLng.latitude,startLatLng.longitude,
                            endLatLng.latitude,endLatLng.longitude));
                    // r1.setTiepo(new Date(););
                    r1.setInicio("Actual");
                    if(!txtDireccion.getText().toString().equals("")){
                        r1.setFin(txtDireccion.getText().toString());
                    }else{
                        r1.setFin("Ubicación seleccionada");
                    }

                    myRef =FirebaseDatabase.getInstance().getReferenceFromUrl("https://ejerciciostorage.firebaseio.com/");

                    String	key	= myRef.child("rutas").push().getKey();
                    //myRef.push().getKey();
                    myRef=database.getReference(PATH_RUTAS+key);
                    myRef.setValue(r1);
                    Toast.makeText(getBaseContext(),"Ruta Guardada Exitosamente",Toast.LENGTH_LONG).show();
                    endLatLng=null;
                    reiniciar=false;
                    mMap.clear();
                    //myRef=database.getReference("message");
                    //myRef.setValue("Hello	World!");
                }

            }
        });
        ibtnRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(startLatLng==null||endLatLng==null){
                    Toast.makeText(getBaseContext(),"Para guardar esta ruta para el futuro, ingrese los datos de la misma", Toast.LENGTH_LONG);
                }else{
                    Intent i = new Intent(getBaseContext(),PlanearRuta.class);
                    Bundle b = new Bundle();
                    b.putDouble("LatI",startLatLng.latitude);
                    b.putDouble("LonI",startLatLng.longitude);
                    b.putBoolean("Fin",false);
                    if(endLatLng!=null)
                    {
                        b.putDouble("LatF",endLatLng.latitude);
                        b.putDouble("LonF",endLatLng.longitude);
                    }
                    i.putExtra("Bundle",b);
                    reiniciar=true;
                    startActivity(i);
                }
            }
        });

        final Spinner spinner = (Spinner) findViewById(R.id.agregar);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(),R.array.lista_agregar,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id)
            {
                long idi = adapterView.getSelectedItemId();
                if(idi==1) //Selecciono agregar Evento
                {
                    Intent i = new Intent(getBaseContext(), Evento.class);
                    //Bundle b = new Bundle();
                    if(lat!=0){
                        startLatLng = new LatLng(lat,lon);
                        i.putExtra("Lat",lat);
                        i.putExtra("Lon",lon);
                    //   Toast.makeText(getBaseContext(),"Lat:"+lat+"Long"+lon, Toast.LENGTH_SHORT).show();
                        reiniciar=true;
                        startActivity(i);
                    }else{
                        Toast.makeText(getBaseContext(),"Por favor intente de nuevo", Toast.LENGTH_SHORT);
                    }

                }
                if(idi==2){//Selecciono agregar PUnto
                    Intent i = new Intent(getBaseContext(), Punto.class);
                    //Bundle b = new Bundle();
                    if(lat!=0){
                        startLatLng = new LatLng(lat,lon);
                        i.putExtra("Lat",lat);
                        i.putExtra("Lon",lon);
                        //   Toast.makeText(getBaseContext(),"Lat:"+lat+"Long"+lon, Toast.LENGTH_SHORT).show();
                        reiniciar=true;
                        startActivity(i);
                    }else{
                        Toast.makeText(getBaseContext(),"Por favor intente de nuevo", Toast.LENGTH_SHORT);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });

        //  mAuth =	FirebaseAuth.getInstance();
        mLocationRequest =	createLocationRequest();
        mFusedLocationClient =	LocationServices.getFusedLocationProviderClient(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        context = Maps.this;
        txtDireccion = (EditText)findViewById(R.id.texto);
        txtDireccion.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    String addressString = txtDireccion.getText().toString();

                    Geocoder mGeocoder = new Geocoder(getBaseContext());
                    if (!addressString.isEmpty()) {
                        try {
                            List<Address> addresses = mGeocoder.getFromLocationName(
                                    addressString, 2,
                                    lowerLeftLatitude,
                                    lowerLeftLongitude,
                                    upperRightLatitude,
                                    upperRigthLongitude);
                            if (addresses != null && !addresses.isEmpty()) {
                                Address addressResult = addresses.get(0);
                                LatLng position = new LatLng(addressResult.getLatitude(), addressResult.getLongitude());
                                if (mMap != null) {
                                    //Agregar Marcador al mapa
                                    mMap.addMarker(new MarkerOptions().position(position).icon(BitmapDescriptorFactory
                                            .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                                    double d = distance(lat,lon,position.latitude,position.longitude);
                                    txtduracion.setText(String.valueOf(d));
                                    txttiempo.setText(String.valueOf(d/30));
                                    startLatLng = new LatLng(lat,lon);
                                    endLatLng = new LatLng(position.latitude,position.longitude);
                                    String urlTopass = makeURL(lat,lon, position.latitude,
                                            position.longitude);
                                    new connectAsyncTask(urlTopass).execute();
                                }
                            } else {
                                Toast.makeText(Maps.this, "Dirección no encontrada", Toast.LENGTH_SHORT).show();}
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {Toast.makeText(Maps.this, "La dirección esta vacía", Toast.LENGTH_SHORT).show();}

                    return true;
                }
                return false;
            }
        });
        askPermission();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Funcionalidad Limitada!", Toast.LENGTH_LONG).show();
        } else {
            //obtenerLocalizacion();
            obtenerLocSubs();
            mLocationCallback =	new	LocationCallback()	 {
                @Override
                public	void	onLocationResult(LocationResult locationResult) {
                    Location location = locationResult.getLastLocation();
                    Log.i("LOCATION", "Location	update	in	the	callback:	" + location);
                    if (location != null) {
                        // mMap.clear();
                        lat = location.getLatitude();
                        lon = location.getLongitude();
                        // Agregar un marcador en bogotá
                        LatLng ge = new LatLng(lat,lon);
                        if(puntoActual!=null) puntoActual.remove();
                        puntoActual = mMap.addMarker(new MarkerOptions().position(ge).
                                icon(BitmapDescriptorFactory.
                                        fromResource(R.drawable.bike2)));
                        puntoActual.setVisible(true);
                        //mMap.addMarker(new MarkerOptions().position(ge).icon(BitmapDescriptorFactory.fromResource(R.drawable.bike2)));

                        if(cantidadActualizaciones==0){
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(ge));
                            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                        }

                        mMap.getUiSettings().setMyLocationButtonEnabled(true);
                        mMap.getUiSettings().setZoomGesturesEnabled(true);
                        mMap.getUiSettings().setZoomControlsEnabled(true);
                        loadTodo();
                        cantidadActualizaciones++;
                        //  Toast.makeText(getBaseContext(),"Lat: "+lat+", Long:"+lon, Toast.LENGTH_LONG).show();
                    }
                }
            };
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Date horaActual=new Date();
        if(horaActual.getHours()>18)
        {
            mMap.setMapStyle(MapStyleOptions
                    .loadRawResourceStyle(this, R.raw.style_json));

        }else{
            mMap.setMapStyle(MapStyleOptions
                    .loadRawResourceStyle(this, R.raw.style_jsond));


        }
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.addMarker(new MarkerOptions()
                        .anchor(0.0f, 1.0f)
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                endLatLng=latLng;
                double la = latLng.latitude;
                double lo = latLng.longitude;
                if(startLatLng==null){
                    startLatLng = new LatLng(lat,lon);
                }
                double d = distance(startLatLng.latitude,startLatLng.longitude,la,lo);
                txtduracion.setText(String.valueOf(d));
                double tiemp = d/30*60;
                txttiempo.setText(String.valueOf(tiemp));
                String urlTopass = makeURL(startLatLng.latitude,startLatLng.longitude,latLng.latitude,latLng.longitude);
                new connectAsyncTask(urlTopass).execute();
                mMap.addMarker(new MarkerOptions().position(startLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.bike2)));

            }
        });


        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(4.662039,-74.119431)));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {

                myRef = database.getReference(PATH_EVENTOS);
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                            EventoEnt myEvento =	singleSnapshot.getValue(EventoEnt.class);
                            Log.i("Evento: ", "Encontró evento:	");
                            Double eveLat = myEvento.getLat();
                            Double eveLon = myEvento.getLon();
                            if(eveLat==marker.getPosition().latitude&& eveLon==marker.getPosition().longitude)
                            {
                                Toast.makeText(getBaseContext(),"EVENTO: "+myEvento.getComentarios(),Toast.LENGTH_LONG).show();

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("Error: ", "error	en	la	consulta", databaseError.toException());
                    }
                });

                myRef = database.getReference(PATH_PUNTOS);
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                            PuntoEnt myPunto =	singleSnapshot.getValue(PuntoEnt.class);
                            Log.i("Evento: ", "Encontró evento:	");
                            Double eveLat = myPunto.getLat();
                            Double eveLon = myPunto.getLon();
                            if(eveLat==marker.getPosition().latitude&& eveLon==marker.getPosition().longitude)
                            {
                                Toast.makeText(getBaseContext(),"Punto: "+myPunto.getNombre(),Toast.LENGTH_LONG).show();
                                Intent i = new Intent(getBaseContext(),ComentarPuntos.class);
                                i.putExtra("Punto",myPunto);
                                startActivity(i);

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("Error: ", "error	en	la	consulta", databaseError.toException());
                    }
                });
                // Return false to indicate that we have not consumed the event and that we wish
                // for the default behavior to occur (which is for the camera to move such that the
                // marker is centered and for the marker's info window to open, if it has one).

                return false;
            }
        });

    }
    private void askPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an expanation to the user *asynchronously   
                Toast.makeText(this, "Se necesita el permiso para poder acceder a la locación!", Toast.LENGTH_LONG).show();
            }
            // Request the permission.
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Acceso a lOCALIZACION!", Toast.LENGTH_LONG).show();
                    obtenerLocSubs();
                    mLocationCallback =	new	LocationCallback()	 {
                        @Override
                        public	void	onLocationResult(LocationResult locationResult)	 {
                            Location	location	=	locationResult.getLastLocation();

                            Log.i("LOCATION",	"Location	update	in	the	callback:	"	+	location);
                            if	(location	 !=	null) {
                                lat = location.getLatitude();
                                lon = location.getLongitude();
                            }
                        }
                    };
                    obtenerLocSubs();
                } else {
                    Toast.makeText(this, "Funcionalidad Limitada!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    protected	LocationRequest createLocationRequest()	 {
        LocationRequest mLocationRequest =	new	LocationRequest();
        mLocationRequest.setInterval(10000);	 //tasa de	refresco en	milisegundos
        mLocationRequest.setFastestInterval(5000);	 //máxima tasa de	refresco
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return	mLocationRequest;
    }
    public void obtenerLocSubs() {
        LocationSettingsRequest.Builder builder	=	new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        SettingsClient client	 =	LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task	=	client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                startLocationUpdates();     //Todas las condiciones para	recibir localizaciones
            }
        });
        task.addOnFailureListener(this,	 new	OnFailureListener()	 {
            @Override
            public	void	onFailure(@NonNull Exception	 e)	{
                int statusCode =	((ApiException)	e).getStatusCode();
                switch	(statusCode)	{
                    case	CommonStatusCodes.RESOLUTION_REQUIRED:
//	Location	settings	are	not	satisfied,	but	this	can	be	fixed	by	showing	the	user	a	dialog.
                        try	{//	Show	the	dialog	by	calling	startResolutionForResult(),	and	check	the	result	in	onActivityResult().
                            ResolvableApiException resolvable	 =	(ResolvableApiException)	 e;
                            resolvable.startResolutionForResult(Maps.this, REQUEST_CHECK_SETTINGS);
                        }	catch	(IntentSender.SendIntentException sendEx)	{
//	Ignore	the	error.
                        }	break;
                    case	LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//	Location	settings	are	not	satisfied.	No	way	to	fix	the	settings	so	we	won't	show	the	dialog.
                        break;
                }
            }
        });
    }
    @Override
    protected	void	onActivityResult(int requestCode,	 int resultCode,	 Intent data)	 {
        switch	(requestCode)	 {
            case	REQUEST_CHECK_SETTINGS:	 {
                if	(resultCode ==	RESULT_OK)	 {
                    startLocationUpdates();	 	//Se	encendió la	localización!!!

                }	else	{
                    Toast.makeText(this,
                            "Sin	acceso a	localización,	hardware	deshabilitado!",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
            case RESULTADOH: {
                if(reiniciar){
                    long latI = data.getExtras().getLong("LatI");
                    long lonI = data.getExtras().getLong("LonI");
                    final double latF =  data.getExtras().getDouble("LatF");
                    final double lonF = data.getExtras().getDouble("LonF");
                    obtenerLocSubs();
                    mLocationCallback =	new	LocationCallback()	 {
                        @Override
                        public	void	onLocationResult(LocationResult locationResult) {
                            Location location = locationResult.getLastLocation();
                            Log.i("LOCATION", "Location	update	in	the	callback:	" + location);
                            if (location != null) {
                                // mMap.clear();
                                lat = location.getLatitude();
                                lon = location.getLongitude();
                                startLatLng = new LatLng(lat,lon);
                                endLatLng = new LatLng(latF,lonF);
                                mMap.addMarker(new MarkerOptions().position(endLatLng).icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(endLatLng));
                                mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                                double d = distance(lat,lon,latF,lonF);
                                txtduracion.setText(String.valueOf(d));
                                double tiemp = d/30*60;
                                loadTodo();
                                mMap.addMarker(new MarkerOptions().position(startLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.bike2)));
                                txttiempo.setText(String.valueOf(tiemp));

                                String urlTopass = makeURL(lat,lon, latF,
                                        lonF);
                                new connectAsyncTask(urlTopass).execute();
                            }
                        }
                    };
                }
                return;
            }
        }
    }
    private	void	startLocationUpdates()	 {
        if	(ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)	 ==
                PackageManager.PERMISSION_GRANTED)	 {				//Verificación de	permiso!!
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,	 mLocationCallback,
                    null);
        }

    }
    private class connectAsyncTask extends AsyncTask<Void, Void, String> {
        private ProgressDialog progressDialog;
        String url;

        connectAsyncTask(String urlPass) {
            url = urlPass;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Fetching route, Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            JSONParser jParser = new JSONParser();
            String json = jParser.getJSONFromUrl(url);
            return json;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.hide();
            if (result != null) {
                drawPath(result);
            }
        }
    }

    public String makeURL(double sourcelat, double sourcelog, double destlat,
                          double destlog) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString.append(Double.toString(sourcelog));
        urlString.append("&destination=");// to
        urlString.append(Double.toString(destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        return urlString.toString();
    }

    public class JSONParser {

        InputStream is = null;
        JSONObject jObj = null;
        String json = "";

        // constructor
        public JSONParser() {
        }

        public String getJSONFromUrl(String url) {
            // Making HTTP request
            try {
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + " ");
                }

                json = sb.toString();
                is.close();
            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
            }
            return json;

        }
    }
    public void drawPath(String result) {
        if (line != null) {
            mMap.clear();
        }
      /*  mMap.addMarker(new MarkerOptions().position(endLatLng).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.)));
        mMap.addMarker(new MarkerOptions().position(startLatLng).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.redpin_marker)));*/
        try {
            // Tranform the string into a json object
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes
                    .getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);

            for (int z = 0; z < list.size() - 1; z++) {
                LatLng src = list.get(z);
                LatLng dest = list.get(z + 1);
                line = mMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(src.latitude, src.longitude),
                                new LatLng(dest.latitude, dest.longitude))
                        .width(5).color(Color.BLUE).geodesic(true));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }
    public	double	distance(double	 lat1,	double	long1,	double	lat2,	double	long2)	{
        double	latDistance =	Math.toRadians(lat1	 - lat2);
        double	lngDistance =	Math.toRadians(long1	 - long2);
        double	a	=	Math.sin(latDistance /	2)	*	Math.sin(latDistance /	2)
                +	Math.cos(Math.toRadians(lat1))	 *	Math.cos(Math.toRadians(lat2))
                *	Math.sin(lngDistance /	2)	*	Math.sin(lngDistance /	2);
        double	c	=	2	*	Math.atan2(Math.sqrt(a),	 Math.sqrt(1	- a));
        double	result	=	RADIUS_OF_EARTH_KM	 *	c;
        return	Math.round(result*100.0)/100.0;
    }
    @Override
    public void onResume(){
        super.onResume();
        this.reiniciar=true;

    }
    public void loadTodo(){
        loadEventos();
        loadPuntos();
        loadPuntosEmpresas();
    }



    public void loadEventos() {
        myRef = database.getReference(PATH_EVENTOS);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    EventoEnt myEvento =	singleSnapshot.getValue(EventoEnt.class);
                    Log.i("Evento: ", "Encontró evento:	");
                    Double eveLat = myEvento.getLat();
                    Double eveLon = myEvento.getLon();
                    Double dis = distance(lat,lon, eveLat,eveLon);
                    LatLng eveL = new LatLng(eveLat,eveLon);
                    if(dis<=10)
                    {
                        mMap.addMarker(new MarkerOptions().position(eveL).icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                    }
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Error: ", "error	en	la	consulta", databaseError.toException());
            }
        });
    }
    public void loadPuntos() {
        myRef = database.getReference(PATH_PUNTOS);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    PuntoEnt myEvento =	singleSnapshot.getValue(PuntoEnt.class);
                    Log.i("Evento: ", "Encontró evento:	");
                    Double eveLat = myEvento.getLat();
                    Double eveLon = myEvento.getLon();
                    Double dis = distance(lat,lon, eveLat,eveLon);
                    LatLng eveL = new LatLng(eveLat,eveLon);
                    if(dis<=10)
                    {
                        mMap.addMarker(new MarkerOptions().position(eveL).icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));


                    }
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Error: ", "error	en	la	consulta", databaseError.toException());
            }
        });
    }

    public void loadPuntosEmpresas() {
        myRef = database.getReference(PATH_PUNTOS_EMPRESAS);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    PuntoEmpresa puntoEmpresa = singleSnapshot.getValue(PuntoEmpresa.class);

                    Log.i("PuntoEmpresa: ", "Encontró empresa:	"+puntoEmpresa.toString());
                    Date fechaActual = Calendar.getInstance().getTime();

                    if(puntoEmpresa.getHora_cierre().after(fechaActual)){
                        final LatLng coordenadaPunto = new LatLng(puntoEmpresa.getLatitud(),
                                puntoEmpresa.getLongitud());
                        final String informacion = puntoEmpresa.getNombre() + " - " + puntoEmpresa.getTelefono();

                        StorageReference mStorageRef;
                        mStorageRef = FirebaseStorage.getInstance().
                                getReference(PATH_IMAGENES +puntoEmpresa.getIdEmpresa()
                                        +"/"+puntoEmpresa.getFoto());



                        mStorageRef.getBytes(Long.MAX_VALUE)
                                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                // Use the bytes to display the image
                                Bitmap bmp = BitmapFactory.decodeByteArray(bytes,
                                        0, bytes.length);
                                Bitmap resize = getResizedBitmap(bmp,30,30);
                                mMap.addMarker(new MarkerOptions().position(coordenadaPunto)
                                        .icon(BitmapDescriptorFactory.fromBitmap(resize))
                                        .title(informacion));


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                                Log.i("ERROR",exception.toString());
                            }
                        });

                    }
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Error: ", "error	en	la	consulta", databaseError.toException());
            }
        });
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }


}
