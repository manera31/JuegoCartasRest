package main.java.service;

import com.google.gson.Gson;
import main.java.modelos.Carta;
import main.java.modelos.Sesion;
import main.java.modelos.respuesta.RespuestaLogin;
import main.java.modelos.respuesta.RespuestaNuevoJuego;
import main.java.utils.Error;
import main.java.utils.Lib;
import main.java.utils.MySQLHelper;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

@Path("/mirest")
public class PartidaApi extends ResourceConfig {
    private static Gson g = null;
    private static ArrayList<Sesion> sesiones = null;
    private static ArrayList<Carta> cartas = null;

    private static Gson getGSON(){
        if (g == null){
            g = new Gson();
        }
        return g;
    }

    private static  ArrayList<Sesion> getSesiones(){
        if (sesiones == null){
            sesiones = new ArrayList<>();
        }
        return sesiones;
    }

    private static ArrayList<Carta> getCartas(){
        if (cartas == null){
            cartas = MySQLHelper.getCartas();
        }
        return cartas;
    }

    @GET
    @Path("/login")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String login(String json){

        String userPass = getGSON().fromJson(json, String.class);
        String user = userPass.split(":")[0];
        String pass = userPass.split(":")[1];

        RespuestaLogin respuesta;
        boolean estaConectado = false;

        if (MySQLHelper.login(user, pass)){

            for (Sesion s: getSesiones()){
                if(s.getUser().equals(user) && !s.isSesionCaducada()){
                    estaConectado = true;
                    //TODO Eliminar las sesiones que esten caducadas.
                }
            }

            if (estaConectado){
                respuesta = new RespuestaLogin(Error.Login.USER_IS_ALREADY_CONNECTED);
            } else {
                String idSesion = Lib.getUUID();
                respuesta = new RespuestaLogin(idSesion);
                getSesiones().add(new Sesion(idSesion, user));
            }

        } else {
            respuesta = new RespuestaLogin(Error.Login.USER_PASS_FAIL);
        }

        return getGSON().toJson(respuesta);
    }

    @GET
    @Path("/crearJuego")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String newGame(String json){
        String idSesion = getGSON().fromJson(json, String.class);

        boolean encontrado = false;
        boolean isCaducado = false;

        for (Sesion s: getSesiones()){
            if (s.getIdSesion().equals(idSesion)){
                encontrado = true;
                if (s.isSesionCaducada()){
                    isCaducado = true;
                }
            }
        }

        String idPartida;
        ArrayList<Carta> cartasJugador;
        RespuestaNuevoJuego respuesta = null;
        if (encontrado){
            if (isCaducado){
                respuesta = new RespuestaNuevoJuego(Error.Session.EXPIRED_SESSION);
            } else {
                for (Sesion s: getSesiones()){
                    if (s.getIdSesion().equals(idSesion) && !s.isSesionCaducada()){
                        idPartida = s.crearPartida();
                        cartasJugador = s.getPartida().repartirCartas();
                        respuesta = new RespuestaNuevoJuego(idPartida, cartasJugador);
                    }
                }

            }
        } else {
            respuesta = new RespuestaNuevoJuego(Error.Session.NOT_FOUND);
        }

        return getGSON().toJson(respuesta);
    }


}
