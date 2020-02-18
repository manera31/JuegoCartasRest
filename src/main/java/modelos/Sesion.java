package main.java.modelos;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Sesion {
    private String idSesion;
    private String user;
    private boolean sesionCaducada;
    private ArrayList<Partida> partidas;

    public Sesion(String idSesion, String user) {
        this.idSesion = idSesion;
        this.user = user;
        this.sesionCaducada = false;
        this.partidas = new ArrayList<>();
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

}
