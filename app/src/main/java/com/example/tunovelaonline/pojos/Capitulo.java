package com.example.tunovelaonline.pojos;

import java.io.Serializable;
import java.sql.Date;

public class Capitulo implements Serializable{
    private Integer idCapitulo;
    private Integer idUsuario;
    private String titulo;
    private Integer numCapitulo;
    private String contenido;
    private Date fechaSubida;

    public Capitulo() {
    }

    public Capitulo(Integer idCapitulo, String titulo, Integer numCapitulo) {
        this.idCapitulo = idCapitulo;
        this.titulo = titulo;
        this.numCapitulo = numCapitulo;
    }

    public Capitulo(Integer idCapitulo, Integer idUsuario, String titulo, Integer numCapitulo, String contenido, Date fechaSubida) {
        this.idCapitulo = idCapitulo;
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

    public Date getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(Date fechaSubida) {
        this.fechaSubida = fechaSubida;
    }
}
