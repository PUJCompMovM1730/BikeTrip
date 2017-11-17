package com.example.carlos.biketrip;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import entidades.RutaEnt;
import entidades.Usuario;

public class CrearCuenta extends AppCompatActivity {

    //firebase	authentication
    private FirebaseAuth mAuth;
    private	FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser user;

    FirebaseDatabase database;
    DatabaseReference myRef;
    Usuario nuevoUsuario;
    int posSpinner;


    Spinner spinner;
    Button bcrear;
    Button bcancelar;

    EditText mUserName;
    EditText mUserLastName;
    EditText mUser;
    EditText mUserPassword;
    EditText mFechaNac;

    public static final String TAG="TAG";
    public	static	final	String	PATH_USERS="users/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_cuenta);


        posSpinner = -1;
        spinner = (Spinner)findViewById(R.id.spinnerTipoUsuario);
        bcrear = (Button)findViewById(R.id.btnConfirmCrear);
        bcancelar = (Button)findViewById(R.id.btnCancelarCrear);

        mUser= (EditText)findViewById(R.id.CCEmail);
        mUserPassword = (EditText)findViewById(R.id.CCPassword);
        mUserName= (EditText)findViewById(R.id.CCNombre);
        mUserLastName = (EditText)findViewById(R.id.CCApellido);


        mFechaNac = (EditText) findViewById(R.id.CCFDN);


        mAuth =	FirebaseAuth.getInstance();
        database=	FirebaseDatabase.getInstance();
        nuevoUsuario = new Usuario();



        mAuthListener =	new	FirebaseAuth.AuthStateListener()	{
            @Override
            public	void	onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)	{
                user	=	firebaseAuth.getCurrentUser();
                if	(user	!=	null)	{
                    //	User	is	signed	in
                    Log.d(TAG,	"onAuthStateChanged:signed_in:"	+	user.getUid());
                    Intent intent = new Intent(getBaseContext(), MenuPrincipal.class);
                    startActivity(intent);

                }	else	{
                    //	User	is	signed	out
                    Log.d(TAG,	"onAuthStateChanged:signed_out");
                }
            }
        };


        mUserPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i== EditorInfo.IME_ACTION_DONE){
                    signUp();
                    return true;
                }
                return false;
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


        validateDate();

        bcrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        bcancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


    private	boolean validateForm()	{
        boolean valid	=	true;

        if(posSpinner>1 || posSpinner<0)
            valid = false;

        String	email	=	mUser.getText().toString();
        if	(TextUtils.isEmpty(email))	{
            mUser.setError("Required.");
            valid	=	false;
        }	else	{
            mUser.setError(null);
        }
        String	password	=	mUserPassword.getText().toString();
        if	(TextUtils.isEmpty(password))	 {
            mUserPassword.setError("Required.");
            valid	=	false;
        }	else	{
            mUserPassword.setError(null);
        }

        String	name	=	mUserName.getText().toString();
        if	(TextUtils.isEmpty(name))	 {
            mUserName.setError("Required.");
            valid	=	false;
        }	else	{
            mUserName.setError(null);
        }

        String	lastName	=	mUserLastName.getText().toString();
        if	(TextUtils.isEmpty(lastName))	 {
            mUserLastName.setError("Required.");
            valid	=	false;
        }	else	{
            mUserLastName.setError(null);
        }

        String fechaNac = mFechaNac.getText().toString();

        if	(TextUtils.isEmpty(fechaNac))	 {
            mFechaNac.setError("Required.");
            valid	=	false;
        }
        else	{


            String s = fechaNac.replace("/","");
            if (!s.matches("[0-9]+")) {
                valid = false;
                mFechaNac.setError("Incorrect.");
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
                    mFechaNac.setText(current);
                    mFechaNac.setSelection(sel < current.length() ? sel : current.length());
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }



        };
        mFechaNac.addTextChangedListener(tw);

    }

    public void signUp(){
        if(validateForm()){

            String email, password;
            email = mUser.getText().toString().trim();
            password = mUserPassword.getText().toString().trim();

            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(CrearCuenta.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Log.d(TAG,	"createUserWithEmail:onComplete:"	+	task.isSuccessful());
                                user	=	mAuth.getCurrentUser();
                                if(user!=null){	//Update	user	Info
                                    UserProfileChangeRequest.Builder upcrb =	new	UserProfileChangeRequest.Builder();
                                    upcrb.setDisplayName(mUserName.getText().toString()+"	 "+mUserLastName.getText().toString());
                                    user.updateProfile(upcrb.build());

                                    insertarEnBaseDatos();
                                    Intent intent = new Intent(getBaseContext(), MenuPrincipal.class);
                                    startActivity(intent);

                                }
                            }
                            if	(!task.isSuccessful())	 {

                                try {
                                    throw task.getException();
                                }
                                catch(FirebaseAuthUserCollisionException e) {
                                    mUser.setError(getString(R.string.errorUsuarioExistente));
                                    mUser.requestFocus();
                                }
                                catch(FirebaseAuthWeakPasswordException e) {
                                    mUserPassword.setError(getString(R.string.errorContraseñaDebil));
                                    mUserPassword.requestFocus();
                                }
                                catch(FirebaseAuthInvalidCredentialsException e) {
                                    mUser.setError(getString(R.string.errorUsuarioInvalido));
                                    mUser.requestFocus();
                                } catch(Exception e) {
                                    Log.e(TAG, e.getMessage());
                                }

                                Log.e(TAG,	task.getException().getMessage());


                            }
                        }
                    });
        }
        else{
            Toast.makeText(getBaseContext(),"Forma no llenada correctamente.",
                    Toast.LENGTH_SHORT).show();
        }
    }


    public  void insertarEnBaseDatos(){
          if(user!=null){

            nuevoUsuario = new Usuario();
            nuevoUsuario.setApellido(mUserLastName.getText().toString());
            nuevoUsuario.setCorreo(mUser.getText().toString());
            nuevoUsuario.setNombre(mUserName.getText().toString());
            nuevoUsuario.setID(user.getUid());
            nuevoUsuario.setEdad(mFechaNac.getText().toString());
            nuevoUsuario.setEstatura(0);
            nuevoUsuario.setImagen("");
            nuevoUsuario.setPortada("");
            nuevoUsuario.setPeso(0);
            nuevoUsuario.setTipo(posSpinner);
            myRef=database.getReference(PATH_USERS+user.getUid());
            myRef.setValue(nuevoUsuario);
            /*
            String	key	=	myRef.push().getKey();
            myRef=database.getReference(PATH_USERS+key);
            myRef.setValue(nuevoUsuario);*/
        }

    }

    @Override
    protected	void	onStart()	 {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public	void	onStop()	 {
        super.onStop();
        if	(mAuthListener !=	null)	{
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
