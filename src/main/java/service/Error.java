package main.java.service;

public abstract class Error {
    public class User{
        public static final String USER_PASS_FAIL = "-1";
        public static final String IS_ALREADY_CONNECTED = "-2";
    }
    public class Session{
        public static final String NOT_FOUND = "-3";
        public static final String EXPIRED_SESSION = "-4";
    }

}
