package twi518;

import java.net.*; // ServerSocket, Socket
import java.io.*; // IOException
import java.util.*; // IOException
import javax.xml.parsers.*; // DocumentBuilderFactory, DocumentBuilder
import org.xml.sax.*; // InputSource
import org.w3c.dom .*; // Document
import javax.xml.transform.*; // Transformer, TransformerFactory, TransformerException
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*; 
import twi.*; // librerie per messaggi xml su stream
import twi518.cacheTestiTitoli.CacheTestiETitoli;
import twi518.cacheTestiTitoli.ListaElementiRss;
import twi518.cacheTestiTitoli.RssElemento;
import twi518.conf.*;
import twi518.NewsXmlFactory.*;
import twi518.*;

/** Gestore delle richieste dei client.
 *  Ogni volta che un client richiede un servizio, la classe Application
 *  genera un ApplicationThread che si occupa di soddisfare la richiesta.
 *  Subito dopo la creazione del ApplicationThread, l'Application torna in
 *  ascolto sulla porta definita nella costante PORT
 *  @author Elvis
 */
public class Application {
    
    /** Avvia Application: gestisce un serverSocket
     *  che si preoccupa di attivare un ApplicationThread per ogni
     *  connessione aperta sul socket.
     *  @param args array di stringhe (attualmente non viene effettuata
     *  nessuna valutazione di args)
     */
    public static void main( String args[] ) {
        try {
            ServerSocket serverSocket = new ServerSocket(Config.getAPPPORT());
            System.out.println("[SERVER PRONTO]");
            while ( true ) {
                // Attendo una connessione ascoltando la ServerSocket
                Socket clientSocket = serverSocket.accept();
                
                // Genero un thread che gestisca la richiesta
                ApplicationThread applicationThread =
                        new ApplicationThread( clientSocket );
                applicationThread.start();
            }
        } catch ( IOException e ) {
            e.printStackTrace();
            System.exit( 1 );
        }
        
    }
}


/** Classe che valuta e svolge le richieste dei client.
 *  Tale classe estende la classe Thread, in particolare viene effettuato
 *  l'override del metodo run() della superclasse.
 */
class ApplicationThread extends Thread {
    
    // campi privati
    private Socket socket;
    private DocumentBuilder builder;
    private Transformer serializer;
    private boolean chiusurasocket = false;
    //
    private String username = null;
    private Config conf = new Config();
    
    //ogni thread ne istanzia uno
    private NewsXmlFactory nxf = null;
    
    private CacheTestiETitoli cacheTestiTitoli;
    
    //MappaCategorie ca = new MappaCategorie();
    
    /** Costruttore
     *  @param s socket sul quale il thread deve ricevere la richiesta da parte
     *  del client
     *
     */
    public ApplicationThread(Socket s) {
        socket = s;
        try {
            // creo l'oggetto DocumentBuilder
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            
            // creo l'oggetto Transformer utilizzato per serializzare su
            // stream oggetti Document
            serializer = TransformerFactory.newInstance().newTransformer();
            
        } catch ( ParserConfigurationException pce ) {
            pce.printStackTrace();
        } catch ( TransformerConfigurationException tce ) {
            tce.printStackTrace();
        }
        
        
    }
    
