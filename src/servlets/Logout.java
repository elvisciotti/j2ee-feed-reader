package twi518.servlets;

import java.io.*;
import java.net.*;

import javax.servlet.*;
import javax.servlet.http.*;

/** Servlet che cancella il cookie username e redirige su index.jsp
 * @author Elvis
 */
public class Logout extends HttpServlet {
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
	response.setContentType("text/html;charset=UTF-8");
	PrintWriter out = response.getWriter();
	Cookie username = new Cookie("username","");
	username.setMaxAge(0);
	response.addCookie(username);
	String address = "index.jsp";
	response.sendRedirect(address);
	/* TODO output your page here
	out.println("<html>");
	out.println("<head>");
	out.println("<title>Servlet Logout</title>");
	out.println("</head>");
	out.println("<body>");
	out.println("<h1>Servlet Logout at " + request.getContextPath () + "</h1>");
	out.println("</body>");
	out.println("</html>");
	 */
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
