/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author Janco1
 */
public class JSoup {

    private Connection connection;
    String ua = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0";

    public Document getPage(String url) {
        // 3 pokusy na loadnutie url
        int pokus = 2;
        Document doc = loadUrl(url);
        // skusame 3x getnut stranku
        while (doc == null && pokus > 0) {
            doc = loadUrl(url);
            pokus--;
        }
        return doc;
    }

    private Document loadUrl(String url) {
        Document document = null;
        try {
            connection = Jsoup.connect(url).userAgent(ua);
            connection.method(Connection.Method.GET);

            Connection.Response response = connection.execute();

            //System.out.println("jsoup charset: " + response.charset() + " " + response.contentType());;
            document = response.parse();
            //System.out.println(document);
            return document;
        } catch (Exception e) {
            System.out.println("getPage exception: " + e);
            //System.out.println("NO RETRY");
        }
//        InputStream input = null;
//        try {
//            input = new URL(url).openStream();
//        } catch (MalformedURLException ex) {
//            Logger.getLogger(JSoup.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(JSoup.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        try {
//            Document doc = Jsoup.parse(input, "UTF-8", url);
//            System.out.println(doc);
//            return doc;
//        } catch (IOException ex) {
//            Logger.getLogger(JSoup.class.getName()).log(Level.SEVERE, null, ex);
//        }
        return null;
    }
}
