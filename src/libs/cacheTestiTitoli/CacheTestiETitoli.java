package twi518.cacheTestiTitoli;
import java.util.*;
/**
 * Classe che memorizza i Titoli letti dai feeds.<br />
 * Libreria usata da applet e application per mantenere contenuti in memoria<br />
 * @see ListaElementiRss  
 * @see RssElemento
 * @author Elvis
 */
public class CacheTestiETitoli {
    
    /** mappa l'id del Feed su elementi rss letti
     @see ListaElementiRss */
    
    private HashMap<String, ListaElementiRss> mappaFeed = null;
    
    
    /**
     * cotruttore, inizializza hashmap
     */
    public CacheTestiETitoli() {
        mappaFeed = new HashMap<String, ListaElementiRss>();
        
    }
    
    /** controllo esistenza titoli nel feed (cio� se esiste chiave nella map) */
    public boolean esistonoTitoliNelFeed(String idFeed)
    {
        return mappaFeed.containsKey(idFeed);
    }
    
    /** aggiunte alla mappa gli elementi rss (titoli) usando l'ID come chiave */
    public void settaElementiRss(String idFeed, ListaElementiRss ler)
    {
        mappaFeed.put(idFeed, ler);
    }
    
    /** ritorna ListaElementiRss mappato sul feed   */
    public ListaElementiRss getListaElemRss(String idFeed)
    {
        return mappaFeed.get(idFeed);
    }
    
    /** ritorna array degli ids per i feeds 
     @see ListaElementiRss#getIdsTitoli */
    public String[] getIdsTitoli(String idFeed)
    {
        return mappaFeed.get(idFeed).getIdsTitoli();
    }
    
   /** ritorna nome titolo
     @see ListaElementiRss#getNomeTitolo */
    public String getNomeDelTitolo(String idFeed, String idTitolo)
    {
        return mappaFeed.get(idFeed).getNomeTitolo(idTitolo);
    }
    
    /** ritorna nome titolo
     @see ListaElementiRss#getDataTitolo */
    public String getDataTitolo(String idFeed, String idTitolo)
    {
        return mappaFeed.get(idFeed).getDataTitolo(idTitolo);
    }
    
   /*
    public String getDescTitolo(String idFeed, String idTitolo)
    {
        return mappaFeed.get(idFeed).getDescTitolo(idTitolo);
    }*/
    
     /** ritorna descrizione (description o testo) del titolo
     @see ListaElementiRss#getDescTitolo */
    public String getDescription(String idFeed, String n)
    {
        ListaElementiRss listaElementiDelFeed = mappaFeed.get(idFeed);
        
        int nInt = new Integer(n).intValue();
        RssElemento elemRss = listaElementiDelFeed.get(nInt);
        return elemRss.getDescription();
        
    }
    
    
    /** aggiungo l'rss element alla lista puntata dal feed 
    public void addRssElement(String idFeed, String nome, String data) 
    {
        //prendo elemento puntato
        ListaElementiRss listaElementiDelFeed = mappaFeed.get(idFeed);
        //creo nuovo elementoRss
        RssElemento nuovoRssElement = new RssElemento(nome, data);
       // listaElementiDelFeed.aggiungi();
        
        //RssElemento re = new RssElemento(idTitolo, nome, data);
        //mappa.
    }*/
    
    
    /** setta descrizione per il titolo passato */
    public void setDescription(String idFeed, String nTitolo, String descrizione)
    {
        mappaFeed.get(idFeed).setDesc(nTitolo, descrizione);
    }
    
    /** restituisce iteratore dell'oggetto RssElemento mappato sull'id del feed passato
     @see RssElemento#getIterator */
    public Iterator<RssElemento> getIterator(String idFeed)
    {
        //ritorno iteratore per ListaElementiRss
        return mappaFeed.get(idFeed).getIterator();
        //costruisco i nodi <titolo> interni
    }
    
    /** per debug codice */
    public void debug(String s) {
        //System.out.println(s);
    }
    
}


