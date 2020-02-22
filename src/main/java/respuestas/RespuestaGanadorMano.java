package main.java.respuestas;

import main.java.utils.Enums;

public class RespuestaGanadorMano {
    private int idCartaJugador;
    private int idCartaCPU;
    private Enums.Caracteristica caracteristica;
    private int idCartaGanadora;

    public RespuestaGanadorMano(int idCartaJugador, int idCartaCPU, Enums.Caracteristica caracteristica, int idCartaGanadora) {
        this.idCartaJugador = idCartaJugador;
        this.idCartaCPU = idCartaCPU;
        this.caracteristica = caracteristica;
        this.idCartaGanadora = idCartaGanadora;
    }

    public int getIdCartaJugador() {
        return idCartaJugador;
    }

    public void setIdCartaJugador(int idCartaJugador) {
        this.idCartaJugador = idCartaJugador;
    }

    public int getIdCartaCPU() {
        return idCartaCPU;
    }

    public void setIdCartaCPU(int idCartaCPU) {
        this.idCartaCPU = idCartaCPU;
    }

    public Enums.Caracteristica getCaracteristica() {
        return caracteristica;
    }

    public void setCaracteristica(Enums.Caracteristica caracteristica) {
        this.caracteristica = caracteristica;
    }

    public int getIdCartaGanadora() {
        return idCartaGanadora;
    }

    public void setIdCartaGanadora(int idCartaGanadora) {
        this.idCartaGanadora = idCartaGanadora;
    }
}
