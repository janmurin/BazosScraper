/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deprecated;

import crawler.Inzerat;
import crawler.Kategoria;
import crawler.Okres;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Janco1
 */
public class TextDatabase {

    public static final String TEXT_DATABASE_DIR = "textDB";
    private List<String> okresneMesta;
    private List<Okres> okresy;
    private String AKTUALNY_CAS;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

    public TextDatabase() {
        initOkresneMesta();
    }

    List<String> getOkresneMesta() {
        return okresneMesta;
    }

    List<Okres> getOkresy() {
        return okresy;
    }

    // appenduje inzeraty
    synchronized void inzertInzeraty(List<Inzerat> inzeraty) {
        if (inzeraty.isEmpty()) {
            return;
        }
        List<List<Inzerat>> inzeratyLists = new ArrayList<>();
        boolean added;
        for (Inzerat inz : inzeraty) {
            added = false;
            for (List<Inzerat> zoznam : inzeratyLists) {
                if (zoznam.get(0).getPortal().equals(inz.getPortal())) {
                    zoznam.add(inz);
                    added = true;
                }
            }
            if (!added) {
                List<Inzerat> novy = new ArrayList<>();
                novy.add(inz);
                inzeratyLists.add(novy);
            }
        }
        for (List<Inzerat> zoznam : inzeratyLists) {
            String name = zoznam.get(0).getPortal().replace("http://", "").replace(".bazos.sk", "");

            Writer out = null;
            try {
                out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(TEXT_DATABASE_DIR + "/" + name + ".txt", true), "UTF-8"));
                for (Inzerat inz : zoznam) {
                    out.write(inz.getTextString()+"\n");
                }
                out.flush();
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(TextDatabase.class.getName()).log(Level.SEVERE, null, ex);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(TextDatabase.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(TextDatabase.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException ex) {
                        Logger.getLogger(TextDatabase.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
        }
    }

    public synchronized List<Inzerat> getInzeratyList(String portalName) {
        String name = portalName.replace("http://", "").replace(".bazos.sk", "");
        File file = new File(TEXT_DATABASE_DIR + "/" + name + ".txt");
        List<Inzerat> inzeraty = new ArrayList<>();

        BufferedReader f = null;
        try {
            f = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
            while (true) {
                StringTokenizer st = null;
                try {
                    String line = f.readLine();
                    if (line == null || line.length() == 0) {
                        break;
                    }
                    String[] parts = line.split("\\|");
                    if (parts.length != 12) {
                        throw new RuntimeException("poskodeny riadok, pocet stlpov != 12");
                    }
                    Inzerat novy = new Inzerat();
                    novy.setPortal(parts[0]);
                    novy.setNazov(parts[1]);
                    novy.setText(parts[2]);
                    novy.setMeno(parts[3]);
                    novy.setTelefon(parts[4]);
                    novy.setLokalita(parts[5]);
                    novy.setAktualny_link(parts[6]);
                    novy.setTyp(parts[7]);
                    novy.setKategoria(parts[8]);
                    novy.setCena(parts[9]);
                    novy.setEmail(parts[10]);
                    novy.setDatumInzeratu(parts[11]);
                    inzeraty.add(novy);
                } catch (Exception ex) {
                    Logger.getLogger(MainForm2.class.getName()).log(Level.SEVERE, null, ex);
                    break;
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainForm2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MainForm2.class.getName()).log(Level.SEVERE, null, ex);
        }

        return inzeraty;
    }

    private String getTimestamp() {
        return sdf.format(new Date(System.currentTimeMillis()));
    }

    synchronized Set<String> getInzeratyUrls(Kategoria kategoria) {
        System.out.println("nacitavam inzeraty z kategorie: " + kategoria.nazov);
        String name = kategoria.nazov;
        File file = new File(TEXT_DATABASE_DIR + "/" + name + ".txt");
        Set<String> inzeraty = new HashSet<>();

        BufferedReader f = null;
        try {
            f = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
            int riadok = 0;
            while (true) {
                riadok++;
                StringTokenizer st = null;
                try {
                    String line = f.readLine();
                    if (line == null || line.length() == 0) {
                        break;
                    }
                    String[] parts = line.split("\\|");
                    if (parts.length != 12) {
                        throw new RuntimeException("poskodeny riadok, pocet stlpov != 12, casti: " + parts.length + " riadok: " + riadok + " subor: " + file.getName());
                    }
                    inzeraty.add(parts[6]);
                } catch (Exception ex) {
                    Logger.getLogger(MainForm2.class.getName()).log(Level.SEVERE, null, ex);
                    break;
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainForm2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MainForm2.class.getName()).log(Level.SEVERE, null, ex);
        }

        return inzeraty;
    }

    synchronized String getLastTimeInserted() {
        File file = new File(TEXT_DATABASE_DIR + "/data.txt");

        BufferedReader f = null;
        try {
            f = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
            StringBuilder sb = new StringBuilder();
            while (true) {
                StringTokenizer st = null;
                try {
                    String line = f.readLine();
                    if (line == null || line.length() == 0) {
                        break;
                    }
                    sb.append(line + "\n");

                } catch (Exception ex) {
                    Logger.getLogger(MainForm2.class.getName()).log(Level.SEVERE, null, ex);
                    break;
                }
            }
            return sb.toString().split("\n")[0].split("=")[1];
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainForm2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MainForm2.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "unknown";
    }

    synchronized String getLastKontrola() {
        File file = new File(TEXT_DATABASE_DIR + "/data.txt");

        BufferedReader f = null;
        try {
            f = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
            StringBuilder sb = new StringBuilder();
            while (true) {
                StringTokenizer st = null;
                try {
                    String line = f.readLine();
                    if (line == null || line.length() == 0) {
                        break;
                    }
                    sb.append(line + "\n");

                } catch (Exception ex) {
                    Logger.getLogger(MainForm2.class.getName()).log(Level.SEVERE, null, ex);
                    break;
                }
            }
            return sb.toString().split("\n")[1].split("=")[1];
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainForm2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MainForm2.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "unknown";
    }

//    int getInzeratyPocet() {
//        File file = new File(TEXT_DATABASE_DIR);
//
//        int pocet = 0;
//        for (File subor : file.listFiles()) {
//            if (subor.getName().contains("data") || subor.getName().contains("okresy")) {
//                continue;
//            }
//            BufferedReader f = null;
//            try {
//                f = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
//                while (true) {
//                    try {
//                        String line = f.readLine();
//                        if (line == null || line.length() == 0) {
//                            break;
//                        }
//                        pocet++;
//
//                    } catch (Exception ex) {
//                        Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
//                        break;
//                    }
//                }
//
//            } catch (FileNotFoundException ex) {
//                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (UnsupportedEncodingException ex) {
//                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//
//        return pocet;
//    }
    synchronized private void initOkresneMesta() {
        System.out.println("nacitavam okresne mesta");
        File file = new File(TEXT_DATABASE_DIR + "/okresy.txt");
        okresneMesta = new ArrayList<>();
        okresy = new ArrayList<>();

        BufferedReader f = null;
        try {
            f = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
            while (true) {
                StringTokenizer st = null;
                try {
                    String line = f.readLine();
                    if (line == null) {
                        break;
                    }
                    String[] split = line.split("_");
                    if (okresneMesta.contains(split[1])) {
                        okresneMesta.add(split[1]);
                    }
                    okresy.add(new Okres(split[0], split[1]));

                } catch (Exception ex) {
                    Logger.getLogger(MainForm2.class.getName()).log(Level.SEVERE, null, ex);
                    break;
                }
            }
            System.out.println("skoncene citanie okresnych miest");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainForm2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MainForm2.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    synchronized void deleteInzeratyWithURL(Set<String> toDeleteURLs, String url) {
        if (toDeleteURLs.isEmpty()) {
            return;
        }
        List<Inzerat> platneInzeraty = new ArrayList<>();

        String name = url.replace("http://", "").replace(".bazos.sk", "");
        File file = new File(TEXT_DATABASE_DIR + "/" + name + ".txt");

        BufferedReader f = null;
        try {
            f = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
            while (true) {
                StringTokenizer st = null;
                try {
                    String line = f.readLine();
                    if (line == null || line.length() == 0) {
                        break;
                    }
                    String[] parts = line.split("\\|");
                    if (parts.length != 12) {
                        throw new RuntimeException("poskodeny riadok, pocet stlpov != 12");
                    }
                    if (!toDeleteURLs.contains(parts[6])) {
                        Inzerat novy = new Inzerat();
                        novy.setPortal(parts[0]);
                        novy.setNazov(parts[1]);
                        novy.setText(parts[2]);
                        novy.setMeno(parts[3]);
                        novy.setTelefon(parts[4]);
                        novy.setLokalita(parts[5]);
                        novy.setAktualny_link(parts[6]);
                        novy.setTyp(parts[7]);
                        novy.setKategoria(parts[8]);
                        novy.setCena(parts[9]);
                        novy.setEmail(parts[10]);
                        novy.setDatumInzeratu(parts[11]);
                        platneInzeraty.add(novy);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(MainForm2.class.getName()).log(Level.SEVERE, null, ex);
                    break;
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainForm2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MainForm2.class.getName()).log(Level.SEVERE, null, ex);
        }

        Writer out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(TEXT_DATABASE_DIR + "/" + name + ".txt"), "UTF-8"));
            for (Inzerat inz : platneInzeraty) {
                out.write(inz.getTextString()+"\n" );
            }
            out.flush();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TextDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TextDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TextDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    Logger.getLogger(TextDatabase.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    synchronized void updateLastKontrola() {
        File file = new File(TEXT_DATABASE_DIR + "/data.txt");

        StringBuilder sb = new StringBuilder();
        BufferedReader f = null;
        try {
            f = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));

            while (true) {
                StringTokenizer st = null;
                try {
                    String line = f.readLine();
                    if (line == null || line.length() == 0) {
                        break;
                    }
                    sb.append(line + "\n");

                } catch (Exception ex) {
                    Logger.getLogger(MainForm2.class.getName()).log(Level.SEVERE, null, ex);
                    break;
                }
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainForm2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MainForm2.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (sb.toString().split("\n").length < 2) {
            throw new RuntimeException("poskodene data v subore data.txt");
        }

        String novy = sb.toString().split("\n")[0] + "\nlastKontrola=" + getTimestamp();

        Writer out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(TEXT_DATABASE_DIR + "/data.txt"), "UTF-8"));
            out.write(novy);
            out.flush();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TextDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TextDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TextDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    Logger.getLogger(TextDatabase.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    synchronized void updateLastTimeInserted() {
        File file = new File(TEXT_DATABASE_DIR + "/data.txt");

        StringBuilder sb = new StringBuilder();
        BufferedReader f = null;
        try {
            f = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));

            while (true) {
                StringTokenizer st = null;
                try {
                    String line = f.readLine();
                    if (line == null || line.length() == 0) {
                        break;
                    }
                    sb.append(line + "\n");

                } catch (Exception ex) {
                    Logger.getLogger(MainForm2.class.getName()).log(Level.SEVERE, null, ex);
                    break;
                }
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainForm2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MainForm2.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (sb.toString().split("\n").length < 2) {
            throw new RuntimeException("poskodene data v subore data.txt");
        }

        String novy = "lastTimeInserted=" + getTimestamp() + "\n" + sb.toString().split("\n")[1];

        Writer out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(TEXT_DATABASE_DIR + "/data.txt"), "UTF-8"));
            out.write(novy);
            out.flush();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TextDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TextDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TextDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    Logger.getLogger(TextDatabase.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}
