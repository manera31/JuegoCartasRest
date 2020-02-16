package main.java.modelos;

public class Sesion {
    private String idSesion;
    private String user;
    private boolean sesionCaducada;
    private String idGame;

    public Sesion(String idSesion, String user) {
        this.idSesion = idSesion;
        this.user = user;
        this.sesionCaducada = false;
        this.idGame = "";
    }

    public String getIdSesion() {
        return idSesion;
    }

    public String getUser() {
        return user;
    }

    public boolean isSesionCaducada() {
        return sesionCaducada;
    }

    public void setSesionCaducada(boolean sesionCaducada) {
        this.sesionCaducada = sesionCaducada;
    }

    public String getIdGame() {
        return idGame;
    }

    public void setIdGame(String idGame) {
        this.idGame = idGame;
    }
}
