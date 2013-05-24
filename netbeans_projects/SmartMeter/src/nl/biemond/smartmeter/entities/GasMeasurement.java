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
public class GasMeasurement {

    public GasMeasurement(int id, Date date, Date time, float measurement, int enabled,Device device) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.measurement = measurement;
        this.enabled = enabled;
        this.device  = device;
    }

    public GasMeasurement() {
    }
    public Device device;    
    public int id;
    public Date date;
    public Date time;
    public float measurement;
    public int enabled;
}
