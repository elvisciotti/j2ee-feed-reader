package twi518.servlets;

import twi518.NewsXmlFactory.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Operazioni extends HttpServlet {
    String linkElencoCat = "<a href=\"jsp/categorie-elenco.jsp\">Torna all'elenco categorie</a>";
    String opOk = "<h2>Operazione eseguita correttamente</h2>";
    String opErr = "<h2>Operazione <b>NON</b> eseguita !</h2>";

    public String linkElencoFeeds(String idCat) {
        return new String("<a href=\"jsp/feed-elenco.jsp?idcat=" + idCat + "\">Torna all'elenco feeds</a>");
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
        out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\"><head>");
        out.println("<title>Operazioni Servlet</title>");
        out.println("</head><body>");
        if (request.getParameterNames().hasMoreElements()) {
            String azione = request.getParameter("azione");
            String username = request.getParameter("username");
            NewsXmlFactory nxf = new NewsXmlFactory(username + ".xml");
            if (azione.equals("inseriscicategoria")) {
                if (request.getParameter("nome").equals("")) {
                    out.println("E' necessario specificare un nome per la categoria" + opErr + linkElencoCat);
                } else {
                    String note = request.getParameter("note");
                    if (note == null)
                        note = new String("");
                    nxf.aggiungiCat(convertiAccenti(request.getParameter("nome")), note);
                    nxf.salvaXml();
                    nxf.loadXml();
                    out.println(opOk + nxf.getErr() + linkElencoCat);
                }
            } else if (azione.equals("cancellacategoria")) {
                if (nxf.cancellaCat(request.getParameter("id"))) {
                    out.println("Categoria cancellata" + opOk);
                    nxf.salvaXml();
                } else
                    out.println("Categoria non esistente" + opErr);
                out.println(linkElencoCat);
            } else if (azione.equals("modificacategoria")) {
                if (nxf.modificaCat(request.getParameter("idcat"),
                        convertiAccenti(request.getParameter("nuovonome")),
                        convertiAccenti(request.getParameter("nuovanota")))) {
                    out.println("Categoria modificata" + opOk);
                    nxf.salvaXml();
                } else
                    out.println("Categoria non esistente" + opErr);
                out.println(linkElencoCat);
            } else if (azione.equals("inseriscifeed")) {
                String[] urls = new String[]
                        {
                                request.getParameter("link2"),
                                request.getParameter("link3"),
                                request.getParameter("link4")
                        };
                String idCat = request.getParameter("idcat");
                if (nxf.aggiungiFeed(idCat,
                        convertiAccenti(request.getParameter("nome")),
                        request.getParameter("urlprim"),
                        urls)) {
                    out.println("Feed aggiunto" + opOk);
                    nxf.salvaXml();
                } else
                    out.println("Impossibile aggiungere il feed" + opErr);
                out.println(linkElencoFeeds(idCat));
            } else if (azione.equals("cancellafeed")) {
                String idCat = request.getParameter("idcat");
                if (nxf.cancellaFeed(idCat,
                        request.getParameter("idfeed"))) {
                    out.println("Feed cancellato" + opOk);
                    nxf.salvaXml();
                } else
                    out.println("Impossibile cancellare il feed" + opErr);
                out.println(linkElencoFeeds(idCat));
            } else if (azione.equals("modificafeed")) {
                String idCat = request.getParameter("idcat");
                String[] urls = new String[]
                        {
                                request.getParameter("link2"),
                                request.getParameter("link3"),
                                request.getParameter("link4")
                        };
                if (nxf.modificaFeed(idCat,
                        request.getParameter("idfeed"),
                        convertiAccenti(request.getParameter("nome")),
                        request.getParameter("urlprim"),
                        urls)) {
                    out.println("Feed modificato" + opOk);
                    nxf.salvaXml();
                } else
                    out.println("Impossibile modificare il feed" + opErr);
                out.println(linkElencoFeeds(idCat));
            } else {
                out.println("Operazione non valida!" + linkElencoCat);
            }
        } else {
            out.println("<head>\n<title>Servlet Operazioni</title></head>");
            out.println("<body>Nessuna azione specificata");
        }
        out.println("</body></html>");
        out.close();
    }

    public String convertiAccenti(String in) {
        return in.replace("�", "a'").replace("�", "e'").replace("�", "e'").replace("�", "i'").replace("�", "o'").replace("�", "u'");
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
