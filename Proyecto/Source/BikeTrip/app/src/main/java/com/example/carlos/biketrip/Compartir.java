package com.example.carlos.biketrip;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.share.internal.ShareConstants;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import entidades.Usuario;

public class Compartir extends AppCompatActivity {

    public	static	final	String	PATH_IMAGENES="images/";
    StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private Usuario u;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compartir);
        ShareButton shareButton = (ShareButton)findViewById(R.id.btnCompartir);
        /*  mAuth =	FirebaseAuth.getInstance();
        u= mAuth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference(PATH_IMAGENES
                +u.getID()+"/"+u.getImagen());
        Bitmap image =
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();*/
        ShareContent content =new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://www.google.es/maps"))
                .build();
        shareButton.setShareContent(content);

    }
}
