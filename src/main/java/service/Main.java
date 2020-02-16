package main.java.service;

import com.google.gson.Gson;
import main.java.modelos.Carta;
import main.java.modelos.Partida;
import main.java.modelos.Sesion;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

@Path("/mirest")
public class Main extends ResourceConfig {

    public static final String USER_PASS_FAIL = "-1";
    public static final String IS_ALREADY_CONNECTED = "-2";
    public static final String NOT_FOUND = "-3";
    public static final String EXPIRED_SESSION = "-4";

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

        String idSesion;
        boolean estaConectado = false;
        if (MySQLHelper.login(user, pass)){
            for (Sesion s: getSesiones()){
                if(s.getUser().equals(user) && !s.isSesionCaducada()){
                    estaConectado = true;
                }
            }
            if (estaConectado){
                idSesion = IS_ALREADY_CONNECTED;
            } else {
                idSesion = Lib.getUUID();
                getSesiones().add(new Sesion(idSesion, user));
            }

        } else {
            idSesion = USER_PASS_FAIL;
        }

        return getGSON().toJson(idSesion);

    }

    @GET
    @Path("/newGame")
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


        String idGame;
        if (encontrado){
            if (isCaducado){
                idGame = EXPIRED_SESSION;
            } else {
                idGame = Lib.getUUID();
                for (Sesion s: getSesiones()){
                    if (s.getIdSesion().equals(idSesion) && !s.isSesionCaducada()){
                        s.setIdGame(idGame);
                    }
                }

            }
        } else {
            idGame = NOT_FOUND;
        }

        return getGSON().toJson(idGame);
    }

    /*private void setSesionTimeout(final String sesion){
        Thread salida = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(MINUTOS_TIMEOUT_SESION * 60000);
                    for (Sesion s: getSesiones()){
                        if(s.getIdSesion().equals(sesion)){
                            s.setSesionCaducada(true);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        salida.start();
    }*/

    private Object repartirCartas(){
        ArrayList<Carta> cartasJugador = new ArrayList<>();
        ArrayList<Carta> cartasCPU = new ArrayList<>();

        ArrayList<Carta> cartaArrayList = getCartas();
        Carta[] cartas1 = new Carta[cartaArrayList.size()];
        for (int i = 0 ; i < cartas1.length ; i++){
            cartas1[i] = cartaArrayList.get(i);
        }


        int contadorCartas = cartas1.length-1;
        int posicion;
        Carta cartaRandom, ultima;
        for (int i = 0 ; i < 6 ; i++){
            posicion = Lib.getRandom(contadorCartas, 0);
            cartaRandom = cartas1[posicion];
            cartasJugador.add(cartaRandom);

            ultima = cartas1[contadorCartas];

            cartas1[contadorCartas] = cartaRandom;
            cartas1[posicion] = ultima;

            contadorCartas--;
        }

        for (int i = 0 ; i < 6 ; i++){
            posicion = Lib.getRandom(contadorCartas, 0);
            cartaRandom = cartas1[posicion];
            cartasCPU.add(cartaRandom);

            ultima = cartas1[contadorCartas];

            cartas1[contadorCartas] = cartaRandom;
            cartas1[posicion] = ultima;

            contadorCartas--;
        }

        return new Object[]{cartasJugador, cartasCPU};
    }



    @GET
    @Path("/carta")
    @Produces(MediaType.APPLICATION_JSON)
    public String getCartass(){
        //int i = MySQLHelper.addJugador("joanet31", "Joan", "Manera Perez", "123456789");
        return getGSON().toJson(repartirCartas());
    }

    @GET
    @Path("/uuid")
    @Produces(MediaType.APPLICATION_JSON)
    public String getUUIDJson(){
        return getGSON().toJson(Lib.getUUID());
    }


}
