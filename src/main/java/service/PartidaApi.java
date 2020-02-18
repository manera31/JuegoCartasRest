package main.java.service;

import com.google.gson.Gson;
import main.java.modelos.Carta;
import main.java.modelos.Sesion;
import main.java.respuestas.RespuestaLogin;
import main.java.respuestas.RespuestaNuevoJuego;
import main.java.utils.Alert;
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

        // Separa el usuario y la contraseña que el clente ha pasado.
        String userPass = getGSON().fromJson(json, String.class);
        String user = userPass.split(":")[0];
        String pass = userPass.split(":")[1];

        RespuestaLogin respuesta;

        // Si la función .loguin devuelve true es que el usuario y la contraseña coinciden en la base de datos.
        if (MySQLHelper.login(user, pass)){

            Sesion sesionAuxiliar = null;
            boolean estaConectado = false;

            // Comprueba si el usuario tiene una sesion activa.
            for (Sesion s: getSesiones()){
                if(s.getUser().equals(user) && !s.isSesionCaducada()){
                    estaConectado = true;
                    sesionAuxiliar = s;
                    //TODO Eliminar las sesiones que esten caducadas.
                }
            }

            if (estaConectado){
                // Si el usuario tiene una sesion activa le pasamos la idSesion que tiene y una alerta para indicar que el usuario ya estaba conectado.
                respuesta = new RespuestaLogin(sesionAuxiliar.getIdSesion(), Alert.Login.USER_IS_ALREADY_CONNECTED);
            } else {
                // Crea un id único.
                String idSesion = Lib.getUUID();
                // Crea la respuesta.
                respuesta = new RespuestaLogin(idSesion);
                // Añade el id de sesion al array de sesiones.
                getSesiones().add(new Sesion(idSesion, user));
            }

        } else {
            // Si no coincide el usuario y la contraseña en la base de datos envia un mensaje de error.
            respuesta = new RespuestaLogin(Error.Login.USER_PASS_FAIL);
        }

        return getGSON().toJson(respuesta);
    }

    @GET
    @Path("/crearJuego")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String newGame(String json){
        // Cargamos la id de sesion que pasa el cliente.
        String idSesion = getGSON().fromJson(json, String.class);

        boolean encontrado = false;
        boolean isCaducado = false;

        // Comprueba si la idSesion se encuentra dentro de las sesiones conectadas y si esta caducada o no.
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
                // Si la sesion existe pero esta caducada envia un mensaje de error.
                respuesta = new RespuestaNuevoJuego(Error.Session.EXPIRED_SESSION);
            } else {
                // Si la sesion existe y NO esta caducada.
                // Busca la sesion.
                for (Sesion s: getSesiones()){
                    if (s.getIdSesion().equals(idSesion) && !s.isSesionCaducada()){
                        // Crea una partida. Devuelve el id de la partida.
                        idPartida = s.crearPartida();

                        // Reparte las cartas. Devuelve las cartas que le corresponden al jugador, las de la CPU se encuentran dentro del objeto Partida.
                        cartasJugador = s.getPartida().repartirCartas();

                        // Crea la respuesta pasandole la idPartida que se acaba de crear y las cartas del jugador.
                        respuesta = new RespuestaNuevoJuego(idPartida, cartasJugador);
                    }
                }

            }
        } else {
            // Si la sesion no existe evia un mensaje de error.
            respuesta = new RespuestaNuevoJuego(Error.Session.NOT_FOUND);
        }

        return getGSON().toJson(respuesta);
    }


}
