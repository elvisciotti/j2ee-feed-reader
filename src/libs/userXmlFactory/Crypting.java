package twi518.userXmlFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
/**
 * Applica md5 alle password 
 * @author Elvis
 */
public class Crypting {

    /** Creates a new instance of Crypting */
    private MessageDigest md5 = null;
    /**
     *  Password da criptare
     */
    private String password = null;
    
    /**
     *  Crea una nuova istanza di PasswordEncryption
     *  inizializzando il MessageDigest
     *  @param pwd Password da criptare
     */
    public Crypting(){
        try{
            md5 = MessageDigest.getInstance("MD5");
        } 
	catch (NoSuchAlgorithmException nsae) {
            nsae.printStackTrace();
        }
    }
    
    /**
     *  Cripta la password dell'utente convertendo
     *  la password in bytes e quest'ultimi in esadecimale
     */
    public String getMd5(String pwd){
        password = pwd;
        //  Aggiorna il digest usando l'array di bytes ottenuto dalla password
        md5.update(password.getBytes());
        
        //  Calcola l'hash sull'array di bytes precedentemente creato
        //  e restituisce un array di hash
        byte[] digest = md5.digest();
        
        //  Creo una StringBuffer per appendere tutti i byte in hash
        //  convertiti in formato decimale
        StringBuffer stringbuffer = new StringBuffer();
        
        for (int i = 0; i < digest.length; i++){
            //  Converto ogni byte di digest in formato decimale e poi in
            //  stringa cos� da poterlo "appendere" al buffer.
            stringbuffer.append(Integer.toString(0xFF & digest[i]));
        }
        
        //  Restituisco il buffer in formato Stringa
        return stringbuffer.toString();
    }
}
