package main.java.modelos;

import main.java.respuestas.RespuestaJugarCartaCPU;
import main.java.respuestas.RespuestaResultadoMano;
import main.java.utils.Enums;
import main.java.utils.Lib;
import main.java.utils.MySQLHelper;

import java.util.ArrayList;

public class Partida {
    private String idPartida;
    private Usuario usuario;
    public static int NUMERO_CARTAS_POR_JUGADOR = 6;
    private Carta[] cartasCPU;
    private int manoActual;
    private Enums.Turno empieza;
    private int idCartaJugadaCPU;
    private int contadorManosGanadasJugador;
    private int contadorManosGanadasCPU;

    public Partida(String idPartida, Usuario user) {
        this.idPartida = idPartida;
        this.usuario = user;
        cartasCPU = null;
        manoActual = 0;
        empieza = null;
        idCartaJugadaCPU = 0;
        contadorManosGanadasJugador = 0;
        contadorManosGanadasCPU = 0;
    }

    public String getIdPartida() {
        return idPartida;
    }

    public ArrayList<Carta> repartirCartas(){
        manoActual = 1;
        ArrayList<Carta> cartasJugador = new ArrayList<>();
        cartasCPU = new Carta[NUMERO_CARTAS_POR_JUGADOR];

        ArrayList<Carta> cartaArrayList = MySQLHelper.getCartas();
        Carta[] cartas1 = new Carta[cartaArrayList.size()];
        for (int i = 0 ; i < cartas1.length ; i++){
            cartas1[i] = cartaArrayList.get(i);
        }

        int contadorCartas = cartas1.length-1;
        int posicion;
        Carta cartaRandom, ultima;
        for (int i = 0 ; i < NUMERO_CARTAS_POR_JUGADOR ; i++){

            posicion = Lib.getRandom(contadorCartas, 0);
            cartaRandom = cartas1[posicion];
            cartasJugador.add(cartaRandom);

            ultima = cartas1[contadorCartas];

            cartas1[contadorCartas] = cartaRandom;
            cartas1[posicion] = ultima;

            contadorCartas--;
        }

        for (int i = 0 ; i < NUMERO_CARTAS_POR_JUGADOR ; i++){
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

    public Enums.Turno sortearInicio(){
        int random = Lib.getRandom(1, 0);
        if(random == 1){
            empieza = Enums.Turno.Jugador;
        } else {
            empieza = Enums.Turno.CPU;
        }
        return empieza;
    }

    public RespuestaResultadoMano compararCartaJugador(int idCartaJugador, Enums.Caracteristica caracteristica){
        int idCartaGanadora = -1;

        Carta cartaCPU;
        if (idCartaJugadaCPU == 0) {
            // Elige una carta aleatoria de la cpu.
            cartaCPU = getCartaRandomCPU();
        } else {
            // Busca la carta que ha jugado ya la CPU
            cartaCPU = MySQLHelper.buscarCarta(idCartaJugadaCPU);

            // La igualamos a null para que cuando se llame otra vez elija una carta aleatoria.
            idCartaJugadaCPU = 0;
        }

        // Ubica la carta del jugador.
        Carta cartaJugador = MySQLHelper.buscarCarta(idCartaJugador);

        if(cartaJugador != null && cartaCPU != null){

            // Compara las cartas y obtiene el resultado.
            switch (caracteristica){
                case MOTOR:
                    if(cartaJugador.getMotor() > cartaCPU.getMotor()){
                        idCartaGanadora = idCartaJugador;
                        contadorManosGanadasJugador++;
                    } else if(cartaJugador.getMotor() == cartaCPU.getMotor()) {
                        idCartaGanadora = 0;
                    } else {
                        idCartaGanadora = cartaCPU.getId();
                        contadorManosGanadasCPU++;
                    }
                    break;
                case POTENCIA:
                    if(cartaJugador.getPotencia() > cartaCPU.getPotencia()){
                        idCartaGanadora = idCartaJugador;
                        contadorManosGanadasJugador++;
                    } else if(cartaJugador.getPotencia() == cartaCPU.getPotencia()) {
                        idCartaGanadora = 0;
                    } else {
                        idCartaGanadora = cartaCPU.getId();
                        contadorManosGanadasCPU++;
                    }
                    break;
                case VELOCIDAD:
                    if(cartaJugador.getVelocidad() > cartaCPU.getVelocidad()){
                        idCartaGanadora = idCartaJugador;
                        contadorManosGanadasJugador++;
                    } else if(cartaJugador.getVelocidad() == cartaCPU.getVelocidad()) {
                        idCartaGanadora = 0;
                    } else {
                        idCartaGanadora = cartaCPU.getId();
                        contadorManosGanadasCPU++;
                    }
                    break;
                case CILINDROS:
                    if(cartaJugador.getCilindros() > cartaCPU.getCilindros()){
                        idCartaGanadora = idCartaJugador;
                        contadorManosGanadasJugador++;
                    } else if(cartaJugador.getCilindros() == cartaCPU.getCilindros()) {
                        idCartaGanadora = 0;
                    } else {
                        idCartaGanadora = cartaCPU.getId();
                        contadorManosGanadasCPU++;
                    }
                    break;
                case RPM:
                    if(cartaJugador.getRpm() < cartaCPU.getRpm()){
                        idCartaGanadora = idCartaJugador;
                        contadorManosGanadasJugador++;
                    } else if(cartaJugador.getRpm() == cartaCPU.getRpm()) {
                        idCartaGanadora = 0;
                    } else {
                        idCartaGanadora = cartaCPU.getId();
                        contadorManosGanadasCPU++;
                    }
                    break;
                case CONSUMO:
                    if(cartaJugador.getConsumo() < cartaCPU.getConsumo()){
                        idCartaGanadora = idCartaJugador;
                        contadorManosGanadasJugador++;
                    } else if(cartaJugador.getConsumo() == cartaCPU.getConsumo()) {
                        idCartaGanadora = 0;
                    } else {
                        idCartaGanadora = cartaCPU.getId();
                        contadorManosGanadasCPU++;
                    }
                    break;
            }
        }

        MySQLHelper.insertarManoJugada(idCartaJugador, cartaCPU.getId(), caracteristica, idCartaGanadora);

        if (manoActual < NUMERO_CARTAS_POR_JUGADOR){
            manoActual++;
        } else {
            MySQLHelper.insertarResultadoPartida(idPartida, usuario.getNick(), contadorManosGanadasJugador, contadorManosGanadasCPU);
            return new RespuestaResultadoMano(idCartaJugador, cartaCPU.getId(), caracteristica, idCartaGanadora, contadorManosGanadasJugador, contadorManosGanadasCPU);
        }

        // 0 si es empate
        // idCarta si ha ganado
        return new RespuestaResultadoMano(idCartaJugador, cartaCPU.getId(), caracteristica, idCartaGanadora);
    }

    public RespuestaJugarCartaCPU jugarCartaCPU(){
        Carta cartaCPU = getCartaRandomCPU();
        Enums.Caracteristica caracteristicaCPU = getRandomCaracteristica();

        RespuestaJugarCartaCPU respuesta =  new RespuestaJugarCartaCPU(caracteristicaCPU);
        idCartaJugadaCPU = cartaCPU.getId();

        return respuesta;
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

    private Enums.Caracteristica getRandomCaracteristica(){
        return Enums.Caracteristica.values()[Lib.getRandom(Enums.Caracteristica.values().length-1, 0)];
    }

}
