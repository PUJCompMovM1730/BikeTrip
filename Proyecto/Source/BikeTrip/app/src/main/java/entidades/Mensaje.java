package entidades;

import java.util.HashMap;

/**
 * Created by carlos on 30/10/17.
 */

public class Mensaje {

    String origen;
    String destino;
    String mensaje;


    public Mensaje() {
        this.origen = "";
        this.destino ="";
        this.mensaje ="";
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
