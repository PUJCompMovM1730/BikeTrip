package com.example.carlos.biketrip;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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

import entidades.RutaEnt;

public class PlanearRuta extends AppCompatActivity implements View.OnClickListener {

    public static final double lowerLeftLatitude = 4.475113;
    public static final double lowerLeftLongitude= -74.216308;
    public static final double upperRightLatitude= 4.815938;
    public static final double upperRigthLongitude= -73.997955;
    EditText txtInicio;
    EditText txtFinal;
    EditText txtNombre;
    EditText txtDesc;
    EditText txtFechaRP;
    EditText txtHoraRP;
    Button btnGuardar;
    Button btnRutasPu;
    RutaEnt r = new RutaEnt();
    FirebaseDatabase database;
    DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private String fechaPR;
    public	static	final	String	PATH_RUTASP="rutasP/";
    public	final	static	double	RADIUS_OF_EARTH_KM	 =	6371;

    public Spinner spinner;



    private final static int RESULTADOH = 0;
    private DatePickerDialog fromDatePickerDialog;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planear_ruta);
        //Inicialización en	onCreate()
        database=	FirebaseDatabase.getInstance();
        btnGuardar = (Button) findViewById(R.id.btnGuardar);
        txtDesc = (EditText)findViewById(R.id.txtDescripción);
        txtNombre = (EditText)findViewById(R.id.txtNombre);
        txtFechaRP = (EditText) findViewById(R.id.fechaRP);
        txtHoraRP = (EditText) findViewById(R.id.horaRP);
        mAuth =	FirebaseAuth.getInstance();
        r.setIdUsuario(mAuth.getCurrentUser().getUid());
        txtInicio = (EditText)findViewById(R.id.txtInicio);
        spinner = (Spinner) findViewById(R.id.spPublica);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getBaseContext(),R.array.lista_publica,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        r.setPrivada(true);
        Intent i = getIntent();
        int ed = i.getIntExtra("Actividad",0);
        if (ed==1)
        {
            RutaEnt a = new RutaEnt();
            a = (RutaEnt) i.getExtras().getSerializable("Ruta");
            txtNombre.setText(a.getNombre());
            txtDesc.setText(a.getDescripcion());
            long lnMilisegundos = a.getTiempo().getTime();
            java.sql.Date sqlDate = new java.sql.Date(lnMilisegundos);
            java.sql.Time sqlTime = new java.sql.Time(lnMilisegundos);

            txtFechaRP.setText(sqlDate.toString());
            txtHoraRP.setText(sqlTime.toString());
            if(a.isPrivada())
            {
                spinner.setSelection(2);
            }


            spinner.setSelection(1);

        }
        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                        long idi = adapterView.getSelectedItemId();
                        if (idi == 1)
                        {
                            r.setPrivada(false);
                        }else {
                            Toast.makeText(getBaseContext(), "Por favor seleccione si la ruta es pública o privada, de lo contrario por defecto será una ruta privada", Toast.LENGTH_LONG);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });
        txtInicio.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    String addressString = txtInicio.getText().toString();
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
                                r.setLatInicio( position.latitude);
                                r.setLonInicio( position.longitude);
                                Toast.makeText(getBaseContext(),"Dirección de incio ingresada correctamente ", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(PlanearRuta.this, "Dirección no encontrada", Toast.LENGTH_SHORT).show();}
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {Toast.makeText(PlanearRuta.this, "La dirección esta vacía", Toast.LENGTH_SHORT).show();}

                    return true;
                }
                return false;
            }
        });
        txtFinal = (EditText)findViewById(R.id.txtFinal);
        txtFinal.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    String addressString = txtFinal.getText().toString();
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
                                Toast.makeText(getBaseContext(),"Dirección final ingresada correctamente ", Toast.LENGTH_LONG).show();
                                r.setLatFinal( position.latitude);
                                r.setLonFinal(position.longitude);
                            } else {
                                Toast.makeText(PlanearRuta.this, "Dirección no encontrada", Toast.LENGTH_SHORT).show();}
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {Toast.makeText(PlanearRuta.this, "La dirección esta vacía", Toast.LENGTH_SHORT).show();}

                    return true;
                }
                return false;
            }
        });

        txtFechaRP.setOnClickListener(this);
        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                fechaPR = new SimpleDateFormat("dd-MM-yyyy").format(newDate.getTime());
                txtFechaRP.setText(new SimpleDateFormat("dd-MM-yyyy").format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if(txtDesc.getText().toString().equals("")||txtNombre.getText().toString().equals("")||txtInicio.getText().toString().equals("")||txtFinal.getText().toString().equals("")){
                    Toast.makeText(PlanearRuta.this, "Por favor ingrese todos los datos", Toast.LENGTH_SHORT).show();
                }else {
                    r.setDescripcion(txtDesc.getText().toString());
                    r.setNombre(txtNombre.getText().toString());
                    r.setInicio(txtInicio.getText().toString());
                    r.setFin(txtFinal.getText().toString());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                    String hora = txtHoraRP.getText().toString();
                    String fecha = txtFechaRP.getText().toString();
                    String completo = fecha +" "+hora;
                    Log.i("COMPLETO",completo);
                    Date myDate = new Date();
                    try {
                        myDate = dateFormat.parse(completo);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    r.setTiempo(myDate);
                    r.setDistancia(distance(r.getLatInicio(),r.getLonInicio(),r.getLatFinal(),r.getLonFinal()));
                    if(r.getDistancia()!=0) {
                        myRef= FirebaseDatabase.getInstance().getReferenceFromUrl("https://ejerciciostorage.firebaseio.com/");
                        String key = myRef.push().getKey();
                        myRef = database.getReference(PATH_RUTASP + key);
                        myRef.setValue(r);
                        Toast.makeText(PlanearRuta.this, "La ruta ya se guardó", Toast.LENGTH_SHORT).show();
                        startActivityForResult(new Intent(getBaseContext(), HistoriaYPlanea.class),RESULTADOH);
                    }else{
                        Toast.makeText(PlanearRuta.this, "Ingrese direcciones válidas", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        btnRutasPu = (Button) findViewById(R.id.btnRutasPu);
        btnRutasPu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(),RCompartidas.class));
            }
        });
    }
    public	double	distance(double	 lat1,	double	long1,	double	lat2,	double	long2)	{
        double	latDistance =	Math.toRadians(lat1	 - lat2);
        double	lngDistance =	Math.toRadians(long1	 - long2);
        double	a	=	Math.sin(latDistance /	2)	*	Math.sin(latDistance /	2)
                +	Math.cos(Math.toRadians(lat1))	 *	Math.cos(Math.toRadians(lat2))
                *	Math.sin(lngDistance /	2)	*	Math.sin(lngDistance /	2);
        double	c	=	2	*	Math.atan2(Math.sqrt(a),	 Math.sqrt(1	- a));
        double	result	=	RADIUS_OF_EARTH_KM	 *	c;
        return	Math.round(result*100.0)/100.0;
    }

    public void onClick(View view) {
        if(view == txtFechaRP) {
            fromDatePickerDialog.show();
        }
    }
    protected	void	onActivityResult(int requestCode,	 int resultCode,	 Intent data)	 {
        switch	(requestCode)	 {

            case RESULTADOH: {
                RutaEnt a = new RutaEnt();
                a = (RutaEnt) data.getExtras().getSerializable("Ruta");
                txtNombre.setText(a.getNombre());
                //txtInicio.setText("Actual");
                txtDesc.setText(a.getDescripcion());
                long lnMilisegundos = a.getTiempo().getTime();
                java.sql.Date sqlDate = new java.sql.Date(lnMilisegundos);
                java.sql.Time sqlTime = new java.sql.Time(lnMilisegundos);

                txtFechaRP.setText(sqlDate.toString());
                txtHoraRP.setText(sqlTime.toString());
                if(a.isPrivada())
                {
                    spinner.setSelection(2);
                }


                spinner.setSelection(1);
                return;
            }
        }

    }
}
