package twi518.NewsXmlFactory;
import java.util.*;
/**
 * Contiene i dati del Feed
 * @see NewsXmlFactory
 *  @author Elvis
 */
public class Feed {
    
    private String id;
    private String nome;
    
    private String urlPrimaria;
    private String[] urls;
    
    /**
     * @param idFeed numero identificativo del feed (univoco nell'albero xml)
     * @param nomeFeed nome del feed
     * @param urlPrimFeed url primaria del feed
     * @param urlsFeed array (String) con le eventuali url del feed (anche array vuoto)
     */
    public Feed(String idFeed, String nomeFeed, String urlPrimFeed, String[] urlsFeed)
    {
        id = idFeed;
        nome = nomeFeed;
        urlPrimaria = urlPrimFeed;
        urls = urlsFeed;
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
    public String getUrlPrimaria()
    {
        return urlPrimaria;
    }
    /**
     *  ritorna il relativo parametro
     */
    public String[] getUrls()
    {
        return urls;
    }
    
    /**
     * ritorna la i-esima url presente nell'array urls interno.<br />
     * se lla i-esima posizione non esiste, restituisce la stringa vuota
     **/
    public String getUrl(int i)
    {
        if (i<urls.length)
            return urls[i];
        else
            return new String("");
    }
}
