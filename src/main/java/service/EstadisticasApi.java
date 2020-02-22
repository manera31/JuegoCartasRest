package main.java.service;

import com.google.gson.Gson;
import main.java.modelos.EstadisticaUsuario;
import main.java.modelos.ResultadoPartida;
import main.java.modelos.Usuario;
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

        Gson gson = new Gson();
        return gson.toJson(arrayList);

    }

    @GET
    @Path("/winrateCartas")
    @Produces({MediaType.APPLICATION_JSON})
    public void getWinrateCartas(String json) {

    }
}
