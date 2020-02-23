package main.java.modelos;

public class EstadisticaCarta {
    private int idCarta;
    private int ganadas;
    private int perdidas;
    private int empatadas;
    private double winrate;

    public EstadisticaCarta(int idCarta, int ganadas, int perdidas, int empatadas) {
        this.idCarta = idCarta;
        this.ganadas = ganadas;
        this.perdidas = perdidas;
        this.empatadas = empatadas;
    }

    public EstadisticaCarta(int idCarta){
        this(idCarta, 0, 0, 0);
    }

    public int getIdCarta() {
        return idCarta;
    }

    public int getGanadas() {
        return ganadas;
    }

    public int getPerdidas() {
        return perdidas;
    }

    public int getEmpatadas() {
        return empatadas;
    }

    public void setGanadas() {
        this.ganadas++;
    }

    public void setPerdidas() {
        this.perdidas++;
    }

    public void setEmpatadas() {
        this.empatadas++;
    }

    public double getWinrate() {
        return winrate;
    }

    public void setWinrate(double winrate) {
        this.winrate = winrate;
    }
}
