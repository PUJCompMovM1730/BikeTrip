package entidades;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Created by carlos on 16/11/17.
 */

public class PuntoEmpresa {

    double longitud;
    double latitud;
    String idEmpresa;
    String nombre;
    String telefono;
    Date hora_apertura;
    Date hora_cierre;
    String foto;

    public PuntoEmpresa(){

    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public String getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(String idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Date getHora_apertura() {
        return hora_apertura;
    }

    public void setHora_apertura(Date hora_apertura) {
        this.hora_apertura = hora_apertura;
    }

    public Date getHora_cierre() {
        return hora_cierre;
    }

    public void setHora_cierre(Date hora_cierre) {
        this.hora_cierre = hora_cierre;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String toString(){
        return nombre+" - " + telefono + " --- " + hora_apertura.toString()
                +" --- "+hora_cierre+" --- "+foto;
    }
}
