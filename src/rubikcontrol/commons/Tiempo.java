/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rubikcontrol.commons;

/**
 *
 * @author Fernando
 */
public class Tiempo {
    int minutos;
    int segundos;
    int milesimas;
    
    public Tiempo (int minutos, int segundos, int milesimas) {
        this.minutos = minutos;
        this.segundos = segundos;
        this.milesimas = milesimas;
    }
    
    public int getMinutos() {
        return minutos;
    }
    
    public int getSegundos() {
        return segundos;
    }
    
    public int getMiliSegundos() {
        return milesimas;
    }
    
    public String toString() {
        String mins, segs, mil;
        
        if (minutos == 0) {
            mins = "00";
        } else {
            mins = String.valueOf(minutos);
            if (mins.length() == 1) {
                mins = "0" + mins; 
            }
        }
        if (segundos == 0) {
            segs = "00";
        } else {
            segs = String.valueOf(segundos);
            if (segs.length() == 1) {
                segs = "0" + segs; 
            }
        }
        if (milesimas == 0) {
            mil = "000";
        } else {
            mil = String.valueOf(milesimas);
            if (mil.length() == 1) {
                mil = "00" + mil; 
            } else if (mil.length() == 2) {
                mil = "0" + mil;
            }
        }
        
        return mins + ":" + segs + ":" + mil;
    }
}
