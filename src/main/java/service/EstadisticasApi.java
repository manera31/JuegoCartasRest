package main.java.service;

import com.google.gson.Gson;
import main.java.modelos.*;
import main.java.utils.MySQLHelper;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.*;

@Path("/estadisticas")
public class EstadisticasApi {
    @GET
    @Path("/ranking")
    @Produces({MediaType.APPLICATION_JSON})
    public String getRanking(String json) {

        // Array de enteros con valor 3. Ganadas, empatadas y perdidas
        HashMap<String, EstadisticaUsuario> contadorPartidas = new HashMap<>();

        for (ResultadoPartida r: MySQLHelper.getResultadosPartidas()){

            if (!contadorPartidas.containsKey(r.getNickJugador())){
                contadorPartidas.put(r.getNickJugador(), new EstadisticaUsuario(r.getNickJugador()));
            }

            if (r.getPuntosJugador() > r.getPuntosCPU()){
                contadorPartidas.get(r.getNickJugador()).setPartidasGanadas();
            } else if (r.getPuntosJugador() == r.getPuntosCPU()){
                contadorPartidas.get(r.getNickJugador()).setPartidasEmpatadas();
            } else if (r.getPuntosJugador() < r.getPuntosCPU()){
                contadorPartidas.get(r.getNickJugador()).setPartidasPerdidas();
            }
        }

        ArrayList<EstadisticaUsuario> estadisticas = new ArrayList<>();

        for (Map.Entry<String, EstadisticaUsuario> e: contadorPartidas.entrySet()){
            estadisticas.add(e.getValue());
        }

        Collections.sort(estadisticas, new Comparator<EstadisticaUsuario>() {
            @Override
            public int compare(EstadisticaUsuario o1, EstadisticaUsuario o2) {
                return Integer.compare(o2.getPartidasGanadas(), o1.getPartidasGanadas());
            }
        });



        int numeroJugadores = estadisticas.size();
        if (numeroJugadores > 5){
            numeroJugadores = 5;
        }

        ArrayList<EstadisticaUsuario> arrayList = new ArrayList<>();
        for (int i = 0 ; i < numeroJugadores ; i++)
            arrayList.add(estadisticas.get(i));

        return PartidaApi.getGSON().toJson(arrayList);

    }

    @GET
    @Path("/winrateCartas")
    @Produces({MediaType.APPLICATION_JSON})
    public String getWinrateCartas(String json) {

        ArrayList<ManoJugada> manoJugadas = MySQLHelper.getManosJugadas();

        HashMap<Integer, EstadisticaCarta> map = new HashMap<>();

        for (Carta c: MySQLHelper.getCartas()){
            if (!map.containsKey(c.getId())){
                map.put(c.getId(), new EstadisticaCarta(c.getId()));
            }
        }

        for (ManoJugada m: manoJugadas){

            if (m.getIdCartaGanadora() != 0){
                if (m.getIdCartaGanadora() == m.getIdCarta1()){
                    map.get(m.getIdCarta1()).setGanadas();
                    map.get(m.getIdCarta2()).setPerdidas();

                } else {
                    map.get(m.getIdCarta1()).setPerdidas();
                    map.get(m.getIdCarta2()).setGanadas();
                }

            } else {
                map.get(m.getIdCarta1()).setEmpatadas();
                map.get(m.getIdCarta2()).setEmpatadas();
            }

        }

        ArrayList<EstadisticaCarta> estadisticaCartas = new ArrayList<>();

        for (Map.Entry<Integer, EstadisticaCarta> e: map.entrySet()){
            estadisticaCartas.add(e.getValue());
        }

        for (EstadisticaCarta e: estadisticaCartas){
            double sumaPartidas = e.getGanadas() + e.getPerdidas() + e.getEmpatadas();
            double winrate;
            try {
                winrate = e.getGanadas() / sumaPartidas;
            } catch (ArithmeticException ae){
                winrate = 0;
            }
            e.setWinrate(winrate);
        }

        Collections.sort(estadisticaCartas, new Comparator<EstadisticaCarta>() {
            @Override
            public int compare(EstadisticaCarta o1, EstadisticaCarta o2) {
                return Double.compare(o2.getWinrate(), o1.getWinrate());
            }
        });

        return PartidaApi.getGSON().toJson(estadisticaCartas);
    }
}
