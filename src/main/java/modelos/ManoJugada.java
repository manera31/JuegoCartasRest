package main.java.modelos;

public class ManoJugada {
    private int idCarta1;
    private int idCarta2;
    private int idCartaGanadora;

    public ManoJugada(int idCarta1, int idCarta2, int idCartaGanadora) {
        this.idCarta1 = idCarta1;
        this.idCarta2 = idCarta2;
        this.idCartaGanadora = idCartaGanadora;
    }

    public int getIdCarta1() {
        return idCarta1;
    }

    public int getIdCarta2() {
        return idCarta2;
    }

    public int getIdCartaGanadora() {
        return idCartaGanadora;
    }
}
