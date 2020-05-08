package twi518.cacheTestiTitoli;

import java.util.*;

public class ListaElementiRss {
    public LinkedList<RssElemento> elementiRss = new LinkedList<RssElemento>();

    public void aggiungi(RssElemento elementoRss) {
        elementiRss.add(elementoRss);
    }

    public Iterator<RssElemento> getIterator() {
        return elementiRss.iterator();
    }

    public String[] getIdsTitoli() {
        int size = elementiRss.size();
        String[] ids = new String[size];
        for (int i = 0; i < size; i++) {
            ids[i] = elementiRss.get(i).getId();
        }
        return ids;
    }

    public String getNomeTitolo(String idTitolo) {
        int size = elementiRss.size();
        String[] ids = new String[size];
        for (int i = 0; i < size; i++) {
            if (elementiRss.get(i).getId().equals(idTitolo))
                return elementiRss.get(i).getTitle();
        }
        return "";
    }

    public String getDataTitolo(String idTitolo) {
        int size = elementiRss.size();
        String[] ids = new String[size];
        for (int i = 0; i < size; i++) {
            if (elementiRss.get(i).getId().equals(idTitolo))
                return elementiRss.get(i).getPubDate();
        }
        return "";
    }

    public String getDescTitolo(String idTitolo) {
        int size = elementiRss.size();
        String[] ids = new String[size];
        for (int i = 0; i < size; i++) {
            if (elementiRss.get(i).getId().equals(idTitolo))
                return elementiRss.get(i).getDescription();
        }
        return "";
    }

    public void setDesc(String idTitolo, String desc) {
        int size = elementiRss.size();
        for (int i = 0; i < size; i++) {
            String idCorr = elementiRss.get(i).getId();
            if (idCorr.equals(idTitolo)) {
                elementiRss.get(i).setDesc(desc);
            }
        }
    }

    public RssElemento get(int i) {
        if (i < elementiRss.size())
            return elementiRss.get(i);
        else
            return new RssElemento("", "", "");
    }

    public void set(int i, RssElemento re) {
        elementiRss.set(i, re);
    }

    public int getSize() {
        return elementiRss.size();
    }
}
