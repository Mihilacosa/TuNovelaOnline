package com.example.tunovelaonline;

public class  ListaNovelas {
    private String titulo;
    private String id;
    private String imagen;
    private String resena;

    public ListaNovelas() {

    }

    public ListaNovelas(String titulo, String id, String imagen, String resena) {
        this.titulo = titulo;
        this.id = id;
        this.imagen = imagen;
        this.resena = resena;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getResena() {
        return resena;
    }

    public void setResena(String resena) {
        this.resena = resena;
    }
}
