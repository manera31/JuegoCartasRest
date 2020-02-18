package main.java.respuestas;

public class RespuestaLogin {
    private String idSesion;
    private int codigoError;

    public RespuestaLogin(String idSesion) {
        this.idSesion = idSesion;
        codigoError = 0;
    }

    public RespuestaLogin(int codigoError) {
        idSesion = "";
        this.codigoError = codigoError;
    }

    public RespuestaLogin(String idSesion, int codigoError) {
        this.idSesion = idSesion;
        this.codigoError = codigoError;
    }

    public RespuestaLogin() {
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
}
