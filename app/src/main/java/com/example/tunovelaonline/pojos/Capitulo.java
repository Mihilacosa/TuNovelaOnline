package com.example.tunovelaonline.pojos;

import java.io.Serializable;
import java.sql.Date;

public class Capitulo implements Serializable{
    private Integer idCapitulo;
    private Integer idNovela;
    private Integer idUsuario;
    private String titulo;
    private Integer numCapitulo;
    private String contenido;
    private String fechaSubida;

    public Capitulo() {
    }

    public Capitulo(Integer idCapitulo, String titulo, Integer numCapitulo) {
        this.idCapitulo = idCapitulo;
        this.titulo = titulo;
        this.numCapitulo = numCapitulo;
    }

    public Capitulo(Integer idCapitulo, Integer idNovela, Integer idUsuario, String titulo, Integer numCapitulo, String contenido, String fechaSubida) {
        this.idCapitulo = idCapitulo;
        this.idUsuario = idNovela;
        this.idUsuario = idUsuario;
        this.titulo = titulo;
        this.numCapitulo = numCapitulo;
        this.contenido = contenido;
        this.fechaSubida = fechaSubida;
    }

    public Integer getIdCapitulo() {
        return idCapitulo;
    }

    public void setIdCapitulo(Integer idCapitulo) {
        this.idCapitulo = idCapitulo;
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

    public Integer getNumCapitulo() {
        return numCapitulo;
    }

    public void setNumCapitulo(Integer numCapitulo) {
        this.numCapitulo = numCapitulo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(String fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    @Override
    public String toString() {
        return "Capitulo{" + "idCapitulo=" + idCapitulo + ", idNovela=" + idNovela + ", idUsuario=" + idUsuario + ", titulo=" + titulo + ", numCapitulo=" + numCapitulo + ", contenido=" + contenido + ", fechaSubida=" + fechaSubida + '}';
    }

}
