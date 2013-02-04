package twi518.cacheApplet;

/** Interfaccia con metodi di CacheAppletFactory usati dall'applet
 * Implementando questi metodi in un'altra classe l'applet funziona comunque
 * vedi commenti in classe CacheAppletFactory
 * @see CacheAppletFactory
 * @author Elvis
 */
public interface ClientInterface {
    public boolean accesso(String username);
    
    public String[] getIdsCategorie() ;
    
    public String getNomeCategoria(String idCat);
    
    public String getNoteCategoria(String idCat);
    
    public String[] getIdsFeedsDellaCat(String idCat);
    
    public String getNomeFeed(String idFeed);
    
    public String[] getIdsTitoli(String idFeed);
    
    public String getNomeDelTitolo(String idFeed, String idTitolo);
    
    
    public String getDataDelTitolo(String idFeed, String idTitolo);
    
    public String getDescriptionDelTitolo(String idFeed, String idTitolo);
    
    public void chiudiSocket();
    
}
