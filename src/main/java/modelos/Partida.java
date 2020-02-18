package main.java.modelos;

import java.util.ArrayList;

public class Partida {
    private String idGame;
    private ArrayList<Carta> cartasCPU;
    private int manoActual;

    public Partida(String idGame) {
        this.idGame = idGame;
        this.cartasCPU = new ArrayList<>();
        this.manoActual = 1;
    }


}
