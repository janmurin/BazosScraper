/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Janco1
 */
public class Inzerat implements Comparable<Inzerat> {

    private String portal;
    private String nazov;
    private String text;
    private String meno;
    private String telefon;
    private String lokalita;
    private String aktualny_link;
    private String typ;
    private String kategoria;
    private String cena;
    private String email = "";
    private String datumInzeratu;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public String getDatumInzeratu() {
        return datumInzeratu;
    }

    public void setDatumInzeratu(String datumInzeratu) {
        this.datumInzeratu = datumInzeratu;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.replaceAll("\"", "");
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public String getKategoria() {
        return kategoria;
    }

    public void setKategoria(String kategoria) {
        this.kategoria = kategoria;
    }

    public String getPortal() {
        return portal;
    }

    public void setPortal(String portal) {
        this.portal = portal.replaceAll("\"", "");
    }

    public String getNazov() {
        return nazov;
    }

    public void setNazov(String nazov) {
        this.nazov = nazov.replaceAll("\\|", "");
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text.substring(0, Math.min(text.length(), 3999)).replaceAll("\\|", "");
    }

    public String getMeno() {
        return meno;
    }

    public void setMeno(String meno) {
        this.meno = meno.replaceAll("\\|", "");
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon.replaceAll("\\|", "");
    }

    public String getLokalita() {
        return lokalita;
    }

    public void setLokalita(String lokalita) {
        this.lokalita = lokalita.replaceAll("\\|", "");
    }

    public String getAktualny_link() {
        return aktualny_link;
    }

    public void setAktualny_link(String aktualny_link) {
        this.aktualny_link = aktualny_link.replaceAll("\\|", "");
    }

    public String getCena() {
        return cena;
    }

    public void setCena(String cena) {
        this.cena = cena;
    }

    public String getTextString() {
        return portal
                + "|" + nazov.replaceAll("\\|", "")
                + "|" + text.replaceAll("\n", " ").replaceAll("\\|", "")
                + "|" + meno.replaceAll("\\|", "")
                + "|" + telefon.replaceAll("\\|", "")
                + "|" + lokalita
                + "|" + aktualny_link
                + "|" + typ
                + "|" + kategoria
                + "|" + cena.replaceAll("\\|", "")
                + "|" + email
                + "|" + datumInzeratu + "|";
    }

    @Override
    public int compareTo(Inzerat o) {
        try {
            Date datum1=sdf.parse(datumInzeratu.substring(0, 10));
            Date datum2=sdf.parse(o.getDatumInzeratu().substring(0, 10));
            if(datum1.before(datum2)){
                return 1;
            }
        } catch (ParseException ex) {
            Logger.getLogger(Inzerat.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

}
