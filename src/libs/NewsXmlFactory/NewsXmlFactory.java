package twi518.NewsXmlFactory;

import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import org.xml.sax.*;

import twi518.conf.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

/** Classe di interfaccia per la scrittura/lettura di categorie e feed su file xml<br />
 * Utilizzata da JSP e Servlets
 * @author Elvis
 */
public class NewsXmlFactory {
    
    /** Document su cui viene effettuato il parsing da Jaxp */
    private Document document;
    //percorso DTD
    private static Config conf = new Config();
    
    /** path DTD dei feeds  */
    private final String DTD_PATH = new String(conf.getWEBDIR() + "/xml-types/feeds.dtd");
    //importo configurazione applicazione web
   
    private File xmlfile;
    
    /** percorso file xml contentente i dati di cat e feeds */
    private String FEEDS_XML_PATH = null; //new String(conf.getWEBDIR() +  "/xml/feeds.xml");
    
    /** nome dell'attributo dei tag  per identificazione univoca categoria nel file xml*/
    private final String ID_CAT = "id_cat";
    /** nome dell'attributo dei tag feed per identificazione  univoco feed*/
    private final String ID_FEED = "id_feed";
    
    /** root del documento xml */
    private Node root;
    private Element elementRoot;
    
    /** elenco interno categorie */
    private CategoriaFeed[] elencoInternoCat = null;
    private boolean elencoCreato = false;
    /** numero di categorie inserite*/
    private int size = 0;
    /** id numerici di partenza per creazione ids dei nodi */
    private int maxIdCat = 1;
    private int maxIdFeed = 1;
    /** prefisso per ID categorie */
    private static final String PREFISSO_ID_CAT = "c";
    /** prefisso per ID feeds */
    private static final String PREFISSO_ID_FEEDS = "f";
    
    private String errori = "";
    
    /**
     * <b>Costruttore</b>: imposta path  xml, carica l'xml e costruisce gerarchia classi interne
     * @param xmlName nome del file xml (es: utente2.xml)
     * @see #loadXml()
     * @see #creaElencoCat()
     */
    public NewsXmlFactory(String xmlName) {
        
        if (xmlName==null ||  xmlName.length()<1 ) {
            System.err.print("nome file ("+xmlName+") non valido"); return; }
        FEEDS_XML_PATH = new String(conf.getWEBDIR() +  "/xml/" + xmlName);
        loadXml();
        creaElencoCat();
    }
    
