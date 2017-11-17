package com.example.carlos.biketrip;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
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
import java.util.Calendar;
import java.util.Date;

import entidades.PuntoEmpresa;

import static android.app.Activity.RESULT_OK;

public class CrearPuntoEmpresa extends Fragment {

    FirebaseDatabase database;
    DatabaseReference myRef;
    StorageReference mStorageRef;


    public	static	final	String	PATH_PUNTOS="puntosEmpresa/";
    public	static	final	String	PATH_IMAGENES="images/";
    final static int IMAGE_PICKER_REQUEST = 3;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    ImageView imagenPunto;
    EditText etNombrePunto;
    EditText etTelefonoPunto;
    EditText etLongitudPunto;
    EditText etLatitudPunto;
    Button btnCamaraPunto;
    Button btnGaleriaPunto;
    Button btnAgregarPunto;
    Spinner spinner;
    int posSpinner;
    Uri uriPunto;

    View v;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_crear_punto_empresa, container, true);

        database=	FirebaseDatabase.getInstance();

        mStorageRef = FirebaseStorage.getInstance().getReference();

        posSpinner = -1;
        imagenPunto = v.findViewById(R.id.ivImagenPunto);
        etNombrePunto = v.findViewById(R.id.PuntoNombre);
        etTelefonoPunto = v.findViewById(R.id.PuntoTelefono);
        etLongitudPunto = v.findViewById(R.id.PuntoLongitud);
        etLatitudPunto = v.findViewById(R.id.PuntoLatitud);
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

        return super.onCreateView(inflater, container, savedInstanceState);
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

                Date actual,futuro;
                actual = Calendar.getInstance().getTime();
                futuro = new Date(theFuture);

                lat = Double.valueOf(etLatitudPunto.getText().toString());
                lon = Double.valueOf(etLongitudPunto.getText().toString());
                coordenada = new LatLng(lat,lon);

                nuevoPunto = new PuntoEmpresa();

                nuevoPunto.setNombre(etNombrePunto.getText().toString());
                nuevoPunto.setIdEmpresa(FirebaseAuth.getInstance().getCurrentUser().getUid());
                nuevoPunto.setTelefono(etTelefonoPunto.getText().toString());
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

        if(uriPunto==null){
            Toast.makeText(getContext(),"No se ha cargado la foto del punto",
                    Toast.LENGTH_SHORT).show();
            valid = false;
        }

        String	name	=	etNombrePunto.getText().toString();
        if	(TextUtils.isEmpty(name))	 {
            etNombrePunto.setError("Required.");
            valid	=	false;
        }	else	{
            etNombrePunto.setError(null);
        }

        String	phone	=	etTelefonoPunto.getText().toString();
        if	(TextUtils.isEmpty(phone))	 {
            etTelefonoPunto.setError("Required.");
            valid	=	false;
        }	else	{
            etTelefonoPunto.setError(null);
        }

        String	lat	=	etLatitudPunto.getText().toString();
        if	(TextUtils.isEmpty(lat))	 {
            etLatitudPunto.setError("Required.");
            valid	=	false;
        }	else	{
            etLatitudPunto.setError(null);
        }

        String	lon	=	etLongitudPunto.getText().toString();
        if	(TextUtils.isEmpty(lon))	 {
            etLongitudPunto.setError("Required.");
            valid	=	false;
        }	else	{
            etLongitudPunto.setError(null);
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
