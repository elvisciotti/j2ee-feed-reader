package twi518.cacheTestiTitoli;

import java.util.*;

public class CacheTestiETitoli {
    private HashMap<String, ListaElementiRss> mappaFeed = null;

    public CacheTestiETitoli() {
        mappaFeed = new HashMap<String, ListaElementiRss>();
    }

    public boolean esistonoTitoliNelFeed(String idFeed) {
        return mappaFeed.containsKey(idFeed);
    }

    public void settaElementiRss(String idFeed, ListaElementiRss ler) {
        mappaFeed.put(idFeed, ler);
    }

    public ListaElementiRss getListaElemRss(String idFeed) {
        return mappaFeed.get(idFeed);
    }

    public String[] getIdsTitoli(String idFeed) {
        return mappaFeed.get(idFeed).getIdsTitoli();
    }

    public String getNomeDelTitolo(String idFeed, String idTitolo) {
        return mappaFeed.get(idFeed).getNomeTitolo(idTitolo);
    }

    public String getDataTitolo(String idFeed, String idTitolo) {
        return mappaFeed.get(idFeed).getDataTitolo(idTitolo);
    }

    public String getDescription(String idFeed, String n) {
        ListaElementiRss listaElementiDelFeed = mappaFeed.get(idFeed);
        int nInt = new Integer(n).intValue();
        RssElemento elemRss = listaElementiDelFeed.get(nInt);
        return elemRss.getDescription();
    }

    public void setDescription(String idFeed, String nTitolo, String descrizione) {
        mappaFeed.get(idFeed).setDesc(nTitolo, descrizione);
    }

    public Iterator<RssElemento> getIterator(String idFeed) {
        return mappaFeed.get(idFeed).getIterator();
    }

    public void debug(String s) {
    }
}
