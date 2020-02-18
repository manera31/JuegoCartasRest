package main.java.service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

public class CRUDApi {

    @GET
    @Path("/carta")
    @Produces({MediaType.APPLICATION_JSON})
    public void getCartas(String json) {

    }

    @GET
    @Path("/carta")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public void getCarta(String json) {

    }

    @POST
    @Path("/carta")
    @Consumes({MediaType.APPLICATION_JSON})
    public void addCarta(String json) {

    }

    @GET
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

    @GET
    @Path("/jugador")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public void getJugador(String json) {

    }

    @POST
    @Path("/jugador")
    @Consumes({MediaType.APPLICATION_JSON})
    public void addJugador(String json) {

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
