package twi518.cacheApplet;
import java.awt.event.ActionEvent; // ActionEvent per la gestione degli eventi
import java.awt.event.ActionListener; // ActionListener per l'ascolto degli eventi
import java.awt.event.MouseAdapter; // MouseAdapter
import java.awt.event.MouseEvent;   //MouseEvent per la gestione degli eventi del mouse
import java.io.File; // File
import java.io.IOException; //IOException
import java.lang.*;


/*
 *  Librerie JAXP API
 */
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
/*
 *  Librerie per le specifiche W3C DOM e DOM Exceptions
 */
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import java.net.*;

import twi.*;
import twi518.cacheTestiTitoli.CacheTestiETitoli;
import twi518.cacheTestiTitoli.ListaElementiRss;
import twi518.cacheTestiTitoli.RssElemento;
import twi518.conf.*;



/** Classe che crea e gestisce CacheApplet e CacheTestieTitoli comunicando con l'application<br/ >
 * La classe invia e riceve messaggi con l'application, costruendo cache interna
 * Tramite questa classe è possibile ottenere dall'application tutti i contenuti, nascondendo i dettagli della comunicazione
 * (invio messaggi e ricerca dati eventualmente già in cache).
 * All'interno
 * Sono forniti metodi per identificare categorie, feeds, titoli e testi tramite id ad alto livello,
 * utilizzando quando possibile i metodi delle classi cache interne
 *
 * @see CacheApplet
 * @author Gruppo 518  Elvis Ciotti (elvisciotti@gmail.com), Ugo Ceccarelli(ugo.ceccarelli@hotmail.it)
 */
public class CacheAppletFactory implements ClientInterface {
    
    /**
     * DocumentBuilder per il parsing delle risposte ottenute dall'Application
     */
    private DocumentBuilder builder;
    private Transformer serializer;
    private Document documentReceived;
    private Socket socket;
    private String host;
    private int port;
    private Config conf = new Config();
    CacheApplet ca = null;
    CacheTestiETitoli ctt = null;
    
    String username;
    String[] idsCategorie = null;
    
    String erroreCorrente = new String("");
    //private HashMap<String, CacheApplet> categorieMap = new HashMap() ; // "turtle 1" -> 1
    
