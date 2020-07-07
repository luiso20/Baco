package es.puntocomaapps.baco;

public class Comentario {

    private String comentario;
    private String fecha;
    private String usuario;
    private String id_evento;
    private String valoracion;

    public Comentario() {
    }

    public Comentario(String comentario, String fecha, String usuario, String id_evento,  String valoracion) {
        this.comentario = comentario;
        this.fecha = fecha;
        this.usuario = usuario;
        this.id_evento = id_evento;
        this.valoracion = valoracion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getId_evento() {
        return id_evento;
    }

    public void setId_evento(String id_evento) {
        this.id_evento = id_evento;
    }

    public String getValoracion() {
        return valoracion;
    }

    public void setValoracion(String valoracion) {
        this.valoracion = valoracion;
    }
}
