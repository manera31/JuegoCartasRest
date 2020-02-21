package main.java.service;

import com.google.gson.Gson;
import main.java.modelos.Carta;
import main.java.modelos.Sesion;
import main.java.modelos.Usuario;
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

    @POST
    @Path("/login")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String login(String json){

        // Separa el usuario y la contraseña que el clente ha pasado.
        String userPass = getGSON().fromJson(json, String.class);
        String user = userPass.split(":")[0];
        String pass = userPass.split(":")[1];

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
                respuesta = new RespuestaLogin(sesionAuxiliar.getIdSesion(), usuario, Alerta.Login.YA_CONECTADO);
            } else {
                // Crea un id único.
                String idSesion = Lib.getUUID();
                // Crea la respuesta.
                respuesta = new RespuestaLogin(idSesion, usuario, Acierto.Login.ENCONTRADO);
                // Añade el id de sesión al array de sesiones.
                getSesiones().add(new Sesion(idSesion, usuario));
            }

        } else {
            // Si no coincide el usuario y la contraseña en la base de datos envia un mensaje de error.
            respuesta = new RespuestaLogin(Error.Login.USUARIO_PASS_INCORRECTO);
        }

        return getGSON().toJson(respuesta);
    }

    @POST
    @Path("/crearJuego")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    // Siempre crea una nueva partida, exista o no una en curso.
    public String nuevoJuego(String json){
        // Cargamos la id de sesión que pasa el cliente.
        String idSesion = getGSON().fromJson(json, String.class);

        int respuestaBusqueda = comprobarSesion(idSesion);

        String idPartida;
        ArrayList<Carta> cartasJugador;
        RespuestaNuevoJuego respuesta = null;

        switch (respuestaBusqueda){
            case Error.Sesion.CADUCADA:
                // Si la sesión existe pero esta caducada envia un mensaje de error.
                respuesta = new RespuestaNuevoJuego(Error.Sesion.CADUCADA);
                break;
            case Error.Sesion.NO_ENCONTRADA:
                // Si la sesion no existe evia un mensaje de error.
                respuesta = new RespuestaNuevoJuego(Error.Sesion.NO_ENCONTRADA);
                break;
            case Acierto.Sesion.ENCONTRADO:
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
                break;
        }

        return getGSON().toJson(respuesta);
    }

    @POST
    @Path("/sortearInicio")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String sortearInicio(String json){
        // Separa la idSesion y la idPartida.
        String idSesionPartida = getGSON().fromJson(json, String.class);
        String idSesion = idSesionPartida.split(":")[0];
        String idPartida = idSesionPartida.split(":")[1];

        int respuestaBusqueda = comprobarPartida(idSesion, idPartida);

        RespuestaSorteo respuesta = null;
        Enums.PrimeroEnSacar empieza = null;

        switch (respuestaBusqueda){
            case Error.Sesion.CADUCADA:
                // Si la sesión existe pero esta caducada envia un mensaje de error.
                respuesta = new RespuestaSorteo(Error.Sesion.CADUCADA);
                break;
            case Error.Sesion.NO_ENCONTRADA:
                // Si la sesion no existe evia un mensaje de error.
                respuesta = new RespuestaSorteo(Error.Sesion.NO_ENCONTRADA);
                break;
            case Error.Partida.NO_ENCONTRADA:
                respuesta = new RespuestaSorteo(Error.Partida.NO_ENCONTRADA);
                break;
            case Acierto.Partida.ENCONTRADA:
                for(Sesion s: getSesiones()){
                    if (s.getIdSesion().equals(idSesion) && !s.isSesionCaducada() && s.getPartida().getIdPartida().equals(idPartida)){
                        // Si los datos coinciden sortea quien empieza. El metodo devuelve un enum (Jugador o CPU).
                        empieza = s.getPartida().sortearInicio();

                        // Crea la respuesta a partir de quien empieza.
                        respuesta = new RespuestaSorteo(empieza);
                    }
                }
                break;
        }

        return getGSON().toJson(respuesta);
    }

    @POST
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
                return Error.Sesion.CADUCADA;
            } else {
                return Acierto.Sesion.ENCONTRADO;
            }
        } else {
            return Error.Sesion.NO_ENCONTRADA;
        }
    }
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
                return Error.Sesion.CADUCADA;
            } else {
                if (partidaEncontrada){
                    return Acierto.Partida.ENCONTRADA;
                } else {
                    return Error.Partida.NO_ENCONTRADA;
                }
            }
        } else {
            return Error.Sesion.NO_ENCONTRADA;
        }
    }


}
