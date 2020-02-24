package main.java.service;

import com.google.gson.Gson;
import main.java.modelos.Carta;
import main.java.modelos.Sesion;
import main.java.modelos.Usuario;
import main.java.respuestas.*;
import main.java.utils.*;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

/**
 * Clase para almacenar todos los endpoints relacionados con la partida.
 * @author Joan Manera Perez
 */
@Path("/partida")
public class PartidaApi extends ResourceConfig {
    private static Gson g = null;
    private static ArrayList<Sesion> sesiones = null;

    /**
     * Sigleton Gson.
     * @return gson
     */
    public static Gson getGSON(){
        if (g == null){
            g = new Gson();
        }
        return g;
    }

    /**
     * Singleton Sesiones.
     * @return sesiones
     */
    public static  ArrayList<Sesion> getSesiones(){
        if (sesiones == null){
            sesiones = new ArrayList<>();
        }
        return sesiones;
    }

    /**
     * Enpoint para iniciar sesión en el servidor.
     * @param userPass
     * @return idSesion
     */
    @POST
    @Path("/login")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String login(String userPass){

        // Separa el usuario y la contraseña que el clente ha pasado.
        String userPassAux = getGSON().fromJson(userPass, String.class);
        String user = userPassAux.split(":")[0];
        String pass = userPassAux.split(":")[1];

        RespuestaLogin respuesta;

        // La función devuelve el usuario si es correcto o null si no.
        Usuario usuario = MySQLHelper.login(user, pass);
        if (usuario != null){

            Sesion sesionAuxiliar = null;
            boolean estaConectado = false;

            // Comprueba si el usuario tiene una sesión activa.
            for (Sesion s: getSesiones()){
                if(s.getUser().getNick().equals(user) && !s.isSesionCaducada()){
                    estaConectado = true;
                    sesionAuxiliar = s;
                    //TODO Eliminar las sesiones que esten caducadas.
                }
            }

            if (estaConectado){
                // Si el usuario tiene una sesión activa le pasamos la idSesion que tiene y una alerta para indicar que el usuario ya estaba conectado.
                respuesta = new RespuestaLogin(sesionAuxiliar.getIdSesion(), usuario, Control.Login.YA_CONECTADO);
            } else {
                // Crea un id único.
                String idSesion = Lib.getUUID();
                // Crea la respuesta.
                respuesta = new RespuestaLogin(idSesion, usuario, Control.Login.ENCONTRADO);
                // Añade el id de sesión al array de sesiones.
                getSesiones().add(new Sesion(idSesion, usuario));
            }

        } else {
            // Si no coincide el usuario y la contraseña en la base de datos envia un mensaje de error.
            respuesta = new RespuestaLogin(Control.Login.USUARIO_PASS_INCORRECTO);
        }

        return getGSON().toJson(respuesta);
    }


    /**
     * Endpoint para crear una nueva partida.
     * @param idSesionJson
     * @return idPartida
     */
    @POST
    @Path("/crearJuego")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    // Siempre crea una nueva partida, exista o no una en curso.
    public String nuevoJuego(String idSesionJson){
        // Cargamos la id de sesión que pasa el cliente.
        String idSesion = getGSON().fromJson(idSesionJson, String.class);

        int respuestaBusqueda = comprobarSesion(idSesion);

        String idPartida;
        ArrayList<Carta> cartasJugador;
        RespuestaNuevoJuego respuesta = null;

        switch (respuestaBusqueda){
            case Control.Sesion.CADUCADA:
                // Si la sesión existe pero esta caducada envia un mensaje de error.
                respuesta = new RespuestaNuevoJuego(Control.Sesion.CADUCADA);
                break;
            case Control.Sesion.NO_ENCONTRADA:
                // Si la sesion no existe evia un mensaje de error.
                respuesta = new RespuestaNuevoJuego(Control.Sesion.NO_ENCONTRADA);
                break;
            case Control.Sesion.ENCONTRADA:
                // Si la sesion existe y NO esta caducada.
                // Busca la sesión.
                for (Sesion s: getSesiones()){
                    if (s.getIdSesion().equals(idSesion) && !s.isSesionCaducada()){
                        // Crea una partida. Devuelve el id único de la partida.
                        idPartida = s.crearPartida();

                        // Reparte las cartas. Devuelve las cartas que le corresponden al jugador, las de la CPU se encuentran dentro del objeto Partida.
                        cartasJugador = s.getPartida().repartirCartas();

                        // Crea la respuesta pasandole la idPartida que se acaba de crear y las cartas del jugador.
                        respuesta = new RespuestaNuevoJuego(idPartida, cartasJugador, Control.Sesion.ENCONTRADA);
                    }
                }
                break;
        }

        return getGSON().toJson(respuesta);
    }


