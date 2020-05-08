import java.util.*;

public class CacheApplet {
    String nome;
    String note;
    int nFeeds;
    private HashMap<String, Categoria> mappaCat = new HashMap<String, Categoria>();
    private HashMap<String, Feed> mappaFeed = new HashMap<String, Feed>();
    private HashMap<String, LinkedList> corrispondenze = new HashMap<String, LinkedList>();

    public CacheApplet() {
    }

    public void addCat(String id, String nome, String note) {
        mappaCat.put(id, new Categoria(nome, note));
        LinkedList<String> l = new LinkedList<String>();
        corrispondenze.put(id, l);
    }

    public void addFeed(String idCat, String idFeed, String nome) {
        mappaFeed.put(idFeed, new Feed(nome));
        corrispondenze.get(idCat).add(idFeed);
    }

    public boolean catExists(String idCat) {
        return mappaCat.containsKey(idCat);
    }

    public boolean feedExists(String idFeed) {
        return mappaFeed.containsKey(idFeed);
    }

    public String getNomeCat(String idCat) {
        return mappaCat.get(idCat).nome;
    }

    public String getNoteCat(String idCat) {
        return mappaCat.get(idCat).note;
    }

    public String getNomeFeed(String idFeed) {
        return mappaFeed.get(idFeed).nome;
    }

    public String[] getIdsCats() {
        Set idsSet = mappaCat.keySet();
        int size = idsSet.size();
        Iterator it = idsSet.iterator();
        String[] returnIds = new String[size];
        for (int i = 0; i < size; i++) {
            returnIds[i] = (String) it.next();
        }
        return returnIds;
    }

    public String[] getIdsFeedsDellaCat(String idCat) {
        LinkedList<String> list = corrispondenze.get(idCat);
        int size = list.size();
        String[] returnIdsFeeds = new String[size];
        for (int i = 0; i < size; i++) {
            returnIdsFeeds[i] = list.get(i);
        }
        return returnIdsFeeds;
    }

    public boolean esistonoFeeds(String idCat) {
        if (corrispondenze.get(idCat).size() > 0)
            return true;
        else
            return false;
    }
}

class Categoria {
    public String nome;
    public String note;

    public Categoria(String nome, String note) {
        this.nome = nome;
        this.note = note;
    }
}

class Feed {
    public String nome;

    public Feed(String nome) {
        this.nome = nome;
    }
}
