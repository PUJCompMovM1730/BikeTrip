package entidades;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.carlos.biketrip.Amigos;
import com.example.carlos.biketrip.Maps;
import com.example.carlos.biketrip.Perfil;
import com.example.carlos.biketrip.R;
import com.example.carlos.biketrip.RCompartidas;
import com.example.carlos.biketrip.Ruta;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by carlos on 28/10/17.
 */

public class CustomAdapter  extends ArrayAdapter<Usuario> {


    Context context;
    int layoutResourceId;
    ArrayList<Usuario> data = null;
    FirebaseDatabase database;
    DatabaseReference myRef;
    StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    FirebaseUser user;

    public	static	final	String	PATH_RUTAS="rutas/";
    public	static	final	String	PATH_IMAGENES="images/";
    public static final String TAG="TAG";
    private Usuario item;

    public CustomAdapter(Context context, int resource, List<Usuario> objects,
                         StorageReference mStorageRef, FirebaseDatabase d,
                         DatabaseReference dr) {
        super(context, resource, objects);
        this.layoutResourceId = resource;
        this.context = context;
        this.data = (ArrayList) objects;
        this.mStorageRef = mStorageRef;
        this.database = d;
        this.myRef=dr;
        mAuth =	FirebaseAuth.getInstance();
        user	=	mAuth.getCurrentUser();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        InfoUsuario infoUS = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();


            row = inflater.inflate(layoutResourceId, parent, false);

            infoUS = new InfoUsuario();
            infoUS.IUnom = row.findViewById(R.id.tvNombreAmigo);
            infoUS.IUkmrecorridos = row.findViewById(R.id.tvKilometrosAmigo);
            infoUS.IUultimoviaje = row.findViewById(R.id.tvFechaViaje);
            infoUS.IUfoto = row.findViewById(R.id.ibtnPerfiAmigo);
            infoUS.IURuta = row.findViewById(R.id.ibtnMapa);
            infoUS.IUBookmark = row.findViewById(R.id.btnBookmark);
            row.setTag(infoUS);

        }
        else
        {
            infoUS = (InfoUsuario) row.getTag();

        }

       item= data.get(position);
        Log.i("CustomAdapter+loadRutas",item.toString());
        infoUS.IUnom.setText(item.getNombre()+" "+item.getApellido());
        loadRutas(item, infoUS);



        if(!item.getImagen().equals("")){
            mStorageRef = FirebaseStorage.getInstance().getReference(PATH_IMAGENES+item.getID()+"/"+item.getImagen());
            Glide.with(getContext())
                    .using(new FirebaseImageLoader())
                    .load(mStorageRef)
                    .into(infoUS.IUfoto);
        }

