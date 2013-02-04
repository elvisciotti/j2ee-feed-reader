package twi518;

import java.net.*;
import java.io.*;
import java.lang.*;
import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
//import com.sun.xml.tree.XmlDocument;
import org.xml.sax.*;
import twi518.MyErrorHandler;
import twi518.cacheTestiTitoli.ListaElementiRss;
import twi518.cacheTestiTitoli.RssElemento;

import twi518.conf.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
/**
 * @author Elvis
 */
public class RssReader {
    
    
    private Document document;
    private Node root;
    private Element nodoRoot;
    private String erroreStr = "";
    
    /** crea albero DOM interno sulla url specificata */
    public boolean processa(String ind) {
        try {
            //enables applications to obtain a parser that produces DOM object trees from XML documents
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            
            factory.setValidating( false );
            //Defines the API to obtain DOM Document instances from an XML documen
            DocumentBuilder builder = factory.newDocumentBuilder();
            
            URL url = new URL(ind);
            InputStreamReader i = new InputStreamReader(url.openStream());
            BufferedReader in = new BufferedReader(i);
            
            builder.setErrorHandler( new MyErrorHandler());
            
            document = builder.parse( url.openStream() );
            //d(document.getDocumentURI() ,"getDocumentURI" );
            
            root = document.getDocumentElement();
            //if (root.getNodeType() != Node.ELEMENT_NODE) { /*err?*/}
            nodoRoot = ( Element ) root;
            //System.out.println("radice: "+ ((Element)root).getTagName() );
            return true;
            
        } catch ( SAXParseException spe ) {
            erroreStr = erroreStr + "\nParsing errore: " + spe.getMessage();
            return false;
            //System.err.println(  );
            //System.exit( 1 );
        } catch ( SAXException se ) {
            //se.printStackTrace();
            erroreStr = erroreStr + "\n" + se.toString();
            return false;
        } catch ( Exception e ) {
            //e.printStackTrace();
            erroreStr = erroreStr + "\n" + e.toString();
            return false;
        }
    }
    
    /**  */
    public RssElemento[] leggiItems() {
        NodeList nodiItem =  nodoRoot.getElementsByTagName( "item" );
        int numeroItems = nodiItem.getLength(); //5;
        //if (nodiItem.getLength() > numeroItems)
        //    numeroItems = nodiItem.getLength();
        RssElemento[] elementi = new RssElemento[numeroItems];
        
        //leggo il contenuto dei nodi <item>
        for(int i=0; i < numeroItems; i++) {
            Element nodoItemCorr = (Element)nodiItem.item(i);
            
            //leggo sotto contenuto
            String title = nodoItemCorr.getElementsByTagName("title").item(0).getTextContent();
            String description = nodoItemCorr.getElementsByTagName("description").item(0).getTextContent();
            String pubDate = "";
            try 
            {
                pubDate = nodoItemCorr.getElementsByTagName("pubDate").item(0).getTextContent();
            }
            catch (Exception e) {pubDate = ""; }
            
            //aggiungo a array degli items
            elementi[i] = new RssElemento(title, description, pubDate);
            // System.out.println("letto" + title);
        }
        return elementi;
    }
    
    /** ritorno oggetto ListaElementiRss con tutti i titoli aggregati presi dalle urls */
    public ListaElementiRss getListaElementiRss(String[] urls) {
        ListaElementiRss ler = new ListaElementiRss();
        int numCorr = 0;
        // per ogni url
        for (int i=0; i<urls.length; i++) {
            //processo documento xml alla url specificata
            if (processa(urls[i])) {
                //prendo i titoli del singolo documento xml
                RssElemento[] elementiRss = null;
                elementiRss =  leggiItems();
                for (int j=0; j<elementiRss.length; j++) {
                    RssElemento e = elementiRss[j]; //setto numero univoco del titolo per il feed
                    e.setId("" + numCorr);
                    
                    numCorr++;
                    //aggiungo a linked list globale
                    ler.aggiungi(e);
                }
            }
        }
        
        //ritorno oggetto creato
        return ler;
        
    }
    
    public String getErrore()
    {
        return erroreStr;
    }
    
    
}




