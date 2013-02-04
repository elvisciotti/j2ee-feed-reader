package twi518.NewsXmlFactory;
import java.util.*;
/**
 * Contiene i dati della categoria e i feeds inseriti nella categoria
 * @see Feed
 * @see NewsXmlFactory
 */
/**
 * @author Elvis
 */
public class CategoriaFeed {
    
    private String id;
    private String nome;
    private Feed[] feeds;
    private String note;
    
    /**
     * @param feedsFeed Elenco feeds del quiz (array)
     * @see Feed
     */
    public CategoriaFeed(String idFeed, String nomeFeed, Feed[] feedsFeed) {
        id = idFeed;
        nome = nomeFeed;
        feeds = feedsFeed;
    }
    
    /**
     * setta l'attributo <code>note</code> per la categoria feeds
     **/
    public void setNote(String nota)
    {
        note = nota;
    }
    
    /**
     *  ritorna il relativo parametro
     */
    public String getNome()
    {
        return nome;
    }
    /**
     *  ritorna il relativo parametro
     */
    public String getId()
    {
        return id;
    }
    /**
     *  ritorna il relativo parametro
     */
    public Feed[] getFeeds()
    {
        return feeds;
    }
    
    /**
     * ritorna l'i-esimo feed presente nell'array urls interno.<br />
     * se lla i-esima posizione non esiste, restituisce null
     **/
    public Feed getFeed(int i)
    {
       if (i<feeds.length)
            return feeds[i];
        else
            return null;
    }
    /**
     *  ritorna il relativo parametro, se null ritorna stringa vuota
     */
    public String getNote()
    {
        if (note==null)
            return "";
        else
            return note;
    }
}