    public String getFEEDS_XML_PATH()
    {
        return FEEDS_XML_PATH;
    }
    
    
    /** crea albero DOM leggendo da file xml e calcola nodo root<br />
     * Se il file xml non esiste, viene automaticamente creato e inizializzato
     * @see #scriviHeaderFile(String pathFile)
     */
    public synchronized void loadXml() {
        try {
            //enables applications to obtain a parser that produces DOM object trees from XML documents
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
           // factory.setValidating( true );
            //Defines the API to obtain DOM Document instances from an XML documen
            DocumentBuilder builder = factory.newDocumentBuilder();
            
            //builder.setErrorHandler( new MyErrorHandler();
            
            File f = new File(FEEDS_XML_PATH);
            
            if (!f.exists()){
                try {
                    //  ...ne crea uno nuovo...
                    f.createNewFile();
                    //  ...e lo inizializza...
                    scriviHeaderFile(FEEDS_XML_PATH);
                    
                } catch (IOException ex) {
                    System.out.println("Impossibile creare il file ["+FEEDS_XML_PATH+"]\n");
                    ex.printStackTrace();
                }
            }
            
            document = builder.parse( f );
            //d(document.getDocumentURI() ,"getDocumentURI" );
            
            root = document.getDocumentElement();
            //if (root.getNodeType() != Node.ELEMENT_NODE) { /*err?*/}
            elementRoot = ( Element ) root;
            
        } catch ( SAXParseException spe ) {
            System.err.println( "Parsing errore: " +
                    spe.getMessage() );
            //System.exit( 1 );
        } catch ( SAXException se ) {
            se.printStackTrace();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
    
    /** Crea il file xml col nome passato e scrive l'header xml */
    private void scriviHeaderFile(String pathFile) {
        String content = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><!DOCTYPE elenco_categorie SYSTEM \"../xml-types/feeds.dtd\"><elenco_categorie></elenco_categorie>";
        try{
            BufferedWriter outputFile = new BufferedWriter(new FileWriter(pathFile));
            outputFile.write(content);
            outputFile.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    /**
     * Calcola (e memorizza dentro l'oggetto di questa classe) gli oggetti <code>CategoriaFeed</code>, leggendo dai relativi nodi xml <br />
     *
     * @see CategoriaFeed
     * @see #CategoriaFromNode(Node nodeCat)
     */
    private void creaElencoCat() {
        try {
            /* creazione elencoInternoCat */
            NodeList nodiCat = elementRoot.getElementsByTagName( "categoria_feed" );
            //if (nodiCat==null) { elencoCreato = true; return; }
            size = nodiCat.getLength();
            elencoInternoCat = new CategoriaFeed[size];
            //per ogni categoria_feed
            for (int i=0; i< nodiCat.getLength(); i++) {
                elencoInternoCat[i] = CategoriaFromNode(nodiCat.item( i ));
            }
            elencoCreato = true;
            
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * restituisce oggetto <code>CategoriaFeed</code> dal relativo Nodo
     * Il contatore interno dell massimo id corrente viene incrementato
     * @see #getFeedsFromNodeCat(Node nodeCat)
     *
     */
    private CategoriaFeed CategoriaFromNode(Node nodeCat) {
        
        Element elemCatCorr = (Element)nodeCat;
        
        //creo l'oggetto CategoriaFeed, con l'elenco feeds creato sopra
        String idC = elemCatCorr.getAttribute(ID_CAT);
        int idCat = new Integer(idC.substring(1)).intValue();
        //setta maxIdCat (interno)
        if (idCat>maxIdCat)
            maxIdCat = idCat;
        
        CategoriaFeed c = new CategoriaFeed( idC,
                elemCatCorr.getAttribute("nome"),
                getFeedsFromNodeCat(nodeCat));
        
        if (elemCatCorr.hasAttribute("note"))
            c.setNote(elemCatCorr.getAttribute("note"));
        
        return c;
        
    }
    
    
    /**
     * Restituisce array di oggetti <code>Feed</code> in base ai feeds contenute nel nodo della cateogoria passato<br />
     * @see #feedFromNode(Node nodoDom)
     */
    private Feed[] getFeedsFromNodeCat(Node nodeCat) {
        NodeList nodiFeeds =  ((Element)nodeCat).getElementsByTagName( "feed" );
        Feed[] feeds = new Feed[nodiFeeds.getLength()];
        
        //per ogni <feed></feed>
        for (int j=0; j< nodiFeeds.getLength(); j++) {
            Element nodoFeed = (Element)nodiFeeds.item( j );
            feeds[j] = feedFromNode(nodoFeed);
        }
        return feeds;
    }
    
    
    /**
     * Restituisce oggetto <code>Feed</code> leggendo da nodo feed passato<br />
     * L'oggetto <code>Feed</code> restituito comprende anche le altre eventuali urls relative al feed<br />
     * Il contatore interno dell massimo id corrente viene incrementato
     * @see Feed
     */
    private Feed feedFromNode(Node nodoFeed) {
        
        Element elemFeed = (Element)nodoFeed;
        String urlPrimaria = elemFeed.getElementsByTagName( "url_primaria" ).item(0).getTextContent();
        //creo l'oggetto Feed
        
        String idF = elemFeed.getAttribute(ID_FEED);
        int idFeed = new Integer(idF.substring(1)).intValue();
        if (idFeed>maxIdFeed)
            maxIdFeed = idFeed;
        
        return new Feed(idF,
                elemFeed.getAttribute("nome"),
                urlPrimaria ,
                getAltreUrls(elemFeed)   );
    }
    
    
    /** Restituisce altre urls del feed, dato il nodo del feed */
    private String[] getAltreUrls(Element nodoFeed) {
        NodeList nodiUrls =  nodoFeed.getElementsByTagName( "url" );
        String[] altreUrls = new String[nodiUrls.getLength()];
        //per ogni <risposta></risposta>
        for (int k=0; k < nodiUrls.getLength(); k++) {
            //Node risposta = ;
            altreUrls[k] = nodiUrls.item(k).getTextContent();
        }
        return altreUrls;
    }
    
    
    /**
     * Ritorna array interno categorie
     */
    public CategoriaFeed[] getElencoCat() {
        return elencoInternoCat;
    }
    
    
    /** Ritorna il numero di categorie totali  */
    public int getSize() {
        return size;
    }
    
    
    /**
     * Ritorna l'i-esima categoria (null se non esiste)
     */
    public CategoriaFeed getCat(int i) {
        //if (!elencoCreato)
        //    creaElencoCat();
        if (elencoInternoCat.length < i)
            return elencoInternoCat[i];
        else
            return null;
    }
    
    
    /**
     * Crea e aggiunge in categoria all'elenco interno<br />
     * Viene settato l'identificativo della categoria con un ID non gi� presente)<br />
     * La nota non viene settata se il parametro <code>note</code> � una stringa vuota<br />
     */
    public boolean aggiungiCat(String nome, String note) {
        Element nuovaCat = document.createElement("categoria_feed");
        if (!elencoCreato)
            creaElencoCat();
        maxIdCat++;
        nuovaCat.setAttribute(ID_CAT, PREFISSO_ID_CAT + maxIdCat ); // es: "c" + 11 = "c11"
        nuovaCat.setAttribute("nome",nome);
        if (note != null && !note.equals(""))
            nuovaCat.setAttribute("note",note);
        appendiNodo(nuovaCat, document.createTextNode("\n\t"), true);
        
        appendiNodo(elementRoot, nuovaCat, true);
        return true;
    }
    
    
    
    /** Restituisce la classe relativa alla categoria con l'id specificato     */
    public Node getNodeCatById(String idCat) {
        NodeList nodiCat = elementRoot.getElementsByTagName( "categoria_feed" );
        for (int i=0; i< nodiCat.getLength(); i++) {
            Node nodeCatCorr = nodiCat.item( i );
            if (  (((Element)nodeCatCorr).getAttribute(ID_CAT)).equals(idCat)   )
                return nodeCatCorr;
        }
        return null;
    }
    
    /** Modifica internamente la categoria con l'ID specificato, assegnandogli nuovo nome e note
     * @param nuovaNota usare stringa vuota per non settare l'attributo
     * @return true = categoria creata, false = categoria non esistente
     */
    public boolean modificaCat(String idCat, String nuovoNome, String nuovaNota) {
        
        Node cat = getNodeCatById(idCat);
        Element elCat = (Element)cat;
        if (cat==null) {
            return false;
        } else {
            elCat.setAttribute("nome", nuovoNome);
            if (!nuovaNota.equals(""))
                elCat.setAttribute("note",nuovaNota);
            else
                elCat.removeAttribute("note");
            //
            return true;
        }
    }
    
    /**
     * Restituisce oggetto CategoriaFeed in base all'ID
     * @see #getNodeCatById
     * @see #CategoriaFromNode
     */
    public CategoriaFeed getCatById(String idCat) {
        Node nodeCat = getNodeCatById(idCat);
        if (nodeCat==null) {
            return null;
        } else {
            return CategoriaFromNode(nodeCat);
        }
    }
    
    /** Cancella la categoria dall'elenco interno in base all'ID<br />
     * vengono cancellate anche feeds interne alla categoria
     * @return se la categoria non esiste, viene restituito false
     */
    public boolean cancellaCat(String idCat) {
        Node nodeCat = getNodeCatById(idCat);
        if (nodeCat==null) {
            return false;
        } else {
            Node padre = nodeCat.getParentNode();
            padre.removeChild(nodeCat);
            return true;
        }
    }
    
    
    
    
    /** Aggiunge un feed alla categoria con l'id specificato
     * @return ritorna false se la categoria non esiste
     */
    public boolean aggiungiFeed(String idCat, String nomeFeed, String urlPrimaria, String[] altreUrls) {
        //se idCat non esiste
        Node nodeCat = getNodeCatById(idCat);
        
        if (nodeCat == null)  return false;
        
        //creo elemento feed con nomeFeed e id
        Element elemNuovoFeed = document.createElement("feed");
        if (!elencoCreato)
            creaElencoCat();
        maxIdFeed++;
        elemNuovoFeed.setAttribute(ID_FEED, PREFISSO_ID_FEEDS + maxIdFeed );
        elemNuovoFeed.setAttribute("nome",nomeFeed);
        
        Element elemUrlPrimaria = document.createElement("url_primaria");
        //appendo url primaria
        appendiNodo(elemUrlPrimaria, document.createTextNode(urlPrimaria), false);
        //appendo altre urls all'albero
        appendiNodo(elemNuovoFeed, elemUrlPrimaria, true);
        for(int i=0; i<altreUrls.length; i++) {
            if (!altreUrls[i].equals("")) {
                Element elemUrl = document.createElement("url");
                appendiNodo(elemUrl, document.createTextNode(altreUrls[i]), false );
                appendiNodo(elemNuovoFeed, elemUrl, true);
            }
        }
        //appendo newline
        appendiNodo(elemNuovoFeed, document.createTextNode("\n\t"), false);
        //appendo il nuovo nodo del feed creato al nodo della cateogira
        appendiNodo(nodeCat, elemNuovoFeed, true);
        
        return true;
    }
    
    
    /** Modifica la feed (con l'ID specificato) della categoria (con l'ID specificato)<br />
     */
    public boolean modificaFeed(String idCat, String idFeed, String nuovoNome, String nuovaUrlPrimaria, String[] nuoveUrls) {
        
        Node nodeFeed = getNodeFeedByIds(idCat, idFeed);
        if (nodeFeed == null)  return false;
        
        //creo nuovo elemento feed
        Element elemNuovoFeed =  document.createElement("feed");
        elemNuovoFeed.setAttribute(ID_FEED, idFeed );
        elemNuovoFeed.setAttribute("nome",nuovoNome);
        
        //creo url primaria
        Element elemUrlPrim = document.createElement("url_primaria");
        appendiNodo(elemUrlPrim, document.createTextNode(nuovaUrlPrimaria), false);
        //
        appendiNodo(elemNuovoFeed, elemUrlPrim, true);
        
        //creo altre urls
        for(int i=0; i<nuoveUrls.length; i++) {
            //creo elemUrl
            if (!nuoveUrls[i].equals("")) {
                Element elemUrl = document.createElement("url");
                appendiNodo(elemUrl, document.createTextNode(nuoveUrls[i]), false );
                appendiNodo(elemNuovoFeed, elemUrl, true);
            }
        }
        appendiNodo(elemNuovoFeed, document.createTextNode("\n\t"), false);
        
        //rimpiazzo vecchio nodo feed con il nuovo appena creato
        Node padre = nodeFeed.getParentNode(); //get categoria padre
        padre.replaceChild(elemNuovoFeed, nodeFeed);
        
        return true;
    }
    
    /**
     * Restituisce nodo feed in base a ID nodeCat e ID feed
     */
    private Node getNodeFeedByIds(String idCat, String idFeed) {
        Node nodeCat = getNodeCatById(idCat);
        if (nodeCat == null)  return null;
        
        NodeList nodelistFeeds = ((Element)nodeCat).getElementsByTagName( "feed" );
        for (int i=0; i< nodelistFeeds.getLength(); i++) {
            Node nodeFeedCorr = nodelistFeeds.item( i );
            if (  (((Element)nodeFeedCorr).getAttribute(ID_FEED)).equals(idFeed)   ) {
                return nodeFeedCorr;
            }
        }
        
        
        
        return null;
    }
    
    
    /** Cancella il feed della categoria, in base agli ids specificati
     * @return ritorna false se non trova il feed nell'albero
     */
    public boolean cancellaFeed(String idCat, String idFeed) {
        Node nodoFeed = getNodeFeedByIds(idCat, idFeed);
        if (nodoFeed == null)  return false;
        Node padre = nodoFeed.getParentNode();
        padre.removeChild(nodoFeed);
        return true;
    }
    
    /**
     * restituisce oggetto Feed con ID cat e ID feed specificato
     */
    public Feed getFeedByIds(String idCat, String idFeed) {
        Node nodoFeed = getNodeFeedByIds(idCat, idFeed);
        if (nodoFeed==null) {
            return null;
        } else {
            return feedFromNode(nodoFeed);
        }
    }
    
    public Feed getFeedById(String idFeed) {
        Node nodoFeed = document.getElementById(idFeed);
        if (nodoFeed==null) {
            return null;
        } else {
            return feedFromNode(nodoFeed);
        }
    }
    
    
    /** Salva l'elenco interno (eventualmente modificato dalle funzioni di questa classe)<br />
     * sul file xml, rispettando la DTD<br />
     */
    public synchronized void salvaXml() {
        
        try{
            //  Istanzia un oggetto di tipo DOMSource
            
            //  Crea istanza x serializzazione flusso
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            //  ...definisce propriet� document...
            
            // transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8"); 
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, DTD_PATH    ); //"../xml-types/feeds.dtd"
            //  salvata
            document.normalize();
            transformer.transform(new DOMSource(document),
                    new StreamResult(new FileWriter(FEEDS_XML_PATH)));
            
        }catch(TransformerConfigurationException tce){
            if(tce.getException()!=null) {
                tce.getException().printStackTrace();
                errori = errori + tce.toString();
            }
        }catch(TransformerException te){
            if(te.getException()!=null) {
                te.getException().printStackTrace();
                errori = errori + te.toString();
            }
        }catch(IOException ioe){
            ioe.printStackTrace();
            errori = errori + ioe.toString();
        }
    }
    
    /** restituisce errore corrente */
    public String getErr()
    {
        return errori;
    }
    
    
    /** funzione di testing
   
    public static void main( String args[] ) {
        NewsXmlFactory nxf = new NewsXmlFactory("lucio.xml");
        
        //nxf.aggiungiCat("new1","123");
        //nxf.modificaCat("c2","nuovo","nota nuova");
        //nxf.aggiungiFeed("c2","uno", "lp", new String[]{"l2"});
        //nxf.modificaFeed("c2", "f2", "gazzetta2", "gaz.itt", new String[]{"gaz.comm"} );
        //qxf.aggiungiCat("new2","");
        //qxf.cancellaCat("q3");
        //qxf.modificaCat("q4","quattro","pass");
        //CategoriaFeed q = qxf.getCatById("q2");
        
        //String[] risps = new String[]{"uno", "tre"};
        //qxf.aggiungiFeed("q2","1+1", "2", risps);
        Feed d = qxf.getFeedByIds("q1", "d2" );
        System.out.println(d.testo);
         
        for(int i=0; i<d.risposte.length; i++) {
            //d.risposte[i]
        }
        
        //qxf.modificaFeed("q2", "d9", "uno+uno", "due", risps);
        
        Feed f = nxf.getFeedById("f2");
        
        
        System.out.println(f.getNome());
        
        //nxf.salvaXml();
        
    }*/
    
    /** Appende un nodo spaziando il file per migliorare la lettura  */
    private void appendiNodo(Node padre, Node figlio, boolean spaziato) {
        if (spaziato)
            padre.appendChild(document.createTextNode("\n\t"));
        padre.appendChild(figlio);
        if (spaziato)
            padre.appendChild(document.createTextNode("\n\t"));
    }
}
