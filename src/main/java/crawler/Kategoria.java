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
public class Kategoria {

    public String nazov;
    public String url;
    public String searchPhraseDnes;
    public String searchPhraseVsetko;
    public int id;

    public Kategoria(String nazov, String url, String searchPhraseVsetko, String searchPhraseDnes, int id) {
        this.nazov = nazov;
        this.url = url;
        this.searchPhraseDnes = searchPhraseDnes;
        this.searchPhraseVsetko = searchPhraseVsetko;
        this.id = id;
    }

    @Override
    public String toString() {
        return "Kategoria{" + "nazov=" + nazov + ", url=" + url + '}';
    }

}
