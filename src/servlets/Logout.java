package twi518.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.net.*;

public class Logout extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Cookie username = new Cookie("username", "");
        username.setMaxAge(0);
        response.addCookie(username);
        String address = "index.jsp";
        response.sendRedirect(address);
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
