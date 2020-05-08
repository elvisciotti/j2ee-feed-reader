package twi518;

import org.w3c.dom.*;
import org.xml.sax.*;
import twi.*;
import twi518.*;
import twi518.NewsXmlFactory.*;
import twi518.cacheTestiTitoli.CacheTestiETitoli;
import twi518.cacheTestiTitoli.ListaElementiRss;
import twi518.cacheTestiTitoli.RssElemento;
import twi518.conf.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Application {
    public static void main(String args[]) {
        try {
            ServerSocket serverSocket = new ServerSocket(Config.getAPPPORT());
            System.out.println("[SERVER PRONTO]");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ApplicationThread applicationThread =
                        new ApplicationThread(clientSocket);
                applicationThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}

class ApplicationThread extends Thread {
    private Socket socket;
    private DocumentBuilder builder;
    private Transformer serializer;
    private boolean chiusurasocket = false;
    private String username = null;
    private Config conf = new Config();
    private NewsXmlFactory nxf = null;
    private CacheTestiETitoli cacheTestiTitoli;

    public ApplicationThread(Socket s) {
        socket = s;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            serializer = TransformerFactory.newInstance().newTransformer();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerConfigurationException tce) {
            tce.printStackTrace();
        }
    }

    public void run() {
        try {
            while (!chiusurasocket) {
                Document document = builder.parse(
                        MultiXML.getTerminatingInputStream(socket.getInputStream()));
                documentReceived(document);
            }
            socket.close();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void documentReceived(Document message) {
        Element radice = message.getDocumentElement();
        String richiestaClient = radice.getTagName();
        if (richiestaClient.equals("accesso")) {
            inviaElencoCats(radice.getAttribute("username"));
        } else if (richiestaClient.equals("get_feeds")) {
            inviaElencoFeeds(radice.getAttribute("idcat"));
        } else if (richiestaClient.equals("get_titoli")) {
            inviaElencoTitoli(radice.getAttribute("idfeed"));
        } else if (richiestaClient.equals("get_testo")) {
            String idFeed = radice.getAttribute("idfeed");
            String id = radice.getAttribute("id");
            if (idFeed.equals("") || id.equals(""))
                debug("richiesta di testo non valida !!");
            inviaTesto(idFeed, id);
        } else if (richiestaClient.equals("closeSocket")) {
            System.out.println("chiudo socket");
            chiusurasocket = true;
        } else if (richiestaClient.equals("esci")) {
            System.out.println("shutdown application...");
            System.exit(1);
        }
    }

    private Document creaDocumento(String nomeRoot, String nomeAttr, String valAttr) {
        Document doc = builder.newDocument();
        Node root = doc.createElement(nomeRoot);
        Element elemRoot = (Element) root;
        elemRoot.setAttribute(nomeAttr, valAttr);
        doc.appendChild(root);
        return doc;
    }

    private void inviaElencoCats(String username) {
        if (username == null || username.equals("")) {
            inviaErrore("username non valido");
            return;
        }
        String xmlFilePath = new String(conf.getWEBDIR() + "/xml/" + username + ".xml");
        File f = new File(xmlFilePath);
        if (f.exists()) {
            nxf = new NewsXmlFactory(username + ".xml");
            cacheTestiTitoli = new CacheTestiETitoli();
            Document doc = builder.newDocument();
            Node root = doc.createElement("elenco_cat");
            CategoriaFeed[] cf = nxf.getElencoCat();
            for (int i = 0; i < cf.length; i++) {
                Node cat = doc.createElement("cat");
                Element elemCat = (Element) cat;
                elemCat.setAttribute("id", cf[i].getId());
                elemCat.setAttribute("nome", cf[i].getNome());
                elemCat.setAttribute("note", cf[i].getNote());
                elemCat.setAttribute("feeds", "" + cf[i].getFeeds().length + "");
                root.appendChild(cat);
            }
            doc.appendChild(root);
            sendDocument(doc);
        } else {
            inviaErrore("Impossibile leggere i dati per l'utente (" + username + ")");
        }
    }

    private void inviaElencoFeeds(String idCat) {
        Document doc = builder.newDocument();
        Node root = doc.createElement("elenco_feeds");
        ((Element) root).setAttribute("idcat", idCat);
        CategoriaFeed cf = nxf.getCatById(idCat);
        Feed[] feeds = cf.getFeeds();
        for (int i = 0; i < feeds.length; i++) {
            Node feed = doc.createElement("feed");
            Element elemFeed = (Element) feed;
            elemFeed.setAttribute("id", feeds[i].getId());
            elemFeed.setAttribute("nome", feeds[i].getNome());
            root.appendChild(feed);
        }
        doc.appendChild(root);
        sendDocument(doc);
    }

    private void inviaElencoTitoli(String idFeed) {
        Document doc = builder.newDocument();
        Node root = doc.createElement("titoli");
        ((Element) root).setAttribute("idfeed", idFeed);
        if (!cacheTestiTitoli.esistonoTitoliNelFeed(idFeed)) {
            Feed f = nxf.getFeedById(idFeed);
            String urlPr = f.getUrlPrimaria();
            String[] altreUrls = f.getUrls();
            String[] urlsAll = new String[altreUrls.length + 1];
            urlsAll[0] = urlPr;
            for (int i = 1; i < urlsAll.length; i++) {
                urlsAll[i] = altreUrls[i - 1];
            }
            RssReader rr = new RssReader();
            ListaElementiRss ler = rr.getListaElementiRss(urlsAll);
            if (ler.getSize() == 0) {
            }
            cacheTestiTitoli.settaElementiRss(idFeed, ler);
        }
        Node titolo = doc.createElement("titolo");
        Element elemTitolo = (Element) titolo;
        root.appendChild(titolo);
        Iterator it = cacheTestiTitoli.getIterator(idFeed);
        while (it.hasNext()) {
            RssElemento elemRssCorr = ((RssElemento) it.next());
            titolo = doc.createElement("titolo");
            elemTitolo = (Element) titolo;
            String nome = elemRssCorr.getTitle();
            elemTitolo.setAttribute("nome", nome);
            String id = elemRssCorr.getId();
            elemTitolo.setAttribute("id", id);
            String data = elemRssCorr.getPubDate();
            elemTitolo.setAttribute("data", data);
            if (id != null && nome != null) {
                root.appendChild(titolo);
            }
        }
        doc.appendChild(root);
        sendDocument(doc);
    }

    private void inviaTesto(String idFeed, String id) {
        Document doc = builder.newDocument();
        Node root = doc.createElement("testo");
        Element elemRoot = (Element) root;
        elemRoot.setAttribute("idfeed", idFeed);
        elemRoot.setAttribute("id", id);
        String testo = cacheTestiTitoli.getDescription(idFeed, id);
        if (testo.equals("") || testo == null)
            debug("testo non esistente");
        root.appendChild(doc.createTextNode(testo));
        doc.appendChild(root);
        sendDocument(doc);
    }

    public void sendDocument(Document message) {
        try {
            serializer.transform(new DOMSource(message),
                    new StreamResult(socket.getOutputStream()));
            MultiXML.terminate(socket.getOutputStream());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (TransformerException tre) {
            tre.printStackTrace();
        }
    }

    private void inviaErrore(String err) {
        Document doc = builder.newDocument();
        Node root = doc.createElement("errore");
        ((Element) root).setAttribute("msg", err);
        doc.appendChild(root);
        sendDocument(doc);
    }
}
