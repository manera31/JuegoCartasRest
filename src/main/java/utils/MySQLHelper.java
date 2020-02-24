package main.java.utils;

import main.java.modelos.Carta;
import main.java.modelos.ManoJugada;
import main.java.modelos.ResultadoPartida;
import main.java.modelos.Usuario;

import java.sql.*;
import java.util.ArrayList;

/**
 * Clase para controlar las entradas y las salidas de la base de datos.
 * @author Joan Manera Perez
 */
public class MySQLHelper {
    private String driver = "com.mysql.cj.jdbc.Driver";
    private String database = "autocartas";
    private String hostname = "localhost";
    private String port = "3306";
    private String url = "jdbc:mysql://" + hostname + ":" + port + "/" + database + "?useSSL=false";
    private String username = "root";
    private String password = "";
    private static Connection connection = null;
    private static ArrayList<Carta> cartas = null;
    /*
    TABLE cartas(id_carta integer PRIMARY KEY, marca varchar(50), modelo varchar(50), motor integer, potencia integer, velocidad integer, cilindros integer, rpm integer, consumo float);
    TABLE jugadores(nombre_usuario varchar(50) PRIMARY KEY, nombre varchar(50), appellidos varchar (50), pass varchar(100));
    TABLE partidas(id_partida varchar(50) PRIMARY KEY, jugador varchar(50), puntos_jugador integer, puntos_cpu integer, fecha_partida timestamp, FOREIGN KEY (jugador) REFERENCES jugadores (nombre_usuario));
    TABLE manos_jugadas(id_carta1 integer, id_carta2 integer, caracteristica varchar(50), id_carta_ganadora integer, FOREIGN KEY (id_carta1) REFERENCES cartas (id_carta), FOREIGN KEY (id_carta2) REFERENCES cartas (id_carta), FOREIGN KEY (id_carta_ganadora) REFERENCES cartas (id_carta));
    */

    /**
     * Constructor de la clase.
     */
    private MySQLHelper(){
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Conexión a la base de datos Singleton.
     * @return
     */
    public static Connection getConnection(){
        if (connection == null){
            new MySQLHelper();
        }
        return connection;
    }

    /**
     * Comprueba el usuario y la contraseña en la base de datos.
     * @param user
     * @param pass
     * @return usuario/null
     */
    public static Usuario login(String user, String pass){
        final String query2 = "SELECT * FROM jugadores WHERE nombre_usuario = ? AND pass = ?";
        Usuario usuario = null;
        PreparedStatement preparedStatement;

        try {
            preparedStatement = getConnection().prepareStatement(query2);
            preparedStatement.setString(1, user);
            preparedStatement.setString(2, pass);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.first();

            usuario = new Usuario(resultSet.getString("nombre_usuario")
                    , resultSet.getString("nombre")
                    , resultSet.getString("apellidos"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuario;
    }

    /**
     * Crea un usuario en la base de datos.
     * @param usuario
     * @return codigoError
     */
    public static int addUsuario(Usuario usuario){
        final String query = " INSERT INTO jugadores (nombre_usuario, nombre, apellidos, pass) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement;

        try {
            preparedStatement = getConnection().prepareStatement(query);
            preparedStatement.setString(1, usuario.getNick());
            preparedStatement.setString(2, usuario.getNombre());
            preparedStatement.setString(3, usuario.getApellidos());
            preparedStatement.setString(4, usuario.getPass());
            preparedStatement.execute();
            return Control.CRUD.USUARIO_ANADIDO;
        } catch (SQLIntegrityConstraintViolationException sqle){
            sqle.printStackTrace();
            return Control.CRUD.USUARIO_EXISTE;
        } catch (SQLException e) {
            e.printStackTrace();
            return Control.CRUD.USUARIO_FALLO_GENERAL;
        }
    }

    /**
     * Busca las cartas de la base de datos.
     * @return cartas
     */
    public static ArrayList<Carta> getCartas(){
        if (cartas == null) {
            cartas = new ArrayList<>();

            PreparedStatement preparedStatement;
            try {
                preparedStatement = getConnection().prepareStatement("SELECT * FROM cartas");
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next())
                    cartas.add(new Carta(rs.getInt("id_carta"), rs.getString("marca"), rs.getString("modelo"),
                            rs.getInt("motor"), rs.getInt("potencia"), rs.getInt("velocidad"),
                            rs.getInt("cilindros"), rs.getInt("rpm"), rs.getDouble("consumo")));
                rs.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return cartas;
    }

    /**
     * Busca una carta entre todas las cartas.
     * @param idABuscar
     * @return carta
     */
    public static Carta buscarCarta(int idABuscar){

        for (Carta c: getCartas()){
            if (c.getId() == idABuscar){
                return c;
            }
        }

        return null;

    }

    /**
     * Inserta la mano jugada en la base de datos.
     * @param idCarta1
     * @param idCarta2
     * @param caracteristica
     * @param idCartaGanadora
     */
    public static void insertarManoJugada(int idCarta1, int idCarta2, Enums.Caracteristica caracteristica, int idCartaGanadora){
        final String query = " INSERT INTO manos_jugadas (id_carta1, id_carta2, caracteristica, id_carta_ganadora) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement;

        try {
            preparedStatement = getConnection().prepareStatement(query);
            preparedStatement.setInt(1, idCarta1);
            preparedStatement.setInt(2, idCarta2);
            preparedStatement.setString(3, caracteristica.name());
            preparedStatement.setInt(4, idCartaGanadora);
            preparedStatement.execute();
        } catch (SQLIntegrityConstraintViolationException sqle){
            sqle.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inserta el resultado de la partida en la base de datos.
     * @param idPartida
     * @param nickJugador
     * @param puntosJugador
     * @param puntosCPU
     */
    public static void insertarResultadoPartida(String idPartida, String nickJugador, int puntosJugador, int puntosCPU ){

        final String query = " INSERT INTO partidas (id_partida, jugador, puntos_jugador, puntos_cpu) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement;

        try {
            preparedStatement = getConnection().prepareStatement(query);
            preparedStatement.setString(1, idPartida);
            preparedStatement.setString(2, nickJugador);
            preparedStatement.setInt(3, puntosJugador);
            preparedStatement.setInt(4, puntosCPU);
            preparedStatement.execute();
        } catch (SQLIntegrityConstraintViolationException sqle){
            sqle.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return
     */
    public static ArrayList<ResultadoPartida> getResultadosPartidas(){
        ArrayList<ResultadoPartida> resultados = new ArrayList<>();
        PreparedStatement preparedStatement;
        try {
            preparedStatement = getConnection().prepareStatement("SELECT * FROM partidas");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next())
                resultados.add(new ResultadoPartida(rs.getString("id_partida"), rs.getString("jugador"), rs.getInt("puntos_jugador"), rs.getInt("puntos_cpu"), rs.getDate("fecha_partida")));
            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultados;
    }

    public static ArrayList<ManoJugada> getManosJugadas(){
        ArrayList<ManoJugada> manosJugadas = new ArrayList<>();
        PreparedStatement preparedStatement;
        try {
            preparedStatement = getConnection().prepareStatement("SELECT id_carta1, id_carta2, id_carta_ganadora FROM manos_jugadas");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next())
                manosJugadas.add(new ManoJugada(rs.getInt("id_carta1"), rs.getInt("id_carta2"), rs.getInt("id_carta_ganadora")));
            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return manosJugadas;
    }
}
