package entidades;

import java.util.Date;

/**
 * Created by user-pc on 20/11/2017.
 */

public class ComentarioEnt {

    private String idComentario;
    private String idPunto;
    private String idUsuarioC;
    private float calificacion;
    private Date fCalificacion;
    private String comentario;


    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public ComentarioEnt() {
    }

    public String getIdComentario() {
        return idComentario;
    }

    public void setIdComentario(String idComentario) {
        this.idComentario = idComentario;
    }

    public String getIdPunto() {
        return idPunto;
    }

    public void setIdPunto(String idPunto) {
        this.idPunto = idPunto;
    }

    public String getIdUsuarioC() {
        return idUsuarioC;
    }

    public void setIdUsuarioC(String idUsuarioC) {
        this.idUsuarioC = idUsuarioC;
    }

    public float getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(float calificacion) {
        this.calificacion = calificacion;
    }

    public Date getfCalificacion() {
        return fCalificacion;
    }

    public void setfCalificacion(Date fCalificacion) {
        this.fCalificacion = fCalificacion;
    }
}
