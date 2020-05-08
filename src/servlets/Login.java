package twi518.servlets;

import twi518.userXmlFactory.*;
import twi518.userXmlFactory.UserXml;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.net.*;

public class Login extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html PUBLIC \"-
                out.println("<html xmlns=\"http:
                        out.println("<title>Login</title>");
        out.println("<link href=\"style-sheets/stili.css\" rel=\"stylesheet\" type=\"text/css\" />");
        out.println(" </head><body>");
        UserXml ux = new UserXml();
        String[] account = ux.findUser(request.getParameter("username"), request.getParameter("password"));
        if (account[0].compareTo("0") != 0) {
            out.println("<h1> Login effettuato</h1> ");
            out.println("<p> <a href=\"index.jsp\"> Vai all'homepage </a> - ");
            out.println("<a href=\"jsp/categorie-elenco.jsp\">Gestione fonti</a></p>");
            Cookie u = new Cookie("username", request.getParameter("username"));
            response.addCookie(u);
        } else {
            out.println("<p> Username non esistente o password errata <br/>Accesso non effettuato</p>");
            out.println("<a href=\"index.jsp\">Rieffettua il Login </a>");
        }
        out.println("</body>");
        out.println("</html>");
        out.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    public String getServletInfo() {
        return "Short description";
    }
}
