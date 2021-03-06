/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deprecated;

import crawler.AktualizaciaStatus;
import crawler.CrawlerManagerTask;
import crawler.Shared;
import crawler.Utils;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;

/**
 *
 * @author Janco1
 */
public class AktualizaciaFrame2 extends javax.swing.JFrame {

    private final TextDatabase database;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private PropertyChangeSupport changes = new PropertyChangeSupport(this);
    private int searcherov;
    private int crawlerov;
    private int pocetDni;
    private ExecutorService crawlerManagerExecutor;

    /**
     * Creates new form AktualizaciaFrame
     *
     * @param database
     */
    public AktualizaciaFrame2(TextDatabase database, int pocet) {
        initComponents();
        setLocationRelativeTo(null);

        this.database = database;
        String poslednyInsert = database.getLastTimeInserted();
        String poslednaKontrola = database.getLastKontrola();
        int inzeratov = pocet;//database.getInzeratyPocet();
        Date lastInsertDate = new Date();
        Date lastKontrolaDate = new Date();
        //Date maxDate = new Date();
        Date currentDate = new Date();
        int diffDni = 1;
        try {
            lastInsertDate = sdf.parse(poslednyInsert);
            lastKontrolaDate = sdf.parse(poslednaKontrola);
            //maxDate = sdf.parse("2015-12-31 23:59:20.0");
            diffDni = (int) Math.ceil((currentDate.getTime() - lastInsertDate.getTime()) / (1000 * 60 * 60 * 24.0));
            System.out.println("diffDni: " + diffDni);

        } catch (ParseException ex) {
            Logger.getLogger(AktualizaciaFrame2.class.getName()).log(Level.SEVERE, null, ex);
        }
//        if (currentDate.after(maxDate)) {
//            JOptionPane.showMessageDialog(rootPane, "vyprsala platnost programu");
//            nastavButtony(false);
//            return;
//        }
//        if (currentDate.after(maxDate)) {
//            JOptionPane.showMessageDialog(rootPane, "vyprsala platnost programu");
//            nastavButtony(false);
//            return;
//        }

        poslednaAktualizaciaLabel.setText("Posledná aktualizácia: " + poslednyInsert + " (pred " + Utils.getElapsedTime(lastInsertDate.getTime()) + " )");
        poslednaKontrolaLabel.setText("Posledná kontrola: " + poslednaKontrola + " (pred " + Utils.getElapsedTime(lastKontrolaDate.getTime()) + " )");
        inzeratovVDBLabel.setText("Inzerátov v DB: " + inzeratov + " ");

        if (diffDni < 1) {
            diffDni = 1;
        }
        searcherovSpinner.setModel(new SpinnerNumberModel(2, 1, 10, 1));
        odstranitSearcherovSpinner.setModel(new SpinnerNumberModel(20, 1, 20, 1));
        crawlerovSpinner.setModel(new SpinnerNumberModel(12, 1, 30, 1));
        pocetDniSpinner.setModel(new SpinnerNumberModel(diffDni, 1, 365, 1));
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("window closing action");
                if (crawlerManagerExecutor != null) {
                    crawlerManagerExecutor.shutdownNow();
                }
                super.windowClosing(e);
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        poslednaAktualizaciaLabel = new javax.swing.JLabel();
        inzeratovVDBLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        aktualizovatButton = new javax.swing.JButton();
        searcherovSpinner = new javax.swing.JSpinner();
        crawlerovSpinner = new javax.swing.JSpinner();
        pocetDniSpinner = new javax.swing.JSpinner();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        odstranitNeaktualneButton = new javax.swing.JButton();
        odstranitSearcherovSpinner = new javax.swing.JSpinner();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        logTextArea = new javax.swing.JTextArea();
        poslednaKontrolaLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        searcheryProgressBar = new javax.swing.JProgressBar();
        crawleryProgressBar = new javax.swing.JProgressBar();
        uplynuloLabel = new javax.swing.JLabel();
        ostavaSearcheryLabel = new javax.swing.JLabel();
        ostavaCrawleryLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Aktualizacia");
        setResizable(false);

        poslednaAktualizaciaLabel.setText("Posledná aktualizácia prebehla: 22.12.2015");

        inzeratovVDBLabel.setText("Inzerátov v DB: 700 000");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Aktualizácia"));

        jLabel3.setText("searcherov:");

        jLabel4.setText("crawlerov:");

        jLabel5.setText("počet dní na aktualizovanie:");

        aktualizovatButton.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        aktualizovatButton.setText("Aktualizovať");
        aktualizovatButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aktualizovatButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(aktualizovatButton)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(searcherovSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(crawlerovSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pocetDniSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(searcherovSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(crawlerovSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(pocetDniSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addComponent(aktualizovatButton)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Odstránenie starých inzerátov"));

        jTextArea1.setBackground(java.awt.SystemColor.control);
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("Skontroluje aktuálnosť starých inzerátov v databáze a odstráni neaktuálne");
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextArea1.setEnabled(false);
        jScrollPane1.setViewportView(jTextArea1);

        odstranitNeaktualneButton.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        odstranitNeaktualneButton.setText("Odstrániť neaktuálne");
        odstranitNeaktualneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                odstranitNeaktualneButtonActionPerformed(evt);
            }
        });

        jLabel7.setText("searcherov:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(odstranitSearcherovSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(odstranitNeaktualneButton)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(odstranitNeaktualneButton)
                        .addContainerGap())
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(odstranitSearcherovSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        logTextArea.setColumns(20);
        logTextArea.setRows(5);
        jScrollPane2.setViewportView(logTextArea);

        poslednaKontrolaLabel.setText("Posledná kontrola aktuálnosti: 22.12.2015");

        jLabel1.setText("Searchery:");

        jLabel2.setText("Crawlery:");

        uplynuloLabel.setText("Uplynulo: ");

        ostavaSearcheryLabel.setText("Ostava:");

        ostavaCrawleryLabel.setText("Ostava:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(crawleryProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(searcheryProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(poslednaAktualizaciaLabel)
                                .addComponent(inzeratovVDBLabel)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(ostavaSearcheryLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(uplynuloLabel)
                                .addGap(131, 131, 131))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(poslednaKontrolaLabel)
                                    .addComponent(ostavaCrawleryLabel))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(poslednaAktualizaciaLabel)
                    .addComponent(poslednaKontrolaLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(inzeratovVDBLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(searcheryProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(uplynuloLabel)
                        .addComponent(ostavaSearcheryLabel)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(crawleryProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(ostavaCrawleryLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void aktualizovatButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aktualizovatButtonActionPerformed
        searcherov = (int) searcherovSpinner.getValue();
        crawlerov = (int) crawlerovSpinner.getValue();
        pocetDni = (int) pocetDniSpinner.getValue();
        nastavButtony(false);
        logTextArea.setText("");
        zapisDoLogu("Zacinam aktualizaciu DB: searcherov=[" + searcherov + "], crawlerov=[" + crawlerov + "], pocetDni=[" + pocetDni + "]");
        Shared.logMessages = new LinkedBlockingQueue<>();

        SwingWorker<Void, AktualizaciaStatus> aktualizaciaWorker = new SwingWorker<Void, AktualizaciaStatus>() {

            @Override
            protected Void doInBackground() throws Exception {
                crawlerManagerExecutor = Executors.newSingleThreadExecutor();
                CrawlerManagerTask task = new CrawlerManagerTask(database, searcherov, crawlerov, pocetDni);
                long start = System.currentTimeMillis();
                Future<?> future = crawlerManagerExecutor.submit(task);

                int searcherNasiel, searcherHlada, crawlerNasiel, crawlerHlada;
                long st, wait;
                while (!future.isDone()) {
                    searcherNasiel = Shared.searcherNasiel.intValue();
                    searcherHlada = Shared.searcherHlada.intValue();
                    int searcherProgress = (int) ((searcherNasiel / (double) searcherHlada) * 100);
                    crawlerNasiel = Shared.crawlerNasiel.intValue();
                    crawlerHlada = Shared.crawlerHlada.intValue();
                    int crawlerProgress = (int) ((crawlerNasiel / (double) crawlerHlada) * 100);

                    publish(new AktualizaciaStatus(searcherProgress, crawlerProgress,
                            Utils.getElapsedTime(start),
                            Utils.getETAtime(start, searcherNasiel, searcherHlada),
                            Utils.getETAtime(start, crawlerNasiel, crawlerHlada)));
                    Thread.sleep(500);
                }
                System.out.println("manager cancelled: " + future.isCancelled() + " isDone=" + future.isDone() + " ");
                System.out.println("crawlerManagerExecutor.isShutdown()=" + crawlerManagerExecutor.isShutdown() + " isTerminated()=" + crawlerManagerExecutor.isTerminated());

//                try {
//                    future.get();
//                } catch (ExecutionException e) { // úloha vyhodila výnimku, vyhodíme ju tiež 
//                    Logger.getLogger(AktualizaciaFrame.class.getName()).log(Level.SEVERE, null, e);
//                    JOptionPane.showMessageDialog(rootPane, e.getMessage());
//                } finally { // nastaví interrupt, ak úloha ešte beží 
//                    future.cancel(true);
//                }
//                crawlerManagerExecutor.shutdown();
//                crawlerManagerExecutor.awaitTermination(365, TimeUnit.DAYS);
                zapisDoLogu("Aktualizacia ukoncena, Čas trvania: " + Utils.getElapsedTime(start));
                zapisDoLogu("naslo sa " + Shared.crawlerNasiel + " novych inzeratov.");
                return null;
            }

            @Override
            protected void process(List<AktualizaciaStatus> chunks) {
                AktualizaciaStatus status = chunks.get(chunks.size() - 1);
                searcheryProgressBar.setValue(status.searcherProgress);
                searcheryProgressBar.setString(status.searcherProgress + " %");
                crawleryProgressBar.setValue(status.crawlerProgress);
                crawleryProgressBar.setName(status.crawlerProgress + " %");
                uplynuloLabel.setText("Uplynulo: " + status.uplynulo);
                ostavaSearcheryLabel.setText("Ostava: " + status.ostavaSearchery);
                ostavaCrawleryLabel.setText("Ostava: " + status.ostavaCrawlery);
            }

            @Override
            protected void done() {
                nastavButtony(true);
            }
        };
        aktualizaciaWorker.execute();
        System.out.println("aktualizaciaWorker executed");

        SwingWorker<Void, String> logWorker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    String message = Shared.logMessages.take();
                    System.out.println("AktualizaciaFrame logWorker: prvy message prijaty: " + message);
                    while (message != null) {
                        if (message.equals("poison.pill")) {
                            break;
                        }
                        //System.out.println("publishujem message: " + message);
                        publish(message);
                        message = Shared.logMessages.take();
                    }
                    System.out.println("AktualizaciaFrame logWorker: ziadne dalsie message");
                } catch (Exception e) {
                    Logger.getLogger(AktualizaciaFrame2.class.getName()).log(Level.SEVERE, null, e);
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
    }//GEN-LAST:event_aktualizovatButtonActionPerformed

    private void odstranitNeaktualneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_odstranitNeaktualneButtonActionPerformed
        searcherov = (int) odstranitSearcherovSpinner.getValue();
        nastavButtony(false);
        logTextArea.setText("");
        zapisDoLogu("Zacinam odstranovanie neaktualnych: searcherov=[" + searcherov + "]");
        Shared.logMessages = new LinkedBlockingQueue<>();

        SwingWorker<Void, AktualizaciaStatus> aktualizaciaWorker = new SwingWorker<Void, AktualizaciaStatus>() {

            @Override
            protected Void doInBackground() throws Exception {
                crawlerManagerExecutor = Executors.newSingleThreadExecutor();
                CrawlerManagerTask task = new CrawlerManagerTask(database, searcherov);
                long start = System.currentTimeMillis();
                Future<?> future = crawlerManagerExecutor.submit(task);

                int searcherNasiel, searcherHlada, crawlerNasiel, crawlerHlada;
                long st, wait;
                while (!future.isDone()) {
                    searcherNasiel = Shared.searcherNasiel.intValue();
                    searcherHlada = Shared.searcherHlada.intValue();
                    int searcherProgress = (int) ((searcherNasiel / (double) searcherHlada) * 100);

                    publish(new AktualizaciaStatus(searcherProgress, 0, Utils.getElapsedTime(start), Utils.getETAtime(start, searcherNasiel, searcherHlada), "0:00:00"));
                    Thread.sleep(500);
                }
                System.out.println("manager cancelled: " + future.isCancelled() + " isDone=" + future.isDone() + " ");
                System.out.println("crawlerManagerExecutor.isShutdown()=" + crawlerManagerExecutor.isShutdown() + " isTerminated()=" + crawlerManagerExecutor.isTerminated());
                zapisDoLogu("Aktualizacia ukoncena, Čas trvania: " + Utils.getElapsedTime(start));
                return null;
            }

            @Override
            protected void process(List<AktualizaciaStatus> chunks) {
                AktualizaciaStatus status = chunks.get(chunks.size() - 1);
                searcheryProgressBar.setValue(status.searcherProgress);
                searcheryProgressBar.setString(status.searcherProgress + " %");
                uplynuloLabel.setText("Uplynulo: " + status.uplynulo);
                ostavaSearcheryLabel.setText("Ostava: " + status.ostavaSearchery);
            }

            @Override
            protected void done() {
                nastavButtony(true);
                database.updateLastKontrola();
            }
        };
        aktualizaciaWorker.execute();
        System.out.println("aktualizaciaWorker executed");

        SwingWorker<Void, String> logWorker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    String message = Shared.logMessages.take();
                    System.out.println("AktualizaciaFrame logWorker: prvy message prijaty: " + message);
                    while (message != null) {
                        if (message.equals("poison.pill")) {
                            break;
                        }
                        //System.out.println("publishujem message: " + message);
                        publish(message);
                        message = Shared.logMessages.take();
                    }
                    System.out.println("AktualizaciaFrame logWorker: ziadne dalsie message");
                } catch (Exception e) {
                    Logger.getLogger(AktualizaciaFrame2.class.getName()).log(Level.SEVERE, null, e);
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
    }//GEN-LAST:event_odstranitNeaktualneButtonActionPerformed

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changes.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changes.removePropertyChangeListener(listener);
    }

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
            java.util.logging.Logger.getLogger(AktualizaciaFrame2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AktualizaciaFrame2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AktualizaciaFrame2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AktualizaciaFrame2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AktualizaciaFrame2(new TextDatabase(),0).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton aktualizovatButton;
    private javax.swing.JSpinner crawlerovSpinner;
    private javax.swing.JProgressBar crawleryProgressBar;
    private javax.swing.JLabel inzeratovVDBLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea logTextArea;
    private javax.swing.JButton odstranitNeaktualneButton;
    private javax.swing.JSpinner odstranitSearcherovSpinner;
    private javax.swing.JLabel ostavaCrawleryLabel;
    private javax.swing.JLabel ostavaSearcheryLabel;
    private javax.swing.JSpinner pocetDniSpinner;
    private javax.swing.JLabel poslednaAktualizaciaLabel;
    private javax.swing.JLabel poslednaKontrolaLabel;
    private javax.swing.JSpinner searcherovSpinner;
    private javax.swing.JProgressBar searcheryProgressBar;
    private javax.swing.JLabel uplynuloLabel;
    // End of variables declaration//GEN-END:variables

    private void nastavButtony(boolean b) {
        aktualizovatButton.setEnabled(b);
        odstranitNeaktualneButton.setEnabled(b);
    }
}
