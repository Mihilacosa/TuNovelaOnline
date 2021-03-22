package com.example.tunovelaonline.pojos;

import java.io.Serializable;
import java.sql.Date;

public class Usuario implements Serializable {
    private Integer idUsuario;
    private String usuario;
    private String email;
    private String contrasena;
    private String imagen;
    private Date suscripcion;
    private Integer tamanoLetra;
    private String fontLetra;
    private String tema;

    public Usuario() {
    }

    public Usuario(Integer idUsuario, String usuario, String email, String contrasena, String imagen, Date suscripcion, Integer tamanoLetra, String fontLetra, String tema) {
        this.idUsuario = idUsuario;
        this.usuario = usuario;
        this.email = email;
        this.contrasena = contrasena;
        this.imagen = imagen;
        this.suscripcion = suscripcion;
        this.tamanoLetra = tamanoLetra;
        this.fontLetra = fontLetra;
        this.tema = tema;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Date getSuscripcion() {
        return suscripcion;
    }

    public void setSuscripcion(Date suscripcion) {
        this.suscripcion = suscripcion;
    }

    public Integer getTamanoLetra() {
        return tamanoLetra;
    }

    public void setTamanoLetra(Integer tamanoLetra) {
        this.tamanoLetra = tamanoLetra;
    }

    public String getFontLetra() {
        return fontLetra;
    }

    public void setFontLetra(String fontLetra) {
        this.fontLetra = fontLetra;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }
}
