/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.biemond.smartmeter.entities;

import java.sql.Date;
import java.sql.Timestamp;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@XmlRootElement
public class GasMeasurement {

    public GasMeasurement(int id, int device, Date date, Timestamp time, int measurement, int active) {
        this.id = id;
        this.device = device;
        this.date = date;
        this.time = time;
        this.measurement = measurement;
        this.active = active;
    }

    public GasMeasurement() {
    }
    public int id;
    public int device;
    public Date date;
    public Timestamp time;
    public int measurement;
    public int active;
}
