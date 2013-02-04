/** Memorizza il singolo elemento Rss ( o titolo)
 * @author Elvis
 */
public class RssElemento {
    private String title = "";
    private String description = "";
    private String pubDate = "";
    private String id = "";
    
    /** costruttore completo (tranne ID) */
    public RssElemento(String title, String description, String pubDate) {
        this.title = title;
        this.description = description;
        this.pubDate = pubDate;
    }
    
    /** costruttore senza description (usato da applet che non conosce inizialmente le descrizioni) */
     public RssElemento(String title, String pubDate) {
        this.title = title;
        this.pubDate = pubDate;
    }
    
     /** ritorna contenuto in stringa, per debug locali */
    public String toString()
    {
        return new String("["+title+" ("+pubDate+") ]\n"+description.substring(0, 10)+"...");
    }
    
    /** ritorna il relativo campo settato */
    public String getTitle()
    {
        return title;
    }
    
    /** ritorna il relativo campo settato */
    public String getDescription()
    {
        return description;
    }
    
    /** ritorna il relativo campo settato */
    public String getPubDate()
    {
        return pubDate;
    }
    
    /** setta il relativo campo settato, operazione eseguita dall'Rss Reader e cache applet */
    public void setId(String id)
    {
        this.id = id;
    }
    
    /** setta il relativo campo settato */
    public void setDesc(String d)
    {
       description = d;
    }
    
    /** ritorna il relativo campo settato */
    public String getId()
    {
        return id;
    }
}
