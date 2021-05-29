package com.example.tunovelaonline.pojos;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;

public class Novela implements Serializable{
    private Integer idNovela;
    private Integer idUsuario;
    private String titulo;
    private String portada;
    private String resena;
    private String nombreAlternativo;
    private String autor;
    private String artista;
    private String traductor;
    private String genero;
    private Date fechaSubida;
    private Integer idMarcapaginas;
    private ArrayList<Capitulo> listaCapitulos;

    public Novela() {
    }

    public Novela(Integer idNovela, Integer idUsuario, String titulo, String portada, String resena, String nombreAlternativo, String autor, String artista, String traductor, String genero, Date fechaSubida) {
        this.idNovela = idNovela;
        this.idUsuario = idUsuario;
        this.titulo = titulo;
        this.portada = portada;
        this.resena = resena;
        this.nombreAlternativo = nombreAlternativo;
        this.autor = autor;
        this.artista = artista;
        this.traductor = traductor;
        this.genero = genero;
        this.fechaSubida = fechaSubida;
    }

    public Novela(Integer idNovela, Integer idUsuario, String titulo, String portada, String resena, String nombreAlternativo, String autor, String artista, String traductor, String genero, Date fechaSubida, Integer idMarcapaginas) {
        this.idNovela = idNovela;
        this.idUsuario = idUsuario;
        this.titulo = titulo;
        this.portada = portada;
        this.resena = resena;
        this.nombreAlternativo = nombreAlternativo;
        this.autor = autor;
        this.artista = artista;
        this.traductor = traductor;
        this.genero = genero;
        this.fechaSubida = fechaSubida;
        this.idMarcapaginas = idMarcapaginas;
    }

    public Novela(Integer idNovela, Integer idUsuario, String titulo, String portada, String resena, String nombreAlternativo, String autor, String artista, String traductor, String genero, Date fechaSubida, ArrayList<Capitulo> listaCapitulos) {
        this.idNovela = idNovela;
        this.idUsuario = idUsuario;
        this.titulo = titulo;
        this.portada = portada;
        this.resena = resena;
        this.nombreAlternativo = nombreAlternativo;
        this.autor = autor;
        this.artista = artista;
        this.traductor = traductor;
        this.genero = genero;
        this.fechaSubida = fechaSubida;
        this.listaCapitulos = listaCapitulos;
    }

    public Novela(Integer idNovela, Integer idUsuario, String titulo, String portada, String resena, String nombreAlternativo, String autor, String artista, String traductor, String genero, Date fechaSubida, Integer idMarcapaginas, ArrayList<Capitulo> listaCapitulos) {
        this.idNovela = idNovela;
        this.idUsuario = idUsuario;
        this.titulo = titulo;
        this.portada = portada;
        this.resena = resena;
        this.nombreAlternativo = nombreAlternativo;
        this.autor = autor;
        this.artista = artista;
        this.traductor = traductor;
        this.genero = genero;
        this.fechaSubida = fechaSubida;
        this.idMarcapaginas = idMarcapaginas;
        this.listaCapitulos = listaCapitulos;
    }

    public Integer getIdNovela() {
        return idNovela;
    }

    public void setIdNovela(Integer idNovela) {
        this.idNovela = idNovela;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getPortada() {
        return portada;
    }

    public void setPortada(String portada) {
        this.portada = portada;
    }

    public String getResena() {
        return resena;
    }

    public void setResena(String resena) {
        this.resena = resena;
    }

    public String getNombreAlternativo() {
        return nombreAlternativo;
    }

    public void setNombreAlternativo(String nombreAlternativo) {
        this.nombreAlternativo = nombreAlternativo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public String getTraductor() {
        return traductor;
    }

    public void setTraductor(String traductor) {
        this.traductor = traductor;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Date getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(Date fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    public Integer getIdMarcapaginas() {
        return idMarcapaginas;
    }

    public void setIdMarcapaginas(Integer idMarcapaginas) {
        this.idMarcapaginas = idMarcapaginas;
    }

    public ArrayList<Capitulo> getListaCapitulos() {
        return listaCapitulos;
    }

    public void setListaCapitulos(ArrayList<Capitulo> listaCapitulos) {
        this.listaCapitulos = listaCapitulos;
    }

    @Override
    public String toString() {
        return "Novela{" + "idNovela=" + idNovela + ", idUsuario=" + idUsuario + ", titulo=" + titulo + ", portada=" + portada + ", resena=" + resena + ", nombreAlternativo=" + nombreAlternativo + ", autor=" + autor + ", artista=" + artista + ", traductor=" + traductor + ", genero=" + genero + ", fechaSubida=" + fechaSubida + ", idMarcapaginas=" + idMarcapaginas + ", listaCapitulos=" + listaCapitulos + '}';
    }

}
