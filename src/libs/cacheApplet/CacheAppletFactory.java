package twi518.cacheApplet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import twi.*;
import twi518.cacheTestiTitoli.CacheTestiETitoli;
import twi518.cacheTestiTitoli.ListaElementiRss;
import twi518.cacheTestiTitoli.RssElemento;
import twi518.conf.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import java.io.IOException;
import java.net.*;

public class CacheAppletFactory implements ClientInterface {
    CacheApplet ca = null;
    CacheTestiETitoli ctt = null;
    String username;
    String[] idsCategorie = null;
    String erroreCorrente = new String("");
    private DocumentBuilder builder;
    private Transformer serializer;
    private Document documentReceived;
    private Socket socket;
    private String host;
    private int port;
    private Config conf = new Config();

    public CacheAppletFactory(CacheApplet cacheApplet) {
        this.ca = cacheApplet;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            serializer = TransformerFactory.newInstance().newTransformer();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerConfigurationException tce) {
            tce.printStackTrace();
        }
    }

    public boolean accesso(String username) {
        if (!inizializzaSocket()) {
            erroreCorrente = "Impossibile connettersi all'application";
            return false;
        }
        ctt = new CacheTestiETitoli();
        this.username = username;
        richiediCats(username);
        receiveDocument();
        if (esisteErrore())
            return false;
        else {
            salvaCategorie();
            return true;
        }
    }

    public String[] getIdsCategorie() {
        if (idsCategorie == null) {
            return ca.getIdsCats();
        } else
            return idsCategorie;
    }

    public String getNomeCategoria(String idCat) {
        return ca.getNomeCat(idCat);
    }

    public String getNoteCategoria(String idCat) {
        return ca.getNoteCat(idCat);
    }

    public String[] getIdsFeedsDellaCat(String idCat) {
        if (!ca.esistonoFeeds(idCat)) {
            richiediFeedsdellaCat(idCat);
            receiveDocument();
            salvaFeedsRicevuti();
        }
        return ca.getIdsFeedsDellaCat(idCat);
    }

    public String getNomeFeed(String idFeed) {
        return ca.getNomeFeed(idFeed);
    }

    public String[] getIdsTitoli(String idFeed) {
        if (!ctt.esistonoTitoliNelFeed(idFeed)) {
            richiediTitoliDelFeed(idFeed);
            receiveDocument();
            salvaTitoliRicevuti();
        }
        return ctt.getIdsTitoli(idFeed);
    }

    public String getNomeDelTitolo(String idFeed, String idTitolo) {
        return ctt.getNomeDelTitolo(idFeed, idTitolo);
    }

    public String getDataDelTitolo(String idFeed, String idTitolo) {
        return ctt.getDataTitolo(idFeed, idTitolo);
    }

    public String getDescriptionDelTitolo(String idFeed, String idTitolo) {
        String desc = ctt.getDescription(idFeed, idTitolo);
        if (desc.equals("")) {
            richiediDescription(idFeed, idTitolo);
            receiveDocument();
            salvaDescription();
            desc = ctt.getDescription(idFeed, idTitolo);
        }
        return desc;
    }

    public void richiediCats(String username) {
        sendDocument(creaDocumento("accesso", "username", username, ""));
    }

    public void salvaCategorie() {
        Element radice = documentReceived.getDocumentElement();
        if (!radice.getTagName().equals("elenco_cat")) {
            debug("non ricevo elenco categorie");
            return;
        }
        System.out.println(radice.getTagName());
        NodeList nodiCat = radice.getElementsByTagName("cat");
        for (int i = 0; i < nodiCat.getLength(); i++) {
            Element catCorr = (Element) nodiCat.item(i);
            debug("ricevuta categoria [" + catCorr.getAttribute("nome") + "][" + catCorr.getAttribute("id") + "]");
            ca.addCat(catCorr.getAttribute("id"),
                    catCorr.getAttribute("nome"),
                    catCorr.getAttribute("note"));
        }
    }

    public void richiediFeedsdellaCat(String idCat) {
        sendDocument(creaDocumento("get_feeds", "idcat", idCat, ""));
    }

    public void salvaFeedsRicevuti() {
        Element radice = documentReceived.getDocumentElement();
        NodeList nodiCat = radice.getElementsByTagName("feed");
        String idCat = radice.getAttribute("idcat");
        if (idCat.equals("")) {
            debug("id della cat non leggibile, annullo");
            return;
        }
        for (int i = 0; i < nodiCat.getLength(); i++) {
            Element feedCorr = (Element) nodiCat.item(i);
            String id = feedCorr.getAttribute("id");
            String nome = feedCorr.getAttribute("nome");
            ca.addFeed(idCat, id, nome);
            debug("ricevuto feed [" + id + "][" + nome + "]");
        }
    }

    public void richiediTitoliDelFeed(String idFeed) {
        sendDocument(creaDocumento("get_titoli", "idfeed", idFeed, ""));
    }

    public void salvaTitoliRicevuti() {
        Element radice = documentReceived.getDocumentElement();
        String idFeed = radice.getAttribute("idfeed");
        ListaElementiRss ler = new ListaElementiRss();
        NodeList nodiTitoli = radice.getElementsByTagName("titolo");
        for (int i = 0; i < nodiTitoli.getLength(); i++) {
            Element elemTitoloCorr = (Element) nodiTitoli.item(i);
            String idS = elemTitoloCorr.getAttribute("id");
            if (idS.equals("")) {
                debug("indice nullo !!");
            } else {
                RssElemento elemRss = new RssElemento(elemTitoloCorr.getAttribute("nome"), elemTitoloCorr.getAttribute("data"));
                elemRss.setId(idS);
                debug("ricevuto titolo [" + idS + "][" + elemTitoloCorr.getAttribute("nome") + "]");
                Integer in = new Integer(idS);
                if (in == null) {
                    debug("indice non valido !!");
                } else {
                    ler.aggiungi(elemRss);
                    debug("aggiunto titolo");
                }
            }
        }
        ctt.settaElementiRss(idFeed, ler);
    }

    public void richiediDescription(String idFeed, String nTitolo) {
        Document doc = builder.newDocument();
        Node root = doc.createElement("get_testo");
        Element elemRoot = (Element) root;
        elemRoot.setAttribute("idfeed", idFeed);
        elemRoot.setAttribute("id", nTitolo);
        doc.appendChild(root);
        sendDocument(doc);
    }

    public void salvaDescription() {
        Node root = documentReceived.getDocumentElement();
        Element elemRoot = (Element) root;
        String idFeed = elemRoot.getAttribute("idfeed");
        String id = elemRoot.getAttribute("id");
        String description = root.getTextContent();
        debug("ricevo description [" + id + "][" + idFeed + "][" + description + "]");
        ctt.setDescription(idFeed, id, description);
    }

    private Document creaDocumento(String nomeRoot, String nomeAttr, String valAttr, String textInside) {
        Document doc = builder.newDocument();
        Node root = doc.createElement(nomeRoot);
        Element elemRoot = (Element) root;
        if (!nomeAttr.equals(""))
            elemRoot.setAttribute(nomeAttr, valAttr);
        doc.appendChild(root);
        return doc;
    }

    public void sendDocument(Document message) {
        try {
            serializer.transform(new DOMSource(message), new StreamResult(socket.getOutputStream()));
            MultiXML.terminate(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerException tre) {
            tre.printStackTrace();
        }
    }

    void receiveDocument() {
        try {
            documentReceived = builder.parse(MultiXML.getTerminatingInputStream(socket.getInputStream()));
        } catch (SAXException saxe) {
            saxe.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void chiudiSocket() {
        Document closeSocket = builder.newDocument();
        Node root = closeSocket.createElement("closeSocket");
        closeSocket.appendChild(root);
        sendDocument(closeSocket);
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean inizializzaSocket() {
        try {
            socket = new Socket(conf.getSERVERADD(), conf.getAPPPORT());
        } catch (UnknownHostException uhe) {
            uhe.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean esisteErrore() {
        Element radice = documentReceived.getDocumentElement();
        if (radice.getTagName().equals("errore")) {
            erroreCorrente = new String("ERRORE : " + radice.getAttribute("msg"));
            return true;
        } else
            return false;
    }

    public String getErrore() {
        return erroreCorrente;
    }

    public void debug(String s) {
    }
}