    /** costruttore
     @param cacheApplet oggetto cacheApplet da istanziare: oggetto dove verranno salvati e letti tutti i dati ricevuti*/
    public CacheAppletFactory(CacheApplet cacheApplet){
        this.ca = cacheApplet;
        try{
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            serializer = TransformerFactory.newInstance().newTransformer();
        } catch(ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch(TransformerConfigurationException tce) {
            tce.printStackTrace();
        }
    }
    
    /**  inizializza la socket, in caso positivo istanzia CacheTestiETitoli,
     * quindi richiede le categorie, setta l'username, richiede le categorie per 'username,
     * se le categorie sono ricevute le salva e restituisce true, altrimenti false e setta l'errore internamente     * @see #inizializzaSocket
     * @see #richiediCats
     * @see #receiveDocument
     * @see #salvaCategorie
     * @param username username dell'utente che ha salvato dati nel server
     */
    public boolean accesso(String username) {
        if (!inizializzaSocket())
        {
            erroreCorrente = "Impossibile connettersi all'application";
            return false;
        }
        
        //importante farlo qua, così se rifaccio accesso resetto tutto
        ctt = new CacheTestiETitoli();
        
        this.username = username;
        //eseguo accesso
        richiediCats(username);
        receiveDocument();
        
        // controllo errore, in caso positivo salvo qua dentro categorie
        if (esisteErrore())
            return false;
        else {
            salvaCategorie();
            return true;
        }
    }
    
    
    
    /** ritorna array ids delle categorie salvato dopo l'accesso. <br />
     * @see CacheApplet#getIdsCats*/
    public String[] getIdsCategorie() {
        if (idsCategorie==null)
        {
            return ca.getIdsCats();
        } else
            return idsCategorie;
    }
    
    /** ritorna nome della categoria
     *@see CacheApplet#getNomeCat*/
    public String getNomeCategoria(String idCat) {
        return ca.getNomeCat(idCat);
    }
    
    /**  ritorna note della categoria */
    public String getNoteCategoria(String idCat) {
        return ca.getNoteCat(idCat);
    }
    
    /** ritorna ids dei feed della categoria <br />
     * se non presenti, li richiede ed elabora risposta, quindi li ritorna
     @see CacheApplet#esistonoFeeds
     @see #richiediFeedsdellaCat
     @see #salvaFeedsRicevuti
     */
    public String[] getIdsFeedsDellaCat(String idCat) {
        
        // titoli caricati ?
        if (!ca.esistonoFeeds(idCat)) {
            richiediFeedsdellaCat(idCat);
            receiveDocument();
            salvaFeedsRicevuti();
        }
        return ca.getIdsFeedsDellaCat(idCat);
    }
    
    /** ritorno nome feed leggendo da
      @see CacheApplet#getNomeFeed */
    public String getNomeFeed(String idFeed) {
        return ca.getNomeFeed(idFeed);
    }
    
    /** ritorno ids titoli <br />
     * Se non presenti, li richiedo e li salvo, poi li restituisco 
     @see CacheTestiETitoli#esistonoTitoliNelFeed
     @see #richiediTitoliDelFeed
     @see #salvaTitoliRicevuti
     */
    public String[] getIdsTitoli(String idFeed) {
        
        if (!ctt.esistonoTitoliNelFeed(idFeed)) {
            richiediTitoliDelFeed(idFeed);
            receiveDocument();
            salvaTitoliRicevuti();
        }
        
        return ctt.getIdsTitoli(idFeed);
    }
    
    /** restituisce valore leggendo da CacheTestiETitoli */
    public String getNomeDelTitolo(String idFeed, String idTitolo) {
        return ctt.getNomeDelTitolo(idFeed, idTitolo);
    }
    
    /** restituisce valore leggendo da CacheTestiETitoli  */
    public String getDataDelTitolo(String idFeed, String idTitolo) {
        return ctt.getDataTitolo(idFeed, idTitolo);
    }
    
    /** restituisce descrizione del titolo<br> se non è in cache
     effettua la richiesta all'application e salva,quindi la restituisce
     * @see CacheTestiETitoli#getDescription
     * @see #richiediDescription
     * @see #salvaDescription
     */
    public String getDescriptionDelTitolo(String idFeed, String idTitolo) {
        String desc =  ctt.getDescription(idFeed, idTitolo);
        if (desc.equals("")) {
            richiediDescription(idFeed, idTitolo);
            receiveDocument();
            salvaDescription();
            //debug("description non presente in cache, la richiedo");
            desc =  ctt.getDescription(idFeed, idTitolo);
        }
        return desc;
    }
    
    
    /** invia richiesta di accesso (con username) all'application */
    public void richiediCats(String username) {
        sendDocument(creaDocumento("accesso","username",username,""));
    }
    
    /** legge l'elenco delle categorie ricevute dall'application e salva in cacheApplet */
    public void salvaCategorie() {
        /** processo
         * <elenco_cat>
         * <cat nome="sport" note="" feeds="3" id="c01"></cat>
         * <cat nome="italia" feeds="0" id="c02"></cat>
         * </elenco_cat>
         */
        Element radice = documentReceived.getDocumentElement();
        if (!radice.getTagName().equals("elenco_cat"))
        {
            debug("non ricevo elenco categorie");
            return;
        }
        System.out.println(radice.getTagName());
        NodeList nodiCat = radice.getElementsByTagName( "cat" );
        
        //per ogni nodo <cat>
        for (int i=0; i< nodiCat.getLength(); i++) {
            Element catCorr = (Element)nodiCat.item( i );
            
            //costruisco oggetto Categoria (lib4, solo per applet)
            debug("ricevuta categoria ["+catCorr.getAttribute("nome")+"]["+catCorr.getAttribute("id")+"]");
            ca.addCat( catCorr.getAttribute("id"),
                    catCorr.getAttribute("nome"),
                    catCorr.getAttribute("note") );
            
        }
    }
    
    
    /** invio messaggio di richiesta feeds della categoria */
    public void richiediFeedsdellaCat(String idCat) {
        /* //es: <get_feeds idcat="c02"></get_feeds> */
        sendDocument(creaDocumento("get_feeds","idcat",idCat,""));
        
    }
    
    /** leggo feeds ricevuti e salvo in cacheapplet  */
    public void salvaFeedsRicevuti() {
         /*
          * leggi:
         <elenco_feeds idcat="c1">
           <feed nome="gazzetta.it" id="f1"></feed>
           <feed nome="ansa.it sport" id="f2"></feed>
         </elenco_feeds>
          */
        
        Element radice = documentReceived.getDocumentElement();
        //if (!radice.getTagName().equals("elenco_feeds"))
        //    errore("");
        
        NodeList nodiCat = radice.getElementsByTagName( "feed" );
        String idCat = radice.getAttribute("idcat");
        if (idCat.equals("")) {
            debug("id della cat non leggibile, annullo"); return; }
        
        //per ogni nodo <cat>, aggiungo a mappa interna
        for (int i=0; i< nodiCat.getLength(); i++) {
            Element feedCorr = (Element)nodiCat.item( i );
            
            //costruisco oggetto Categoria (lib4, solo per applet)
            String  id = feedCorr.getAttribute("id");
            String nome = feedCorr.getAttribute("nome") ;
            ca.addFeed(idCat, id, nome);
            debug("ricevuto feed ["+id+"]["+nome+"]");
        }
        
    }
    
    
    /** invio richiesta titoli del feed */
    public void richiediTitoliDelFeed(String idFeed) {
        //es: <get_titoli idfeed="f1"></get_titoli>
        sendDocument(creaDocumento("get_titoli","idfeed",idFeed,""));
    }
    
    /** leggo titoli inviati dall'Application e li salvo in cacheTitolieTesti */
    public void salvaTitoliRicevuti() {
        /** processo
         * <titoli idfeed="c1">
         * <titolo nome="kaka 4ever" id="1" data="12 set 07"></titolo>
         * <titolo nome="italia" id="2" data="12 set 07"></titolo>
         * </titoli>
         */
        
        Element radice = documentReceived.getDocumentElement(); //titoli
        String idFeed = radice.getAttribute("idfeed");
        
        //
        ListaElementiRss ler = new ListaElementiRss();
        
        //itero su nodi <titolo>
        NodeList nodiTitoli = radice.getElementsByTagName( "titolo" );
        for (int i=0; i< nodiTitoli.getLength(); i++) {
            Element elemTitoloCorr = (Element)nodiTitoli.item( i );
            String idS = elemTitoloCorr.getAttribute("id");
            if (idS.equals("") /*|| idS.length() < 1*/) {
                debug("indice nullo !!");
            } else {
                //creo RssElemento con id, nome, data del NODO corrente
                RssElemento elemRss = new RssElemento(elemTitoloCorr.getAttribute("nome"), elemTitoloCorr.getAttribute("data"));
                elemRss.setId(idS);
                
                debug("ricevuto titolo ["+idS+"]["+elemTitoloCorr.getAttribute("nome")+"]");
                
                //aggiungo nella giusta posizione nella LinkedList di ListaElementiRss
                Integer in = new Integer(idS);
                if (in == null) {
                    debug("indice non valido !!");
                } else {
                    //ler.set(in.intValue(), elemRss);
                    ler.aggiungi(elemRss);
                    debug("aggiunto titolo");
                }
            }
        }
        //aggiungo ListaElementiRss in cache
        ctt.settaElementiRss(idFeed, ler);
    }
    
    
    /** invio messaggio di richiesta description del titolo del feed */
    public void richiediDescription(String idFeed, String nTitolo) {
        // invio messaggio: <get_testo idfeed="f1" id="1"></get_testo>
        Document doc = builder.newDocument();
        
        Node root = doc.createElement("get_testo");
        Element elemRoot = (Element)root;
        elemRoot.setAttribute("idfeed", idFeed);
        elemRoot.setAttribute("id", nTitolo);
        doc.appendChild(root);
        sendDocument(doc);
    }
    
    /** leggo la description inviata dall'Application e la salvo in cacheApplet */
    public void salvaDescription() {
        /*processo <description idfeed="f1" id="0">
           bla bla
         </description>*/
        Node root = documentReceived.getDocumentElement(); //description
        Element elemRoot = (Element)root;
        String idFeed = elemRoot.getAttribute("idfeed");
        String id = elemRoot.getAttribute("id");
        String description = root.getTextContent();
        debug("ricevo description ["+id+"]["+idFeed+"]["+description+"]");
        ctt.setDescription( idFeed,  id, description);
    }
    
    /*
    //ritorna description per il titolo (n) del feed (idFeed)
    public String getDescription(String idFeed, String n) {
        // se c'è in cache...
        return ctt.getDescription(idFeed, n);
        //altrimenti fai richiesta e salva
    }
    */
    
    /** crea un documento con solo un nodo e attributo */
    private  Document creaDocumento(String nomeRoot, String nomeAttr, String valAttr, String textInside) {
        Document doc = builder.newDocument();
        
        Node root = doc.createElement(nomeRoot);
        Element elemRoot = (Element)root;
        if (!nomeAttr.equals(""))
            elemRoot.setAttribute(nomeAttr,valAttr);
        doc.appendChild(root);
        return doc;
    }
    
    /**
     *  Invia documento all'Application
     *  @param message Richiesta da inviare
     */
    public void sendDocument(Document message){
        
        try {
            //  Serializzo il document da inviare attraverso la socket
            serializer.transform(new DOMSource(message),new StreamResult(socket.getOutputStream()));
            //  Aggiungo al messaggio da inviare un carattere di "fine messaggio"
            MultiXML.terminate(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerException  tre) {
            tre.printStackTrace();
        }
    }
    
    /**
     *  Metodo per la ricezione via Socket dei documenti
     *  XML ricevuti dall'Application
     */
    void receiveDocument(){
        
        try{
            //  Parso il documento ricevuto sulla socket cercando il carattere di fine documento
            documentReceived = builder.parse(MultiXML.getTerminatingInputStream(socket.getInputStream()));
            //  Chiudo la socket chiedendo anche all'Application di farlo
            //chiudiSocket();
            //  Analizzo il documento ricevuto
            //parseDocument(documentReceived);
        } catch(SAXException saxe){
            saxe.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    
    /**
     *  Chiude la connessione stabilita attraverso la socket con l'Application
     *  chidendo anche a quest'ultima di fare altrettanto
     */
    public void chiudiSocket(){
        
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
    
    /**
     *  Inizializza la socket per la comunicazione con
     *  l'Application.
     */
    public boolean inizializzaSocket() {
        //  ...per istanziare la socket
        try{
            socket = new Socket(conf.getSERVERADD(), conf.getAPPPORT());

        } catch(UnknownHostException uhe){
            uhe.printStackTrace();
            return false;
        } catch(IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
        
    }
    
    /** controllo che il documento ricevuto non sia un errore */
    public  boolean esisteErrore() {
        Element radice = documentReceived.getDocumentElement();
        
        if ( radice.getTagName().equals("errore") ) {
            erroreCorrente = new String("ERRORE : " + radice.getAttribute("msg"));
            return true;
        } else
            return false;
        
    }
    
    /** ritorna errore settato */
    public String getErrore() {
        return erroreCorrente;
    }
    
    /** per debug locali */
    public void debug(String s) {
       //System.out.println(s);
    }
    
}
