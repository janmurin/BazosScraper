/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author Janco1
 */
public class JSoup {

    private Connection connection;
    String ua = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0";

    public Document getPage(String url) throws Status400Exception, Exception {
        // 3 pokusy na loadnutie url
        int pokus = 3;
        Document doc = null;

        // skusame 3x getnut stranku
        while (doc == null && pokus > 0) {
            try {
                doc = loadUrl(url);
                // ak je Http exception so statusom 400, tak uz nebudeme dalsi request robit lebo inzerat pravdepodobne neexistuje
            } catch (HttpStatusException he) {
                String message = he.toString();
                System.out.println("message: " + message);
                if (message.contains("Status=400")) {
                    throw new Status400Exception(he);
                } else {
                    System.out.println("getPage exception: " + he);
                }
                // ostatne vynimky zatial specialne neriesim a mozem sa pokusat o dalsi request
            } catch (Exception ex) {
                System.out.println("getPage exception: " + ex);
                if (pokus == 1) {
                    // toto je posledny pokus a nastala vynimka, tak ju vyhodime vyssie
                    throw ex;
                }
                //Logger.getLogger(JSoup.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                pokus--;
            }
        }
        if (doc == null) {
            // tento pripad by sa asi nemal nikdy stat lebo vynimka v cykle pri poslednom pokuse by to mala zachytit
            throw new Exception("nenaparsovalo dokument");
        }
        return doc;
    }

    private Document loadUrl(String url) throws Exception {
        connection = Jsoup.connect(url).userAgent(ua);
        connection.method(Connection.Method.GET);

        Connection.Response response = connection.execute();
        return response.parse();
    }
}
