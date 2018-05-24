/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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
    private static final String USER_AGENT = "Mozilla/5.0";

    public static void sendPost(String message) {
        Document doc;
        try {
            doc = Jsoup.connect("http://81.2.244.134/api/logger.php")
                    .data("note", message)
                    // and other hidden fields which are being passed in post request.
                    .userAgent("Mozilla")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .postDataCharset("UTF-8")
                    .post();
            System.out.println(doc); // will print html source of homepage of facebook.
        } catch (IOException ex) {
            Logger.getLogger(Shared.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
