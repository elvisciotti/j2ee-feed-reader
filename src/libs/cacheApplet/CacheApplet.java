import java.util.*;
/** Classe che funziona da cache per l'applet per categorie e feeds<br />
 * I titoli sono invece mantenuti in cache da classe <code>CacheTestieTitoli</code><br />
 * Comprende funzioni per settare e leggere categorie e feeds in base all'ID (tramite HashMaps)
 * @see CacheTestieTitoli
 * @see Feed
 * @see Categoria 
 * @author Elvis
 */
public class CacheApplet {
    
    String nome;
    String note;
    int nFeeds;

    /** mappa ID categorie su oggetti Categoria*/
    private HashMap<String, Categoria> mappaCat = new HashMap<String, Categoria>();
    /** mappa ID feeds su oggetti Feed*/
    private HashMap<String, Feed> mappaFeed = new HashMap<String, Feed>();
    /** corrispondenze idCat -> lista id Feeds */
    private HashMap<String, LinkedList> corrispondenze = new HashMap<String, LinkedList>();
    
    /**
     * costruttore classe
     */
    public CacheApplet() {

    }
    
    /** crea categoria e aggiunge alla mappa */
    public void addCat(String id, String nome, String note) {
        mappaCat.put(id, new Categoria(nome, note));
        LinkedList<String> l = new LinkedList<String>();
        corrispondenze.put(id, l);
    }
    
    /** crea e aggiunge feed alla mappa (per avere i feed nella categoria, aggiungo id del feed ai feed della categoria passata) */
    public void addFeed(String  idCat, String  idFeed, String  nome) {
        mappaFeed.put(idFeed, new Feed(nome));
        //aggiungo id del feed alle corrispondenze
        corrispondenze.get(idCat).add(idFeed);
    }
    
       
    /** controlla esistenza categoria */
    public boolean catExists(String idCat)
    {
        return mappaCat.containsKey(idCat);
    }
    
    /** controlla esistenza feeds */
    public boolean feedExists(String idFeed)
    {
       return mappaFeed.containsKey(idFeed); 
    }
    
    /** ritorna nome della categoria in base all'ID */
    public String getNomeCat(String idCat)
    {
        return mappaCat.get(idCat).nome;
    }
    
    /** ritorna le note della categoria in base all'ID */
    public String getNoteCat(String idCat)
    {
        return mappaCat.get(idCat).note;
    }
     
    /** ritorna nome del feed in base all'ID */
    public String getNomeFeed(String idFeed)
    {
        return mappaFeed.get(idFeed).nome;
    } 
    
    /** ritorna array di stringhe con ids della categorie */
    public String[] getIdsCats()
    {
        Set idsSet = mappaCat.keySet();
        int size = idsSet.size();
        Iterator it = idsSet.iterator();
        
        String[] returnIds = new String[size];
        for(int i=0; i < size; i++)
        {
            returnIds[i] = (String)it.next();
        }
         return    returnIds;    
           
    }
    
    /** ritorno array ids dei feeds nella categoria */
    public String[] getIdsFeedsDellaCat(String idCat) {
       LinkedList<String> list = corrispondenze.get(idCat);
       int size = list.size();
       String[] returnIdsFeeds = new String[size];
       for(int i=0; i<size; i++)
       {
           returnIdsFeeds[i] = list.get(i);
       }
       return returnIdsFeeds;
       
    }
    
    /** controlla esistenza feeds nella categoria */
    public boolean esistonoFeeds(String idCat)
    {
        if (corrispondenze.get(idCat).size() > 0)
            return true;
        else
            return false;
    }
    
}

/***  classi interne di ausilio *******************/
class Categoria {
    public String nome;
    public String note;
   
    public Categoria(String nome,String note) {
        this.nome = nome;
        this.note = note;
    }
    
    
}

/** memorizza dati del feed (solo nome, l'ID � conservato nella mappa)<br />
  L'utilizzo di una classe consente l'eventuale espansione se al feed vengono aggiunti attributi (es: descrizione )*/
class Feed{
    public String nome;
    
    public Feed(String nome) {
        this.nome = nome;
    }
    
}

