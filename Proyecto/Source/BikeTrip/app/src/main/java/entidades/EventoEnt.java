package entidades;

import java.util.Date;

/**
 * Created by Magda on 15/11/2017.
 */

public class EventoEnt {

    private double lat;
    private double lon;
    private String imagen;
    private String comentarios;
    private String nombre;
    private Date tiempo;
    private String idCreador;
    private String idEvento;

    public EventoEnt() {
    }

    public String getIdEvento() {return idEvento;}

    public void setIdEvento(String idEvento) {this.idEvento = idEvento;}

    public String getIdCreador() {return idCreador;}

    public void setIdCreador(String creador) {this.idCreador = creador;}

    public double getLat() {return lat;}

    public void setLat(double latInicio) {
        this.lat = latInicio;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lonInicio) {
        this.lon = lonInicio;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getTiempo() {
        return tiempo;
    }

    public void setTiempo(Date tiempo) {
        this.tiempo = tiempo;
    }
}
