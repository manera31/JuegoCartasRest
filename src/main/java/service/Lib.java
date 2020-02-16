package main.java.service;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Random;
import java.util.UUID;

public class Lib {

    public static String encryptPassword(String password) {
        String sha1 = "";
        try
        {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(password.getBytes("UTF-8"));
            sha1 = byteToHex(crypt.digest());
        }
        catch(NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch(UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return sha1;
    }

    public static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    @Deprecated
    public static double getDoubleUUID(){
        return Double.parseDouble(String.format("%040d", new BigInteger(UUID.randomUUID().toString().replace("-", ""), 16)));
    }

    public static int getRandom(int max, int min){
        Random rdn = new Random();
        return rdn.nextInt(max - min + 1) + min;
    }
}
