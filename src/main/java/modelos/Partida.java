package main.java.modelos;

import java.util.ArrayList;

public class Partida {
    private String idGame;
    private ArrayList<Carta> cartas;

    public Partida(String idGame, ArrayList<Carta> cartas) {
        this.idGame = idGame;
        this.cartas = cartas;
    }

    public Partida() {
    }

    public String getIdGame() {
        return idGame;
    }

    public void setIdGame(String idGame) {
        this.idGame = idGame;
    }

    public ArrayList<Carta> getCartas() {
        return cartas;
    }

    public void setCartas(ArrayList<Carta> cartas) {
        this.cartas = cartas;
    }
}
