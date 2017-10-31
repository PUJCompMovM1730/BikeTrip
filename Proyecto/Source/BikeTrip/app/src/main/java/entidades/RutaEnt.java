package entidades;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;
//import java.util.GregorianCalendar;

/**
 * Created by user-pc on 25/10/2017.
 */

public class RutaEnt implements Serializable {
    private double latInicio;
    private double lonInicio;
    private double latFinal;
    private double lonFinal;
    private String descripcion;
    private String nombre;
    private String inicio;
    private String fin;
    private Date tiempo;
    private String idUsuario;
    private double distancia;
    private boolean privada;
    public RutaEnt() {
        //this.tiempo= new GregorianCalendar();
    }

    public String getInicio() {
        return inicio;
    }

    public void setInicio(String inicio) {
        this.inicio = inicio;
    }

    public String getFin() {
        return fin;
    }

    public void setFin(String fin) {
        this.fin = fin;
    }

    public boolean isPrivada() {
        return privada;
    }

    public void setPrivada(boolean privada) {
        this.privada = privada;
    }

    public double getDistancia() {
        return distancia;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }

    public double getLatInicio() {
        return latInicio;
    }

    public void setLatInicio(double latInicio) {
        this.latInicio = latInicio;
    }

    public double getLonInicio() {
        return lonInicio;
    }

    public void setLonInicio(double lonInicio) {
        this.lonInicio = lonInicio;
    }

    public double getLatFinal() {
        return latFinal;
    }

    public void setLatFinal(double latFinal) {
        this.latFinal = latFinal;
    }

    public double getLonFinal() {
        return lonFinal;
    }

    public void setLonFinal(double lonFinal) {
        this.lonFinal = lonFinal;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }
}