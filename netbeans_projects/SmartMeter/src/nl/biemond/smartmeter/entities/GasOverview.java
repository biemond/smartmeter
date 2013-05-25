/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.biemond.smartmeter.entities;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@XmlRootElement
public class GasOverview {

    public GasOverview( Date date, float consumption, int difference, Device device) {
        this.device = device;
        this.date = date;
        this.consumption = consumption;
        this.difference = difference;
    }

    public GasOverview() {
    }
    
    public Device device;    
    public Date date;
    public float consumption;
    public int difference;

}
