package entidades;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by carlos on 15/11/17.
 */

public class DownloadImage extends AsyncTask<String,Void,Bitmap>{
    Context con;
    Uri uri =null;
    Usuario usuario;
    String id;

    StorageReference mStorageRef;
    FirebaseDatabase database;
    DatabaseReference myRef;


    public	static	final	String	PATH_USERS="users/";
    public	static	final	String	PATH_IMAGENES="images/";

    public DownloadImage(Context c, Usuario u,String idU){
        con = c;
        usuario = u;
        id = idU;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        String urlDisp = strings[0];
        Bitmap bitmap = null;

        try {
            InputStream in = new java.net.URL(urlDisp).openStream();
            bitmap = BitmapFactory.decodeStream(in);
        }catch (Exception e){
            Log.i("DownloadImageException", e.toString());
        }


        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        uri = getImageUri(result);
        Log.i("ONPOSTEXECUTE",uri.getLastPathSegment().toString().trim());
        verificarEnBD();

    }



    public void insertarEnBaseDatos(){

        if(uri!=null){

            database=	FirebaseDatabase.getInstance();
            mStorageRef = FirebaseStorage.getInstance().getReference();

            StorageReference mRef = mStorageRef.child(PATH_IMAGENES+id+"/"
                    +uri.getLastPathSegment().trim());
            mRef.putFile(uri)
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
                            Log.i("ERROR_DOWN_IMAGEN_DE_FB",exception.toString());

                        }
                    });
            usuario.setImagen(uri.getLastPathSegment());
            usuario.setID(id);
            myRef=database.getReference(PATH_USERS+id);
            myRef.setValue(usuario);



        }

    }

    public	void verificarEnBD()	{

        final Map<String, String> m = new HashMap<>();


        final FirebaseUser user	=	FirebaseAuth.getInstance().getCurrentUser();

        database=	FirebaseDatabase.getInstance();
        myRef =	database.getReference(PATH_USERS);
        myRef.addListenerForSingleValueEvent(new	ValueEventListener()	{
            @Override
            public	void	onDataChange(DataSnapshot dataSnapshot)	 {

                for	(DataSnapshot singleSnapshot :	dataSnapshot.getChildren())	{

                    Usuario u=new Usuario();
                    u=	singleSnapshot.getValue(Usuario.class);
                    u.setID(singleSnapshot.getKey());

                    Log.i("ACTUALVSIT", u.getID() + " --- " + user.getUid());
                    if(u.getID().equals(user.getUid())){
                        m.put(user.getUid(),"");
                    }
                }
                if(!m.containsKey(user.getUid()))insertarEnBaseDatos();

            }
            @Override
            public	void	onCancelled(DatabaseError databaseError)	{
                Log.w("TAG",	"error	en	la	consulta",	databaseError.toException());
            }

        });


    }

    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(con.
                getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }





}