        final String IDUPerfil = item.getID();
        final String nombre = infoUS.IUnom.getText().toString();
        infoUS.IUfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Perfil.class);
                intent.putExtra("idPerilAmigoDesdeCustom",IDUPerfil);
                intent.putExtra("nombreUsuarioDesdeCustom",nombre);
                getContext().startActivity(intent);
            }
        });

        infoUS.IURuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RutaEnt miRutabebe= cargarRuta(item);
                if(miRutabebe.getDescripcion()!=null) {
                    Intent i = new Intent(getContext(),Maps.class);
                    i.putExtra("Ruta",miRutabebe);
                    i.putExtra("Actividad",1);
                    getContext().startActivity(i);
                }
            }
        });


        infoUS.IUBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Intent i = new Intent(getContext(), RCompartidas.class);
                    i.putExtra("Actividad",1);
                    i.putExtra("Usuario",IDUPerfil);
                      getContext().startActivity(i);
            }
        });

        return row;
    }

    private class InfoUsuario {
        public TextView IUnom;
        public TextView IUkmrecorridos;
        public ImageButton IUfoto;
        public ImageButton IURuta;
        public ImageButton IUBookmark;
        public TextView IUultimoviaje;

    }
    public RutaEnt cargarRuta(final Usuario user){
        myRef =FirebaseDatabase.getInstance().getReferenceFromUrl("https://ejerciciostorage.firebaseio.com/");
        myRef.child("rutas");
        final RutaEnt rutaMAx = new RutaEnt();
        Date d = new Date();
        d.setTime(0);
        rutaMAx.setTiempo(d);
        myRef = database.getReference(PATH_RUTAS);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    RutaEnt rut = singleSnapshot.getValue(RutaEnt.class);
                    Log.i("Ruta", "Encontró ruta:	" + rut.getNombre());
                    Date today = new Date();
                    today.setTime(today.getTime());
                    Date daR = rut.getTiempo();
                    Boolean b1 = today.before(daR);
                    Boolean b2 = today.equals(daR);
                    if(!rut.isPrivada() && (b1 || b2)&&user.getID().equals(rut.getIdUsuario()))
                    {
                        if(rut.getTiempo().after( rutaMAx.getTiempo()))
                        {
                            rutaMAx.setTiempo(rut.getTiempo());
                            rutaMAx.setFin(rut.getFin());
                            rutaMAx.setInicio(rut.getInicio());
                            rutaMAx.setDescripcion(rut.getDescripcion());
                            rutaMAx.setDistancia(rut.getDistancia());
                            rutaMAx.setIdUsuario(rut.getIdUsuario());
                            rutaMAx.setLatFinal(rut.getLatFinal());
                            rutaMAx.setLatInicio(rut.getLatInicio());
                            rutaMAx.setLonFinal(rut.getLonFinal());
                            rutaMAx.setLonInicio(rut.getLonInicio());

                        }
                    }
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Consulta", "error	en	la	consulta", databaseError.toException());
            }
        });
        return rutaMAx;
    };

    public	void	loadRutas(final Usuario user, final InfoUsuario infoUS)	{

        myRef =	database.getReference(PATH_RUTAS);
        myRef.addValueEventListener(new	ValueEventListener()	{
            @Override
            public	void	onDataChange(DataSnapshot dataSnapshot)	 {

                boolean tieneRuta=false;
                double km =0;
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, 5000);
                cal.set(Calendar.MONTH, Calendar.DECEMBER);
                cal.set(Calendar.DAY_OF_MONTH, 30);
                Date fechaMasReciente = new Date(Calendar.getInstance().getTime().getTime());
                for	(DataSnapshot singleSnapshot :	dataSnapshot.getChildren())	{
                    RutaEnt r=new RutaEnt();
                    r=	singleSnapshot.getValue(RutaEnt.class);

                    Date fechaRuta = r.getTiempo();
                    if(r.getIdUsuario().equals(user.getID())){
                        km+=r.getDistancia();
                        if(fechaRuta.before(fechaMasReciente))
                            fechaMasReciente=fechaRuta;
                        Log.i("USUARIOCONRUTA",r.getIdUsuario()+ "---"+user.toString());
                        tieneRuta = true;
                    }
                }
                infoUS.IUkmrecorridos.setText("Kilometros recorridos: "+ km);
                //infoUS.IUultimoviaje.setText(fechaMasReciente.toString());

                if(tieneRuta) {
                    //Fecha mas reciente
               //     calcularFechaUltimaRuta(fechaMasReciente,infoUS);
                }


            }
            @Override
            public	void	onCancelled(DatabaseError databaseError)	{
                Log.w(TAG,	"error	en	la	consulta",	databaseError.toException());
            }
        });

    }

    public void calcularFechaUltimaRuta(Date fechaMasReciente,InfoUsuario infoUS){

        int segundos,minutos,horas,dias,semanas,meses, anios;
        int segundosActual,minutosActual,horasActual,diasActual,
                semanasActual,mesesActual,aniosActual;
        int difMin,difHor,difD,difSem,difMes,difA,difS;

        String valor="Hace ";

        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaMasReciente);
        segundos = cal.get(Calendar.SECOND);
        minutos = cal.get(Calendar.MINUTE);
        horas = cal.get(Calendar.HOUR);
        dias = cal.get(Calendar.DAY_OF_YEAR);
        semanas = cal.get(Calendar.WEEK_OF_YEAR);
        meses = cal.get(Calendar.MONTH);
        anios = cal.get(Calendar.YEAR);


        Calendar calendar = Calendar.getInstance();

        segundosActual = calendar.get(Calendar.SECOND);
        minutosActual = calendar.get(Calendar.MINUTE);
        horasActual = calendar.get(Calendar.HOUR);
        diasActual = calendar.get(Calendar.DAY_OF_YEAR);
        semanasActual = calendar.get(Calendar.WEEK_OF_YEAR);
        mesesActual = calendar.get(Calendar.MONTH);
        aniosActual = calendar.get(Calendar.YEAR);


        Log.i("FechaActual",calendar.toString());
        Log.i("FechaUlRuta", cal.toString());


        difS = Math.abs(segundosActual-segundos);
        difMin = Math.abs(minutosActual-minutos);
        difHor = Math.abs(horasActual-horas);
        difD = Math.abs(diasActual-dias);
        difSem = Math.abs(semanasActual-semanas);
        difMes = Math.abs(mesesActual-meses);
        difA = Math.abs(aniosActual-anios);


        if (difA<=0){
            if(difMes<=0){
                if(difSem<=0){
                    if(difD<=0){
                        if(difHor<=0){
                            if(difMin<=0){
                                valor+=difS+" segundo(s).";
                            }else valor+=minutos+" minuto(s).";
                        }else valor+=difHor+" hora(s).";
                    }else valor+=dias+" dia(s).";
                }else valor+=difSem+" semana(s).";
            }else valor+=difMes+" mes(es)";
        }else valor+=difSem+" año(s).";



        infoUS.IUultimoviaje.setText(valor);


    }




}

