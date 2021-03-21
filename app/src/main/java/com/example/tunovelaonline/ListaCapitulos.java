package com.example.tunovelaonline;

public class ListaCapitulos {
    private String capitulo;
    private String id;

    public ListaCapitulos() {

    }

    public ListaCapitulos(String capitulo, String id) {
        this.capitulo = capitulo;
        this.id = id;
    }

    public String getCapitulo() {
        return capitulo;
    }

    public void setCapitulo(String capitulo) {
        this.capitulo = capitulo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
