package main.java.respuestas;

import main.java.modelos.Usuario;

public class RespuestaLogin {
    private String idSesion;
    private Usuario usuario;
    private int codigoError;

    public RespuestaLogin(String idSesion, Usuario usuario) {
        this.idSesion = idSesion;
        this.usuario = usuario;
        codigoError = 0;
    }

    public RespuestaLogin(int codigoError) {
        idSesion = "";
        usuario = null;
        this.codigoError = codigoError;
    }

    public RespuestaLogin(String idSesion, Usuario usuario, int codigoError) {
        this.idSesion = idSesion;
        this.usuario = usuario;
        this.codigoError = codigoError;
    }

    public String getIdSesion() {
        return idSesion;
    }

    public void setIdSesion(String idSesion) {
        this.idSesion = idSesion;
    }

    public int getCodigoError() {
        return codigoError;
    }

    public void setCodigoError(int codigoError) {
        this.codigoError = codigoError;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
