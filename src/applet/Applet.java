package twi518.applet1;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.Color.*;
import java.awt.event.*;
import java.util.*;
import java.net.*;
import java.io.*;
import java.lang.*;
import twi518.cacheApplet.CacheApplet;
import twi518.cacheApplet.CacheAppletFactory;

/** Applet per visualizzare  categorie, feeds e news comunicando con application
 *  Utilizza lib4 e lib5 per mantenere dati in cache ed evitare di richiedeerli se gi� precedentente richiesti
 * 
 * @author Elvis
 * 
 */
public class Applet extends javax.swing.JApplet
        implements	TreeSelectionListener,
        ActionListener{
    //componenti
    private CacheAppletFactory caf = null;
    private JEditorPane  txtArea;
    private JLabel debugLabel;
    private JButton connButton;
    private DefaultMutableTreeNode nodoVuoto = new DefaultMutableTreeNode("nessun titolo fonte nn disp");
    
    private DefaultMutableTreeNode topNode;
    private Container cp;
    private JTree jt;
    
    /** associa nodi a ID (categoria|feed) corrispondente */
    private HashMap<DefaultMutableTreeNode, String> hmap = new HashMap<DefaultMutableTreeNode, String>();
    /** associa nodi a ID titoli news (classe in fondo) */
    private HashMap<DefaultMutableTreeNode, CTitolo> hmapTitoli = new HashMap<DefaultMutableTreeNode, CTitolo>();
    
    //array di ausilio con ids categorie, feeds e titoli
    private String[] idCats = null;
    private String[] idFeeds = null;
    private String[] idsTitoli = null;
    
    //username preso dal cookie e parametro applet
    private String USERNAME = "";
    //x evitare applet 
    boolean operazioneInCorso = false;
    
    /** Inizializza e mostra componenti, il listener della jtree non � ancora aggiunto */
    public void init() {
        //prendo parametro da applet
        USERNAME = getParameter("username");
        inizializzaCacheEFactory();
        
        //istanzio controlli
        txtArea = new JEditorPane("text/html","");
        debugLabel = new JLabel("clicca per la connessione");
        //txtArea.setLineWrap(true); // questa divide il testo su pi� righe
        connButton = new JButton("Connetti");
        connButton.setFocusPainted(true);
        connButton.addActionListener(this);
        
        //main panel
        cp = getContentPane();
        cp.setLayout(new BorderLayout());
        
        //pannelli
        Container altoCont = new Container();
        altoCont.setLayout(new FlowLayout());
        altoCont.add(connButton);
        altoCont.add(debugLabel);
        
        //NORTH
        cp.add(BorderLayout.NORTH,altoCont);
        
        //CENTER
        topNode = new DefaultMutableTreeNode("Categorie");
        jt = new JTree(topNode);
        
        //jt.addTreeSelectionListener(this);
        jt.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        //jt.setRootVisible(true);
        //jt.putClientProperty("JTree.lineStyle", "Angled");
        /*JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(new JScrollPane(jt));
        jt.setMinimumSize(new Dimension(560,100));
        splitPane.setBottomComponent(new JScrollPane(txtArea));
        cp.add(BorderLayout.CENTER, splitPane);
         */
        Container centerCont = new Container();
        centerCont.setLayout(new GridLayout(2,1));
        centerCont.add(new JScrollPane(jt))/*.setPreferredSize(new Dimension(560,150))*/;
        //centerCont.add(new JSplitPane());
        centerCont.add(new JScrollPane(txtArea))/*.setPreferredSize(new Dimension(560,50))*/;
        cp.add(BorderLayout.CENTER, centerCont);
    }
    
    /** Chiude la socket all'unloed dell'applet */
    public void destroy(){
        caf.chiudiSocket();
    }
    
    /** Esegue connessione e stampa risultato in Label accanto, associato a pulsante "connetti" */
    public void actionPerformed(ActionEvent e){
        //JButton b = (JButton)e.getSource();
        debug("In connessione...");
        if(inizializzaJtree()){
            //connessione ok, stampo
            debug("scegli una categoria");
            connButton.setText("Connesso");
            connButton.setEnabled(false);
            //espando nodo "categoria"
            jt.expandRow(0);
        }else
            debug("Impossibile connettersi ("+caf.getErrore()+")");
    }
    
    /** Azzera cache per ricevere dati dall'application */
    public void inizializzaCacheEFactory() {
        caf = new CacheAppletFactory(new CacheApplet());
        idCats = null;
        idFeeds = null;
        idsTitoli = null;
    }
    
    /** Crea albero dei contenuti e carica categorie */
    public boolean inizializzaJtree(){
        if (caf.accesso(USERNAME)) //getParameter("username");
        {
            //in caso di accesso corretto, aggiungo listener
            jt.addTreeSelectionListener(this);
            //carico categorie
            caricaCategorie(topNode);
            return true;
        } else {
            debug("errore " + caf.getErrore());
            return false;
        }
    }
    
    /** Leggo categorie dall'applet, le inserisco nel Jtree e associo listener nodi agli ID categorie ricevute */
    private void caricaCategorie(DefaultMutableTreeNode nodoRoot) {
        DefaultMutableTreeNode categoria = null;
        //carico delle categorie
        idCats = caf.getIdsCategorie();
        //aggiungo categorie alla tree e alla map
        for(int i=0; i<idCats.length; i++){
            // creo oggetto DefaultMutableTreeNode
            categoria = new DefaultMutableTreeNode(caf.getNomeCategoria(idCats[i]));
            // aggiungo alla tree e alla map
            nodoRoot.add(categoria);
            //con la map trovo l'Id della cat in base al feed
            hmap.put(categoria,idCats[i]);
        }
    }
    
    /** Carico i feeds nel nodo, e aggiungo listener dei nodi alla map */
    public void caricaFeed(DefaultMutableTreeNode categoria){
        DefaultMutableTreeNode feed = null;
        //leggo l'ID della cat corrente
        String idCat = (String) hmap.get(categoria);
        // leggo gli id dei feeds
        idFeeds = caf.getIdsFeedsDellaCat(idCat);
        for(int i=0; i<idFeeds.length; i++){
            //creo oggetto Nodo associato al feed
            feed = new DefaultMutableTreeNode(caf.getNomeFeed(idFeeds[i]));
            categoria.add(feed);
            //txtarea.setText("caricamento in corso...");
            //aggiungo il nod alla map
            hmap.put(feed,idFeeds[i]);
        }
    }
    
    
    /** Carico descrizioni */
    public void caricaDescrizione(DefaultMutableTreeNode nodoTitolo){
        //leggo dati del titolo della map (idFeed e nTitolo)
        CTitolo t = hmapTitoli.get(nodoTitolo);
        String desc = caf.getDescriptionDelTitolo(t.idFeed,t.nTitolo);
        txtArea.setText(desc);
    }
    
    /** Listener click JTree sui vari livelli. chiamo funzioni di caricamento in base al livello del nodo nell'albero.<br />
     per il caricamento titoli, lancio thread (calsse interna) */
    public void valueChanged(TreeSelectionEvent e) {
        //vedo in che nodo ho cliccato
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)jt.getLastSelectedPathComponent();
        //questo controllo prima di tutto
        if(node == null)
            return;
        //controllo che non abbia cliccato su nodo "titoli non presenti"
        if (node.equals(nodoVuoto)) {
            debug("clicca su un titolo valido !");
            return;
        }
        //se il thread caricamento titoli � attivo, esce
        if (operazioneInCorso) {
            debug("attendere la fine della operazione in corso...");
            return;
        }
        
        //switch sul livello dei nodi
        //livello 1 = categoria
        if(node.getLevel() == 1){
            caricaFeed(node);
            debug("caricamento feeds effettuato");
            jt.expandPath(e.getPath());
        }
        
        //livello 2 = titolo
        if(node.getLevel() == 2){
            debug("caricamento titoli in corso...");
            new caricaTitoliThread(node, e.getPath()).start();
            //caricaTitoli(node);
            jt.expandPath(e.getPath());
        }
        
        //livello 3 = descrizione
        if(node.getLevel() == 3){
            caricaDescrizione(node);
            debug("caricamento descrizione effettuato");
            jt.expandPath(e.getPath());
        }
    }
    
    /** stampa sulla label a fianco del pulsante di connessione */
    public void debug(String s) {
        debugLabel.setText(s);
    }
    
    /** classe interna estende da Thread<br />
     * consente di caricare i titoli mentre la JLabel mostra il messaggio di caricamento,
     evitando di bloccare l'esecuzione dell'applet */
    class caricaTitoliThread extends Thread {
        DefaultMutableTreeNode feed;
        TreePath tp;
        
        /** costruttore che prende argomenti da passare alla funzione di caricamento titoli  */
        public caricaTitoliThread(DefaultMutableTreeNode feed, TreePath tp) {
            this.feed = feed;
            this.tp = tp;
        }
        
        /** thread run */
        public void run() {
            //blocco altre operazioni
            operazioneInCorso = true;
            //carico titoli
            try {
                caricaTitoli(feed);
                debug("caricamento titoli effettuato");
                jt.expandPath(tp);
                operazioneInCorso = false;
            } catch (Exception e) {
                //in caso di eccezione, stampo che i titoli nn sono disponibili
                debug("titoli non disponibili");
                jt.expandPath(tp);
                operazioneInCorso = false;
            }
            //sblocco listener JTree per altre operaizoni
            operazioneInCorso = false;
        }
        
        /** funzione lanciata nella run, carica titoli e associa listener nella map  */
        public void caricaTitoli(DefaultMutableTreeNode nodoFeed) {
            DefaultMutableTreeNode nodoTitolo = null;
            //leggo idFeed associato
            String idFeed = (String) hmap.get(nodoFeed);
            //chiedo titoli del feed all' application
            idsTitoli = caf.getIdsTitoli(idFeed);
            
            //nessun titolo
            if (idsTitoli.length == 0)
                nodoFeed.add(nodoVuoto);
            else{
                //leggo titoli, quindi creo nodi e aggiungo a map
                for(int i=0; i<idsTitoli.length; i++){
                    nodoTitolo = new DefaultMutableTreeNode(caf.getNomeDelTitolo(idFeed, idsTitoli[i]).replace("&nbsp;"," ")+ " [" + caf.getDataDelTitolo(idFeed,idsTitoli[i])+"]");
                    //add nodo titolo al nodo feed
                    nodoFeed.add(nodoTitolo);
                    //aggiungo alla map dei titoli
                    hmapTitoli.put(nodoTitolo, new CTitolo(idFeed, idsTitoli[i]));
                }
            }
            
        }
        
    }
    
    
}

/** classe interna rappresentante il titolom
 * che viene identificato univocamente con l'ID del feed e ID del titolo */
class CTitolo {
    public String idFeed;
    public String nTitolo;
    
    public CTitolo(String idFeed, String nTitolo) {
        this.idFeed = idFeed;
        this.nTitolo = nTitolo;
    }
    
}