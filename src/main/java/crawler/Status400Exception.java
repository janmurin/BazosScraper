/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler;

/**
 *
 * @author Janco1
 */
class Status400Exception extends Exception {

    public final Exception povodnaException;

    public Status400Exception(Exception povodnaException) {
        this.povodnaException=povodnaException;
    }

    @Override
    public String toString() {
        return povodnaException.toString(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