    public void run() {
         /* NOTA BENE: chiusurasocket � la variabile booleana che
        indica quando l'interazione � da considerarsi conclusa
        e quindi il socket va chiuso
        Gestite OPPORTUNAMENTE il suo valore a seconda di quando
        effettuate la chiusura della connessione */
        
        try {
            while (! chiusurasocket ) {
                
                // Costruisco il documento arrivato
                Document document = builder.parse(
                        MultiXML.getTerminatingInputStream(socket.getInputStream()) );
                
                // Elaboro il documento
                documentReceived( document );
            }
            socket.close();
        } catch ( SAXException e ) {
            e.printStackTrace();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    } // fine del metodo run()
    
      /** Gestione dei messaggi arrivati al server
     *   @param message L'oggetto Document creato a partire dal documento
     *   ricevuto sull'input stream del socket
     */
    private void documentReceived(Document message) {
        // Importazione del nodo radice dal nuovo documento.
        Element radice = message.getDocumentElement();
        String richiestaClient = radice.getTagName();
        
        
        if (richiestaClient.equals("accesso")) {
            //es: <accesso username="elvis"></accesso>
            //leggo username
            //username = radice.getAttribute("username");
            //invio elenco categorie (o errore)
            inviaElencoCats(radice.getAttribute("username"));
        }
        
        else if (richiestaClient.equals("get_feeds")) {
            //es: <get_feeds idcat="c02"></get_feeds>
            inviaElencoFeeds( radice.getAttribute("idcat") );
            
        } else if (richiestaClient.equals("get_titoli")) {
            //es: <get_titoli idfeed="c02"></get_titoli>
           
            inviaElencoTitoli( radice.getAttribute("idfeed") );
        }
        
        else if (richiestaClient.equals("get_testo")) {
            //es: <get_testo idfeed="f1" id="1"></get_testo>
            String idFeed =  radice.getAttribute("idfeed");
            String id =  radice.getAttribute("id");
            if (idFeed.equals("") || id.equals("") )
                debug("richiesta di testo non valida !!");
            inviaTesto(idFeed, id );
        }
        else if (richiestaClient.equals("closeSocket")){
            System.out.println("chiudo socket");
            chiusurasocket = true;
        } else if (richiestaClient.equals("esci")){
            System.out.println("shutdown application...");
            //chiusurasocket = true;
            System.exit(1);
        }
        // Codice per soddisfare Richiesta2
    }
    
    
    /** Crea documento per risposte brevi, con un solo attributo */
    private  Document creaDocumento(String nomeRoot, String nomeAttr, String valAttr)
    {
        Document doc = builder.newDocument();
        Node root = doc.createElement(nomeRoot);
        Element elemRoot = (Element)root;
        elemRoot.setAttribute(nomeAttr,valAttr);
        doc.appendChild(root);
        //System.out.println("doc creata. invio  ("+doc+")");
        return doc;
    }

    
    /**  Istanzia oggetto NewsXmlFactory leggendo da file relativo all'utente e 
     * invia elenco delle categorie all'applet, leggendo da <code>nxf</code> interno.
     *  Se il file non esiste (utente non ha inserito niente o tentativo di accesso con utente non esistente) invia messaggio di <b>errore</b><br/>
     */
    private void inviaElencoCats(String username)
    {
        //controllo che username sia valido
        if (username==null || username.equals("")) {
            inviaErrore("username non valido"); return; }
        //creo nome file da aprire
        String xmlFilePath = new String(conf.getWEBDIR() +  "/xml/" + username + ".xml");
        File f = new File(xmlFilePath);
        //file esistente ?
        if (f.exists()) {
            //istanzio NewsXmlFactory (verr� costruito oggetto relativo a tutto il file xml
            nxf = new NewsXmlFactory(username + ".xml");
            cacheTestiTitoli = new CacheTestiETitoli();
             /*
              * Verr� inviato un messaggio xml con questa sintassi:
             <elenco_cat>
               <cat nome="sport" note="" feeds="3" id="c01"></cat>
               <cat nome="italia" feeds="0" id="c02"></cat>
             </elenco_cat>
              */
            Document doc = builder.newDocument();
            Node root = doc.createElement("elenco_cat");
            //creo nodi "<cat>"
            CategoriaFeed[] cf = nxf.getElencoCat();
            for (int i=0; i<cf.length; i++) {
                Node cat = doc.createElement("cat");
                Element elemCat = (Element)cat;
                
                elemCat.setAttribute("id", cf[i].getId());
                elemCat.setAttribute("nome", cf[i].getNome());
                elemCat.setAttribute("note", cf[i].getNote());
                //String nFeeds = new String(new Integer(cf[i].getFeeds().length).toString());
                elemCat.setAttribute("feeds", ""+cf[i].getFeeds().length+"") ;
                root.appendChild(cat);
            }
            doc.appendChild(root);
            sendDocument(doc);
        } else {
            inviaErrore("Impossibile leggere i dati per l'utente ("+username+")");
        }
    }
    
    /** Invia l'elenco dei feeds della categoria */
    private void inviaElencoFeeds(String idCat) {
        /*
         * invia:
         <elenco_feeds idcat="c1">
           <feed nome="gazzetta.it" id="f1"></feed>
           <feed nome="ansa.it sport" id="f2"></feed>
         </elenco_feeds>
         */
        Document doc = builder.newDocument();
        Node root = doc.createElement("elenco_feeds");
        ((Element)root).setAttribute("idcat", idCat);
        
        CategoriaFeed cf = nxf.getCatById(idCat);
        Feed[] feeds = cf.getFeeds();
        //creo nodi "<feed>"
        for (int i=0; i<feeds.length; i++) {
            Node feed = doc.createElement("feed");
            Element elemFeed = (Element)feed;
            
            elemFeed.setAttribute("id", feeds[i].getId());
            elemFeed.setAttribute("nome", feeds[i].getNome());
            
            root.appendChild(feed);
            //aggiungo all'elenco interno per le richieste successive
            //ca.addCat( cf[i].getId(), cf[i].getNome(), cf[i].getNote() );
        }
        doc.appendChild(root);
        sendDocument(doc);
        
    }
    
    /** Invia elenco titoli disponibili per il feed (parsing url)
     @see RssReader
     */
    private void inviaElencoTitoli(String idFeed) {
        /*
         * invia:
         <titoli idfeed="f1">
           <titolo nome="kaka 4ever" id="1" data="12 set 07"></titolo>
           <titolo nome="italia" id="2" data="12 set 07"></titolo>
         </titoli>
         */
        
        // i "tN" sono univoci per il feed !!!
        
        Document doc = builder.newDocument();
        Node root = doc.createElement("titoli");
        ((Element)root).setAttribute("idfeed", idFeed);
        
        //se il feed non � stato caricato in memoria, lo carico !
        if (!cacheTestiTitoli.esistonoTitoliNelFeed(idFeed)) {
            //prendo feed da nxf
            Feed f = nxf.getFeedById(idFeed);
            //prendo url dal feed
            String urlPr = f.getUrlPrimaria();
            String[] altreUrls = f.getUrls();
            //costruisco elenco completo urlsAll
            String[] urlsAll = new String[altreUrls.length + 1];
            urlsAll[0] = urlPr;
            for (int i=1; i< urlsAll.length; i++)
            {
                urlsAll[i] = altreUrls[i-1];
            }
            //dico alla cache di caricare i contenuti per il feed
            // creo ListaElementiRss relativi alla fonti [ RssReader#getListaElementiRss() ]
            RssReader rr = new RssReader();
            
            ListaElementiRss ler = rr.getListaElementiRss(urlsAll);
            if (ler.getSize() == 0)
            { 
                //invio rr.getErrore(); ?
            }
            //anche se vuoto, lo setto cmq, in modo da non richiederlo
            cacheTestiTitoli.settaElementiRss(idFeed, ler);
        }
        
        //creo nodo principale
        Node titolo = doc.createElement("titolo");
        Element elemTitolo = (Element)titolo;
        root.appendChild(titolo);
        
        //costruisco i nodi <titolo> interni
        Iterator it = cacheTestiTitoli.getIterator(idFeed);
        while(it.hasNext())
        {
            RssElemento elemRssCorr = ((RssElemento)it.next());
            titolo = doc.createElement("titolo");
            elemTitolo = (Element)titolo;
            
            String nome = elemRssCorr.getTitle();
            elemTitolo.setAttribute("nome",  nome);
            
            String id = elemRssCorr.getId();
            elemTitolo.setAttribute("id", id  );
            
            String data = elemRssCorr.getPubDate();
            elemTitolo.setAttribute("data", data );
            
            if (id!=null && nome!=null)
            {
                root.appendChild(titolo);
                //debug("["+elemRssCorr.getId()+"]["+elemRssCorr.getTitle()+"]");
            }
            
        }
        //appendo root al documento
        doc.appendChild(root);
        //invio documento
        sendDocument(doc);
    }
    
    /**
     * Invia description per il titolo (id) del feed (idFeed)
     */
    private void inviaTesto(String idFeed, String id) {
        //invia testo dell' id-esimo feed
        /*
         * invia:
         <testo idfeed="f1" id="0">
           bla bla
         </testo>
         */
        Document doc = builder.newDocument();
        Node root = doc.createElement("testo");
        Element elemRoot =  (Element)root;
        elemRoot.setAttribute("idfeed", idFeed);
        elemRoot.setAttribute("id", id);
        
        String testo = cacheTestiTitoli.getDescription(idFeed, id);
        if (testo.equals("") || testo == null)
            debug("testo non esistente");
        root.appendChild(doc.createTextNode(testo));
        //appendo root al documento
        doc.appendChild(root);
        //invio documento
        sendDocument(doc);
    }
    
    /** Gestione dei messaggi inviati dal server utilizzando socket
     *   @param message L'oggetto Document da inviare in output
     *   (sull'outputstream del socket)
     */
    public void sendDocument(Document message) {
        try {
            
            // inserisco nell'outputstream il documento message
            serializer.transform(new DOMSource(message),
                    new StreamResult(socket.getOutputStream()));
            
            // inserisco nell'outputstream il simbolo di terminazione
            MultiXML.terminate(socket.getOutputStream());
            
        } catch (IOException  ioe) {
            ioe.printStackTrace();
        } catch (TransformerException  tre) {
            tre.printStackTrace();
        }
    }
    
    /** invia messaggio xml di segnalazione errore */
    private void inviaErrore(String err) {
        Document doc = builder.newDocument();
        Node root = doc.createElement("errore");
        ((Element)root).setAttribute("msg", err);
        doc.appendChild(root);
        sendDocument(doc);
        
    }
    
   /** stampa su System.out per debug interni */
    public void debug(String s) {
        //System.out.println(s);
    }
    
}
