package twi518.applet1;

import twi518.cacheApplet.CacheApplet;
import twi518.cacheApplet.CacheAppletFactory;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.Color.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Applet extends javax.swing.JApplet
        implements TreeSelectionListener,
        ActionListener {
    boolean operazioneInCorso = false;
    private CacheAppletFactory caf = null;
    private JEditorPane txtArea;
    private JLabel debugLabel;
    private JButton connButton;
    private DefaultMutableTreeNode nodoVuoto = new DefaultMutableTreeNode("nessun titolo fonte nn disp");
    private DefaultMutableTreeNode topNode;
    private Container cp;
    private JTree jt;
    private HashMap<DefaultMutableTreeNode, String> hmap = new HashMap<DefaultMutableTreeNode, String>();
    private HashMap<DefaultMutableTreeNode, CTitolo> hmapTitoli = new HashMap<DefaultMutableTreeNode, CTitolo>();
    private String[] idCats = null;
    private String[] idFeeds = null;
    private String[] idsTitoli = null;
    private String USERNAME = "";

    public void init() {
        USERNAME = getParameter("username");
        inizializzaCacheEFactory();
        txtArea = new JEditorPane("text/html", "");
        debugLabel = new JLabel("clicca per la connessione");
        connButton = new JButton("Connetti");
        connButton.setFocusPainted(true);
        connButton.addActionListener(this);
        cp = getContentPane();
        cp.setLayout(new BorderLayout());
        Container altoCont = new Container();
        altoCont.setLayout(new FlowLayout());
        altoCont.add(connButton);
        altoCont.add(debugLabel);
        cp.add(BorderLayout.NORTH, altoCont);
        topNode = new DefaultMutableTreeNode("Categorie");
        jt = new JTree(topNode);
        jt.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        Container centerCont = new Container();
        centerCont.setLayout(new GridLayout(2, 1));
        centerCont.add(new JScrollPane(jt));
        centerCont.add(new JScrollPane(txtArea));
        cp.add(BorderLayout.CENTER, centerCont);
    }

    public void destroy() {
        caf.chiudiSocket();
    }

    public void actionPerformed(ActionEvent e) {
        debug("In connessione...");
        if (inizializzaJtree()) {
            debug("scegli una categoria");
            connButton.setText("Connesso");
            connButton.setEnabled(false);
            jt.expandRow(0);
        } else
            debug("Impossibile connettersi (" + caf.getErrore() + ")");
    }

    public void inizializzaCacheEFactory() {
        caf = new CacheAppletFactory(new CacheApplet());
        idCats = null;
        idFeeds = null;
        idsTitoli = null;
    }

    public boolean inizializzaJtree() {
        if (caf.accesso(USERNAME)) {
            jt.addTreeSelectionListener(this);
            caricaCategorie(topNode);
            return true;
        } else {
            debug("errore " + caf.getErrore());
            return false;
        }
    }

    private void caricaCategorie(DefaultMutableTreeNode nodoRoot) {
        DefaultMutableTreeNode categoria = null;
        idCats = caf.getIdsCategorie();
        for (int i = 0; i < idCats.length; i++) {
            categoria = new DefaultMutableTreeNode(caf.getNomeCategoria(idCats[i]));
            nodoRoot.add(categoria);
            hmap.put(categoria, idCats[i]);
        }
    }

    public void caricaFeed(DefaultMutableTreeNode categoria) {
        DefaultMutableTreeNode feed = null;
        String idCat = (String) hmap.get(categoria);
        idFeeds = caf.getIdsFeedsDellaCat(idCat);
        for (int i = 0; i < idFeeds.length; i++) {
            feed = new DefaultMutableTreeNode(caf.getNomeFeed(idFeeds[i]));
            categoria.add(feed);
            hmap.put(feed, idFeeds[i]);
        }
    }

    public void caricaDescrizione(DefaultMutableTreeNode nodoTitolo) {
        CTitolo t = hmapTitoli.get(nodoTitolo);
        String desc = caf.getDescriptionDelTitolo(t.idFeed, t.nTitolo);
        txtArea.setText(desc);
    }

    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) jt.getLastSelectedPathComponent();
        if (node == null)
            return;
        if (node.equals(nodoVuoto)) {
            debug("clicca su un titolo valido !");
            return;
        }
        if (operazioneInCorso) {
            debug("attendere la fine della operazione in corso...");
            return;
        }
        if (node.getLevel() == 1) {
            caricaFeed(node);
            debug("caricamento feeds effettuato");
            jt.expandPath(e.getPath());
        }
        if (node.getLevel() == 2) {
            debug("caricamento titoli in corso...");
            new caricaTitoliThread(node, e.getPath()).start();
            jt.expandPath(e.getPath());
        }
        if (node.getLevel() == 3) {
            caricaDescrizione(node);
            debug("caricamento descrizione effettuato");
            jt.expandPath(e.getPath());
        }
    }

    public void debug(String s) {
        debugLabel.setText(s);
    }

    class caricaTitoliThread extends Thread {
        DefaultMutableTreeNode feed;
        TreePath tp;

        public caricaTitoliThread(DefaultMutableTreeNode feed, TreePath tp) {
            this.feed = feed;
            this.tp = tp;
        }

        public void run() {
            operazioneInCorso = true;
            try {
                caricaTitoli(feed);
                debug("caricamento titoli effettuato");
                jt.expandPath(tp);
                operazioneInCorso = false;
            } catch (Exception e) {
                debug("titoli non disponibili");
                jt.expandPath(tp);
                operazioneInCorso = false;
            }
            operazioneInCorso = false;
        }

        public void caricaTitoli(DefaultMutableTreeNode nodoFeed) {
            DefaultMutableTreeNode nodoTitolo = null;
            String idFeed = (String) hmap.get(nodoFeed);
            idsTitoli = caf.getIdsTitoli(idFeed);
            if (idsTitoli.length == 0)
                nodoFeed.add(nodoVuoto);
            else {
                for (int i = 0; i < idsTitoli.length; i++) {
                    nodoTitolo = new DefaultMutableTreeNode(caf.getNomeDelTitolo(idFeed, idsTitoli[i]).replace("&nbsp;", " ") + " [" + caf.getDataDelTitolo(idFeed, idsTitoli[i]) + "]");
                    nodoFeed.add(nodoTitolo);
                    hmapTitoli.put(nodoTitolo, new CTitolo(idFeed, idsTitoli[i]));
                }
            }
        }
    }
}

class CTitolo {
    public String idFeed;
    public String nTitolo;

    public CTitolo(String idFeed, String nTitolo) {
        this.idFeed = idFeed;
        this.nTitolo = nTitolo;
    }
}