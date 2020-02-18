package main.java.modelos;

import com.sun.xml.internal.ws.resources.BindingApiMessages;
import main.java.respuestas.RespuestaJugarCartaJugador;
import main.java.respuestas.RespuestaSorteo;
import main.java.utils.Enums;
import main.java.utils.Lib;
import main.java.utils.MySQLHelper;

import java.util.ArrayList;

public class Partida {private String idPartida;
    private Carta[] cartasCPU;
    private int manoActual;
    private Enums.PrimeroEnSacar empieza;

    public Partida(String idPartida) {
        this.idPartida = idPartida;
        cartasCPU = null;
        manoActual = 0;
        empieza = null;
    }

    public String getIdPartida() {
        return idPartida;
    }

    public ArrayList<Carta> repartirCartas(){
        manoActual = 1;
        ArrayList<Carta> cartasJugador = new ArrayList<>();
        cartasCPU = new Carta[6];

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
            cartasCPU[i] = cartaRandom;

            ultima = cartas1[contadorCartas];

            cartas1[contadorCartas] = cartaRandom;
            cartas1[posicion] = ultima;

            contadorCartas--;
        }

        return cartasJugador;
    }

    public Enums.PrimeroEnSacar sortearInicio(){
        int random = Lib.getRandom(1, 0);
        if(random == 1){
            empieza = Enums.PrimeroEnSacar.Jugador;
        } else {
            empieza = Enums.PrimeroEnSacar.CPU;
        }
        return empieza;
    }

    public RespuestaJugarCartaJugador jugarCartaJugador(int idCartaJugador, Enums.Caracteristica caracteristica){
        int idCartaGanadora = -1;

        // Elige una carta aleatoria de la cpu.
        Carta cartaCPU = getCartaRandomCPU();

        // Ubica la carta del jugador.
        Carta cartaJugador = MySQLHelper.buscarCarta(idCartaJugador);

        if(cartaJugador != null){

            // Compara las cartas y obtiene el resultado.
            switch (caracteristica){
                case MOTOR:
                    if(cartaJugador.getMotor() > cartaCPU.getMotor()){
                        idCartaGanadora = idCartaJugador;
                    } else if(cartaJugador.getMotor() == cartaCPU.getMotor()) {
                        idCartaGanadora = 0;
                    } else {
                        idCartaGanadora = cartaCPU.getId();
                    }
                    break;
                case POTENCIA:
                    if(cartaJugador.getPotencia() > cartaCPU.getPotencia()){
                        idCartaGanadora = idCartaJugador;
                    } else if(cartaJugador.getPotencia() == cartaCPU.getPotencia()) {
                        idCartaGanadora = 0;
                    } else {
                        idCartaGanadora = cartaCPU.getId();
                    }
                    break;
                case VELOCIDAD:
                    if(cartaJugador.getVelocidad() > cartaCPU.getVelocidad()){
                        idCartaGanadora = idCartaJugador;
                    } else if(cartaJugador.getVelocidad() == cartaCPU.getVelocidad()) {
                        idCartaGanadora = 0;
                    } else {
                        idCartaGanadora = cartaCPU.getId();
                    }
                    break;
                case CILINDROS:
                    if(cartaJugador.getCilindros() > cartaCPU.getCilindros()){
                        idCartaGanadora = idCartaJugador;
                    } else if(cartaJugador.getCilindros() == cartaCPU.getCilindros()) {
                        idCartaGanadora = 0;
                    } else {
                        idCartaGanadora = cartaCPU.getId();
                    }
                    break;
                case RPM:
                    if(cartaJugador.getRpm() < cartaCPU.getRpm()){
                        idCartaGanadora = idCartaJugador;
                    } else if(cartaJugador.getRpm() == cartaCPU.getRpm()) {
                        idCartaGanadora = 0;
                    } else {
                        idCartaGanadora = cartaCPU.getId();
                    }
                    break;
                case CONSUMO:
                    if(cartaJugador.getConsumo() < cartaCPU.getConsumo()){
                        idCartaGanadora = idCartaJugador;
                    } else if(cartaJugador.getConsumo() == cartaCPU.getConsumo()) {
                        idCartaGanadora = 0;
                    } else {
                        idCartaGanadora = cartaCPU.getId();
                    }
                    break;
            }
        }

        return new RespuestaJugarCartaJugador(idCartaJugador, cartaCPU.getId(), caracteristica, idCartaGanadora);
    }

    private Carta getCartaRandomCPU(){
        // Guardamos la última carta.
        Carta ultima = cartasCPU[cartasCPU.length-manoActual];

        // Escogemos una carta aleatoria.
        int posicionRandom = Lib.getRandom(cartasCPU.length-manoActual, 0);
        Carta cartaRandom = cartasCPU[posicionRandom];

        // Movemos la carta aleatroria al final del array.
        cartasCPU[cartasCPU.length-manoActual] = cartaRandom;

        // Movemos la última carta a la posición donde estaba la carta aleatoria que ha sigo elegida.
        cartasCPU[posicionRandom] = ultima;

        // En la próxima llamada a este método se habrá incrementado el contador 'manoActual' y así la carta que ha salido no volverá a salir.
        return cartaRandom;
    }

}
