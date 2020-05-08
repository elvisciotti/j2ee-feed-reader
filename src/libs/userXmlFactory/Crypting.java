package twi518.userXmlFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;


public class Crypting {
    
    private MessageDigest md5 = null;
    
    private String password = null;

    
    public Crypting() {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException nsae) {
            nsae.printStackTrace();
        }
    }

    public String getMd5(String pwd) {
        password = pwd;
        md5.update(password.getBytes());
        byte[] digest = md5.digest();
        StringBuffer stringbuffer = new StringBuffer();
        for (int i = 0; i < digest.length; i++) {
            stringbuffer.append(Integer.toString(0xFF & digest[i]));
        }
        return stringbuffer.toString();
    }
}
