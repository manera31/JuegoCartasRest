package main.java.utils;

/**
 * Clase abstracta para controlar los posibles códigos de errores.
 * @author Joan Manera Perez
 */
public abstract class Control {
    /**
     * Controla los estados de la sesión.
     * @author Joan Manera Perez
     */
    public abstract static class Sesion {
        public static final int ENCONTRADA = -1;
        public static final int NO_ENCONTRADA = -2;
        public static final int CADUCADA = -3;
    }

    /**
     * Controla los estados de la partida.
     * @author Joan Manera Perez
     */
    public abstract static class Partida {
        public static final int ENCONTRADA = -11;
        public static final int NO_ENCONTRADA = -12;
    }

    /**
     * Controla los estado de login.
     * @author Joan Manera Perez
     */
    public abstract static class Login {
        public static final int ENCONTRADO = -21;
        public static final int YA_CONECTADO = -22;
        public static final int USUARIO_PASS_INCORRECTO = -23;
    }

    /**
     * Controla los estados de crud.
     * @author Joan Manera Perez
     */
    public abstract static class CRUD {
        public static final int USUARIO_ANADIDO = -31;
        public static final int USUARIO_EXISTE = -32;
        public static final int USUARIO_FALLO_GENERAL = -33;
    }
}
