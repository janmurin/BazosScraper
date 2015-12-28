/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Janco1
 */
public class ETATimer implements Runnable {

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        while (true) {
            try {
                Thread.sleep(3000);
                System.out.println("ETA: " + Utils.getETAtime(startTime, Shared.crawlerNasiel.intValue(), Shared.crawlerHlada.intValue())
                        + " spracovanych/najdenych: " + Shared.crawlerNasiel.intValue() + "/" + Shared.crawlerHlada.intValue());
            } catch (InterruptedException ex) {
                System.out.println("timer thread interrupted");
                break;
                //Logger.getLogger(ETATimer.class.getName()).log(Level.SEVERE, null, ex);
            }catch(Exception ex){
                Logger.getLogger(ETATimer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
