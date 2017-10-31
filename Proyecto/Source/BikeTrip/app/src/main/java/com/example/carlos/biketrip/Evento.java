package com.example.carlos.biketrip;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Evento extends AppCompatActivity {

    Button cancelar,enviar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento);
        cancelar = (Button) findViewById(R.id.btncancelare);
        enviar = (Button) findViewById(R.id.btnenviare);
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getBaseContext(), Ruta.class);
                Toast.makeText(getBaseContext(),"Evento creado",Toast.LENGTH_LONG).show();
                finish();
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
}