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
public class SearcherTask {
    
    Kategoria kategoria;
    int inzeratov;
    final int start;
    final boolean aktualizujeme;

    public SearcherTask(Kategoria kategoria, int start, int inzeratov, boolean aktualizujeme) {
        this.kategoria = kategoria;
        this.inzeratov = inzeratov;
        this.start=start;
        this.aktualizujeme=aktualizujeme;
    }

    @Override
    public String toString() {
        return "SearcherTask{" + "kategoria=" + kategoria + ", inzeratov=" + inzeratov + ", start=" + start + ", aktualizujeme=" + aktualizujeme + '}';
    }
    
}
