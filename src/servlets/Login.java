package twi518.servlets;

import twi518.userXmlFactory.*;
import twi518.userXmlFactory.UserXml;
import java.io.*;
import java.net.*;

import javax.servlet.*;
import javax.servlet.http.*;

/** Servlet che cerca l'utente e setta il cookie se esiste
 * @author Elvis
 */
public class Login extends HttpServlet {
    
    /** 
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
        out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\"><head>");
        out.println("<title>Login</title>");
        out.println("<link href=\"style-sheets/stili.css\" rel=\"stylesheet\" type=\"text/css\" />");
        out.println(" </head><body>");
        
        UserXml ux = new UserXml();
        //out.print("["+ux.errore);
       
        String[] account = ux.findUser(request.getParameter("username"),request.getParameter("password"));
     
        if(account[0].compareTo("0") != 0){
            //out.println("<p> Login effettuato <p> ");
            
            out.println("<h1> Login effettuato</h1> ");
            //out.println("<h4>"+account[0]+" "+account[1]+"</h4> ");
            out.println("<p> <a href=\"index.jsp\"> Vai all'homepage </a> - ");
             out.println("<a href=\"jsp/categorie-elenco.jsp\">Gestione fonti</a></p>");
            //out.println("<p> Benvenuto "+ account[0] + " " + account[1] + "!<p>");
            //out.println("<a href=\"jsp/categorie-elenco.jsp\">Vai a elenco categorie feeds</a>");
            Cookie u = new Cookie("username", request.getParameter("username") );
            response.addCookie(u);
        }else{
            out.println("<p> Username non esistente o password errata <br/>Accesso non effettuato</p>");
            out.println("<a href=\"index.jsp\">Rieffettua il Login </a>");
        }
        
        out.println("</body>");
        out.println("</html>");
        
        out.close();
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>
}
