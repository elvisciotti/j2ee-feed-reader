package twi518.NewsXmlFactory;

import org.w3c.dom.*;
import org.xml.sax.*;
import twi518.conf.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.*;
import java.io.*;
import java.util.*;

public class NewsXmlFactory {
    private static final String PREFISSO_ID_CAT = "c";
    private static final String PREFISSO_ID_FEEDS = "f";
    private static Config conf = new Config();
    private final String DTD_PATH = new String(conf.getWEBDIR() + "/xml-types/feeds.dtd");
    private final String ID_CAT = "id_cat";
    private final String ID_FEED = "id_feed";
    private Document document;
    private File xmlfile;
    private String FEEDS_XML_PATH = null;
    private Node root;
    private Element elementRoot;
    private CategoriaFeed[] elencoInternoCat = null;
    private boolean elencoCreato = false;
    private int size = 0;
    private int maxIdCat = 1;
    private int maxIdFeed = 1;
    private String errori = "";

    public NewsXmlFactory(String xmlName) {
        if (xmlName == null || xmlName.length() < 1) {
            System.err.print("nome file (" + xmlName + ") non valido");
            return;
        }
        FEEDS_XML_PATH = new String(conf.getWEBDIR() + "/xml/" + xmlName);
        loadXml();
        creaElencoCat();
    }

    public String getFEEDS_XML_PATH() {
        return FEEDS_XML_PATH;
    }

