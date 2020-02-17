package main.java.service;

import main.java.modelos.Carta;

import java.sql.*;
import java.util.ArrayList;

public class MySQLHelper {
    private String driver = "com.mysql.cj.jdbc.Driver";
    private String database = "autocartas";
    private String hostname = "localhost";
    private String port = "3306";
    private String url = "jdbc:mysql://" + hostname + ":" + port + "/" + database + "?useSSL=false";
    private String username = "root";
    private String password = "";
    private static Connection connection = null;
    /*
    TABLE cartas(id_carta integer PRIMARY KEY, marca varchar(50), modelo varchar(50), motor integer, potencia integer, velocidad integer, cilindros integer, rpm integer, consumo float);
    TABLE jugadores(nombre_usuario varchar(50) PRIMARY KEY, nombre varchar(50), appellidos varchar (50), pass varchar(100));
    TABLE partidas(id_game FLOAT(50) PRIMARY KEY, jugador varchar(50), puntos_jugador integer, puntos_cpu integer, fecha_partida timestamp, FOREIGN KEY (jugador) REFERENCES jugadores (nombre_usuario));
    TABLE manos_jugadas(id_carta1 varchar(50), id_carta2 varchar(50), caracteristica varchar(50), FOREING KEY id_carta1 REFERENCES jugadores (nombre_usuario), FOREING KEY id_carta2 REFERENCES jugadores (nombre_usuario);
     */

    private MySQLHelper(){
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection(){
        if (connection == null){
            connection = new MySQLHelper();
        }
        return connection;
    }

    public static boolean login(String user, String pass){
        PreparedStatement preparedStatement;
        boolean isCorrecto = false;
        try {
            /* Comprueba siempre contra BDD */
            /* La pass siempre te llega en SHA1 desde el cliente */
            /* SELECT COUNT(*) FROM jugadores WHERE nombre_usuario = '[NOMBRE]' and pass = '[EN_SHA1]' */
            /* Obten el total y a partir de ahi sigues */
            preparedStatement = getConnection().prepareStatement("SELECT * FROM jugadores WHERE nombre_usuario = '" + user + "'");
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.first();
            String passSHA1 = resultSet.getString("pass");
            if(passSHA1.equals(Lib.encryptPassword(pass))){
                isCorrecto = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return isCorrecto;
    }

    public static int addJugador(String nombreUsuario, String nombre, String apellidos, String password){
        /* Comprueba si existe el nombre de usuario, si no el login no funcionara bien */
        final String query = " INSERT INTO jugadores VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement;
        boolean isCorrecto = false;

        try {
            preparedStatement = getConnection().prepareStatement(query);
            preparedStatement.setString(1, nombreUsuario);
            preparedStatement.setString(2, nombre);
            preparedStatement.setString(3, apellidos);
            preparedStatement.setString(4, Lib.encryptPassword(password));
            preparedStatement.execute();
            return 0; /* Coge los errores de una clase diferente si no te montarás un lio */
        } catch (SQLIntegrityConstraintViolationException sqle){
            sqle.printStackTrace();
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static Carta buscarCarta(int idABuscar){
        Carta carta = null;
        PreparedStatement preparedStatement;
        try {
            /* El uso de PreparedStatement aqui no tiene logica */
            /* Facilitas la injeccion SQL */
            /*
            preparedStatement = getConnection().prepareStatement("SELECT * FROM cartas WHERE id_carta = ? ");
            */
            preparedStatement = getConnection().prepareStatement("SELECT * FROM cartas WHERE id_carta = "+idABuscar);
            ResultSet rs = preparedStatement.executeQuery();
            rs.first();
            carta = new Carta(rs.getInt("id_carta"), rs.getString("marca"), rs.getString("modelo"),
                    rs.getInt("motor"), rs.getInt("potencia"), rs.getInt("velocidad"),
                    rs.getInt("cilindros"), rs.getInt("rpm"), rs.getDouble("consumo"));
            rs.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return carta;

    }

    public static ArrayList<Carta> getCartas(){
        ArrayList<Carta> cartas = new ArrayList<>();

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

        return cartas;
    }
}
