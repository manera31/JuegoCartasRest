package main.java.service;

import main.java.modelos.ResultadoPartida;
import main.java.utils.MySQLHelper;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;

@Path("/estadisticas")
public class EstadisticasApi {
    @GET
    @Path("/ranking")
    @Produces({MediaType.APPLICATION_JSON})
    public void getRanking(String json) {

        // Array de enteros con valor 3. Ganadas, empatadas y perdidas
        HashMap<String, Integer[]> contadorPartidas = new HashMap<>();

        for (ResultadoPartida r: MySQLHelper.getResultadosPartidas()){
            if (!contadorPartidas.containsKey(r.getNickJugador())){
                contadorPartidas.put(r.getNickJugador(), new Integer[3]);
            }

            if (r.getPuntosJugador() > r.getPuntosCPU()){
                contadorPartidas.get(r.getNickJugador())[0]++;
            } else if (r.getPuntosJugador() == r.getPuntosCPU()){
                contadorPartidas.get(r.getNickJugador())[1]++;
            } else if (r.getPuntosJugador() < r.getPuntosCPU()){
                contadorPartidas.get(r.getNickJugador())[2]++;
            }
        }

    }

    @GET
    @Path("/winrateCartas")
    @Produces({MediaType.APPLICATION_JSON})
    public void getWinrateCartas(String json) {

    }
}
