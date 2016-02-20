/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backup;

import crawler.TextDatabase;
import crawler.*;
import static deprecated.TextDatabase.TEXT_DATABASE_DIR;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 *
 * @author Janco1
 */
public class MainForm extends javax.swing.JFrame {

    private final TextDatabase database;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public int inzeratyPocet;
    private String vybranyDatum;
    private final String aktualnyDatum;
    private final SimpleDateFormat sdf2;

    /**
     * Creates new form MainForm
     */
    public MainForm() {
        initComponents();
        this.database = new TextDatabase();
        setLocationRelativeTo(null);
        try {
            String poslednyInsert = database.getLastTimeInserted();
            String poslednaKontrola = database.getLastKontrola();
            int inzeratov = 0;//database.getInzeratyPocet();
            Date lastInsertDate = new Date();
            Date lastKontrolaDate = new Date();
            Date currentDate = new Date();
            int diffDni = 1;
            try {
                lastInsertDate = sdf.parse(poslednyInsert);
                lastKontrolaDate = sdf.parse(poslednaKontrola);
                diffDni = (int) Math.ceil((currentDate.getTime() - lastInsertDate.getTime()) / (1000 * 60 * 60 * 24.0));
                System.out.println("diffDni: " + diffDni);

            } catch (ParseException ex) {
                Logger.getLogger(AktualizaciaFrame.class.getName()).log(Level.SEVERE, null, ex);
            }

            poslednyUpdateLabel.setText("Posledná aktualizácia: " + poslednyInsert + " (pred " + Utils.getElapsedTime(lastInsertDate.getTime()) + " )");
            poslednaKontrolaLabel.setText("Posledná kontrola: " + poslednaKontrola + " (pred " + Utils.getElapsedTime(lastKontrolaDate.getTime()) + " )");
            nastavButtony(false);
            SwingWorker<Void, String> aktualizaciaWorker = new SwingWorker<Void, String>() {

                @Override
                protected Void doInBackground() throws Exception {
                    zapisDoLogu("hladam inzeratov v DB");
                    inzeratyPocet = database.getInzeratyPocet();
                    zapisDoLogu("inzeratov v DB je: " + inzeratyPocet);
                    publish("inzeratov v DB: " + inzeratyPocet);
                    return null;
                }

                @Override
                protected void process(List<String> chunks) {
                    for (String str : chunks) {
                        inzeratovVDBLabel.setText(str);
                    }
                }

                @Override
                protected void done() {
                    nastavButtony(true);

                }

            };
            aktualizaciaWorker.execute();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, "vynimka: " + e);
        }

