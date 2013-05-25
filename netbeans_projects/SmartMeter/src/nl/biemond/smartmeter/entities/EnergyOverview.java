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
public class EnergyOverview {

    public EnergyOverview() {
    }

    public EnergyOverview(Date date, float consumption, int consDifference, float production, int prodDifference, Device device) {
        this.date = date;
        this.consumption = consumption;
        this.consDifference = consDifference;
        this.production = production;
        this.prodDifference = prodDifference;
        this.device = device;
    }
    
    public Date date;
    public float consumption;
    public int consDifference;
    public float production;
    public int prodDifference;
    public Device device;
    
}