    public synchronized void loadXml() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            File f = new File(FEEDS_XML_PATH);
            if (!f.exists()) {
                try {
                    f.createNewFile();
                    scriviHeaderFile(FEEDS_XML_PATH);
                } catch (IOException ex) {
                    System.out.println("Impossibile creare il file [" + FEEDS_XML_PATH + "]\n");
                    ex.printStackTrace();
                }
            }
            document = builder.parse(f);
            root = document.getDocumentElement();
            elementRoot = (Element) root;
        } catch (SAXParseException spe) {
            System.err.println("Parsing errore: " +
                    spe.getMessage());
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scriviHeaderFile(String pathFile) {
        String content = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><!DOCTYPE elenco_categorie SYSTEM \"../xml-types/feeds.dtd\"><elenco_categorie></elenco_categorie>";
        try {
            BufferedWriter outputFile = new BufferedWriter(new FileWriter(pathFile));
            outputFile.write(content);
            outputFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void creaElencoCat() {
        try {
            NodeList nodiCat = elementRoot.getElementsByTagName("categoria_feed");
            size = nodiCat.getLength();
            elencoInternoCat = new CategoriaFeed[size];
            for (int i = 0; i < nodiCat.getLength(); i++) {
                elencoInternoCat[i] = CategoriaFromNode(nodiCat.item(i));
            }
            elencoCreato = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CategoriaFeed CategoriaFromNode(Node nodeCat) {
        Element elemCatCorr = (Element) nodeCat;
        String idC = elemCatCorr.getAttribute(ID_CAT);
        int idCat = new Integer(idC.substring(1)).intValue();
        if (idCat > maxIdCat)
            maxIdCat = idCat;
        CategoriaFeed c = new CategoriaFeed(idC,
                elemCatCorr.getAttribute("nome"),
                getFeedsFromNodeCat(nodeCat));
        if (elemCatCorr.hasAttribute("note"))
            c.setNote(elemCatCorr.getAttribute("note"));
        return c;
    }

    private Feed[] getFeedsFromNodeCat(Node nodeCat) {
        NodeList nodiFeeds = ((Element) nodeCat).getElementsByTagName("feed");
        Feed[] feeds = new Feed[nodiFeeds.getLength()];
        for (int j = 0; j < nodiFeeds.getLength(); j++) {
            Element nodoFeed = (Element) nodiFeeds.item(j);
            feeds[j] = feedFromNode(nodoFeed);
        }
        return feeds;
    }

    private Feed feedFromNode(Node nodoFeed) {
        Element elemFeed = (Element) nodoFeed;
        String urlPrimaria = elemFeed.getElementsByTagName("url_primaria").item(0).getTextContent();
        String idF = elemFeed.getAttribute(ID_FEED);
        int idFeed = new Integer(idF.substring(1)).intValue();
        if (idFeed > maxIdFeed)
            maxIdFeed = idFeed;
        return new Feed(idF,
                elemFeed.getAttribute("nome"),
                urlPrimaria,
                getAltreUrls(elemFeed));
    }

    private String[] getAltreUrls(Element nodoFeed) {
        NodeList nodiUrls = nodoFeed.getElementsByTagName("url");
        String[] altreUrls = new String[nodiUrls.getLength()];
        for (int k = 0; k < nodiUrls.getLength(); k++) {
            altreUrls[k] = nodiUrls.item(k).getTextContent();
        }
        return altreUrls;
    }

    public CategoriaFeed[] getElencoCat() {
        return elencoInternoCat;
    }

    public int getSize() {
        return size;
    }

    public CategoriaFeed getCat(int i) {
        if (elencoInternoCat.length < i)
            return elencoInternoCat[i];
        else
            return null;
    }

    public boolean aggiungiCat(String nome, String note) {
        Element nuovaCat = document.createElement("categoria_feed");
        if (!elencoCreato)
            creaElencoCat();
        maxIdCat++;
        nuovaCat.setAttribute(ID_CAT, PREFISSO_ID_CAT + maxIdCat);
        nuovaCat.setAttribute("nome", nome);
        if (note != null && !note.equals(""))
            nuovaCat.setAttribute("note", note);
        appendiNodo(nuovaCat, document.createTextNode("\n\t"), true);
        appendiNodo(elementRoot, nuovaCat, true);
        return true;
    }

    public Node getNodeCatById(String idCat) {
        NodeList nodiCat = elementRoot.getElementsByTagName("categoria_feed");
        for (int i = 0; i < nodiCat.getLength(); i++) {
            Node nodeCatCorr = nodiCat.item(i);
            if ((((Element) nodeCatCorr).getAttribute(ID_CAT)).equals(idCat))
                return nodeCatCorr;
        }
        return null;
    }

    public boolean modificaCat(String idCat, String nuovoNome, String nuovaNota) {
        Node cat = getNodeCatById(idCat);
        Element elCat = (Element) cat;
        if (cat == null) {
            return false;
        } else {
            elCat.setAttribute("nome", nuovoNome);
            if (!nuovaNota.equals(""))
                elCat.setAttribute("note", nuovaNota);
            else
                elCat.removeAttribute("note");
            return true;
        }
    }

    public CategoriaFeed getCatById(String idCat) {
        Node nodeCat = getNodeCatById(idCat);
        if (nodeCat == null) {
            return null;
        } else {
            return CategoriaFromNode(nodeCat);
        }
    }

    public boolean cancellaCat(String idCat) {
        Node nodeCat = getNodeCatById(idCat);
        if (nodeCat == null) {
            return false;
        } else {
            Node padre = nodeCat.getParentNode();
            padre.removeChild(nodeCat);
            return true;
        }
    }

    public boolean aggiungiFeed(String idCat, String nomeFeed, String urlPrimaria, String[] altreUrls) {
        Node nodeCat = getNodeCatById(idCat);
        if (nodeCat == null) return false;
        Element elemNuovoFeed = document.createElement("feed");
        if (!elencoCreato)
            creaElencoCat();
        maxIdFeed++;
        elemNuovoFeed.setAttribute(ID_FEED, PREFISSO_ID_FEEDS + maxIdFeed);
        elemNuovoFeed.setAttribute("nome", nomeFeed);
        Element elemUrlPrimaria = document.createElement("url_primaria");
        appendiNodo(elemUrlPrimaria, document.createTextNode(urlPrimaria), false);
        appendiNodo(elemNuovoFeed, elemUrlPrimaria, true);
        for (int i = 0; i < altreUrls.length; i++) {
            if (!altreUrls[i].equals("")) {
                Element elemUrl = document.createElement("url");
                appendiNodo(elemUrl, document.createTextNode(altreUrls[i]), false);
                appendiNodo(elemNuovoFeed, elemUrl, true);
            }
        }
        appendiNodo(elemNuovoFeed, document.createTextNode("\n\t"), false);
        appendiNodo(nodeCat, elemNuovoFeed, true);
        return true;
    }

    public boolean modificaFeed(String idCat, String idFeed, String nuovoNome, String nuovaUrlPrimaria, String[] nuoveUrls) {
        Node nodeFeed = getNodeFeedByIds(idCat, idFeed);
        if (nodeFeed == null) return false;
        Element elemNuovoFeed = document.createElement("feed");
        elemNuovoFeed.setAttribute(ID_FEED, idFeed);
        elemNuovoFeed.setAttribute("nome", nuovoNome);
        Element elemUrlPrim = document.createElement("url_primaria");
        appendiNodo(elemUrlPrim, document.createTextNode(nuovaUrlPrimaria), false);
        appendiNodo(elemNuovoFeed, elemUrlPrim, true);
        for (int i = 0; i < nuoveUrls.length; i++) {
            if (!nuoveUrls[i].equals("")) {
                Element elemUrl = document.createElement("url");
                appendiNodo(elemUrl, document.createTextNode(nuoveUrls[i]), false);
                appendiNodo(elemNuovoFeed, elemUrl, true);
            }
        }
        appendiNodo(elemNuovoFeed, document.createTextNode("\n\t"), false);
        Node padre = nodeFeed.getParentNode();
        padre.replaceChild(elemNuovoFeed, nodeFeed);
        return true;
    }

    private Node getNodeFeedByIds(String idCat, String idFeed) {
        Node nodeCat = getNodeCatById(idCat);
        if (nodeCat == null) return null;
        NodeList nodelistFeeds = ((Element) nodeCat).getElementsByTagName("feed");
        for (int i = 0; i < nodelistFeeds.getLength(); i++) {
            Node nodeFeedCorr = nodelistFeeds.item(i);
            if ((((Element) nodeFeedCorr).getAttribute(ID_FEED)).equals(idFeed)) {
                return nodeFeedCorr;
            }
        }
        return null;
    }

    public boolean cancellaFeed(String idCat, String idFeed) {
        Node nodoFeed = getNodeFeedByIds(idCat, idFeed);
        if (nodoFeed == null) return false;
        Node padre = nodoFeed.getParentNode();
        padre.removeChild(nodoFeed);
        return true;
    }

    public Feed getFeedByIds(String idCat, String idFeed) {
        Node nodoFeed = getNodeFeedByIds(idCat, idFeed);
        if (nodoFeed == null) {
            return null;
        } else {
            return feedFromNode(nodoFeed);
        }
    }

    public Feed getFeedById(String idFeed) {
        Node nodoFeed = document.getElementById(idFeed);
        if (nodoFeed == null) {
            return null;
        } else {
            return feedFromNode(nodoFeed);
        }
    }

    public synchronized void salvaXml() {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, DTD_PATH);
            document.normalize();
            transformer.transform(new DOMSource(document),
                    new StreamResult(new FileWriter(FEEDS_XML_PATH)));
        } catch (TransformerConfigurationException tce) {
            if (tce.getException() != null) {
                tce.getException().printStackTrace();
                errori = errori + tce.toString();
            }
        } catch (TransformerException te) {
            if (te.getException() != null) {
                te.getException().printStackTrace();
                errori = errori + te.toString();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            errori = errori + ioe.toString();
        }
    }

    public String getErr() {
        return errori;
    }

    private void appendiNodo(Node padre, Node figlio, boolean spaziato) {
        if (spaziato)
            padre.appendChild(document.createTextNode("\n\t"));
        padre.appendChild(figlio);
        if (spaziato)
            padre.appendChild(document.createTextNode("\n\t"));
    }
}
