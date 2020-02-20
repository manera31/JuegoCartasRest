package main.java.service;

import com.google.gson.Gson;
import main.java.modelos.Carta;
import main.java.modelos.Sesion;
import main.java.respuestas.*;
import main.java.utils.*;
import main.java.utils.Error;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

@Path("/mirest")
public class PartidaApi extends ResourceConfig {
    private static Gson g = null;
    private static ArrayList<Sesion> sesiones = null;

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

        // Si la función loguin devuelve true es que el usuario y la contraseña coinciden en la base de datos.
        if (MySQLHelper.login(user, pass)){

            Sesion sesionAuxiliar = null;
            boolean estaConectado = false;

            // Comprueba si el usuario tiene una sesión activa.
            for (Sesion s: getSesiones()){
                if(s.getUser().equals(user) && !s.isSesionCaducada()){
                    estaConectado = true;
                    sesionAuxiliar = s;
                    //TODO Eliminar las sesiones que esten caducadas.
                }
            }

            if (estaConectado){
                // Si el usuario tiene una sesión activa le pasamos la idSesion que tiene y una alerta para indicar que el usuario ya estaba conectado.
                respuesta = new RespuestaLogin(sesionAuxiliar.getIdSesion(), Alerta.Login.USER_IS_ALREADY_CONNECTED);
            } else {
                // Crea un id único.
                String idSesion = Lib.getUUID();
                // Crea la respuesta.
                respuesta = new RespuestaLogin(idSesion);
                // Añade el id de sesión al array de sesiones.
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
    // Siempre crea una nueva partida, exista o no una en curso.
    public String nuevoJuego(String json){
        // Cargamos la id de sesión que pasa el cliente.
        String idSesion = getGSON().fromJson(json, String.class);

        boolean encontrado = false;
        boolean isCaducado = false;

        /**
        Llevate esto al modelo Sesion. Intenta no recorrer todas las sesiones, computacionalmente es muy caro.
        Crea un nuevo metodo en Sesion.java que compruebe si existe la sesion con id idSesion, es más efectivo
        Para devolver los dos valores en una funcion: encontrado y isCaducado utliza codigos de error
        Posibles casos que yo entiendo: Sesion correcta y valida, Sesion correcta pero caducada, Sesion incorrecta
        Puede ser que olvide alguno, segun vuestros requerimientos
        IS_ENCONTRADO_OK = 0001
        IS_ENCONTRADO_IS_CADUCADO = 0002
        NO_ENCONTRADO = 0003
        ... Y lo que necesites. Tienes una clase para Errores, usala
        Y luego con un switch comprueba
        */
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
                // Si la sesión existe pero esta caducada envia un mensaje de error.
                respuesta = new RespuestaNuevoJuego(Error.Sesion.EXPIRED_SESSION);
            } else {
                // Si la sesion existe y NO esta caducada.
                // Busca la sesión.
                for (Sesion s: getSesiones()){
                    if (s.getIdSesion().equals(idSesion) && !s.isSesionCaducada()){
                        // Crea una partida. Devuelve el id único de la partida.
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
            respuesta = new RespuestaNuevoJuego(Error.Sesion.NOT_FOUND);
        }

        return getGSON().toJson(respuesta);
    }

    @GET
    @Path("/sortearInicio")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String sortearInicio(String json){
        // Separa la idSesion y la idPartida.
        String idSesionPartida = getGSON().fromJson(json, String.class);
        String idSesion = idSesionPartida.split(":")[0];
        String idPartida = idSesionPartida.split(":")[1];

        boolean sesionEncontrada = false;
        boolean partidaEncontrada = false;
        boolean sesionCaducada = false;
        // Comprueba si existe la sesión, esta activa o no y si existe la partida.
        for(Sesion s: getSesiones()){
            if (s.getIdSesion().equals(idSesion)){
                sesionEncontrada = true;
                if (s.isSesionCaducada()){
                    sesionCaducada = true;
                }
                if (s.getPartida().getIdPartida().equals(idPartida)){
                    partidaEncontrada = true;
                }
            }
        }

        RespuestaSorteo respuesta = null;
        Enums.PrimeroEnSacar empieza = null;
        if (sesionEncontrada){
            if (!sesionCaducada){
                if(partidaEncontrada){
                    for(Sesion s: getSesiones()){
                        if (s.getIdSesion().equals(idSesion) && !s.isSesionCaducada() && s.getPartida().getIdPartida().equals(idPartida)){
                            // Si los datos coinciden sortea quien empieza. El metodo devuelve un enum (Jugador o CPU).
                            empieza = s.getPartida().sortearInicio();

                            // Crea la respuesta a partir de quien empieza.
                            respuesta = new RespuestaSorteo(empieza);
                        }
                    }
                } else {
                    // Si la idPartida no existe devuelve un error.
                    respuesta = new RespuestaSorteo(Error.Partida.NO_ENCONTRADA);
                }
            } else {
                // Si la sesión esta caducada devuelve un error.
                respuesta = new RespuestaSorteo(Error.Sesion.EXPIRED_SESSION);
            }
        } else {
            // Si no se encuentra la sesión devuelve un error.
            respuesta = new RespuestaSorteo(Error.Sesion.NOT_FOUND);
        }

        return getGSON().toJson(respuesta);
    }

    @GET
    @Path("/jugarCarta")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String jugarCarta(String jsonEnvioJugarCarta){
        // Separamos la información.
        EnvioJugarCarta envioJugarCarta = getGSON().fromJson(jsonEnvioJugarCarta, EnvioJugarCarta.class);
        String idSesion = envioJugarCarta.getIdSesion();
        String idPartida = envioJugarCarta.getIdPartida();
        int idCarta = envioJugarCarta.getIdCarta();
        Enums.Caracteristica caracteristica = envioJugarCarta.getCaracteristica();

        RespuestaJugarCartaJugador respuesta = null;
        for(Sesion s: getSesiones()){
            if (s.getIdSesion().equals(idSesion) && !s.isSesionCaducada() && s.getPartida().getIdPartida().equals(idPartida)){
                respuesta = s.getPartida().compararCartaJugador(idCarta, caracteristica);
            }
        }

        return getGSON().toJson(respuesta);
    }

    @GET
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

}