        // initneme poslednych 30 dni na mazanie
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(System.currentTimeMillis()));
        sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        this.aktualnyDatum = sdf2.format(c.getTime());
        System.out.println("aktualny datum: " + aktualnyDatum);
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        for (int i = 0; i < 30; i++) {
            c.add(Calendar.DATE, -1);
            System.out.println("novy datum: " + sdf2.format(c.getTime()));
            model.addElement(sdf2.format(c.getTime()));
        }
        datumComboBox.setModel(model);
        datumComboBox.setSelectedIndex(0);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        spustiButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        logTextArea = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        klucoveSlovaTextArea = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        uloha1Button = new javax.swing.JButton();
        poslednyUpdateLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        uloha2Button = new javax.swing.JButton();
        poslednaKontrolaLabel = new javax.swing.JLabel();
        inzeratovVDBLabel = new javax.swing.JLabel();
        mazanieDBButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        datumComboBox = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MainForm");

        spustiButton.setText("Aktualizovat DB");
        spustiButton.setEnabled(false);
        spustiButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                spustiButtonActionPerformed(evt);
            }
        });

        logTextArea.setColumns(20);
        logTextArea.setRows(5);
        jScrollPane1.setViewportView(logTextArea);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Uloha 1"));

        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("vypise meno, telefon a url vsetkych inzeratov na reality.bazos.sk, ktore obsahuju nasledujuce klucove slova:\n(na velkych pismenach nezalezi)");
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setEnabled(false);
        jScrollPane2.setViewportView(jTextArea1);

        klucoveSlovaTextArea.setColumns(20);
        klucoveSlovaTextArea.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        klucoveSlovaTextArea.setRows(5);
        klucoveSlovaTextArea.setText("predám predam predaj\nprenájom prenajom prenajmem");
        jScrollPane3.setViewportView(klucoveSlovaTextArea);

        jLabel1.setText("klucove slova:");

        uloha1Button.setText("vyhladaj");
        uloha1Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uloha1ButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(uloha1Button)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addComponent(uloha1Button))
        );

        poslednyUpdateLabel.setText("Posledny update: ");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Uloha 2"));

        jTextArea2.setColumns(20);
        jTextArea2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTextArea2.setLineWrap(true);
        jTextArea2.setRows(5);
        jTextArea2.setText("vyhlada vsetky emailove adresy v inzeratoch na bazosi a zapise ich do suboru");
        jTextArea2.setWrapStyleWord(true);
        jTextArea2.setEnabled(false);
        jScrollPane4.setViewportView(jTextArea2);

        uloha2Button.setText("vyhladaj");
        uloha2Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uloha2ButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(uloha2Button)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(uloha2Button))
        );

        poslednaKontrolaLabel.setText("Posledná kontrola aktuálnosti: 22.12.2015");

        inzeratovVDBLabel.setText("Inzerátov v DB: 700 000");

        mazanieDBButton.setText("mazanie inzeratov");
        mazanieDBButton.setEnabled(false);
        mazanieDBButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mazanieDBButtonActionPerformed(evt);
            }
        });

        jLabel2.setText("Zadajte datum od ktoreho treba zmazat inzeraty:");

        datumComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        datumComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                datumComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(datumComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(mazanieDBButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE))))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(spustiButton)
                                .addGap(23, 23, 23)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(poslednaKontrolaLabel)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(poslednyUpdateLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(inzeratovVDBLabel)))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spustiButton)
                    .addComponent(poslednyUpdateLabel)
                    .addComponent(inzeratovVDBLabel))
                .addGap(6, 6, 6)
                .addComponent(poslednaKontrolaLabel)
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(datumComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mazanieDBButton)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void spustiButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_spustiButtonActionPerformed
        AktualizaciaFrame aktualizaciaFrame = new AktualizaciaFrame(database, inzeratyPocet);
        aktualizaciaFrame.setVisible(true);
    }//GEN-LAST:event_spustiButtonActionPerformed

    private void uloha1ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uloha1ButtonActionPerformed
        nastavButtony(false);
        Shared.logMessages = new LinkedBlockingQueue<>();
        SwingWorker<Void, String> aktualizaciaWorker = new SwingWorker<Void, String>() {

            @Override
            protected Void doInBackground() throws Exception {
                Writer out = null;
                String nazovSuboru = "uloha1output.txt";
                try {
                    publish("ziskavam z DB reality.txt zoznam inzeratov");
                    List<Inzerat> inzeratyList = database.getInzeratyList("reality");
                    publish("ziskavam klucove slova");
                    String text = klucoveSlovaTextArea.getText();
                    String[] klucoveSlova = text.replaceAll("\n", " ").split(" ");
                    Set<String> klucove = new HashSet<>();
                    for (int i = 0; i < klucoveSlova.length; i++) {
                        String slovo = klucoveSlova[i].trim();
                        if (slovo.length() > 0) {
                            klucove.add(slovo);
                        }
                    }
                    publish("ziskane klucove slova: " + klucove);
                    publish("vyhladavam inzeraty s klucovymi slovami");
                    List<Inzerat> ziskane = new ArrayList<>();
                    int prejdenych = 0;
                    for (Inzerat inz : inzeratyList) {
                        StringBuilder spojene = new StringBuilder();
                        spojene.append(inz.getNazov() + " ");
                        spojene.append(inz.getText() + " ");
                        String spojenyString = spojene.toString().toLowerCase();
                        for (String slovo : klucove) {
                            if (spojenyString.contains(slovo)) {
                                ziskane.add(inz);
                                break;
                            }
                        }
                        prejdenych++;
                        if (prejdenych % 2000 == 0) {
                            zapisDoLogu(prejdenych + "/" + inzeratyList.size());
                        }
                    }
                    //zapisDoLogu("usporaduvam inzeraty podla datumu");
                    //Collections.sort(ziskane);
                    publish("naslo sa " + ziskane.size() + " inzeratov obsahujuce klucove slova");
                    publish("zapisujem ziskane udaje do suboru " + nazovSuboru);

                    out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(nazovSuboru), "UTF-8"));
                    for (Inzerat inz : ziskane) {
                        out.write("\"" + inz.getDatumInzeratu().substring(0, 10) + "\",\"" + inz.getMeno() + "\",\"" + inz.getTelefon() + "\",\"" + inz.getAktualny_link() + "\"\n");
                    }
                    out.flush();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(rootPane, "MainForm vynimka: " + ex);
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
                publish("udaje zapisane do suboru " + nazovSuboru);
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String str : chunks) {
                    zapisDoLogu(str);
                }
            }

            @Override
            protected void done() {
                nastavButtony(true);
                try {
                    Shared.logMessages.put("poison.pill");
                } catch (InterruptedException ex) {
                    Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                }
                mazanieDBButton.setEnabled(true);
            }

        };
        aktualizaciaWorker.execute();

        SwingWorker<Void, String> logWorker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    String message = Shared.logMessages.take();
                    System.out.println("MainForm logWorker: prvy message prijaty: " + message);
                    while (message != null) {
                        if (message.equals("poison.pill")) {
                            break;
                        }
                        //System.out.println("publishujem message: " + message);
                        publish(message);
                        message = Shared.logMessages.take();
                    }
                    System.out.println("MainForm logWorker: ziadne dalsie message");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(rootPane, "MainForm vynimka: " + ex);
                    Logger.getLogger(AktualizaciaFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String m : chunks) {
                    zapisDoLogu(m);
                }
            }

        };
        logWorker.execute();
        System.out.println("logWorker executed");

    }//GEN-LAST:event_uloha1ButtonActionPerformed

    private void uloha2ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uloha2ButtonActionPerformed
        nastavButtony(false);
        Shared.logMessages = new LinkedBlockingQueue<>();
        SwingWorker<Void, String> aktualizaciaWorker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                Writer out = null;
                try {
                    File file = new File(TEXT_DATABASE_DIR);

                    Set<String> emaily = new HashSet<>();
                    List<String> emailyString = new ArrayList<>();
                    Pattern p;
                    Matcher m;
                    String RE_MAIL = "([\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Za-z]{2,4})";
                    p = Pattern.compile(RE_MAIL);
                    try {
                        for (File subor : file.listFiles()) {
                            if (subor.getName().contains("data") || subor.getName().contains("okresy")) {
                                continue;
                            }
                            publish("hladam emaily v " + subor.getName());
                            List<Inzerat> inzeratyList = database.getInzeratyList(subor.getName().replace(".txt", ""));
                            System.out.println("prehladavam " + inzeratyList.size() + " inzeratov");
                            publish("prehladavam " + inzeratyList.size() + " inzeratov");
                            int c = 0;
                            for (Inzerat inz : inzeratyList) {
                                c++;
                                m = p.matcher(inz.getEmail());
                                while (m.find()) {
                                    String addr = m.group(1);
                                    emaily.add(addr);
                                    emailyString.add(addr);
                                }
//                                if (c % 2000 == 0) {
//                                    publish(c + "/" + inzeratyList.size());
//                                }
                            }
                        }
                    } catch (Exception e) {
                        publish("vynimka: " + e);
                    }
                    publish("najdenych inzeratov s emailom: " + emailyString.size() + "/" + inzeratyPocet);
                    publish("najdenych emailov: " + emaily.size() + " zapisujem emaily do suboru uloha2output.txt");

                    out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("uloha2output.txt"), "UTF-8"));
                    for (String email : emaily) {
                        out.write("\"" + email + "\"\n");
                    }
                    out.flush();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(rootPane, "MainForm vynimka: " + ex);
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
                publish("udaje zapisane");
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String str : chunks) {
                    zapisDoLogu(str);
                }
            }

            @Override
            protected void done() {
                nastavButtony(true);
                try {
                    Shared.logMessages.put("poison.pill");
                } catch (InterruptedException ex) {
                    Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        aktualizaciaWorker.execute();

        SwingWorker<Void, String> logWorker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    String message = Shared.logMessages.take();
                    System.out.println("MainForm logWorker: prvy message prijaty: " + message);
                    while (message != null) {
                        if (message.equals("poison.pill")) {
                            break;
                        }
                        //System.out.println("publishujem message: " + message);
                        publish(message);
                        message = Shared.logMessages.take();
                    }
                    System.out.println("MainForm logWorker: ziadne dalsie message");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(rootPane, "MainForm vynimka: " + ex);
                    Logger.getLogger(AktualizaciaFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String m : chunks) {
                    zapisDoLogu(m);
                }
            }
        };
        logWorker.execute();
        System.out.println("logWorker executed");
    }//GEN-LAST:event_uloha2ButtonActionPerformed

    private void mazanieDBButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mazanieDBButtonActionPerformed
        vybranyDatum = (String) datumComboBox.getSelectedItem();
        System.out.println("mazem z databazy inzeraty od datumu: " + vybranyDatum);
        nastavButtony(false);
        Shared.logMessages = new LinkedBlockingQueue<>();
        SwingWorker<Void, String> aktualizaciaWorker = new SwingWorker<Void, String>() {

//            public void zaloguj(String message, boolean poslatGui) throws InterruptedException {
//                System.out.println(message);
//                if (poslatGui) {
//                    try {
//                        Shared.logMessages.put(message);
//                    } catch (InterruptedException ex) {
//                        //Logger.getLogger(UrlSearcher.class.getName()).log(Level.SEVERE, null, ex);
//                        throw ex;
//                    }
//                }
//            }
            @Override
            protected Void doInBackground() throws Exception {
                File file = new File(TEXT_DATABASE_DIR);
                try {

                    Date vybranyDate = sdf2.parse(vybranyDatum);
                    // 2. prejdeme celu databazu a pytame sa ci je inzerat este aktualny, ak NIE, tak si pridame do zoznamu na zmazanie
                    for (File subor : file.listFiles()) {
                        if (subor.getName().contains("data") || subor.getName().contains("okresy")) {
                            continue;
                        }
                        String kategoria = subor.getName().replace(".txt", "").replace("textdb/", "");
                        publish("\nnacitavam inzeraty z lokal DB pre kategoriu [" + kategoria + "] ");
                        List<Inzerat> inzeraty = database.getInzeratyList(kategoria);
                        publish("hladam inzeraty na vymazanie ktore su v datume: " + vybranyDatum + " --> " + aktualnyDatum);
                        Set<String> toDeleteURLs = new HashSet<String>();
                        // kontrolujeme inzeraty z databazy ci su este platne
                        // hladame nas inzerat v nasearchovanom zozname
                        for (Inzerat inz : inzeraty) {
                            Date datum = sdf.parse(inz.getDatumInzeratu());
                            if (datum.after(vybranyDate)) {
                                toDeleteURLs.add(inz.getAktualny_link());
                            }
                        }

                        publish("to delete inzeraty size: " + toDeleteURLs.size());
                        database.deleteInzeratyWithURL(toDeleteURLs, kategoria);
                        publish("inzeraty v datumovom intervale " + vybranyDatum + " --> " + aktualnyDatum + " ZMAZANE");
                    }
                } catch (Exception e) {
                    Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
                }
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String str : chunks) {
                    zapisDoLogu(str);
                }
            }

            @Override
            protected void done() {
                nastavButtony(true);
                try {
                    Shared.logMessages.put("poison.pill");
                } catch (InterruptedException ex) {
                    Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        };
        aktualizaciaWorker.execute();

        SwingWorker<Void, String> logWorker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    String message = Shared.logMessages.take();
                    System.out.println("MainForm logWorker: prvy message prijaty: " + message);
                    while (message != null) {
                        if (message.equals("poison.pill")) {
                            break;
                        }
                        //System.out.println("publishujem message: " + message);
                        publish(message);
                        message = Shared.logMessages.take();
                    }
                    System.out.println("MainForm logWorker: ziadne dalsie message");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(rootPane, "MainForm vynimka: " + ex);
                    Logger.getLogger(AktualizaciaFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String m : chunks) {
                    zapisDoLogu(m);
                }
            }

        };
        logWorker.execute();
        System.out.println("logWorker executed");

    }//GEN-LAST:event_mazanieDBButtonActionPerformed

    private void datumComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_datumComboBoxActionPerformed

    }//GEN-LAST:event_datumComboBoxActionPerformed

    private void zapisDoLogu(String text) {
        //System.out.println(text);
        logTextArea.append(text + "\n");
        logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> datumComboBox;
    private javax.swing.JLabel inzeratovVDBLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea klucoveSlovaTextArea;
    private javax.swing.JTextArea logTextArea;
    private javax.swing.JButton mazanieDBButton;
    private javax.swing.JLabel poslednaKontrolaLabel;
    private javax.swing.JLabel poslednyUpdateLabel;
    private javax.swing.JButton spustiButton;
    private javax.swing.JButton uloha1Button;
    private javax.swing.JButton uloha2Button;
    // End of variables declaration//GEN-END:variables

    private void nastavButtony(boolean b) {
        uloha1Button.setEnabled(b);
        uloha2Button.setEnabled(b);
        mazanieDBButton.setEnabled(b);
        spustiButton.setEnabled(b);
    }
}