    /**
     * Endpoint para sortear quien empieza tirando la primera carta.
     * @param idSesionPartida
     * @return jugador/cpu
     */
    @POST
    @Path("/sortearInicio")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String sortearInicio(String idSesionPartida){
        // Separa la idSesion y la idPartida.
        String idSesionPartidaAux = getGSON().fromJson(idSesionPartida, String.class);
        String idSesion = idSesionPartidaAux.split(":")[0];
        String idPartida = idSesionPartidaAux.split(":")[1];

        int respuestaBusqueda = comprobarPartida(idSesion, idPartida);

        RespuestaSorteo respuesta = null;
        Enums.Turno empieza = null;

        switch (respuestaBusqueda){
            case Control.Sesion.CADUCADA:
                // Si la sesión existe pero esta caducada envia un mensaje de error.
                respuesta = new RespuestaSorteo(Control.Sesion.CADUCADA);
                break;
            case Control.Sesion.NO_ENCONTRADA:
                // Si la sesion no existe evia un mensaje de error.
                respuesta = new RespuestaSorteo(Control.Sesion.NO_ENCONTRADA);
                break;
            case Control.Partida.NO_ENCONTRADA:
                respuesta = new RespuestaSorteo(Control.Partida.NO_ENCONTRADA);
                break;
            case Control.Partida.ENCONTRADA:
                for(Sesion s: getSesiones()){
                    if (s.getIdSesion().equals(idSesion) && !s.isSesionCaducada() && s.getPartida().getIdPartida().equals(idPartida)){
                        // Si los datos coinciden sortea quien empieza. El metodo devuelve un enum (Jugador o CPU).
                        empieza = s.getPartida().sortearInicio();

                        // Crea la respuesta a partir de quien empieza.
                        respuesta = new RespuestaSorteo(empieza, Control.Partida.ENCONTRADA);
                    }
                }
                break;
        }

        return getGSON().toJson(respuesta);
    }

    /**
     * Endponit para que el cliente envie su carta o su carta y su característica.
     * @param idSesionPartida
     * @return resultadoMano
     */
    @POST
    @Path("/jugarCarta")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String jugarCarta(String idSesionPartida){
        // Separamos la información.
        EnvioJugarCarta envioJugarCarta = getGSON().fromJson(idSesionPartida, EnvioJugarCarta.class);
        String idSesion = envioJugarCarta.getIdSesion();
        String idPartida = envioJugarCarta.getIdPartida();
        int idCarta = envioJugarCarta.getIdCarta();
        Enums.Caracteristica caracteristica = envioJugarCarta.getCaracteristica();

        RespuestaResultadoMano respuesta = null;
        for(Sesion s: getSesiones()){
            if (s.getIdSesion().equals(idSesion) && !s.isSesionCaducada() && s.getPartida().getIdPartida().equals(idPartida)){
                respuesta = s.getPartida().compararCartaJugador(idCarta, caracteristica);
            }
        }

        return getGSON().toJson(respuesta);
    }

    /**
     * Endpoint para informar al servidor que el cliente esta listo para recibir una carta de la CPU.
     * @param json
     * @return caracteristicaCPU
     */
    @POST
    @Path("/clienteListo")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String clienteListo(String json){
        // Separa la idSesion y la idPartida.
        String idSesionPartida = getGSON().fromJson(json, String.class);
        String idSesion = idSesionPartida.split(":")[0];
        String idPartida = idSesionPartida.split(":")[1];

        RespuestaJugarCartaCPU respuesta = null;
        for(Sesion s: getSesiones()){
            if (s.getIdSesion().equals(idSesion) && !s.isSesionCaducada() && s.getPartida().getIdPartida().equals(idPartida)){
                respuesta = s.getPartida().jugarCartaCPU();
            }
        }

        return getGSON().toJson(respuesta);
    }


    /**
     * Endponit para que el servidor cierre la sesión del cliente.
     * @param jsonIdSesion
     */
    @POST
    @Path("/cerrarSesion")
    @Consumes({MediaType.APPLICATION_JSON})
    public void cerrarSesion(String jsonIdSesion){
        String idSesion = getGSON().fromJson(jsonIdSesion, String.class);

        for(Sesion s: getSesiones()){
            if (s.getIdSesion().equals(idSesion)){
                s.setSesionCaducada(true);
            }
        }
    }

    /**
     * Comprueba si existe la sesion o si esta caducada.
     * @param idSesion
     * @return codigoError
     */
    private int comprobarSesion(String idSesion){
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

        if (encontrado){
            if (isCaducado){
                return Control.Sesion.CADUCADA;
            } else {
                return Control.Sesion.ENCONTRADA;
            }
        } else {
            return Control.Sesion.NO_ENCONTRADA;
        }
    }

    /**
     * Comprueba si existe la sesion y la partida
     * @param idSesion
     * @param idPartida
     * @return codigoError
     */
    private int comprobarPartida(String idSesion, String idPartida){
        boolean encontrado = false;
        boolean isCaducado = false;
        boolean partidaEncontrada = false;

        // Comprueba si existe la sesión, esta activa o no y si existe la partida.
        for(Sesion s: getSesiones()){
            if (s.getIdSesion().equals(idSesion)){
                encontrado = true;
                if (s.isSesionCaducada()){
                    isCaducado = true;
                }
                if (s.getPartida().getIdPartida().equals(idPartida)){
                    partidaEncontrada = true;
                }
            }
        }

        if (encontrado){
            if (isCaducado){
                return Control.Sesion.CADUCADA;
            } else {
                if (partidaEncontrada){
                    return Control.Partida.ENCONTRADA;
                } else {
                    return Control.Partida.NO_ENCONTRADA;
                }
            }
        } else {
            return Control.Sesion.NO_ENCONTRADA;
        }
    }


}
