package twi518.NewsXmlFactory;

import java.util.*;

public class Feed {
    private String id;
    private String nome;
    private String urlPrimaria;
    private String[] urls;

    public Feed(String idFeed, String nomeFeed, String urlPrimFeed, String[] urlsFeed) {
        id = idFeed;
        nome = nomeFeed;
        urlPrimaria = urlPrimFeed;
        urls = urlsFeed;
    }

    public String getNome() {
        return nome;
    }

    public String getId() {
        return id;
    }

    public String getUrlPrimaria() {
        return urlPrimaria;
    }

    public String[] getUrls() {
        return urls;
    }

    public String getUrl(int i) {
        if (i < urls.length)
            return urls[i];
        else
            return new String("");
    }
}
