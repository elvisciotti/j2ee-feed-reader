package twi518.servlets;

import java.io.*;
import java.net.*;

import javax.servlet.*;
import javax.servlet.http.*;

/** Classe per lettura cookie
 * @author Elvis
 */
public class CookieFactory {
    
    /** Creates a new instance of CookieFactory */
    public CookieFactory() {
    }
    
    /** ritorna valore del cookie username o stringa vuota se non esiste*/
    public String getUsername(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies!=null) {
            for (int i=0; i<cookies.length; i++) {
                String name = cookies[i].getName();
                if (name.equals("username"))
                    return cookies[i].getValue();
            }
        }
        //nessun match -> ritorno str vuota
        return new String("");
    }
    
    /** setta cookie username con il valore passato */
    public void setUsername(HttpServletResponse response, String username){
        response.addCookie(new Cookie("username",username));
    }
}