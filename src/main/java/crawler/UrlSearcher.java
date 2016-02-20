/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Janco1
 */
class UrlSearcher implements Runnable {

    private final CountDownLatch gate;
    private final JSoup jsoup;
    private Set<String> inzeratyUrls;
    private final int id;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
    private final Calendar calendar;

//    UrlSearcher(Kategoria kategoria, int za24hodinInzeratov, CountDownLatch gate) {
//        this.kategoria = kategoria;
//        this.za24hodinInzeratov = za24hodinInzeratov;
//        this.urlsToAnalyze = Shared.urlsToAnalyze;
//        this.gate = gate;
//        jsoup = new JSoup();
//        this.database = Shared.db;
//        inzeratyUrls = database.getInzeratyUrls(kategoria);
//        System.out.println("searcher pozna " + inzeratyUrls.size() + " urlciek inzeratov.");
//    }
    UrlSearcher(CountDownLatch searcherGate, int id) {
        this.gate = searcherGate;
        jsoup = new JSoup();
        this.id = id;
        calendar = Calendar.getInstance();
    }

    @Override
    public void run() {
        try {
            SearcherTask searcherTask = Shared.searcherTasks.take();
            while (searcherTask != null) {
                if (searcherTask.inzeratov == -2) {
                    break;
                }
                Kategoria kategoria = searcherTask.kategoria;
                zaloguj("SEARCHER " + id + ": Zacinam searchovat kategoriu: " + kategoria + " start=[" + searcherTask.start + "] "
                        + "pocet=[" + searcherTask.inzeratov + "] aktualizujeme=[" + searcherTask.aktualizujeme + "]", true);
                if (searcherTask.aktualizujeme) {
                    // ak aktualizujeme tak potrebujeme vediet ktore uz mame inzeraty
                    inzeratyUrls = Shared.db.getInzeratyUrls(kategoria);
                    zaloguj("SEARCHER " + id + ": searcher pozna " + inzeratyUrls.size() + " urlciek inzeratov.", true);
                } else {
                    inzeratyUrls = new HashSet<>();
                }

                search(searcherTask);
                zaloguj("SEARCHER " + id + ": Ukoncene searchovanie kategorie: " + kategoria, true);

                searcherTask = Shared.searcherTasks.take();
            }

        } catch (InterruptedException e) {
            try {
                zaloguj("SEARCHER " + id + ": prerusenie searchera ", true);
            } catch (InterruptedException ex) {
                //Logger.getLogger(UrlSearcher.class.getName()).log(Level.SEVERE, null, ex);
            }
            // prerusenie z hlavneho vlakna
            //e.printStackTrace();
        } catch (Exception e) {
            Logger.getLogger(UrlSearcher.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            gate.countDown();
        }
    }

    public void zaloguj(String message, boolean poslatGui) throws InterruptedException {
        System.out.println(message);
        if (poslatGui) {
            try {
                Shared.logMessages.put(message);
            } catch (InterruptedException ex) {
                //Logger.getLogger(UrlSearcher.class.getName()).log(Level.SEVERE, null, ex);
                throw ex; // aby sa prerusila run metoda
            }
        }
    }

    private void search(SearcherTask searcherTask) throws InterruptedException {
        try {
            int start = searcherTask.start;
            int max = searcherTask.inzeratov;
            //max =  10;
            long startTime = System.currentTimeMillis();
            int najdenychLinkov = 0;

            // mozem si nastavit poradove cislo inzeratu od ktoreho chcem zacat alebo skoncit
            for (int i = start; i <= start + max; i += 15) {
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }
                long startTime15 = System.currentTimeMillis();

                String currentLink = searcherTask.kategoria.url + "/" + (i) + "/";
                //System.out.println("getting: "+currentLink);
                Document doc = null;
                try {
                    doc = jsoup.getPage(currentLink);
                } catch (Status400Exception ex) {
                    zaloguj("crawler " + this.id + " CHYBNY STAV  url: " + currentLink + " pricina: " + ex, true);
                    continue;
                } catch (Exception ex) {
                    zaloguj("crawler " + this.id + " NEPODARILO SA ZISKAT STRANKU: " + currentLink + " pricina: " + ex, true);
                    continue;
                }
                // pre istotu necham aj tuto podmienku
                if (doc == null) {
                    zaloguj("SEARCHER " + id + ": NEPODARILO SA ZISKAT STRANKU: " + currentLink, true);
                    continue;
                }
                Elements nadpisy = doc.select("span.nadpis");
                //System.out.println("nadpisy size: " + nadpisy.size()); 
                Elements datumy = doc.select("html body div.sirka table tbody tr td span.vypis table.inzeraty tbody tr td span.velikost10");
                if (nadpisy.size() == 0) {
                    zaloguj("SEARCHER " + id + ": nasiel na stranke [" + currentLink + "] 0 linkov,", true);
                }
                // pozrieme sa na inzeraty a ulozime si na nahliadnutie tie ktore este nemame v DB
                int c = 0;
                for (Element el : nadpisy) {
                    String link = searcherTask.kategoria.url + el.getElementsByTag("a").attr("href");
                    String datum;
                    try {
                        datum = datumy.get(c).text();
                        datum = datum.substring(datum.indexOf("[") + 1, datum.indexOf("]"));
                        int den = Integer.parseInt(datum.split("\\.")[0].trim());
                        int mesiac = Integer.parseInt(datum.split("\\.")[1].trim());
                        int rok = Integer.parseInt(datum.split("\\.")[2].trim());
                        calendar.set(rok, mesiac - 1, den);
                        datum = sdf.format(calendar.getTime());
                    } catch (Exception e) {
                        Logger.getLogger(UrlSearcher.class.getName()).log(Level.SEVERE, null, e);
                        datum = "2015-12-23 12:26:07.490";
                    }
                    //System.out.println("kontrola: ["+link+"]");
                    if (searcherTask.aktualizujeme) {
                        boolean added = inzeratyUrls.add(link);
                        if (added) {
                            Shared.urlsToAnalyze.put(new CrawlerUloha(link, searcherTask.kategoria, datum));
                            najdenychLinkov++;
                            Shared.crawlerHlada.incrementAndGet();
                        }
                    } else {
                        najdenychLinkov++;
                        // pridavame vsetky linky do crawler ulohy lebo hladame neaktualne
                        Shared.urlsToAnalyze.put(new CrawlerUloha(link, searcherTask.kategoria, datum));
                    }
                    c++;
                }
                Shared.searcherNasiel.addAndGet(nadpisy.size());

                System.out.printf(" %04d", (System.currentTimeMillis() - startTime15));
                System.out.print(" " + (i - start) + "/" + (max) + "   ");
                System.out.println("ETA: " + Utils.getETAtime(startTime, i - start, max) + " najdenych: " + najdenychLinkov);
            }
            System.out.println("while cyklus skoncil, nasli sme " + (najdenychLinkov) + " linkov");
            zaloguj("SEARCHER " + id + ": skoncil task, nasiel " + (najdenychLinkov) + " linkov na adrese [" + searcherTask + "] ", true);
        } catch (InterruptedException e) {
            //Logger.getLogger(UrlSearcher.class.getName()).log(Level.SEVERE, null, e);
            throw e;
        } catch (Exception exception) {
            Logger.getLogger(UrlSearcher.class.getName()).log(Level.SEVERE, null, exception);
            try {
                Thread.sleep(1000);
                zaloguj("VYNIMKA: " + exception, true);
            } catch (InterruptedException ex) {
                Logger.getLogger(UrlSearcher.class.getName()).log(Level.SEVERE, null, ex);
                throw ex;
            }
        }
    }

}
