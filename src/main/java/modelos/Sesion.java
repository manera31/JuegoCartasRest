package main.java.modelos;

import main.java.utils.Lib;

public class Sesion {
    private String idSesion;
    private String user;
    private boolean sesionCaducada;
    private Partida partida;

    public Sesion(String idSesion, String user) {
        this.idSesion = idSesion;
        this.user = user;
        this.sesionCaducada = false;
        partida = null;
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

    public Partida getPartida() {
        return partida;
    }

    public String crearPartida(){
        String idPartida = Lib.getUUID();
        partida = new Partida(idPartida);
        return idPartida;
    }

}
