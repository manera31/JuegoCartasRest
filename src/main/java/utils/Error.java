package main.java.utils;

public abstract class Error {
    public abstract static class Login {
        public static final int USER_PASS_FAIL = -1;
    }
    public abstract static class Sesion {
        public static final int NOT_FOUND = -2;
        public static final int EXPIRED_SESSION = -3;
    }
    public abstract static class Partida {
        public static final int NO_ENCONTRADA = -4;
    }

}
