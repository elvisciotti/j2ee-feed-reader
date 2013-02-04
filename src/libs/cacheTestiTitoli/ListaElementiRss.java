package twi518.cacheTestiTitoli;

import java.util.*;

/** Memorizza e gestisce la lista di elementi Rss, fornendo funzioni di ricerca e settagio in base all'ID del titolo
 * @see RssElemento
 * @author Elvis
 */
public class ListaElementiRss {
    /** lista di elementi RssElemento
     @see RssElemento */
    public LinkedList<RssElemento> elementiRss = new LinkedList<RssElemento>();
    
    
    /** aggiunge l'elementoRss alla lista */
    public void aggiungi(RssElemento elementoRss) {
        elementiRss.add(elementoRss);
    }
    
    /** ritorna iteratore su elementiRss*/
    public Iterator<RssElemento> getIterator() {
        return elementiRss.iterator();
    }
    
    /** ritorna array di ids dei titoli presenti */
    public String[] getIdsTitoli() {
        int size= elementiRss.size();
        String[] ids = new String[size];
        for (int i=0; i<size; i++) {
            ids[i] = elementiRss.get(i).getId();
        }
        return ids;
    }
    
    /** ritorna nome del titolo */
    public String getNomeTitolo(String idTitolo) {
        int size= elementiRss.size();
        String[] ids = new String[size];
        for (int i=0; i<size; i++) {
            if (elementiRss.get(i).getId().equals(idTitolo) )
                return elementiRss.get(i).getTitle();
        }
        return "";
    }
    
    /** ritorna data del titolo */
    public String getDataTitolo(String idTitolo) {
        int size= elementiRss.size();
        String[] ids = new String[size];
        for (int i=0; i<size; i++) {
            if (elementiRss.get(i).getId().equals(idTitolo) )
                return elementiRss.get(i).getPubDate();
        }
        return "";
    }
    
    /** ritorna descrizione del titolo */
    public String getDescTitolo(String idTitolo) {
        int size= elementiRss.size();
        String[] ids = new String[size];
        for (int i=0; i<size; i++) {
            if (elementiRss.get(i).getId().equals(idTitolo)  )
                return elementiRss.get(i).getDescription();
        }
        return "";
    }
    
    /** setta la descrizione per il titolo specificato*/
    public void setDesc(String idTitolo, String desc) {
        int size = elementiRss.size();
        //String[] ids = new String[size];
        //debug("[inizio settaggio!!!!!!!!][" + size + "]["+idTitolo+"]");
        for (int i=0; i<size; i++) {
            String idCorr = elementiRss.get(i).getId();
            //debug(idCorr);
            if ( idCorr.equals(idTitolo) ) {
                elementiRss.get(i).setDesc(desc);
               // debug("ho salvato: " + elementiRss.get(i).description);
                //elementiRss.get(i).description = desc;
            }
        }
        
    }
    
    /** 
    public void stampa() {
        Iterator it = elementiRss.iterator();
        while (it.hasNext()) {
            System.out.println((RssElemento)it.next());
        }
    }*/
    
    /** ritorna elemento all'indice specificato nella lista */
    public RssElemento get(int i) {
        if (i<elementiRss.size())
            return elementiRss.get(i);
        else
            return new RssElemento("","","");
        
    }
    
    /** setta elemento nella lista */
    public void set(int i, RssElemento re) {
        elementiRss.set(i, re);
    }
    
    /** ritorna size lista*/
    public int getSize() {
        return elementiRss.size();
    }
    
    /** funzione per debug */
    public void debug(String s) {
        //System.out.println(s);
    }
    
}
