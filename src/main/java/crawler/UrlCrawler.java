/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;

/**
 *
 * @author Janco1
 */
class UrlCrawler implements Runnable {

    private final BlockingQueue<CrawlerUloha> urlsToAnalyze;
    private final JSoup jsoup;
    private final Pattern p;
    private Matcher m;
    private int najdenychAdries;
    private List<Inzerat> inzeraty = new ArrayList<>();
    private final int id;
    private final CountDownLatch crawlerGate;
    private final List<String> okresneMesta;
    private final List<Okres> okresy;

    UrlCrawler(int id, CountDownLatch crawlerGate) {
        this.urlsToAnalyze = Shared.urlsToAnalyze;
        jsoup = new JSoup();
        String RE_MAIL = "([\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Za-z]{2,4})";
        p = Pattern.compile(RE_MAIL);
        this.id = id;
        this.crawlerGate = crawlerGate;
        okresneMesta = Shared.db.getOkresneMesta();
        okresy = Shared.db.getOkresy();
    }

    @Override
    public void run() {
        try {
            CrawlerUloha uloha = urlsToAnalyze.take();
            while (uloha != null) {
                if (uloha.url.equals("poison.pill")) {
                    // vypisem svoj status a insertnem do databazy
                    zaloguj("crawler " + this.id + " nasiel emailov: " + najdenychAdries, true);
                    Shared.db.inzertInzeraty(inzeraty);
                    break;
                }
                // System.out.println("crawlujem url: "+uloha.url);
                Document page = null;
                try {
                    page = jsoup.getPage(uloha.url);
                } catch (Status400Exception ex) {
                    zaloguj("crawler " + this.id + " INZERAT PRAVDEPODOBNE NEEXISTUJE  url: " + uloha.url + " pricina: " + ex, true);
                    continue;
                } catch (Exception ex) {
                    zaloguj("crawler " + this.id + " NEPODARILO SA ZISKAT STRANKU: " + uloha.url + " pricina: " + ex, true);
                    continue;
                }
                // pre istotu necham aj tuto podmienku
                if (page == null) {
                    zaloguj("crawler " + this.id + " NEPODARILO SA ZISKAT STRANKU: " + uloha.url, true);
                    continue;
                }
                Inzerat novy = new Inzerat();
                parseInzerat(page, novy, uloha);
                inzeraty.add(novy);
                if (inzeraty.size() > 100) {
                    Shared.db.inzertInzeraty(inzeraty);
                    inzeraty = new ArrayList<>();
                }
                Shared.crawlerNasiel.incrementAndGet();

                uloha = urlsToAnalyze.take();
            }
        } catch (InterruptedException e) {
            try {
                Shared.db.inzertInzeraty(inzeraty);
                zaloguj("crawler " + id + ": prerusenie CRAWLERA ", true);
            } catch (InterruptedException ex) {
                //Logger.getLogger(UrlCrawler.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (Exception e) {
            Logger.getLogger(UrlCrawler.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            crawlerGate.countDown();
        }
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

    private void parseInzerat(Document page, Inzerat novy, CrawlerUloha uloha) {
        novy.setAktualny_link(uloha.url);
        novy.setPortal(uloha.kategoria.url);
        novy.setDatumInzeratu(uloha.datum);
        // 1. text
        String popisText = "";
        try {
            popisText = page.select("html body div.sirka table tbody tr td table tbody tr td div.popis").text();
            novy.setText(popisText.replaceAll("'", ""));
            if (novy.getText().length() == 0) {
                throw new RuntimeException("nenasla sa text");
            }
        } catch (Exception e) {
            novy.setText("NEZISTENE");
            try {
                zaloguj("crawler " + id + ": NEZISTENE text, url: [" + uloha.url + "] vynimka: [" + e.getMessage() + "]", true);
            } catch (InterruptedException ex) {
                Logger.getLogger(UrlCrawler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // 2. nazov TODO datum pridania
        try {
            String nazov = page.select("html body div.sirka table tbody tr td table.listainzerat tbody tr td h1.nadpis").text();
            novy.setNazov(nazov.replaceAll("'", ""));
            if (novy.getNazov().length() == 0) {
                throw new RuntimeException("nenasla sa nazov");
            }
        } catch (Exception e) {
            novy.setNazov("NEZISTENE");
            try {
                zaloguj("crawler " + id + ": NEZISTENE nazov, url: [" + uloha.url + "] vynimka: [" + e.getMessage() + "]", true);
            } catch (InterruptedException ex) {
                Logger.getLogger(UrlCrawler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // 3. meno
        try { //                       html body div.sirka table tbody tr td table tbody tr td.listadvlevo table tbody tr td b a
            String meno = page.select("html body div.sirka table tbody tr td table tbody tr td.listadvlevo table tbody tr td b a").text();// old: html body div.sirka table tbody tr td table tbody tr td.listal table tbody tr td b a
            novy.setMeno(meno.replaceAll("'", ""));
            if (novy.getMeno().length() == 0) {
                throw new RuntimeException("nenasla sa meno");
            }
        } catch (Exception e) {
            novy.setMeno("NEZISTENE");
            try {
                zaloguj("crawler " + id + ": NEZISTENE meno, url: [" + uloha.url + "] vynimka: [" + e.getMessage() + "]", true);
            } catch (InterruptedException ex) {
                Logger.getLogger(UrlCrawler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // 4. telefon
        try { //
            String telefon = page.select("html body div.sirka table tbody tr td table tbody tr td.listadvlevo table tbody tr td a").get(1).text();// old: html body div.sirka table tbody tr td table tbody tr td.listal table tbody tr td a
            novy.setTelefon(telefon.replaceAll("'", ""));
            if (novy.getTelefon().length() == 0) {
                throw new RuntimeException("nenasla sa telefon");
            }
        } catch (Exception e) {
            novy.setTelefon("NEZISTENE");
            try {
                zaloguj("crawler " + id + ": NEZISTENE telefon, url: [" + uloha.url + "] vynimka: [" + e.getMessage() + "]", true);
            } catch (InterruptedException ex) {
                Logger.getLogger(UrlCrawler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // 5. lokalita
        try {
            String lokalita = page.select("html body div.sirka table tbody tr td table tbody tr td.listadvlevo table tbody tr td a").get(2).text();
            novy.setLokalita(getOkresneMesto(lokalita));
            if (novy.getLokalita().length() == 0) {
                throw new RuntimeException("nenasla sa lokalita");
            }
        } catch (Exception e) {
            novy.setLokalita("NEZISTENE");
            try {
                zaloguj("crawler " + id + ": NEZISTENE lokalita, url: [" + uloha.url + "] vynimka: [" + e.getMessage() + "]", true);
            } catch (InterruptedException ex) {
                Logger.getLogger(UrlCrawler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // 6. typ
        try {
            String typ = page.select("div.barvalevat a#zvyraznenikat").text();
            novy.setTyp(typ.replaceAll("'", ""));
            if (novy.getTyp().length() == 0) {
                throw new RuntimeException("nenasla sa typ");
            }
        } catch (Exception e) {
            novy.setTyp("NEZISTENE");
            try {
                zaloguj("crawler " + id + ": NEZISTENE typ, url: [" + uloha.url + "] vynimka: [" + e.getMessage() + "]", true);
            } catch (InterruptedException ex) {
                Logger.getLogger(UrlCrawler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // 7. kategoria
        try {
            String kategoria = page.select("div.barvaleva a#zvyraznenikat").text();
            novy.setKategoria(kategoria.replaceAll("'", ""));
            if (novy.getKategoria().length() == 0) {
                throw new RuntimeException("nenasla sa kategoria");
            }
        } catch (Exception e) {
            novy.setKategoria("NEZISTENE");
            try {
                zaloguj("crawler " + id + ": NEZISTENE kategoria, url: [" + uloha.url + "] vynimka: [" + e.getMessage() + "]", true);
            } catch (InterruptedException ex) {
                Logger.getLogger(UrlCrawler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // 8. cena
        try { //html body div.sirka table tbody tr td table tbody tr td.listadvlevo table tbody tr td b
            String cena = page.select("html body div.sirka table tbody tr td table tbody tr td.listadvlevo table tbody tr td b").get(1).text(); //old: html body div.sirka table tbody tr td table tbody tr td.listal table tbody tr td b
            novy.setCena(cena.replaceAll("'", ""));
            if (novy.getCena().length() == 0) {
                throw new RuntimeException("nenasla sa cena");
            }
        } catch (Exception e) {
            novy.setCena("NEZISTENE");
            try {
                zaloguj("crawler " + id + ": NEZISTENE cena, url: [" + uloha.url + "] vynimka: [" + e.getMessage() + "]", true);
            } catch (InterruptedException ex) {
                Logger.getLogger(UrlCrawler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // najdi adresy
        List<String> adresy = new ArrayList<>();
        m = p.matcher(popisText);
        while (m.find()) {
            String addr = m.group(1);
            adresy.add(addr);
        }

        if (!adresy.isEmpty()) {
            //System.out.println("najdene emaily pre url [" + uloha.url + "]: " + adresy);
            this.najdenychAdries += adresy.size();
        }
        novy.setEmail(adresy.toString());
    }

    public String getOkresneMesto(String lokalita) {
        for (String okres : okresneMesta) {
            if (lokalita.contains(okres)) {
                return okres;
            }
        }
        for (Okres o : okresy) {
            if (lokalita.contains(o.obec)) {
                return o.okres;
            }
        }
        if (lokalita.contains("Nové Mesto n.Váhom")) {
            return "Nové Mesto nad Váhom";
        }
        return "Ostatné";
    }

}
