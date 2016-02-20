/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Janco1
 */
public class Shared {

    public static TextDatabase db;
    static BlockingQueue<CrawlerUloha> urlsToAnalyze;
    static BlockingQueue<SearcherTask> searcherTasks;
    public static AtomicInteger crawlerNasiel = new AtomicInteger(0);
    public static AtomicInteger crawlerHlada = new AtomicInteger(0);
    public static AtomicInteger searcherNasiel = new AtomicInteger(0);
    public static AtomicInteger searcherHlada = new AtomicInteger(0);
    public static BlockingQueue<String> logMessages;
}
