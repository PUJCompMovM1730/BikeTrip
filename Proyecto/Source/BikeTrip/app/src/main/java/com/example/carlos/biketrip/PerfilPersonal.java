package com.example.carlos.biketrip;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import entidades.Mensaje;
import entidades.Usuario;

public class PerfilPersonal extends AppCompatActivity {

    ArrayAdapter arrayAdapter;
    List<Mensaje> listDatosAMostrar;

    ListView listaMensajes;
    Button btnAceptarCambios;
    ImageButton ibEditar;
    ImageButton PerfilPersonalCover;
    ImageButton PerfilFoto;
    ImageButton ibtnEditarPerfil;

    EditText PerfilPersonalNombre;
    EditText PerfilPersonalBio;
    EditText PerfilPersonalEdad;
    EditText PerfilPersonalCorreo;

    FirebaseDatabase database;
    FirebaseUser user;
    Usuario actual;

    Uri uriPortada;
    Uri uriPerfil;


    private FirebaseAuth mAuth;
    private	FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference myRef;
    StorageReference mStorageRef;

    public	static	final	String	PATH_RUTAS="rutas/";
    public	static	final	String	PATH_USERS="users/";
    public	static	final	String	PATH_MENSAJES="mensajes/";
    public	static	final	String	PATH_IMAGENES="images/";
    public static final String TAG="TAG";
    public int CONSECUTIVO = 0;

