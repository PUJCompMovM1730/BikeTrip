package entidades;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.carlos.biketrip.Perfil;
import com.example.carlos.biketrip.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
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
import java.util.List;
import java.util.Locale;

/**
 * Created by carlos on 29/10/17.
 */

public class ListViewAdapter extends android.widget.BaseAdapter {
    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    private List<Usuario> userpopulationlist = null;
    private ArrayList<Usuario> arraylist;

    FirebaseDatabase database;
    DatabaseReference myRef;
    StorageReference mStorageRef;

    public	static	final	String	PATH_RUTAS="rutas/";
    public	static	final	String	PATH_IMAGENES="images/";
    public static final String TAG="TAG";


    public ListViewAdapter(Context context, List<Usuario> listaUsuarios, FirebaseDatabase f, DatabaseReference d,
                           StorageReference sr) {
        mContext = context;
        this.userpopulationlist = listaUsuarios;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(listaUsuarios);
        Log.i("Lista de parametro", listaUsuarios.toString());


        this.mStorageRef=sr;
        this.database =f;
        this.myRef = d;
    }

    public class ViewHolder {
        public TextView IUnom;
        public TextView IUkmrecorridos;
        public ImageButton IUfoto;
        public ImageButton IURuta;
        public ImageButton IUBookmark;
        public TextView IUultimoviaje;
    }

    @Override
    public int getCount() {
        return userpopulationlist.size();
    }

    @Override
    public Usuario getItem(int position) {
        return userpopulationlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.layout, null);


            holder.IUnom = view.findViewById(R.id.tvNombreAmigo);
            holder.IUkmrecorridos = view.findViewById(R.id.tvKilometrosAmigo);
            holder.IUultimoviaje = view.findViewById(R.id.tvFechaViaje);
            holder.IUfoto = view.findViewById(R.id.ibtnPerfiAmigo);
            holder.IURuta = view.findViewById(R.id.ibtnMapa);
            holder.IUBookmark = view.findViewById(R.id.btnBookmark);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Usuario item = userpopulationlist.get(position);
        Log.i("ListViewAdapter",item.toString());
        holder.IUnom.setText(item.getNombre()+" "+item.getApellido());
        loadRutas(item, holder);

        if(!item.getImagen().equals("")){
            mStorageRef = FirebaseStorage.getInstance().getReference(PATH_IMAGENES+item.getImagen());
            Glide.with(mContext)
                    .using(new FirebaseImageLoader())
                    .load(mStorageRef)
                    .into(holder.IUfoto);
        }

        final String IDUPerfil = item.getID();
        holder.IUfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, Perfil.class);
                intent.putExtra("idPerilAmigoDesdeCustom",IDUPerfil);
                mContext.startActivity(intent);
            }
        });

        holder.IURuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Un toast por ahora
                Toast.makeText(mContext,"Toca mostrar esa ruta...",
                        Toast.LENGTH_LONG).show();
            }
        });



        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());

        userpopulationlist.clear();
        if (charText.length() == 0) {
            userpopulationlist.addAll(arraylist);
        }
        else
        {
            for (Usuario wp : arraylist)
            {
                Log.i("Recorriendo array", wp.toString());

                if (wp.getNombre().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    userpopulationlist.add(wp);
                }
            }
        }
        Log.i("Despues de filtro", userpopulationlist.toString());
        notifyDataSetChanged();
    }



    public	void	loadRutas(final Usuario user, final ViewHolder infoUS)	{

        myRef =	database.getReference(PATH_RUTAS);
        myRef.addValueEventListener(new	ValueEventListener()	{
            @Override
            public	void	onDataChange(DataSnapshot dataSnapshot)	 {

                double km =0;
                int segundos,minutos,horas,dias,semanas,meses;
                int segundosActual,minutosActual,horasActual,diasActual,semanasActual
                        ,mesesActual;
                String valor="Hace ";

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, 3000);
                cal.set(Calendar.MONTH, Calendar.DECEMBER);
                cal.set(Calendar.DAY_OF_MONTH, 30);
                Date fechaMenor = cal.getTime();

                for	(DataSnapshot singleSnapshot :	dataSnapshot.getChildren())	{

                    RutaEnt r=new RutaEnt();
                    r=	singleSnapshot.getValue(RutaEnt.class);

                    Date fechaRuta = r.getTiempo();

                    if(fechaRuta.before(fechaMenor))fechaMenor=fechaRuta;

                    if(r.getIdUsuario().equals(user.getID())){
                        km+=r.getDistancia();

                    }
                    Log.i("DATOS ALMACENADOS RUTA",r.toString());
                }
                infoUS.IUkmrecorridos.setText("Kilometros recorridos: "+ km);

                //Fecha mas reciente
                cal.setTime(fechaMenor);
                segundos = cal.get(Calendar.SECOND);
                minutos = cal.get(Calendar.MINUTE);
                horas = cal.get(Calendar.HOUR);
                dias = cal.get(Calendar.DAY_OF_WEEK_IN_MONTH);
                semanas = cal.get(Calendar.WEEK_OF_MONTH);
                meses = cal.get(Calendar.MONTH);


                Calendar calendar = GregorianCalendar.getInstance();
                calendar.setTime(new Date());

                segundosActual = calendar.get(Calendar.SECOND);
                minutosActual = calendar.get(Calendar.MINUTE);
                horasActual = calendar.get(Calendar.HOUR);
                diasActual = calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH);
                semanasActual = calendar.get(Calendar.WEEK_OF_MONTH);
                mesesActual = calendar.get(Calendar.MONTH);


                Log.i("me *** actual *** menor",mesesActual+" *** "+meses);
                Log.i("se *** actual *** menor",semanasActual+" *** "+semanas);
                Log.i("ds *** actual *** menor",diasActual+" *** "+dias);
                Log.i("hs *** actual *** menor",horasActual+" *** "+horas);
                Log.i("sg *** actual *** menor",segundosActual+" *** "+segundos);


                if((segundosActual-segundos)>0)valor+=segundos+" segundos";
                else if((minutosActual-minutos)>0)valor+=minutos+" minutos";
                else if((horasActual-horas)>0)valor+=horas+" horas";
                else if((diasActual-dias)>0)valor+=dias+" dias";
                else if((semanasActual-semanas)>0)valor+=semanas+" semanas";
                else if((mesesActual-meses)>0)valor+=meses+" meses";

                infoUS.IUultimoviaje.setText(valor);
            }
            @Override
            public	void	onCancelled(DatabaseError databaseError)	{
                Log.w(TAG,	"error	en	la	consulta",	databaseError.toException());
            }
        });

    }


}
