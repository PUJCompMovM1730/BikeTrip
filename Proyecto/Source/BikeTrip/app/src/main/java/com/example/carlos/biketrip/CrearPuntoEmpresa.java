package com.example.carlos.biketrip;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import entidades.FacebookShare;
import entidades.PuntoEmpresa;


public class CrearPuntoEmpresa extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private  LatLng coordenadaPunto;
    Marker puntoMarkerEmpresa;
    Bitmap image;

    public static final double lowerLeftLatitude = 4.475113;
    public static final double lowerLeftLongitude= -74.216308;
    public static final double upperRightLatitude= 4.815938;
    public static final double upperRigthLongitude= -73.997955;

    CallbackManager callbackManager;
    FirebaseDatabase database;
    DatabaseReference myRef;
    StorageReference mStorageRef;


    public	static	final	String	PATH_PUNTOS="puntosEmpresa/";
    public	static	final	String	PATH_IMAGENES="images/";
    final static int IMAGE_PICKER_REQUEST = 3;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    ProgressBar progressBar;
    ImageView imagenPunto;
    EditText etNombre;
    EditText etTelefono;
    TextView tvDuracionPunto;
    Button btnCamaraPunto;
    Button btnGaleriaPunto;
    Button btnAgregarPunto;
    Spinner spinner;
    int posSpinner;
    Uri uriPunto;

    Date actual,futuro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_punto_empresa);

        coordenadaPunto = null;
        posSpinner = -1;

        database=	FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        progressBar = findViewById(R.id.progress_barCrearPunto);
        imagenPunto = findViewById(R.id.ivImagenPunto);
        etTelefono = findViewById(R.id.etTelefonoPuntoEmpresa);
        etNombre = findViewById(R.id.etNombrePuntoEmpresa);
        tvDuracionPunto = findViewById(R.id.tvDuracionMarcador);

        btnCamaraPunto = findViewById(R.id.btnCamaraPunto);
        btnGaleriaPunto = findViewById(R.id.btnGaleriaPunto);
        btnAgregarPunto= findViewById(R.id.btnConfirmarPunto);
        spinner = findViewById(R.id.spinnerDuracionPunto);


        btnGaleriaPunto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //si no hay permiso, dar permiso
                    permisoGaleria();
                }
                else{
                    Intent pickImage = new Intent(Intent.ACTION_PICK);
                    pickImage.setType("image/*");
                    startActivityForResult(pickImage, IMAGE_PICKER_REQUEST);
                }
            }
        });

        btnCamaraPunto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    //si no hay permiso, dar permiso
                    permisoCamara();
                }
                else{
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            }
        });

        btnAgregarPunto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertarEnBD();
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                posSpinner = i-1;

                Calendar cal = Calendar.getInstance();

                if(posSpinner==0)cal.add(Calendar.DAY_OF_YEAR, 1);
                else if(posSpinner==1)cal.add(Calendar.WEEK_OF_YEAR, 1);
                else if(posSpinner==2)cal.add(Calendar.MONTH, 1);

                actual = Calendar.getInstance().getTime();
                futuro = cal.getTime();

                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                String fecha = dateFormat.format(futuro);
                tvDuracionPunto.setText("El punto se vence: "+fecha);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        callbackManager = CallbackManager.Factory.create();
        //shareDialog = new ShareDialog(this);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapCrearPunto);
        mapFragment.getMapAsync(this);


    }



    @Override
    public void onMapReady(GoogleMap mMap) {

        googleMap = mMap;


        Date horaActual=new Date();
        if(horaActual.getHours()>18)
        {
            googleMap.setMapStyle(MapStyleOptions
                    .loadRawResourceStyle(getBaseContext(), R.raw.style_json));

        }else{
            googleMap.setMapStyle(MapStyleOptions
                    .loadRawResourceStyle(getBaseContext(), R.raw.style_jsond));


        }

        // For dropping a marker at a point on the Map

        // For zooming automatically to the location of the marker
        LatLng bogota = new LatLng(4.65, -74.05);
        //googleMap.addMarker(new MarkerOptions().position(bogota).title("Marcador en Bogotá"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(bogota));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(bogota).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override

            public void onMapLongClick(LatLng latLng) {

                if(puntoMarkerEmpresa!=null) puntoMarkerEmpresa.remove();

                puntoMarkerEmpresa = googleMap.addMarker(new MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                puntoMarkerEmpresa.setVisible(true);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));

                coordenadaPunto = latLng;
            }
        });

    }

    private void insertarEnBD(){
        PuntoEmpresa nuevoPunto;
        if(validateForm()){

            try {

                nuevoPunto = new PuntoEmpresa();

                nuevoPunto.setNombre(etNombre.getText().toString());
                nuevoPunto.setIdEmpresa(FirebaseAuth.getInstance().getCurrentUser().getUid());
                nuevoPunto.setTelefono(etTelefono.getText().toString());
                nuevoPunto.setLatitud(coordenadaPunto.latitude);
                nuevoPunto.setLongitud(coordenadaPunto.longitude);
                //nuevoPunto.setCoordenadas(coordenadaPunto);
                nuevoPunto.setFoto(uriPunto.getLastPathSegment().toString());
                nuevoPunto.setHora_apertura(actual);
                nuevoPunto.setHora_cierre(futuro);




                mStorageRef = FirebaseStorage.getInstance().getReference();
                StorageReference mRef = mStorageRef.child(PATH_IMAGENES
                        +nuevoPunto.getIdEmpresa()+"/"+uriPunto.getLastPathSegment().trim());

                mRef.putFile(uriPunto)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                // ...
                                Toast.makeText(getBaseContext(), "NO SE PUDO CARGAR IMAGEN",
                                        Toast.LENGTH_SHORT);
                            }
                        });

                myRef=database.getReference(PATH_PUNTOS);
                String	key	=	myRef.push().getKey();
                myRef=database.getReference(PATH_PUNTOS+key);
                myRef.setValue(nuevoPunto);
                Log.i("DATOSPUNTNUEVO",nuevoPunto.toString());


                progressBar.setVisibility(View.VISIBLE);
                btnAgregarPunto.setVisibility(View.GONE);
                btnCamaraPunto.setVisibility(View.GONE);
                btnGaleriaPunto.setVisibility(View.GONE);


                final Activity activity = this;
                final CharSequence[] items = { "Compartir", "Cancelar"};
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        CrearPuntoEmpresa.this);
                builder.setTitle("Opciones para compartir");


                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals("Compartir")) {

                            FacebookShare facebookShare = new FacebookShare(activity,getBaseContext()
                                    ,callbackManager, "Punto añadido satisfactoriamente");

                            facebookShare.sharePhoto(image);

                        } else if (items[item].equals("Cancelar")) {
                            dialog.dismiss();
                            Toast.makeText(getBaseContext(),
                                    "Punto añadido satisfactoriamente", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(getBaseContext(), MenuPrincipal.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }

                    }
                });
                builder.show();

            }catch (Exception e){Log.i("ErrorCrearPunto", e.toString());}
        }
    }




    private	boolean validateForm()	{
        boolean valid	=	true;

        String	name	=	etNombre.getText().toString();
        if	(TextUtils.isEmpty(name))	 {
            etNombre.setError("Required.");
            valid	=	false;
        }	else	{
            etNombre.setError(null);
        }

        String	tel	=	etTelefono.getText().toString();
        if	(TextUtils.isEmpty(tel))	 {
            etTelefono.setError("Required.");
            valid	=	false;
        }	else	{
            etTelefono.setError(null);
        }

        if(coordenadaPunto==null){
            valid = false;
            Toast.makeText(getBaseContext(),"No ha seleccinado un punto",
                    Toast.LENGTH_SHORT).show();
        }else{
            double lat, lon;
            lat = coordenadaPunto.latitude;
            lon = coordenadaPunto.longitude;
            if(lat<lowerLeftLatitude||lat>upperRightLatitude)valid = false;
            if(lon<lowerLeftLongitude||lon>upperRigthLongitude)valid = false;
            if(!valid)Toast.makeText(getBaseContext(),"Marcador fuera del rango",
                    Toast.LENGTH_SHORT).show();
        }

        if(posSpinner>3 || posSpinner<0){
            valid = false;
            Toast.makeText(getBaseContext(),"Opción inválida de duracion del punto",
                    Toast.LENGTH_SHORT).show();
        }
        if(uriPunto==null){
            Toast.makeText(this,"No se ha seleccionado una imágen",
                    Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return	valid;
    }

    public void permisoCamara(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if
                    (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                // Show an expanation to the user *asynchronously*
            }
            //Toast.makeText(getBaseContext(), "Entró permiso de usar camara", Toast.LENGTH_LONG).show();
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_IMAGE_CAPTURE);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant.
            //The callback method gets the result of the request.
        }

    }

    public void permisoGaleria(){
        if (ContextCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if
                    (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an expanation to the user *asynchronously*
            }
            //Toast.makeText(getBaseContext(), "Entró permiso de usar imagenes", Toast.LENGTH_LONG).show();
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    IMAGE_PICKER_REQUEST);


            // app-defined int constant.
            //The callback method gets the result of the request.
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case IMAGE_PICKER_REQUEST:
                Log.e("Msj", "Entró permiso de usar imagenes");
                if(resultCode == RESULT_OK){
                    try {
                        uriPunto = data.getData();
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = this.
                                getApplicationContext().getContentResolver().
                                openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        imagenPunto.setImageBitmap(selectedImage);
                        image = selectedImage;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case REQUEST_IMAGE_CAPTURE:
                Log.e("Msj", "Entró permiso de usar camara");
                if (resultCode == RESULT_OK) {
                    uriPunto = data.getData();
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    image = imageBitmap;
                    imagenPunto.setImageBitmap(imageBitmap);
                }
                break;

        }

    }


}