    final static int IMAGE_PICKER_REQUEST = 3;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    final static int IMAGE_BUTTON_PERFIL = 101;
    final static int IMAGE_BUTTON_PORTADA = 102;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_personal);

        mAuth =	FirebaseAuth.getInstance();
        user	=	mAuth.getCurrentUser();
        database=	FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        listDatosAMostrar = new ArrayList<>();

        listaMensajes = (ListView)findViewById(R.id.ListaMensajes);
        btnAceptarCambios = (Button)findViewById(R.id.bntAceptarCambios);
        ibEditar = (ImageButton)findViewById(R.id.ibtnEditarPerfil);
        PerfilPersonalCover = (ImageButton)findViewById(R.id.PerfilPersonalCover);
        PerfilFoto = (ImageButton)findViewById(R.id.PerfilFoto);
        ibtnEditarPerfil = (ImageButton)findViewById(R.id.ibtnEditarPerfil);

        PerfilPersonalNombre = (EditText) findViewById(R.id.PerfilPersonalNombre);
        PerfilPersonalBio = (EditText)findViewById(R.id.PerfilPersonalBio);
        PerfilPersonalEdad = (EditText)findViewById(R.id.PerfilPersonalEdad);
        PerfilPersonalCorreo = (EditText)findViewById(R.id.PerfilPersonalCorreo);




        loadDatosActual();
        loadMensajesActual();
        validateDate();

        arrayAdapter = new ArrayAdapter<Mensaje>(this, android.R.layout.simple_list_item_1, listDatosAMostrar);
        listaMensajes.setAdapter(arrayAdapter);

        listaMensajes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getBaseContext(),EnviarMensaje.class);
                intent.putExtra("idOrigenMensaje",actual.getID());
                intent.putExtra("idDestinoMensaje",listDatosAMostrar.get(i).getOrigen());
                intent.putExtra("nombreOrigen",actual.getNombre());
                intent.putExtra("nombreDestino",listDatosAMostrar.get(i).getNombreOrigen());
                startActivity(intent);
            }
        });

        ibEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizarBotones();
                ++CONSECUTIVO;
            }
        });

        btnAceptarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    if (validateForm()) {
                        actual.setNombre(PerfilPersonalNombre.getText().toString().trim());
                        actual.setDescripcion(PerfilPersonalBio.getText().toString().trim());
                        actual.setCorreo(PerfilPersonalCorreo.getText().toString().trim());
                        actual.setEdad(PerfilPersonalEdad.getText().toString().trim());

                        myRef=database.getReference(PATH_USERS+actual.getID()+"/nombre");
                        myRef.setValue(PerfilPersonalNombre.getText().toString());

                        myRef=database.getReference(PATH_USERS+actual.getID()+"/descripcion");
                        myRef.setValue(PerfilPersonalBio.getText().toString());

                        myRef=database.getReference(PATH_USERS+actual.getID()+"/correo");
                        myRef.setValue(PerfilPersonalCorreo.getText().toString());

                        myRef=database.getReference(PATH_USERS+actual.getID()+"/edad");
                        myRef.setValue(PerfilPersonalEdad.getText().toString());


                        insertarPortadaEnStorageBD();
                        insertarPefilEnStorageBD();
                        opcionesCampos(false);
                        Toast.makeText(getBaseContext(),"Cambios realizados correctamente",
                                Toast.LENGTH_SHORT).show();


                    }



                }catch (Exception e){Log.i("EXP ACEPTARCAMBIOS",e.toString());}

            }
        });



        PerfilFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogoPerfil();


            }
        });

        PerfilPersonalCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogoCover();

            }
        });



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
                        PerfilFoto.setImageBitmap(selectedImage);
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
                    PerfilFoto.setImageBitmap(imageBitmap);
                }
                break;

            case IMAGE_PICKER_REQUEST+IMAGE_BUTTON_PORTADA:
                if(resultCode == RESULT_OK){
                    try {
                        uriPortada = data.getData();
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        PerfilPersonalCover.setImageBitmap(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case REQUEST_IMAGE_CAPTURE+IMAGE_BUTTON_PORTADA:
                if (resultCode == RESULT_OK) {
                    uriPortada = data.getData();
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    PerfilPersonalCover.setImageBitmap(imageBitmap);
                }
                break;
        }
    }


    public void mostrarDialogoPerfil(){
        AlertDialog.Builder myBuild = new AlertDialog.Builder(this);
        myBuild.setMessage("Seleccione opcion");

        myBuild.setPositiveButton("Camara",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Toast.makeText(PerfilPersonal.this, "Camara", Toast.LENGTH_SHORT).show();
                if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA)
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

                dialogInterface.cancel();
            }
        });

        myBuild.setNegativeButton("Galeria",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //si no hay permiso, dar permiso
                    permisoGaleria();
                }
                else{
                    Intent pickImage = new Intent(Intent.ACTION_PICK);
                    pickImage.setType("image/*");
                    startActivityForResult(pickImage, IMAGE_PICKER_REQUEST+IMAGE_BUTTON_PERFIL);
                }

                dialogInterface.cancel();
            }
        });

        AlertDialog dialog = myBuild.create();
        dialog.show();

    }


    public void mostrarDialogoCover(){
        AlertDialog.Builder myBuild = new AlertDialog.Builder(this);
        myBuild.setMessage("Seleccione opcion");



        myBuild.setPositiveButton("Camara",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Toast.makeText(PerfilPersonal.this, "Camara", Toast.LENGTH_SHORT).show();
                if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    //si no hay permiso, dar permiso
                    permisoCamara();
                }
                else{
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE+IMAGE_BUTTON_PORTADA);
                    }
                }

                dialogInterface.cancel();
            }
        });

        myBuild.setNegativeButton("Galeria",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //si no hay permiso, dar permiso
                    permisoGaleria();
                }
                else{
                    Intent pickImage = new Intent(Intent.ACTION_PICK);
                    pickImage.setType("image/*");
                    startActivityForResult(pickImage, IMAGE_PICKER_REQUEST+IMAGE_BUTTON_PORTADA);
                }

                dialogInterface.cancel();
            }
        });

        AlertDialog dialog = myBuild.create();
        dialog.show();

    }

    public boolean validateForm(){

        boolean valid	=	true;

        String	email	=	PerfilPersonalCorreo.getText().toString();
        if	(TextUtils.isEmpty(email))	{
            PerfilPersonalCorreo.setError("Required.");
            valid	=	false;
        }	else	{
            PerfilPersonalCorreo.setError(null);
        }

        String	name	=	PerfilPersonalNombre.getText().toString();
        if	(TextUtils.isEmpty(name))	 {
            PerfilPersonalNombre.setError("Required.");
            valid	=	false;
        }	else	{
            PerfilPersonalNombre.setError(null);
        }

        String	bio	=	PerfilPersonalBio.getText().toString();
        if	(TextUtils.isEmpty(bio))	 {
            PerfilPersonalBio.setError("Required.");
            valid	=	false;
        }	else	{
            PerfilPersonalBio.setError(null);
        }

        String fechaNac = PerfilPersonalEdad.getText().toString();

        if	(TextUtils.isEmpty(fechaNac))	 {
            PerfilPersonalEdad.setError("Required.");
            valid	=	false;
        }
        else	{


            String s = fechaNac.replace("/","");
            if (!s.matches("[0-9]+")) {
                valid = false;
                PerfilPersonalEdad.setError("Incorrect.");
            }
        }


        return	valid;
    }

    public void validateDate(){

        TextWatcher tw = new TextWatcher() {

            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8){
                        clean = clean + ddmmyyyy.substring(clean.length());
                    }else{
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day  = Integer.parseInt(clean.substring(0,2));
                        int mon  = Integer.parseInt(clean.substring(2,4));
                        int year = Integer.parseInt(clean.substring(4,8));

                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                        cal.set(Calendar.MONTH, mon-1);
                        year = (year<1900)?1900:(year>2100)?2100:year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                        clean = String.format("%02d%02d%02d",day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    PerfilPersonalEdad.setText(current);
                    PerfilPersonalEdad.setSelection(sel < current.length() ? sel : current.length());
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }



        };
        PerfilPersonalEdad.addTextChangedListener(tw);

    }


    public	void	loadDatosActual()	{

        myRef =	database.getReference(PATH_USERS);
        myRef.addListenerForSingleValueEvent(new	ValueEventListener()	{
            @Override
            public	void	onDataChange(DataSnapshot dataSnapshot)	 {

                for	(DataSnapshot singleSnapshot :	dataSnapshot.getChildren())	{

                    Usuario u=new Usuario();
                    u=	singleSnapshot.getValue(Usuario.class);
                    u.setID(singleSnapshot.getKey());

                    if(u.getID().equals(user.getUid())){
                        actual = u;


                        PerfilPersonalNombre.setText(u.getNombre());
                        PerfilPersonalBio.setText(u.getDescripcion());
                        PerfilPersonalCorreo.setText(u.getCorreo());
                        PerfilPersonalEdad.setText(""+u.getEdad());

                        if(u.getImagen()!=null ||!u.getImagen().equals("")){
                            mStorageRef = FirebaseStorage.getInstance().getReference(PATH_IMAGENES
                                    +u.getID()+"/"+u.getImagen());
                            Glide.with(getBaseContext())
                                    .using(new FirebaseImageLoader())
                                    .load(mStorageRef)
                                    .into(PerfilFoto);
                        }

                        if(u.getPortada()!=null ||!u.getPortada().equals("")){
                            mStorageRef = FirebaseStorage.getInstance().getReference(PATH_IMAGENES
                                    +u.getID()+"/"+u.getPortada());
                            Glide.with(getBaseContext())
                                    .using(new FirebaseImageLoader())
                                    .load(mStorageRef)
                                    .into(PerfilPersonalCover);
                        }

                    }
                }
            }
            @Override
            public	void	onCancelled(DatabaseError databaseError)	{
                Log.w(TAG,	"error	en	la	consulta",	databaseError.toException());
            }

        });

    }

    public	void	loadMensajesActual()	{

        myRef =	database.getReference(PATH_MENSAJES);
        myRef.addValueEventListener(new	ValueEventListener()	{
            @Override
            public	void	onDataChange(DataSnapshot dataSnapshot)	 {

                arrayAdapter.clear();
                for	(DataSnapshot singleSnapshot :	dataSnapshot.getChildren())	{
                    Mensaje m=new Mensaje();
                    m =	singleSnapshot.getValue(Mensaje.class);

                    if(m.getDestino().equals(user.getUid())){
                        listDatosAMostrar.add(m);
                        //listDatosAMostrar.add(m.getMensaje()+" - " +m.getNombreOrigen());
                    }
                }
                /*listDatosAMostrar.sort(new Comparator<Mensaje>() {
                    @Override
                    public int compare(Mensaje mensaje, Mensaje t1) {
                        return mensaje.getFechaMensaje().compareTo(t1.getFechaMensaje());
                    }
                });*/
                arrayAdapter.notifyDataSetChanged();

            }
            @Override
            public	void	onCancelled(DatabaseError databaseError)	{
                Log.w(TAG,	"error	en	la	consulta",	databaseError.toException());
            }

        });

    }



    public void insertarPortadaEnStorageBD(){
        /*mStorageRef = FirebaseStorage.getInstance().getReference(PATH_IMAGENES+actual.getPortada());
        mStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("DEL", PATH_IMAGENES+actual.getPortada());
            }
        });*/

        if(uriPortada!=null) {


            mStorageRef = FirebaseStorage.getInstance().getReference();
            StorageReference mRef = mStorageRef.child(PATH_IMAGENES
                    +user.getUid()+"/"+uriPortada.getLastPathSegment().trim());


            mRef.putFile(uriPortada)
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

            myRef=database.getReference(PATH_USERS+actual.getID()+"/portada");
            myRef.setValue(uriPortada.getLastPathSegment().trim());


        }
    }

    public void insertarPefilEnStorageBD(){
        /*mStorageRef = FirebaseStorage.getInstance().getReference(PATH_IMAGENES+actual.getPortada());
        mStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("DEL", PATH_IMAGENES+actual.getPortada());
            }
        });*/

        if(uriPerfil!=null) {
            mStorageRef = FirebaseStorage.getInstance().getReference();
            StorageReference mRef = mStorageRef.child(PATH_IMAGENES
                    +user.getUid()+"/"+uriPerfil.getLastPathSegment().trim());


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

            myRef=database.getReference(PATH_USERS+actual.getID()+"/imagen");
            myRef.setValue(uriPerfil.getLastPathSegment().trim());
        }
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


    void actualizarBotones(){

        if(CONSECUTIVO%2==0){
            PerfilPersonalNombre.setFocusable(true);
            PerfilPersonalNombre.setEnabled(true);

            PerfilPersonalBio.setFocusable(true);
            PerfilPersonalBio.setEnabled(true);

            PerfilPersonalEdad.setFocusable(true);
            PerfilPersonalEdad.setEnabled(true);

            PerfilPersonalCorreo.setFocusable(true);
            PerfilPersonalCorreo.setEnabled(true);

        }
        else{
            PerfilPersonalNombre.setFocusable(false);
            PerfilPersonalNombre.setEnabled(false);

            PerfilPersonalBio.setFocusable(false);
            PerfilPersonalBio.setEnabled(false);

            PerfilPersonalEdad.setFocusable(false);
            PerfilPersonalEdad.setEnabled(false);

            PerfilPersonalCorreo.setFocusable(false);
            PerfilPersonalCorreo.setEnabled(false);

        }
    }

    void opcionesCampos(boolean b){

        PerfilPersonalNombre.setFocusable(b);
        PerfilPersonalNombre.setEnabled(b);

        PerfilPersonalBio.setFocusable(b);
        PerfilPersonalBio.setEnabled(b);

        PerfilPersonalEdad.setFocusable(b);
        PerfilPersonalEdad.setEnabled(b);

        PerfilPersonalCorreo.setFocusable(b);
        PerfilPersonalCorreo.setEnabled(b);
    }
}
