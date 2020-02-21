package main.java.modelos;

import java.util.Date;

public class ResultadoPartida {
    private String idPartida;
    private String nickJugador;
    private int puntosJugador;
    private int puntosCPU;
    private Date fecha;

    public ResultadoPartida(String idPartida, String nickJugador, int puntosJugador, int puntosCPU, Date fecha) {
        this.idPartida = idPartida;
        this.nickJugador = nickJugador;
        this.puntosJugador = puntosJugador;
        this.puntosCPU = puntosCPU;
        this.fecha = fecha;
    }

    public ResultadoPartida() {
    }

    public String getIdPartida() {
        return idPartida;
    }

    public void setIdPartida(String idPartida) {
        this.idPartida = idPartida;
    }

    public String getNickJugador() {
        return nickJugador;
    }

    public void setNickJugador(String nickJugador) {
        this.nickJugador = nickJugador;
    }

    public int getPuntosJugador() {
        return puntosJugador;
    }

    public void setPuntosJugador(int puntosJugador) {
        this.puntosJugador = puntosJugador;
    }

    public int getPuntosCPU() {
        return puntosCPU;
    }

    public void setPuntosCPU(int puntosCPU) {
        this.puntosCPU = puntosCPU;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}
