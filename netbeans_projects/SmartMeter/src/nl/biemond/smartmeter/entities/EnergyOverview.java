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

    public EnergyOverview(Date date, float consumption, double consDifference, float production, double prodDifference, Device device) {
        this.date = date;
        this.consumption = consumption;
        this.consDifference = consDifference;
        this.production = production;
        this.prodDifference = prodDifference;
        this.device = device;
    }
    
    public Date date;
    public float consumption;
    public double consDifference;
    public float production;
    public double prodDifference;
    public Device device;
    
}
