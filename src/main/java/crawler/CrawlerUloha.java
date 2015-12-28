/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler;

/**
 *
 * @author Janco1
 */
public class CrawlerUloha {
    final String url;
    final Kategoria kategoria;
    final String datum;
    int idInzeratu;

    public CrawlerUloha(String url, Kategoria kategoria, String datum) {
        this.url = url;
        this.kategoria = kategoria;
        this.datum=datum;
    }

    @Override
    public String toString() {
        return "CrawlerUloha{" + "url=" + url + '}';
    }
    
    
}
