/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.biemond.smartmeter.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author edwin
 */
@XmlRootElement
public class Device {
    
    public Device(){
    }

    public Device( int id, String type, String device){
       this.device = device;
       this.id = id;
       this.type = type;
    }

    
    public int id;
    // gas or electricity
    public String type;
    // serial number
    public String device;
  
}
