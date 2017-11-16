package entidades;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user-pc on 25/10/2017.
 */

public class Usuario {

    private int tipo;
    private String ID;
    private String nombre;
    private String apellido;
    private String correo;
    private String edad;
    private double peso;
    private double estatura;
    private Map<String,String> amigos; // Representa la lista de id de los amigos DEL  firebase
    private String imagen;
    private String portada;
    private String descripcion;


    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public String getPortada() {
        return portada;
    }

    public void setPortada(String portada) {
        this.portada = portada;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Usuario() {
        this.amigos = new HashMap();
   }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public double getEstatura() {
        return estatura;
    }

    public void setEstatura(double estatura) {
        this.estatura = estatura;
    }

    public Map<String,String> getAmigos() {
        return amigos;
    }

    public void agregarAmigos(String us) {
        this.amigos.put(us,"");
    }

    public void eliminarAmigo(String us) {
        this.amigos.remove(us);
    }


    public String getImagen() {
        return imagen;
    }

    public void setAmigos(Map<String,String> amigos) {
        this.amigos = amigos;
    }

    public void setImagen(String rutaImagen) {
        this.imagen = rutaImagen;
    }

    @Override
    public String toString() {
        return "ID: "+ID+" nombre: "+nombre+" apellido: "+apellido+" Edad: "+edad+
                " estatura: "+estatura+" Amigos: "+amigos.toString()+" ruta: "+imagen;
    }
}
