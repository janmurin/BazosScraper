package crawler;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Janco1
 */
public class CrawlerManagerTask implements Runnable {

    public static int NUMBER_OF_SEARCHERS = 1;
    public static int NUMBER_OF_CRAWLERS = 10;
    List<Kategoria> kategorie;
    private final JSoup jsoup;
    private final int pocetDni;
    private boolean aktualizujeme;

    // konstruktor pre stahovanie novych inzeratov
    public CrawlerManagerTask(TextDatabase db, int searcherov, int crawlerov, int pocetDni) {
        initKategorie();
        jsoup = new JSoup();
        Shared.db = db;
        Shared.urlsToAnalyze = new LinkedBlockingQueue<>();
        Shared.searcherTasks = new LinkedBlockingQueue<>();
        Shared.searcherHlada = new AtomicInteger(0);
        Shared.crawlerHlada = new AtomicInteger(0);
        Shared.searcherNasiel = new AtomicInteger(0);
        Shared.crawlerNasiel = new AtomicInteger(0);
        NUMBER_OF_CRAWLERS = crawlerov;
        NUMBER_OF_SEARCHERS = searcherov;
        this.pocetDni = pocetDni;
        this.aktualizujeme = true;
    }

    // konstruktor pre vymazavanie starych inzeratov
    public CrawlerManagerTask(TextDatabase db, int searcherov) {
        initKategorie();
        jsoup = new JSoup();
        Shared.db = db;
        Shared.urlsToAnalyze = new LinkedBlockingQueue<>();
        Shared.searcherTasks = new LinkedBlockingQueue<>();
        Shared.searcherHlada = new AtomicInteger(0);
        Shared.searcherNasiel = new AtomicInteger(0);
        NUMBER_OF_SEARCHERS = searcherov;
        this.pocetDni = -1; // nepotrebujeme
        this.aktualizujeme = false;
    }

