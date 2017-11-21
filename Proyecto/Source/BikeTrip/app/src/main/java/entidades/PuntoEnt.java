package entidades;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by user-pc on 20/11/2017.
 */

public class PuntoEnt implements Serializable{

    private String nombre;
    private int canUsuarios;
    private String idCreador;
    private String idPunto;
    private double lat;
    private double lon;
    private int telefono;
    private String foto;
    private String comentario;
    private float puntaje;

    public float getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(float puntaje) {
        this.puntaje = puntaje;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public PuntoEnt() {
        this.canUsuarios =0;
        this.puntaje = 0;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCanUsuarios() {
        return canUsuarios;
    }

    public void setCanUsuarios(int canUsuarios) {
        this.canUsuarios = canUsuarios;
    }

    public String getIdCreador() {
        return idCreador;
    }

    public void setIdCreador(String idCreador) {
        this.idCreador = idCreador;
    }

    public String getIdPunto() {
        return idPunto;
    }

    public void setIdPunto(String idPunto) {
        this.idPunto = idPunto;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
