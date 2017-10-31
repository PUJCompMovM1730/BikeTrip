package com.example.carlos.biketrip;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

public class Notificaciones extends Fragment {

    View v;
    ImageButton ibVerNot;
    ImageButton ibElimnarNot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.activity_notificaciones, container, true);
        ibVerNot = v.findViewById(R.id.ibtnVerNot);
        ibElimnarNot = v.findViewById(R.id.ibtnEliminar);

        ibVerNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"Este boton mostrara la actividad de la notificacion",
                        Toast.LENGTH_LONG).show();
            }
        });

        ibElimnarNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"Se elimino la notificacion satisfactoriamente",
                        Toast.LENGTH_LONG).show();
            }
        });



        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
