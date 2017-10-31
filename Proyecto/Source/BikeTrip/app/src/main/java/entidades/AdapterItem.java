package entidades;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.carlos.biketrip.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user-pc on 27/10/2017.
 */

public class AdapterItem extends ArrayAdapter<RutaEnt> {

    protected Activity activity;
    protected ArrayList<RutaEnt> items;


    public AdapterItem(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public AdapterItem(Context context, int resource, List<RutaEnt> items) {
        super(context, resource, items);
    }


    public void clear() {
        items.clear();
    }

    public void addAll(ArrayList<RutaEnt> category) {
        for (int i = 0; i < category.size(); i++) {
            items.add(category.get(i));
        }
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.item_layout, null);
        }

        RutaEnt dir = getItem(position);

        TextView NombreR = (TextView) v.findViewById(R.id.nombre);
        NombreR.setText(dir.getNombre());

        TextView DistanciaR = (TextView) v.findViewById(R.id.distanciaR);
        DistanciaR.setText(String.valueOf(dir.getDistancia())+" km");

        TextView DireccionR = (TextView) v.findViewById(R.id.Direcciones);
        DireccionR.setText("Desde: "+dir.getInicio()+"\n Hasta: "+dir.getFin());

        TextView FechaR = (TextView)v.findViewById(R.id.Fecha);
        FechaR.setText(new java.sql.Date(dir.getTiempo().getTime()).toString());

        return v;
    }
}
