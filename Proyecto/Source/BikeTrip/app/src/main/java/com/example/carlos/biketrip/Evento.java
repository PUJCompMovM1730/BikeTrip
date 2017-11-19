package com.example.carlos.biketrip;

import android.*;
import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import entidades.EventoEnt;

public class Evento extends AppCompatActivity {


    final static int IMAGE_PICKER_REQUEST = 3;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    final static int IMAGE_BUTTON_PERFIL = 101;

    Uri uriPerfil;
    StorageReference mStorageRef;
    Button cancelar,enviar;
    EditText editComentarios, editNombre;
    ImageButton btnFoto;
    DatabaseReference myRef;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    EventoEnt e;
    public	static	final	String	PATH_EVENTOS="eventos/";
    public	static	final	String	PATH_IMAGENES="images/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento);
        e = new EventoEnt();
        database=	FirebaseDatabase.getInstance();
        mAuth =	FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        btnFoto = (ImageButton)findViewById(R.id.btnFoto);
        cancelar = (Button) findViewById(R.id.btncancelare);
        enviar = (Button) findViewById(R.id.btnenviare);
        editComentarios=(EditText)findViewById(R.id.editComentarios);
        editNombre = (EditText)findViewById(R.id.editNombre);
        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    //si no hay permiso, dar permiso
                    permisoCamara();
                }
                else{
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE+IMAGE_BUTTON_PERFIL);
                    }
                }
            }
        });
        Intent i = getIntent();
        e.setLat(i.getExtras().getDouble("Lat"));
        e.setLon(i.getExtras().getDouble("Lon"));
        //Toast.makeText(getBaseContext(),"Lat aca :"+e.getLat()+"Y Lon: "+e.getLon(), Toast.LENGTH_SHORT).show();
        e.setIdCreador(mAuth.getCurrentUser().getUid());

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date d = new Date();
                d.setTime(d.getTime());
                e.setTiempo(d);
                e.setComentarios(editComentarios.getText().toString());
                e.setNombre(editNombre.getText().toString());
                myRef= FirebaseDatabase.getInstance().getReferenceFromUrl("https://ejerciciostorage.firebaseio.com/");
                String key = myRef.push().getKey();
                e.setIdEvento(key);
                myRef = database.getReference(PATH_EVENTOS+ key);
                myRef.setValue(e);
                finish();
                Toast.makeText(getBaseContext(),"Evento creado",Toast.LENGTH_LONG).show();
                insertarImagenlEnStorageBD();
                //startActivity(intent);
            }
        });
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void permisoCamara(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getBaseContext(),
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if
                    (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.CAMERA)) {
                // Show an expanation to the user *asynchronously*
            }
            //Toast.makeText(getBaseContext(), "Entró permiso de usar camara", Toast.LENGTH_LONG).show();
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    REQUEST_IMAGE_CAPTURE);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant.
            //The callback method gets the result of the request.
        }

    }


    public void insertarImagenlEnStorageBD(){

        if(uriPerfil!=null) {
            /*
            mStorageRef = FirebaseStorage.getInstance().getReference();
            StorageReference mRef = mStorageRef.child(PATH_IMAGENES+e.getNombre()/*uriPerfil.getLastPathSegment().trim());
*/
            mStorageRef = FirebaseStorage.getInstance().getReference();
            StorageReference mRef = mStorageRef.child(PATH_IMAGENES
                    +mAuth.getCurrentUser().getUid()+"/"+uriPerfil.getLastPathSegment().trim());

            mRef.putFile(uriPerfil)
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

            myRef=database.getReference(PATH_EVENTOS+e.getIdEvento()+"/imagen");
            myRef.setValue(uriPerfil.getLastPathSegment().trim());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case IMAGE_PICKER_REQUEST+IMAGE_BUTTON_PERFIL:
                if(resultCode == RESULT_OK){
                    try {
                        uriPerfil = data.getData();
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        btnFoto.setImageBitmap(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case REQUEST_IMAGE_CAPTURE+IMAGE_BUTTON_PERFIL:
                if (resultCode == RESULT_OK) {
                    uriPerfil = data.getData();
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    btnFoto.setImageBitmap(imageBitmap);
                }
                break;

        }
    }

}