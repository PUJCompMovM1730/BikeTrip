package com.example.carlos.biketrip;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Punto extends AppCompatActivity {

    Button enviar, cancelar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_punto);
        cancelar = (Button) findViewById(R.id.btncancelarp);
        enviar = (Button) findViewById(R.id.btnenviarp);
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getBaseContext(), Ruta.class);
                Toast.makeText(getBaseContext(),"PuntoEmpresa creado",Toast.LENGTH_LONG).show();
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