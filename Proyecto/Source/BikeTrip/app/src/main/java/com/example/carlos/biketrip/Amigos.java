package com.example.carlos.biketrip;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
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
import java.util.List;
import java.util.Map;

import entidades.CustomAdapter;
import entidades.Usuario;

/**
 * Created by carlos on 27/08/17.
 */

public class Amigos extends Fragment{

    View v;
    Button btnBuscarAmigo;
    ListView LVamigos;


    private FirebaseAuth mAuth;
    private	FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser user;

    FirebaseDatabase database;
    DatabaseReference myRef;

    StorageReference mStorageRef;

    private ArrayList<Usuario> data = new ArrayList<>();
    ArrayAdapter adapter;

    public	static	final	String	PATH_USERS="users/";
    public	static	final	String	PATH_RUTAS="rutas/";
    public static final String TAG="TAG";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.activity_amigos, container, true);

        mAuth =	FirebaseAuth.getInstance();
        user	=	mAuth.getCurrentUser();
        database=	FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        LVamigos = v.findViewById(R.id.listAmigos);
        btnBuscarAmigo = v.findViewById(R.id.btnBuscarUsuario);



        adapter = new CustomAdapter(getActivity(), R.layout.layout, data, mStorageRef,database,
                myRef);
        LVamigos.setAdapter(adapter);
        loadUsers();

        btnBuscarAmigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Lanzar actividad de lista de todos los usuarios
                Intent intent =new Intent(getContext(),BuscarUsuario.class);
                startActivity(intent);
            }
        });
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    public	void	loadUsers()	{

        myRef =	database.getReference(PATH_USERS);
        myRef.addValueEventListener(new	ValueEventListener()	{
            @Override
            public	void	onDataChange(DataSnapshot dataSnapshot)	 {

                Map<String,String> idAmigos=new HashMap<>();
                adapter.clear();

                for	(DataSnapshot singleSnapshot :	dataSnapshot.getChildren())	{
                    Usuario u=new Usuario();
                    u=	singleSnapshot.getValue(Usuario.class);
                    u.setID(singleSnapshot.getKey());
                    if(user!=null){
                        if(u.getID().equals(user.getUid())){
                            idAmigos= u.getAmigos();
                            Log.i("---",u.toString());
                        }
                    }
                }

                for	(DataSnapshot singleSnapshot :	dataSnapshot.getChildren())	{
                    Usuario u=new Usuario();
                    u=	singleSnapshot.getValue(Usuario.class);
                    u.setID(singleSnapshot.getKey());

                    if(idAmigos.containsKey(u.getID())){
                        data.add(u);
                        Log.i("DATOS AMIGOS",u.toString());
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public	void	onCancelled(DatabaseError databaseError)	{
                Log.w(TAG,	"error	en	la	consulta",	databaseError.toException());
            }
        });

    }

}
