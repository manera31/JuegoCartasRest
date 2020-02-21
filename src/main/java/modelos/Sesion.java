package main.java.modelos;

import main.java.utils.Lib;

public class Sesion {
    private String idSesion;
    private Usuario user;
    private boolean sesionCaducada;
    private Partida partida;

    public Sesion(String idSesion, Usuario user) {
        this.idSesion = idSesion;
        this.user = user;
        this.sesionCaducada = false;
        partida = null;
    }

    public String getIdSesion() {
        return idSesion;
    }

    public Usuario getUser() {
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
        partida = new Partida(idPartida, user);
        return idPartida;
    }

}
