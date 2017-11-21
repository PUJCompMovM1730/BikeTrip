package com.example.carlos.biketrip;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import entidades.FacebookShare;
import entidades.RutaEnt;

public class AgregarRecorridosDestacados extends FragmentActivity {

    public static final double lowerLeftLatitude = 4.475113;
    public static final double lowerLeftLongitude = -74.216308;
    public static final double upperRightLatitude = 4.815938;
    public static final double upperRigthLongitude = -73.997955;

    FirebaseDatabase database;
    DatabaseReference myRef;
    private FirebaseAuth mAuth;

    EditText etxtInicio;
    EditText etxtFinal;
    EditText etxtNombre;
    EditText etxtDesc;
    EditText etxtFechaRP;
    EditText etxtHoraRP;
    Button btnCrearReco;
    RutaEnt r = new RutaEnt();
    private String fechaPR;
    CallbackManager callbackManager;

    private DatePickerDialog fromDatePickerDialog;
    private final static int RESULTADOH = 0;

    public static final String PATH_RUTASP = "recorridosDest/";
    public final static double RADIUS_OF_EARTH_KM = 6371;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_recorridos_destacados);
        //Inicialización en	onCreate()
        database = FirebaseDatabase.getInstance();
        etxtInicio = (EditText) findViewById(R.id.txtInicioReco);
        btnCrearReco = (Button) findViewById(R.id.btnCrearReco);
        etxtDesc = (EditText) findViewById(R.id.txtDescripciónReco);
        etxtNombre = (EditText) findViewById(R.id.txtNombreReco);
        etxtFechaRP = (EditText) findViewById(R.id.fechaRPReco);
        etxtHoraRP = (EditText) findViewById(R.id.horaRPReco);
        mAuth = FirebaseAuth.getInstance();
        r.setIdUsuario(mAuth.getCurrentUser().getUid());

        callbackManager = CallbackManager.Factory.create();

        r.setPrivada(false);

            r.setPrivada(false);

            etxtInicio.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // If the event is a key-down event on the "enter" button
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                            (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        // Perform action on key press
                        String addressString = etxtInicio.getText().toString();
                        Geocoder mGeocoder = new Geocoder(getBaseContext());
                        if (!addressString.isEmpty()) {
                            try {
                                List<Address> addresses = mGeocoder.getFromLocationName(
                                        addressString, 2,
                                        lowerLeftLatitude,
                                        lowerLeftLongitude,
                                        upperRightLatitude,
                                        upperRigthLongitude);
                                if (addresses != null && !addresses.isEmpty()) {
                                    Address addressResult = addresses.get(0);
                                    LatLng position = new LatLng(addressResult.getLatitude(), addressResult.getLongitude());
                                    r.setLatInicio(position.latitude);
                                    r.setLonInicio(position.longitude);
                                    Toast.makeText(getBaseContext(), "Dirección de incio ingresada correctamente ", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getBaseContext(), "Dirección no encontrada", Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getBaseContext(), "La dirección esta vacía", Toast.LENGTH_SHORT).show();
                        }

                        return true;
                    }
                    return false;
                }
            });
            etxtFinal = (EditText) findViewById(R.id.txtFinalReco);
            etxtFinal.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // If the event is a key-down event on the "enter" button
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                            (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        // Perform action on key press
                        String addressString = etxtFinal.getText().toString();
                        Geocoder mGeocoder = new Geocoder(getBaseContext());
                        if (!addressString.isEmpty()) {
                            try {
                                List<Address> addresses = mGeocoder.getFromLocationName(
                                        addressString, 2,
                                        lowerLeftLatitude,
                                        lowerLeftLongitude,
                                        upperRightLatitude,
                                        upperRigthLongitude);
                                if (addresses != null && !addresses.isEmpty()) {
                                    Address addressResult = addresses.get(0);
                                    LatLng position = new LatLng(addressResult.getLatitude(), addressResult.getLongitude());
                                    Toast.makeText(getBaseContext(), "Dirección final ingresada correctamente ", Toast.LENGTH_LONG).show();
                                    r.setLatFinal(position.latitude);
                                    r.setLonFinal(position.longitude);
                                } else {
                                    Toast.makeText(getBaseContext(), "Dirección no encontrada", Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getBaseContext(), "La dirección esta vacía", Toast.LENGTH_SHORT).show();
                        }

                        return true;
                    }
                    return false;
                }
            });

            etxtFechaRP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(view == etxtFechaRP) {
                        fromDatePickerDialog.show();
                    }
                }
            });
            Calendar newCalendar = Calendar.getInstance();
            fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, monthOfYear, dayOfMonth);
                    fechaPR = new SimpleDateFormat("dd-MM-yyyy").format(newDate.getTime());
                    etxtFechaRP.setText(new SimpleDateFormat("dd-MM-yyyy").format(newDate.getTime()));
                }

            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


            btnCrearReco.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (etxtDesc.getText().toString().equals("") || etxtNombre.getText().toString().equals("") || etxtInicio.getText().toString().equals("") || etxtFinal.getText().toString().equals("")) {
                        Toast.makeText(getBaseContext(), "Por favor ingrese todos los datos", Toast.LENGTH_SHORT).show();
                    } else {
                        r.setDescripcion(etxtDesc.getText().toString());
                        r.setNombre(etxtNombre.getText().toString());
                        r.setInicio(etxtInicio.getText().toString());
                        r.setFin(etxtFinal.getText().toString());
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                        String hora = etxtHoraRP.getText().toString();
                        String fecha = etxtFechaRP.getText().toString();
                        String completo = fecha + " " + hora;
                        Log.i("COMPLETO", completo);
                        Date myDate = new Date();
                        try {
                            myDate = dateFormat.parse(completo);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        r.setTiempo(myDate);
                        r.setDistancia(distance(r.getLatInicio(), r.getLonInicio(), r.getLatFinal(), r.getLonFinal()));
                        if (r.getDistancia() != 0) {
                            myRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://ejerciciostorage.firebaseio.com/");
                            String key = myRef.push().getKey();
                            myRef = database.getReference(PATH_RUTASP + key);
                            myRef.setValue(r);
                            Toast.makeText(getBaseContext(), "La ruta ya se guardó", Toast.LENGTH_SHORT).show();

                            Log.i("Ruta-creada-bak-main",r.toString());

                            share();

                        } else {
                            Toast.makeText(getBaseContext(), "Ingrese direcciones válidas", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        //}


    }//FIN CREATE

    public void share(){

        final Activity activity = this;
        final Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.bike2);

        final CharSequence[] items = { "Compartir", "Cancelar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(
                AgregarRecorridosDestacados.this);
        builder.setTitle("Opciones para compartir");




        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Compartir")) {

                    FacebookShare facebookShare = new FacebookShare(activity,getBaseContext()
                            ,callbackManager, "Punto añadido satisfactoriamente");

                    facebookShare.sharePhoto(image);

                } else if (items[item].equals("Cancelar")) {
                    dialog.dismiss();
                    Toast.makeText(getBaseContext(),
                            "Punto añadido satisfactoriamente", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getBaseContext(), MenuPrincipal.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

            }
        });
        builder.show();
    }

    public double distance(double lat1, double long1, double lat2, double long2) {
        double latDistance = Math.toRadians(lat1 - lat2);
        double lngDistance = Math.toRadians(long1 - long2);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double result = RADIUS_OF_EARTH_KM * c;
        return Math.round(result * 100.0) / 100.0;
    }

    /*
    public void onClick(View view) {
        if(view == etxtFechaRP) {
            fromDatePickerDialog.show();
        }
    }*/

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case RESULTADOH: {
                RutaEnt a = new RutaEnt();
                a = (RutaEnt) data.getExtras().getSerializable("Ruta");
                etxtNombre.setText(a.getNombre());
                //txtInicio.setText("Actual");
                etxtDesc.setText(a.getDescripcion());
                long lnMilisegundos = a.getTiempo().getTime();
                java.sql.Date sqlDate = new java.sql.Date(lnMilisegundos);
                java.sql.Time sqlTime = new java.sql.Time(lnMilisegundos);

                etxtFechaRP.setText(sqlDate.toString());
                etxtHoraRP.setText(sqlTime.toString());
                r.setPrivada(false);
                return;
            }
        }

    }//Fin StartActivityResult

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }




}
