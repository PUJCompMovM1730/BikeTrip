package com.example.carlos.biketrip;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class Ruta extends Fragment {

    View v;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.activity_ruta, container, true);
        final Spinner spinner = (Spinner) v.findViewById(R.id.agregar);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),R.array.lista_agregar,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id)
            {
                long idi = adapterView.getSelectedItemId();
                if(idi==1) //Selecciono agregar Evento
                {
                    Intent intent = new Intent(getActivity(), Evento.class);
                    startActivity(intent);
                }
                if(idi==2){//Selecciono agregar PUnto
                    Intent intent = new Intent(getActivity(), Punto.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });

        return super.onCreateView(inflater, container, savedInstanceState);





    }
}
