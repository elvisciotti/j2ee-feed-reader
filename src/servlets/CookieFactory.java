package twi518.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.net.*;

public class CookieFactory {
    public CookieFactory() {
    }

    public String getUsername(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                String name = cookies[i].getName();
                if (name.equals("username"))
                    return cookies[i].getValue();
            }
        }
        return new String("");
    }

    public void setUsername(HttpServletResponse response, String username) {
        response.addCookie(new Cookie("username", username));
    }
}