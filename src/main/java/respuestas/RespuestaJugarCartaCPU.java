package main.java.respuestas;

import main.java.utils.Enums;

public class RespuestaJugarCartaCPU {
    private Enums.Caracteristica caracteristica;

    public RespuestaJugarCartaCPU(Enums.Caracteristica caracteristica) {
        this.caracteristica = caracteristica;
    }

    public RespuestaJugarCartaCPU() {
    }

    public Enums.Caracteristica getCaracteristica() {
        return caracteristica;
    }

    public void setCaracteristica(Enums.Caracteristica caracteristica) {
        this.caracteristica = caracteristica;
    }
}
