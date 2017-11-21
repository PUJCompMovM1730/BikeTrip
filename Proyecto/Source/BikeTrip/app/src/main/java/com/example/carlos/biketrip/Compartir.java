package com.example.carlos.biketrip;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.facebook.share.internal.ShareConstants;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import entidades.RutaEnt;
import entidades.Usuario;

public class Compartir extends AppCompatActivity {

    public	static	final	String	PATH_IMAGENES="images/";
    StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private Usuario u;
    private EditText edtCompartir;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compartir);
        ShareButton shareButton = (ShareButton)findViewById(R.id.btnCompartir);
        Intent i = getIntent();
        RutaEnt a = new RutaEnt();
        a = (RutaEnt) i.getExtras().getSerializable("Ruta");
        ShareContent content =new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://www.google.es/maps"))
                .setQuote("dsfdsf")
                .build();
        shareButton.setShareContent(content);

    }
}
