package main.java.modelos;

import main.java.utils.Lib;
import main.java.utils.MySQLHelper;

import java.util.ArrayList;

public class Partida {
    private String idGame;
    private ArrayList<Carta> cartasCPU;
    private int manoActual;

    public Partida(String idGame) {
        this.idGame = idGame;
    }

    public ArrayList<Carta> repartirCartas(){
        manoActual = 1;
        ArrayList<Carta> cartasJugador = new ArrayList<>();
        cartasCPU = new ArrayList<>();

        ArrayList<Carta> cartaArrayList = MySQLHelper.getCartas();
        Carta[] cartas1 = new Carta[cartaArrayList.size()];
        for (int i = 0 ; i < cartas1.length ; i++){
            cartas1[i] = cartaArrayList.get(i);
        }

        int contadorCartas = cartas1.length-1;
        int posicion;
        Carta cartaRandom, ultima;
        for (int i = 0 ; i < 6 ; i++){

            posicion = Lib.getRandom(contadorCartas, 0);
            cartaRandom = cartas1[posicion];
            cartasJugador.add(cartaRandom);

            ultima = cartas1[contadorCartas];

            cartas1[contadorCartas] = cartaRandom;
            cartas1[posicion] = ultima;

            contadorCartas--;
        }

        for (int i = 0 ; i < 6 ; i++){
            posicion = Lib.getRandom(contadorCartas, 0);
            cartaRandom = cartas1[posicion];
            cartasCPU.add(cartaRandom);

            ultima = cartas1[contadorCartas];

            cartas1[contadorCartas] = cartaRandom;
            cartas1[posicion] = ultima;

            contadorCartas--;
        }

        return cartasJugador;
    }

}
