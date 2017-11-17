package com.example.carlos.biketrip;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import entidades.PuntoEmpresa;

import static android.app.Activity.RESULT_OK;

public class CrearPuntoEmpresa extends Fragment{

    MapView mMapView;
    Address addressResult;
    private GoogleMap googleMap;
    Geocoder mGeocoder;

    public static final double lowerLeftLatitude = 4.475113;
    public static final double lowerLeftLongitude= -74.216308;
    public static final double upperRightLatitude= 4.815938;
    public static final double upperRigthLongitude= -73.997955;


    FirebaseDatabase database;
    DatabaseReference myRef;
    StorageReference mStorageRef;


    public	static	final	String	PATH_PUNTOS="puntosEmpresa/";
    public	static	final	String	PATH_IMAGENES="images/";
    final static int IMAGE_PICKER_REQUEST = 3;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    ImageView imagenPunto;
    EditText etDireccion;
    Button btnCamaraPunto;
    Button btnGaleriaPunto;
    Button btnAgregarPunto;
    Spinner spinner;
    int posSpinner;
    Uri uriPunto;

    View v;



    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_crear_punto_empresa, container, true);

        mGeocoder = new Geocoder(getContext());
        addressResult = null;
        mMapView = (MapView) v.findViewById(R.id.mapCrearPunto);
        mMapView.onCreate(savedInstanceState);


        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;


                // For dropping a marker at a point on the Map
                LatLng sydney = new LatLng(-34, 151);
                googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });


        database=	FirebaseDatabase.getInstance();

        mStorageRef = FirebaseStorage.getInstance().getReference();

        posSpinner = -1;
        imagenPunto = v.findViewById(R.id.ivImagenPunto);
        etDireccion = v.findViewById(R.id.etDireccionPunto);
        btnCamaraPunto = v.findViewById(R.id.btnCamaraPunto);
        btnGaleriaPunto = v.findViewById(R.id.btnGaleriaPunto);
        btnAgregarPunto= v.findViewById(R.id.btnConfirmarPunto);
        spinner = v. findViewById(R.id.spinnerDuracionPunto);


        btnGaleriaPunto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
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
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    //si no hay permiso, dar permiso
                    permisoCamara();
                }
                else{
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
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
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                posSpinner = i-1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        etDireccion.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i== EditorInfo.IME_ACTION_SEARCH)
                {
                    direccion();
                    return true;
                }
                return false;
            }
        });


        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void direccion(){

        //Cuando se realice la busqueda
        String addressString = etDireccion.getText().toString();

        if (!addressString.isEmpty()) {
            try {
                List<Address> addresses = mGeocoder.getFromLocationName(
                        addressString, 2,
                        lowerLeftLatitude,
                        lowerLeftLongitude,
                        upperRightLatitude,
                        upperRigthLongitude);

                if (addresses != null && !addresses.isEmpty()) {
                    addressResult = addresses.get(0);
                    if (googleMap != null) {
                        LatLng punto = new LatLng(addressResult.getLatitude(),
                                addressResult.getLongitude());
                        Marker puntoBuscado = googleMap.addMarker(new MarkerOptions().position(punto).
                                icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).
                                title(addressResult.getFeatureName())
                                .snippet(addressResult.toString()) //Texto de información
                                .alpha(0.5f)); //Transparencia);
                        puntoBuscado.setVisible(true);


                    }
                } else {Toast.makeText(getActivity(), "Dirección no encontrada", Toast.LENGTH_SHORT).show();}
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {Toast.makeText(getActivity(), "La dirección esta vacía", Toast.LENGTH_SHORT).show();}
    }



    private void insertarEnBD(){
        PuntoEmpresa nuevoPunto;
        if(validateForm()){

            try {
                int t=1;
                if(posSpinner==0)t=1;
                else if(posSpinner==1)t=7;
                else if(posSpinner==2)t=28;

                LatLng coordenada;
                long theFuture = System.currentTimeMillis() + (86400 * t * 1000);
                double lat,lon;

                lat = addressResult.getLatitude();
                lon = addressResult.getLongitude();
                Date actual,futuro;
                actual = Calendar.getInstance().getTime();
                futuro = new Date(theFuture);

                coordenada = new LatLng(lat,lon);

                nuevoPunto = new PuntoEmpresa();

                nuevoPunto.setNombre(addressResult.getFeatureName());
                nuevoPunto.setIdEmpresa(FirebaseAuth.getInstance().getCurrentUser().getUid());
                nuevoPunto.setTelefono(addressResult.getPhone());
                nuevoPunto.setCoordenadas(coordenada);
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
                                Toast.makeText(getContext(), "NO SE PUDO CARGAR IMAGEN",
                                        Toast.LENGTH_SHORT);
                            }
                        });

                myRef=database.getReference(PATH_PUNTOS);
                String	key	=	myRef.push().getKey();
                myRef=database.getReference(PATH_PUNTOS+key);
                myRef.setValue(nuevoPunto);

                Toast.makeText(getContext(),
                        "Punto añadido satisfactoriamente", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), MenuPrincipal.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


            }catch (Exception e){Log.i("ErrorCrearPunto", e.toString());}



        }
    }

    private	boolean validateForm()	{
        boolean valid	=	true;

        if(posSpinner>3 || posSpinner<0){
            valid = false;
            Toast.makeText(getContext(),"Opción inválida de duracion del punto",
                    Toast.LENGTH_SHORT).show();
        }

        if(addressResult==null){
            valid = false;
            Toast.makeText(getActivity(),"Dirección inválida",Toast.LENGTH_SHORT).show();
        }
        if(uriPunto==null){
            Toast.makeText(getContext(),"No se ha seleccionado una imágen",
                    Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return	valid;
    }

    public void permisoCamara(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if
                    (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.CAMERA)) {
                // Show an expanation to the user *asynchronously*
            }
            //Toast.makeText(getBaseContext(), "Entró permiso de usar camara", Toast.LENGTH_LONG).show();
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_IMAGE_CAPTURE);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant.
            //The callback method gets the result of the request.
        }

    }

    public void permisoGaleria(){
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if
                    (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an expanation to the user *asynchronously*
            }
            //Toast.makeText(getBaseContext(), "Entró permiso de usar imagenes", Toast.LENGTH_LONG).show();
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    IMAGE_PICKER_REQUEST);


            // app-defined int constant.
            //The callback method gets the result of the request.
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case IMAGE_PICKER_REQUEST:
                Log.e("Msj", "Entró permiso de usar imagenes");
                if(resultCode == RESULT_OK){
                    try {
                        uriPunto = data.getData();
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getActivity().
                                getApplicationContext().getContentResolver().
                                openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        imagenPunto.setImageBitmap(selectedImage);
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
                    imagenPunto.setImageBitmap(imageBitmap);
                }
                break;
        }
    }


}
