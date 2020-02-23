package main.java.service;

import com.google.gson.Gson;
import main.java.modelos.Usuario;
import main.java.respuestas.RespuestaKeyValue;
import main.java.utils.Control;
import main.java.utils.MySQLHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;

@Path("/crud")
public class CRUDApi {

    @GET
    @Path("/carta")
    @Produces({MediaType.APPLICATION_JSON})
    public String getCartas(String json) {
        Gson g = new Gson();
        return g.toJson(MySQLHelper.getCartas());
    }

    @POST
    @Path("/carta")
    @Consumes({MediaType.APPLICATION_JSON})
    public void addCarta(String json) {

    }

    @PUT
    @Path("/carta")
    @Consumes({MediaType.APPLICATION_JSON})
    public void updateCarta(String json) {

    }

    @DELETE
    @Path("/carta")
    @Consumes({MediaType.APPLICATION_JSON})
    public void deleteCarta(String json) {

    }

    @GET
    @Path("/jugador")
    @Produces({MediaType.APPLICATION_JSON})
    public void getJugadores(String json) {

    }


    @POST
    @Path("/jugador")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String addJugador(String json) {

        Gson gson = new Gson();
        Usuario usuario = gson.fromJson(json, Usuario.class);

        HashMap <String, Integer> map = new HashMap<>();
        map.put(usuario.getNick(), MySQLHelper.addUsuario(usuario));

        RespuestaKeyValue respuestaKeyValue = new RespuestaKeyValue(map);

        return gson.toJson(respuestaKeyValue);
    }

    @PUT
    @Path("/jugador")
    @Consumes({MediaType.APPLICATION_JSON})
    public void updateJugador(String json) {

    }

    @DELETE
    @Path("/jugador")
    @Consumes({MediaType.APPLICATION_JSON})
    public void deleteJugador(String json) {

    }
}
