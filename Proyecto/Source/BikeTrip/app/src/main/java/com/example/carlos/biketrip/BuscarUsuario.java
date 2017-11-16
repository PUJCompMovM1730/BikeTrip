package com.example.carlos.biketrip;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.Map;

import entidades.CustomAdapter;
import entidades.ListViewAdapter;
import entidades.Usuario;

public class BuscarUsuario extends AppCompatActivity {

    EditText EdBuscar;
    ListView LVUsuarios;

    FirebaseDatabase database;
    DatabaseReference myRef;
    StorageReference mStorageRef;

    private ArrayList<Usuario> data = new ArrayList<>();
    //ListViewAdapter adapter;
    ArrayAdapter adapter;

    public	static	final	String	PATH_USERS="users/";
    public	static	final	String	PATH_RUTAS="rutas/";
    public static final String TAG="TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_usuario);

        database=	FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        LVUsuarios = (ListView)findViewById(R.id.listUsuarios);
        EdBuscar = (EditText)findViewById(R.id.edFiltroUsuario);
        loadListU();
        adapter = new CustomAdapter(this, R.layout.layout, data, mStorageRef,database,
                myRef);
        //adapter = new ListViewAdapter(this,data,database,myRef,mStorageRef);
        LVUsuarios.setAdapter(adapter);



        EdBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                (BuscarUsuario.this).adapter.getFilter().filter(charSequence);

                //(BuscarUsuario.this).adapter.filter(charSequence.toString());


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    public	void loadListU()	{

        myRef =	database.getReference(PATH_USERS);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        myRef.addValueEventListener(new	ValueEventListener()	{
            @Override
            public	void	onDataChange(DataSnapshot dataSnapshot)	 {
                adapter.clear();
                for	(DataSnapshot singleSnapshot :	dataSnapshot.getChildren())	{
                    Usuario u=new Usuario();
                    u=	singleSnapshot.getValue(Usuario.class);
                    u.setID(singleSnapshot.getKey());

                    if(!user.getUid().equals(u.getID()))
                        data.add(u);
                    Log.i("BuscarAmigo", u.toString());
                }

                //adapter.notifyDataSetChanged();
            }
            @Override
            public	void	onCancelled(DatabaseError databaseError)	{
                Log.w(TAG,	"error	en	la	consulta",	databaseError.toException());
            }
        });

    }

}
