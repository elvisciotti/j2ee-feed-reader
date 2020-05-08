public class RssElemento {
    private String title = "";
    private String description = "";
    private String pubDate = "";
    private String id = "";

    public RssElemento(String title, String description, String pubDate) {
        this.title = title;
        this.description = description;
        this.pubDate = pubDate;
    }

    public RssElemento(String title, String pubDate) {
        this.title = title;
        this.pubDate = pubDate;
    }

    public String toString() {
        return new String("[" + title + " (" + pubDate + ") ]\n" + description.substring(0, 10) + "...");
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setDesc(String d) {
        description = d;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
