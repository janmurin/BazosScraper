/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backup;

import crawler.Inzerat;
import crawler.Kategoria;
import crawler.TextDatabase;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Janco1
 */
public class PomocneVypocty {

    private class Polozka{
        String meno;
        String priezvisko;
        String mesto;

        public Polozka(String meno, String priezvisko, String mesto) {
            this.meno = meno;
            this.priezvisko = priezvisko;
            this.mesto = mesto;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 67 * hash + Objects.hashCode(this.meno);
            hash = 67 * hash + Objects.hashCode(this.priezvisko);
            hash = 67 * hash + Objects.hashCode(this.mesto);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Polozka other = (Polozka) obj;
            if (!Objects.equals(this.meno, other.meno)) {
                return false;
            }
            if (!Objects.equals(this.priezvisko, other.priezvisko)) {
                return false;
            }
            if (!Objects.equals(this.mesto, other.mesto)) {
                return false;
            }
            return true;
        }
        
    }
    
    private List<Kategoria> kategorie;

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

    public void execute() {
        initKategorie();
        TextDatabase db = new TextDatabase();
        Set<Polozka> polozky=new HashSet<>();
        for (Kategoria kat : kategorie) {
            System.out.println("spracuvam kategoriu: "+kat.nazov);
            List<Inzerat> inzeratyList = db.getInzeratyList(kat.nazov);
            System.out.println("nacitanych inzeratov: "+inzeratyList.size());
            System.out.println("");
            for (Inzerat inz : inzeratyList) {
                String meno=inz.getMeno();
                String[] mena = inz.getMeno().split(" ");
                if (mena.length < 2) {
                    continue;
                }
                if(meno.contains(".")){
                    continue;
                }
                String lokalita = inz.getLokalita();
                //System.out.println("{\"meno\":\""+mena[0]+"\",\"priezvisko\":\""+mena[1]+"\",\"mesto\":\""+lokalita+"\"},");
                polozky.add(new Polozka(mena[0], mena[1], lokalita));
            }
            
            System.out.println("");
            //break;
        }
        System.out.println("celkovo poloziek: "+polozky.size());
        for(Polozka p:polozky){
            System.out.println("{\"meno\":\""+p.meno.replaceAll("\"", "")+"\",\"priezvisko\":\""+p.priezvisko.replaceAll("\"", "")+"\",\"mesto\":\""+p.mesto.replaceAll("\"", "")+"\"},");
        }
    }

    public static void main(String[] args) {
        PomocneVypocty pomocneVypocty = new PomocneVypocty();
        pomocneVypocty.execute();
    }
}
