package twi518.NewsXmlFactory;

import java.util.*;

public class CategoriaFeed {
    private String id;
    private String nome;
    private Feed[] feeds;
    private String note;

    public CategoriaFeed(String idFeed, String nomeFeed, Feed[] feedsFeed) {
        id = idFeed;
        nome = nomeFeed;
        feeds = feedsFeed;
    }

    public String getNome() {
        return nome;
    }

    public String getId() {
        return id;
    }

    public Feed[] getFeeds() {
        return feeds;
    }

    public Feed getFeed(int i) {
        if (i < feeds.length)
            return feeds[i];
        else
            return null;
    }

    public String getNote() {
        if (note == null)
            return "";
        else
            return note;
    }

    public void setNote(String nota) {
        note = nota;
    }
}
