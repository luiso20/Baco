package es.puntocomaapps.baco;

import java.io.Serializable;

public class Evento implements Serializable {
    private String id;
    private String titulo;
    private String fecha;
    private String foto;
    private String descripcion;
    private String hora;
    private String municipio;
    private String precio;
    private String url;
    private String ubicacion;
    private String keywords;
    private String ordenMunYFecha;

    public Evento() {
        //Es obligatorio incluir constructor por defecto
    }

    public Evento(String id, String titulo, String fecha, String foto, String descripcion, String hora, String municipio, String precio, String url, String ubicacion) {
        this.id = id;
        this.titulo = titulo;
        this.fecha = fecha;
        this.foto = foto;
        this.descripcion = descripcion;
        this.hora = hora;
        this.municipio = municipio;
        this.precio = precio;
        this.url = url;
        this.ubicacion = ubicacion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getOrdenMunYFecha() {
        return ordenMunYFecha;
    }

    public void setOrdenMunYFecha(String ordenMunYFecha) {
        this.ordenMunYFecha = ordenMunYFecha;
    }
}
