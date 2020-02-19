package main.java.utils;

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
    private static ArrayList<Carta> cartas = null;
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
            new MySQLHelper();
        }
        return connection;
    }

    public static boolean login(String user, String pass){
        final String query2 = "SELECT CASE WHEN EXISTS(SELECT * FROM jugadores WHERE nombre_usuario = ? AND pass = ?) THEN 1 ELSE 0 END AS result FROM jugadores";
        PreparedStatement preparedStatement;
        boolean isCorrecto = false;
        try {
            preparedStatement = getConnection().prepareStatement(query2);
            preparedStatement.setString(1, user);
            preparedStatement.setString(2, Lib.encryptPassword(pass));
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.first();

            int result = resultSet.getInt("result");
            if (result == 1){
                isCorrecto = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isCorrecto;
    }

    public static int addJugador(String nombreUsuario, String nombre, String apellidos, String password){
        final String query = " INSERT INTO jugadores (nombre_usuario, nombre, apellidos, pass) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement;
        boolean isCorrecto = false;

        try {
            preparedStatement = getConnection().prepareStatement(query);
            preparedStatement.setString(1, nombreUsuario);
            preparedStatement.setString(2, nombre);
            preparedStatement.setString(3, apellidos);
            preparedStatement.setString(4, Lib.encryptPassword(password));
            preparedStatement.execute();
            return 0;
        } catch (SQLIntegrityConstraintViolationException sqle){
            sqle.printStackTrace();
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

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

    public static Carta buscarCarta(int idABuscar){

        for (Carta c: getCartas()){
            if (c.getId() == idABuscar){
                return c;
            }
        }

        return null;

    }
}