    public CrawlerManagerTask(deprecated.TextDatabase database, int searcherov, int crawlerov, int pocetDni) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public CrawlerManagerTask(deprecated.TextDatabase database, int searcherov) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        if (!aktualizujeme) {
            hladajLinky();
            return;
        }
        System.out.println("CrawlerManagerTask spusteny run");
        long startTime = System.currentTimeMillis();
        ExecutorService searcherExecutor = Executors.newFixedThreadPool(NUMBER_OF_SEARCHERS + 1);
        ExecutorService crawlerExecutor = Executors.newFixedThreadPool(NUMBER_OF_CRAWLERS);
        try {
            List<Future<?>> searcherFutures = new ArrayList<Future<?>>();
            List<Future<?>> crawlerFutures = new ArrayList<Future<?>>();
            // spustime casovac
            ETATimer aTimer = new ETATimer();
            searcherExecutor.submit(aTimer);
            System.out.println("CrawlerManagerTask submitnuty timer");

            // inicializacia searcherTaskov    
            zaloguj("pocitam inzeraty na aktualizovanie: ", true);
            for (Kategoria kategoria : kategorie) {
                int[] poctyInzeratov = getInzeratovZa24hodin(kategoria);
                // vypocitame kolko inzeratov treba skontrolovat podla poctu dni a denneho prirastku inzeratov v kategorii na bazosi
                int pocet = (int) (poctyInzeratov[0] * pocetDni * 1.1);
                pocet = Math.min(pocet, poctyInzeratov[1]);
                if (pocetDni == -1) {
                    pocet = poctyInzeratov[1]; // chceme stiahnut vsetky inzeraty v kategorii
                }
                Shared.searcherHlada.addAndGet(pocet);
                Shared.searcherTasks.put(new SearcherTask(kategoria, 1, pocet, aktualizujeme));
                zaloguj("Kategoria: " + kategoria.nazov + " vsetkych: " + poctyInzeratov[1] + " dnesnych: " + poctyInzeratov[0] + ". "
                        + "Celkom prehladame: " + Shared.searcherHlada.get(), true);
            }
            zaloguj("vsetkych inzeratov na searchovanie: " + Shared.searcherHlada.get(), true);
            for (int i = 0; i < NUMBER_OF_SEARCHERS; i++) {
                Shared.searcherTasks.put(new SearcherTask(kategorie.get(0), 1, -2, aktualizujeme)); // -2 ako poison pill
            }

            // spustime searcherov
            CountDownLatch searcherGate = new CountDownLatch(NUMBER_OF_SEARCHERS);
            for (int i = 0; i < NUMBER_OF_SEARCHERS; i++) {
                UrlSearcher searcher = new UrlSearcher(searcherGate, i);// 0- dnesne, 1- vsetky
//                Thread searcherThread = new Thread(searcher);
//                searcherThread.start();
                Future<?> searcherFuture = searcherExecutor.submit(searcher);
                searcherFutures.add(searcherFuture);
            }

            // spustime Crawlerov
            CountDownLatch crawlerGate = new CountDownLatch(NUMBER_OF_CRAWLERS);
            for (int i = 0; i < NUMBER_OF_CRAWLERS; i++) {
                UrlCrawler urlCrawler = new UrlCrawler(i, crawlerGate);
//                Thread crawlerThread = new Thread(urlCrawler);
//                crawlerThread.start();
                Future<?> crawlerFuture = crawlerExecutor.submit(urlCrawler);
                crawlerFutures.add(crawlerFuture);
            }

            searcherGate.await();
            zaloguj("\nsearchovanie skoncilo, pridavam poison pilly pre crawlerov", true);
            for (int i = 0; i < NUMBER_OF_CRAWLERS; i++) {
                Shared.urlsToAnalyze.put(new CrawlerUloha("poison.pill", kategorie.get(0), "default datum"));
            }
            // este pockame na crawlerov nech dokoncia crawlovanie
            crawlerGate.await();

            //System.out.println("TOTAL TIME: " + Utils.getElapsedTime(startTime));
            zaloguj("Aktualizacia ukoncena, Čas trvania: " + Utils.getElapsedTime(startTime), true);
            zaloguj("naslo sa " + Shared.crawlerNasiel + " novych inzeratov.", true);
            Shared.logMessages.put("poison.pill");

        } catch (InterruptedException e) {
            try {
                // prerusenie z hlavneho vlakna
                Shared.db.updateLastTimeInserted();
                zaloguj("CrawlerManagerTask preruseny", true);
                Shared.logMessages.put("poison.pill");
            } catch (InterruptedException ex) {
                Logger.getLogger(CrawlerManagerTask.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (Exception e) {
            Logger.getLogger(CrawlerManagerTask.class.getName()).log(Level.SEVERE, null, e);
        }
        System.out.println("shutting down searcher a crawler executors");
        searcherExecutor.shutdownNow();
        crawlerExecutor.shutdownNow();
        Shared.db.updateLastTimeInserted();
    }

    public void hladajLinky() {
        System.out.println("CrawlerManagerTask spusteny hladajLinky");
        long startTime = System.currentTimeMillis();
        ExecutorService searcherExecutor = Executors.newFixedThreadPool(NUMBER_OF_SEARCHERS);
        try {
            List<Future<?>> searcherFutures = new ArrayList<Future<?>>();

            // inicializacia searcherTaskov    
            zaloguj("pocitam inzeraty na aktualizovanie: ", true);
            for (Kategoria kategoria : kategorie) {
                int[] poctyInzeratov = getInzeratovZa24hodin(kategoria);
                // vypocitame kolko inzeratov treba skontrolovat podla poctu dni a denneho prirastku inzeratov v kategorii na bazosi
                int pocet = poctyInzeratov[1];
                Shared.searcherHlada.addAndGet(pocet);
                int poc = 1;
                // pridame searcher tasky po davkach
                for (; poc + 10000 < pocet; poc += 10000) {
                    Shared.searcherTasks.put(new SearcherTask(kategoria, poc, 10000, aktualizujeme));
                }
                if (poc < pocet) {
                    Shared.searcherTasks.put(new SearcherTask(kategoria, poc, pocet - poc, aktualizujeme));
                }
                zaloguj("Kategoria: " + kategoria.nazov + " vsetkych: " + poctyInzeratov[1] + " dnesnych: " + poctyInzeratov[0] + ". Celkom prehladame: " + Shared.searcherHlada.get(), true);
            }
            zaloguj("searcher taskov: " + Shared.searcherTasks.size(), true);
            zaloguj("vsetkych inzeratov na searchovanie: " + Shared.searcherHlada.get(), true);
            for (int i = 0; i < NUMBER_OF_SEARCHERS; i++) {
                Shared.searcherTasks.put(new SearcherTask(kategorie.get(0), 1, -2, aktualizujeme)); // -2 ako poison pill
            }

            // spustime searcherov
            CountDownLatch searcherGate = new CountDownLatch(NUMBER_OF_SEARCHERS);
            for (int i = 0; i < NUMBER_OF_SEARCHERS; i++) {
                UrlSearcher searcher = new UrlSearcher(searcherGate, i);// 0- dnesne, 1- vsetky
//                Thread searcherThread = new Thread(searcher);
//                searcherThread.start();
                Future<?> searcherFuture = searcherExecutor.submit(searcher);
                searcherFutures.add(searcherFuture);
            }

            searcherGate.await();
            zaloguj("\nsearchovanie skoncilo, spracuvam ziskane linky", true);
            // netreba poison pilly lebo netreba zastavovat crawlerov
            //Shared.urlsToAnalyze.put(new CrawlerUloha("poison.pill", kategorie.get(0), "default datum"));
            List<List<CrawlerUloha>> crawlerUlohy = new ArrayList<>();
            for (Kategoria kategoria : kategorie) {
                crawlerUlohy.add(new ArrayList<CrawlerUloha>());
            }
            // teraz budem prechadzat cely rad a porozdelujem ich do listov podla kategorii
            for (CrawlerUloha cu : Shared.urlsToAnalyze) {
                crawlerUlohy.get(cu.kategoria.id).add(cu);
            }
            // poupdatovat linky a datumy
            for (List<CrawlerUloha> ulohy : crawlerUlohy) {
                vymazNeaktualneInzeraty(ulohy);
            }

            System.out.println("TOTAL TIME: " + Utils.getElapsedTime(startTime));
            zaloguj("Aktualizacia ukoncena, Čas trvania: " + Utils.getElapsedTime(startTime), true);
            Shared.logMessages.put("poison.pill");

        } catch (InterruptedException e) {
            try {
                // prerusenie z hlavneho vlakna
                zaloguj("CrawlerManagerTask preruseny", true);
                Shared.logMessages.put("poison.pill");
            } catch (InterruptedException ex) {
                Logger.getLogger(CrawlerManagerTask.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (Exception e) {
            Logger.getLogger(CrawlerManagerTask.class.getName()).log(Level.SEVERE, null, e);
        }
        System.out.println("shutting down searcher executor");
        searcherExecutor.shutdownNow();
    }

    private void vymazNeaktualneInzeraty(List<CrawlerUloha> zoznam) throws InterruptedException {
        if (zoznam.isEmpty()) {
            System.out.println("zoznam empty");
            return;
        }
        // 2. prejdeme celu databazu a pytame sa ci je inzerat este aktualny, ak NIE, tak si pridame do zoznamu na zmazanie
        zaloguj("\nnacitavam inzeraty z lokal DB pre kategoriu [" + zoznam.get(0).kategoria.url + "] ", true);
        List<Inzerat> inzeraty = Shared.db.getInzeratyList(zoznam.get(0).kategoria.url);
        zaloguj("hladam inzeraty na vymazanie", true);
        Set<String> toDeleteURLs = new HashSet<String>();
        // kontrolujeme inzeraty z databazy ci su este platne
        for (Inzerat inz : inzeraty) {
            // hladame nas inzerat v nasearchovanom zozname
            boolean jeVZozname = false;
            for (CrawlerUloha cu : zoznam) {
                if (cu.url.equals(inz.getAktualny_link())) {
                    jeVZozname = true;
                    break;
                }
            }
            if (!jeVZozname) {
                toDeleteURLs.add(inz.getAktualny_link());
            }
        }

        zaloguj("to delete inzeraty size: " + toDeleteURLs.size(), true);
        Shared.db.deleteInzeratyWithURL(toDeleteURLs, zoznam.get(0).kategoria.url);
        zaloguj("lokal DB non-existent inzeraty ZMAZANE", true);
    }

    public void zaloguj(String message, boolean poslatGui) throws InterruptedException {
        System.out.println(message);
        if (poslatGui) {
            try {
                Shared.logMessages.put(message);
            } catch (InterruptedException ex) {
                //Logger.getLogger(UrlSearcher.class.getName()).log(Level.SEVERE, null, ex);
                throw ex;
            }
        }
    }

    private void initKategorie() {
        kategorie = new ArrayList<>();
        kategorie.add(new Kategoria("zvierata", "http://zvierata.bazos.sk", "Inzeráty zvierat celkom:", "za 24 hodín:", 0));
        kategorie.add(new Kategoria("deti", "http://deti.bazos.sk", "Inzeráty deti celkom:", "za 24 hodín:", 1));
        kategorie.add(new Kategoria("reality", "http://reality.bazos.sk", "Inzeráty realit celkom:", "za 24 hodín:", 2));
        kategorie.add(new Kategoria("praca", "http://praca.bazos.sk", "Inzeráty práca celkom:", "za 24 hodín:", 3));
        kategorie.add(new Kategoria("auto", "http://auto.bazos.sk", "Inzeráty aut celkom:", "za 24 hodín:", 4));
        kategorie.add(new Kategoria("motocykle", "http://motocykle.bazos.sk", "Inzeráty Motocykle celkom:", "za 24 hodín:", 5));
        kategorie.add(new Kategoria("stroje", "http://stroje.bazos.sk", "Inzeráty Stroje celkom:", "za 24 hodín:", 6));
        kategorie.add(new Kategoria("dom", "http://dom.bazos.sk", "Inzeráty Dom a záhrada celkom:", "za 24 hodín:", 7));
        kategorie.add(new Kategoria("pc", "http://pc.bazos.sk", "Inzeráty PC celkom:", "za 24 hodín:", 8));
        kategorie.add(new Kategoria("mobil", "http://mobil.bazos.sk", "Inzeráty mobily celkom:", "za 24 hodín:", 9));
        kategorie.add(new Kategoria("foto", "http://foto.bazos.sk", "Inzeráty foto celkom:", "za 24 hodín:", 10));
        kategorie.add(new Kategoria("elektro", "http://elektro.bazos.sk", "Inzeráty elektro celkom:", "za 24 hodín:", 11));
        kategorie.add(new Kategoria("sport", "http://sport.bazos.sk", "Inzeráty šport celkom:", "za 24 hodín:", 12));
        kategorie.add(new Kategoria("hudba", "http://hudba.bazos.sk", "Inzeráty hudba celkom:", "za 24 hodín:", 13));
        kategorie.add(new Kategoria("vstupenky", "http://vstupenky.bazos.sk", "Inzeráty vstupenky celkom:", "za 24 hodín:", 14));
        kategorie.add(new Kategoria("knihy", "http://knihy.bazos.sk", "Inzeráty kníh celkom:", "za 24 hodín:", 15));
        kategorie.add(new Kategoria("nabytok", "http://nabytok.bazos.sk", "Inzeráty nábytok celkom:", "za 24 hodín:", 16));
        kategorie.add(new Kategoria("oblecenie", "http://oblecenie.bazos.sk", "Inzeráty oblečenie celkom:", "za 24 hodín:", 17));
        kategorie.add(new Kategoria("sluzby", "http://sluzby.bazos.sk", "Inzeráty služby celkom:", "za 24 hodín:", 18));
        kategorie.add(new Kategoria("ostatne", "http://ostatne.bazos.sk", "Inzeráty ostatné celkom:", "za 24 hodín:", 19));

    }

    private int[] getInzeratovZa24hodin(Kategoria kategoria) {
        System.out.println("CrawlerManagerTask getInzeratovZa24hodin=" + kategoria.url);
        Document page = null;
        try {
            page = jsoup.getPage(kategoria.url);
        } catch (Status400Exception ex) {
            try {
                zaloguj("nepodarilo sa ziskat url[" + kategoria.url + "] a zistit pocet inzeratov za 24 hodin, pricina: " + ex, true);
            } catch (InterruptedException ex1) {
                Logger.getLogger(CrawlerManagerTask.class.getName()).log(Level.SEVERE, null, ex1);
            }
            Logger.getLogger(CrawlerManagerTask.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(CrawlerManagerTask.class.getName()).log(Level.SEVERE, null, ex);
        }
        Elements ems = page.select("div[align=\"left\"] b");
//        String searchPhrase = kategoria.searchPhraseVsetko;
//        String searchPhrase2 = kategoria.searchPhraseDnes;
        int dnesnych = 0;
        try {
            dnesnych = Integer.parseInt(ems.get(2).text());
        } catch (NumberFormatException ex) {
            Logger.getLogger(CrawlerManagerTask.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println("dnesnych: " + dnesnych);

//        int cisloStartIdx = source.indexOf(searchPhrase) + searchPhrase.length();
//        int cisloEndIdx = cisloStartIdx + source.substring(cisloStartIdx).indexOf(",");
        int vsetkych = 0;
        try {
            vsetkych = Integer.parseInt(ems.get(1).text());
        } catch (NumberFormatException ex) {
            Logger.getLogger(CrawlerManagerTask.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println("crawlerHlada: " + crawlerHlada);
        return new int[]{dnesnych, vsetkych};
    }

    private void zapisDoSuboru(String string, String nazovSuboru) {
        Writer out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(nazovSuboru), "UTF-8"));
            out.write(string);
            out.flush();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(CrawlerManagerTask.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CrawlerManagerTask.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CrawlerManagerTask.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    Logger.getLogger(CrawlerManagerTask.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void parseInzerat(Document page, Inzerat novy, CrawlerUloha uloha) {
//        novy.setAktualny_link(uloha.url);
//        novy.setPortal(uloha.kategoria.url);
//        novy.setDatumInzeratu(uloha.datum);
        // 1. text
        String popisText = page.select("html body div.sirka table tbody tr td table tbody tr td div.popis").text();
        novy.setText(popisText.replaceAll("'", ""));
        // 2. nazov TODO datum pridania
        String nazov = page.select("html body div.sirka table tbody tr td table.listainzerat tbody tr td h1.nadpis").text();
        novy.setNazov(nazov.replaceAll("'", ""));
        // 3. meno
        String meno = page.select("html body div.sirka table tbody tr td table tbody tr td.listal table tbody tr td b a").text();
        novy.setMeno(meno.replaceAll("'", ""));
        // 4. telefon
        try {
            String telefon = page.select("html body div.sirka table tbody tr td table tbody tr td.listal table tbody tr td a").get(1).text();
            novy.setTelefon(telefon.replaceAll("'", ""));
        } catch (Exception e) {
            novy.setTelefon("NEZISTENE");
        }
        // 5. lokalita
        try {
            String lokalita = page.select("html body div.sirka table tbody tr td table tbody tr td.listal table tbody tr td a").get(2).text();
            novy.setLokalita(lokalita);
        } catch (Exception e) {
            novy.setLokalita("NEZISTENE");
        }
        // 6. typ
        try {
            String typ = page.select("div.barvalevat a#zvyraznenikat").text();
            novy.setTyp(typ.replaceAll("'", ""));
        } catch (Exception e) {
            novy.setTyp("NEZISTENE");
        }
        // 7. kategoria
        try {
            String kategoria = page.select("div.barvaleva a#zvyraznenikat").text();
            novy.setKategoria(kategoria.replaceAll("'", ""));
        } catch (Exception e) {
            novy.setKategoria("NEZISTENE");
        }
        // 8. cena
        try {
            String cena = page.select("html body div.sirka table tbody tr td table tbody tr td.listal table tbody tr td b").get(1).text();
            novy.setCena(cena.replaceAll("'", ""));
        } catch (Exception e) {
            novy.setCena("NEZISTENE");
        }

        System.out.println("inzerat: " + novy);
    }

    public static void main(String[] args) {
        JSoup jsoup = new JSoup();
        try {
            Document page = jsoup.getPage("https://auto.bazos.sk/inzerat/66550277/Skoda-fabia-combi-12-47kw-12V-111tiskm-Super-stav.php");
            Elements listal = page.select(".listal");
            Elements nadpisy = listal.select("div.nadpismenu");
            Elements barvy = listal.select("div.barvalmenu");
            String typ = "NEZISTENE";

            for (int i = 0; i < nadpisy.size(); i++) {
                Elements select = barvy.get(i).select("#zvyraznenikat");
                if (select.size() > 0) {
                    typ = nadpisy.get(i).text();
                    break;
                }
            }
            System.out.println("typ=" + typ);
        } catch (Status400Exception ex) {
            System.out.println("inzerat pravdepodobne neexistuje, pricina: " + ex);
            Logger.getLogger(CrawlerManagerTask.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            System.out.println("nepodarilo sa ziskat url, pricina: " + ex);
            Logger.getLogger(CrawlerManagerTask.class.getName()).log(Level.SEVERE, null, ex);
        }
//        MysqlDatabase db = new MysqlDatabase();
//        CrawlerManagerTask cm = new CrawlerManagerTask(new TextDatabase(), 1);
////        MysqlDatabase mysqlDB=new MysqlDatabase();
////        // cm.execute();
////        //Shared.db.updateSurneInzeratyAktualnyCas();
////        Document page = cm.jsoup.getPage("http://deti.bazos.sk/inzerat/56428074/Detske-elektricke-auticko-JEEP-2-motory--2-baterky.php");
////        cm.parseInzerat(page, new Inzerat(), null);
////        
////        StringBuilder sql = new StringBuilder();
////        int packet_size=500;
//        for (int j = 0; j < cm.kategorie.size(); j++) {
//            Kategoria kat = cm.kategorie.get(j);
//            Shared.db.getInzeratyUrls(kat);
//        }
//            //System.out.println(kat.url+" = ["+kat.url.replace("http://", "").replace(".bazos.sk", "")+"] ");
//            System.out.println("delete from Inzeraty_"+kat.nazov+";");
////            try {
////                Kategoria kat = cm.kategorie.get(j);
////                System.out.println("insertujem " + kat.nazov);
////                
////                List<Inzerat> inzeratyList = db.getInzeratyList(kat.url);
////                int i = 0;
////                for (; i + packet_size < inzeratyList.size(); i += packet_size) {
////                    System.out.println( i + " to " + (i + packet_size)+",");
////                    List<Inzerat> neinsertnute = inzeratyList.subList(i, i + packet_size);
////                    mysqlDB.insertInseratyPortal(neinsertnute, kat);
////                }
////                System.out.println("from " + i + " to " + (inzeratyList.size()));
////                List<Inzerat> neinsertnute = inzeratyList.subList(i, inzeratyList.size());
////                mysqlDB.insertInseratyPortal(neinsertnute, kat);
////                System.out.println("inserted " + kat.nazov);
////            } catch (Exception e) {
////                 Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
////                break;
////            }
//        }
//        System.out.println(sql);

//        File[] updatySubory = new File("updaty").listFiles();
//        int spracovanych = 0;
//        long start = System.currentTimeMillis();
//
//        for (int i = 0; i < updatySubory.length; i++) {
//            System.out.println("ETA: " + Utils.getETAtime(start, spracovanych, 700000));
//            System.out.println("updatujem kategoriu: " + updatySubory[i].getName());
//            List<CrawlerUloha> ulohy = new ArrayList<>();
//            File file = new File("updaty/" + updatySubory[i].getName());
//
//            BufferedReader f = null;
//            try {
//                f = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
//            } catch (FileNotFoundException ex) {
//                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (UnsupportedEncodingException ex) {
//                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//            while (true) {
//                StringTokenizer st = null;
//                try {
//                    String line = f.readLine();
//                    if (line == null) {
//                        break;
//                    }
//                    int id = Integer.parseInt(line.split("_")[0]);
//                    String datum = line.split("_")[1];
//                    CrawlerUloha cu = new CrawlerUloha("nourl", new Kategoria("", "", "", "", 0), datum);
//                    cu.idInzeratu = id;
//                    ulohy.add(cu);
//
//                } catch (Exception ex) {
//                    Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
//                    break;
//                }
//            }
//            System.out.println("subor nacitany, updatujem db");
//            db.updateDatumyInzeratov(ulohy);
//            spracovanych+=ulohy.size();
//        }
    }

}
