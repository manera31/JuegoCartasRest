package main.java.service;

import com.google.gson.Gson;
import main.java.modelos.Partida;
import main.java.modelos.Usuario;
import main.java.respuestas.RespuestaKeyValue;
import main.java.utils.Control;
import main.java.utils.MySQLHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;

@Path("/crud")
public class CRUDApi {

    /**
     * Endpoint para coger las cartas.
     * @param json
     * @return cartas
     */
    @GET
    @Path("/carta")
    @Produces({MediaType.APPLICATION_JSON})
    public String getCartas(String json) {
        return PartidaApi.getGSON().toJson(MySQLHelper.getCartas());
    }

    /**
     * Enpoint para agregar una carta.
     * @param json
     */
    @POST
    @Path("/carta")
    @Consumes({MediaType.APPLICATION_JSON})
    public void addCarta(String json) {

    }

    /**
     * Enpoint para actualizar una carta.
     * @param json
     */
    @PUT
    @Path("/carta")
    @Consumes({MediaType.APPLICATION_JSON})
    public void updateCarta(String json) {

    }

    /**
     * Endpoint para borrar una carta.
     * @param json
     */
    @DELETE
    @Path("/carta")
    @Consumes({MediaType.APPLICATION_JSON})
    public void deleteCarta(String json) {

    }

    /**
     * Endpoint para coger a todos los jugadores.
     * @param json
     */
    @GET
    @Path("/jugador")
    @Produces({MediaType.APPLICATION_JSON})
    public void getJugadores(String json) {

    }

    /**
     * Endpoint para crear un nuevo jugador.
     * @param json
     * @return clave valor
     */
    @POST
    @Path("/jugador")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String addJugador(String json) {
        Usuario usuario = PartidaApi.getGSON().fromJson(json, Usuario.class);

        HashMap <String, Integer> map = new HashMap<>();
        map.put(usuario.getNick(), MySQLHelper.addUsuario(usuario));

        RespuestaKeyValue respuestaKeyValue = new RespuestaKeyValue(map);

        return PartidaApi.getGSON().toJson(respuestaKeyValue);
    }

    /**
     * Endpoint para modificar un jugador.
     * @param json
     */
    @PUT
    @Path("/jugador")
    @Consumes({MediaType.APPLICATION_JSON})
    public void updateJugador(String json) {

    }

    /**
     * Endpoint para borrar un jugador.
     * @param json
     */
    @DELETE
    @Path("/jugador")
    @Consumes({MediaType.APPLICATION_JSON})
    public void deleteJugador(String json) {

    }

    /**
     * Endpoint para coger todas las sesiones.
     * @return sesiones
     */
    @GET
    @Path("/sesiones")
    @Produces({MediaType.APPLICATION_JSON})
    public String sesiones(){
        return PartidaApi.getGSON().toJson(PartidaApi.getSesiones());
    }
}
