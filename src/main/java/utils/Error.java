package main.java.utils;

public abstract class Error {
    public abstract static class Login {
        public static final int USER_PASS_FAIL = -1;
        public static final int USER_IS_ALREADY_CONNECTED = -2;
    }
    public abstract static class Session{
        public static final int NOT_FOUND = -3;
        public static final int EXPIRED_SESSION = -4;
    }

}
