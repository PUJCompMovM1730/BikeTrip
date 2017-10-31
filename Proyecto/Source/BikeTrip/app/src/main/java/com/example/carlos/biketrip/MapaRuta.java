package com.example.carlos.biketrip;


import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;




public class MapaRuta extends Fragment {

    View v;


    Button btnIniciar;
    Button btnPlanear;
    Button btnHistorial;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.activity_mapa_ruta, container, true);


        btnHistorial = (Button) v.findViewById(R.id.btnHistorial);
        btnIniciar = (Button) v.findViewById(R.id.btnIniciar);
        btnPlanear= (Button) v.findViewById(R.id.btnPlanear);
        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),Maps.class));
            }
        });
        btnHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              Fragment fragment= new Historial();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, fragment); // fragment container id in first parameter is the  container(Main layout id) of Activity
                transaction.addToBackStack(null);  // this will manage backstack
                transaction.commit();

              Intent i = new Intent(getActivity(),HistoriaYPlanea.class);
              i.putExtra("Actividad", 1);
                startActivity(i);

            }
        });
        btnPlanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getActivity(),PlanearRuta.class));

            }
        });
        return super.onCreateView(inflater, container, savedInstanceState);

    }}
