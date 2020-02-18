package main.java.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public class EstadisticasApi {
    @GET
    @Path("/ranking")
    @Produces({MediaType.APPLICATION_JSON})
    public void getRanking(String json) {

    }

    @GET
    @Path("/winrateCartas")
    @Produces({MediaType.APPLICATION_JSON})
    public void getWinrateCartas(String json) {

    }
}
