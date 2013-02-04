package twi518.servlets;

import twi518.NewsXmlFactory.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

/** Servlet operazioni di scrittura dati lato server da parte dell'utente <br />
 * in base al valore del parametro azione, vengono effettuate le varie operazioni e stampati
 * i messaggi di conferma/errore e link di ritorno<br />
 * Stampe in xhtml (validato)
 * @author Gruppo 518  Elvis Ciotti (elvisciotti@gmail.com), Ugo Ceccarelli(ugo.ceccarelli@hotmail.it)
 */  
public class Operazioni extends HttpServlet {
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    String linkElencoCat = "<a href=\"jsp/categorie-elenco.jsp\">Torna all'elenco categorie</a>";
    String opOk = "<h2>Operazione eseguita correttamente</h2>";
    String opErr = "<h2>Operazione <b>NON</b> eseguita !</h2>";
    
    /** stringa link per elenco feeds */
    public String linkElencoFeeds(String idCat) {
        return new String("<a href=\"jsp/feed-elenco.jsp?idcat="+idCat+"\">Torna all'elenco feeds</a>");
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
        out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\"><head>");
        out.println("<title>Operazioni Servlet</title>");
        out.println("</head><body>");
       
        // se ci sono dati in input
        if ( request.getParameterNames().hasMoreElements()) {
            String azione = request.getParameter("azione");
            String username = request.getParameter("username");
            //nei posting il nome � sempre passato
            NewsXmlFactory nxf = new NewsXmlFactory(username+ ".xml");
            
            if (azione.equals("inseriscicategoria")) {
                if (request.getParameter("nome").equals("")) {
                    out.println("E' necessario specificare un nome per la categoria" + opErr + linkElencoCat);
                } else {
                    String note = request.getParameter("note");
                    if (note==null)
                        note=new String("");
                    nxf.aggiungiCat( convertiAccenti(request.getParameter("nome")) , note );
                    nxf.salvaXml();
                    nxf.loadXml();
                    out.println(opOk + nxf.getErr() + linkElencoCat);
                }
                
            } else if (azione.equals("cancellacategoria")) {
                
                if (nxf.cancellaCat( request.getParameter("id") )) {
                    out.println("Categoria cancellata" + opOk );
                    nxf.salvaXml();
                } else
                    out.println("Categoria non esistente" + opErr);
                out.println(linkElencoCat);
                
            } else if (azione.equals("modificacategoria")) {
                //NewsXmlFactory nxf = new NewsXmlFactory(nomeFileXml);
                if (nxf.modificaCat( request.getParameter("idcat"),
                        convertiAccenti(request.getParameter("nuovonome")),
                        convertiAccenti(request.getParameter("nuovanota")) )) {
                    out.println("Categoria modificata" + opOk );
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
                
                /*out.println(convertiAccenti(request.getParameter("nome")) +
                        request.getParameter("urlprim") );*/
                
                if (nxf.aggiungiFeed( idCat ,
                        convertiAccenti(request.getParameter("nome")),
                        request.getParameter("urlprim"),
                        urls)) {
                    out.println("Feed aggiunto" + opOk );
                    
                    nxf.salvaXml();
                } else
                    out.println("Impossibile aggiungere il feed" + opErr);
                
                out.println(linkElencoFeeds(idCat));
                
            }else if (azione.equals("cancellafeed")) {
                String idCat = request.getParameter("idcat");
                if (nxf.cancellaFeed(idCat,
                        request.getParameter("idfeed"))) {
                    out.println("Feed cancellato" + opOk );
                    nxf.salvaXml();
                } else
                    out.println("Impossibile cancellare il feed" + opErr);
                
                out.println(linkElencoFeeds(idCat));
                
            }else if (azione.equals("modificafeed")) {
                String idCat = request.getParameter("idcat");
                
                String[] urls = new String[]
                {
                    request.getParameter("link2"),
                    request.getParameter("link3"),
                    request.getParameter("link4")
                };
                
                if ( nxf.modificaFeed(idCat,
                        request.getParameter("idfeed"),
                        convertiAccenti(request.getParameter("nome")),
                        request.getParameter("urlprim"),
                        urls) ) {
                    out.println("Feed modificato" + opOk );
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
    
    /** converte accenti */
    public String convertiAccenti(String in) {
        return in.replace("�","a'").replace("�","e'").replace("�","e'").replace("�","i'").replace("�","o'").replace("�","u'");
    }
    
    /*public String utf8Convert(String utf8String) throws
            java.io.UnsupportedEncodingException {
        byte[] bytes = new byte[utf8String.length()];
        for (int i = 0; i < utf8String.length(); i++) {
            bytes[i] = (byte) utf8String.charAt(i);
        }
        return new String(bytes, "UTF-8");
    }*/
    
    
    
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
