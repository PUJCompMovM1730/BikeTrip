package com.example.carlos.biketrip;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class PerfilPersonal extends AppCompatActivity {

    ImageButton ibEditar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_personal);

        ibEditar = (ImageButton)findViewById(R.id.ibtnEditarPerfil);

        ibEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(),"Editar el perfil personal",
                        Toast.LENGTH_LONG).show();

            }
        });

    }
}
