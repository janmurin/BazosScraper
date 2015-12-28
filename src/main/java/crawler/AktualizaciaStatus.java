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
public class AktualizaciaStatus {

    public final int searcherProgress;
    public final int crawlerProgress;
    public final String uplynulo;
    public final String ostavaSearchery;
    public final String ostavaCrawlery;

    public AktualizaciaStatus(int searcherProgress, int crawlerProgress, String uplynulo, String ostavaSearchery, String ostavaCrawlery) {
        this.searcherProgress = searcherProgress;
        this.crawlerProgress = crawlerProgress;
        this.ostavaSearchery = ostavaSearchery;
        this.ostavaCrawlery=ostavaCrawlery;
        this.uplynulo = uplynulo;
    }

}
