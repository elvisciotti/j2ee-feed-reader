package twi518;

import org.w3c.dom.*;
import org.xml.sax.*;
import twi518.MyErrorHandler;
import twi518.cacheTestiTitoli.ListaElementiRss;
import twi518.cacheTestiTitoli.RssElemento;
import twi518.conf.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class RssReader {
    private Document document;
    private Node root;
    private Element nodoRoot;
    private String erroreStr = "";

    public boolean processa(String ind) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            URL url = new URL(ind);
            InputStreamReader i = new InputStreamReader(url.openStream());
            BufferedReader in = new BufferedReader(i);
            builder.setErrorHandler(new MyErrorHandler());
            document = builder.parse(url.openStream());
            root = document.getDocumentElement();
            nodoRoot = (Element) root;
            return true;
        } catch (SAXParseException spe) {
            erroreStr = erroreStr + "\nParsing errore: " + spe.getMessage();
            return false;
        } catch (SAXException se) {
            erroreStr = erroreStr + "\n" + se.toString();
            return false;
        } catch (Exception e) {
            erroreStr = erroreStr + "\n" + e.toString();
            return false;
        }
    }

    public RssElemento[] leggiItems() {
        NodeList nodiItem = nodoRoot.getElementsByTagName("item");
        int numeroItems = nodiItem.getLength();
        RssElemento[] elementi = new RssElemento[numeroItems];
        for (int i = 0; i < numeroItems; i++) {
            Element nodoItemCorr = (Element) nodiItem.item(i);
            String title = nodoItemCorr.getElementsByTagName("title").item(0).getTextContent();
            String description = nodoItemCorr.getElementsByTagName("description").item(0).getTextContent();
            String pubDate = "";
            try {
                pubDate = nodoItemCorr.getElementsByTagName("pubDate").item(0).getTextContent();
            } catch (Exception e) {
                pubDate = "";
            }
            elementi[i] = new RssElemento(title, description, pubDate);
        }
        return elementi;
    }

    public ListaElementiRss getListaElementiRss(String[] urls) {
        ListaElementiRss ler = new ListaElementiRss();
        int numCorr = 0;
        for (int i = 0; i < urls.length; i++) {
            if (processa(urls[i])) {
                RssElemento[] elementiRss = null;
                elementiRss = leggiItems();
                for (int j = 0; j < elementiRss.length; j++) {
                    RssElemento e = elementiRss[j];
                    e.setId("" + numCorr);
                    numCorr++;
                    ler.aggiungi(e);
                }
            }
        }
        return ler;
    }

    public String getErrore() {
        return erroreStr;
    }
}
