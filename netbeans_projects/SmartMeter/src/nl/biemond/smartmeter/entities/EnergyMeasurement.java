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
public class EnergyMeasurement {

    public EnergyMeasurement(){
    }

    public EnergyMeasurement(int id, Date date, Date time, float meter181, float meter182, float meter281, float meter282, String tarif, float currentConsumption, float currentProduction, int enabled,Device device) {
        this.id = id;
        this.device = device;
        this.date = date;
        this.time = time;
        this.meter181 = meter181;
        this.meter182 = meter182;
        this.meter281 = meter281;
        this.meter282 = meter282;
        this.tarif = tarif;
        this.currentConsumption = currentConsumption;
        this.currentProduction = currentProduction;
        this.enabled = enabled;
    }
 
    public Device device;
    public int id;
    
    
    public Date date;
    public Date time;
    public float meter181;
    public float meter182;
    public float meter281;
    public float meter282;
    // low or high price
    public String tarif;
    public float currentConsumption;
    public float currentProduction;
    public int enabled;
         
    
}
