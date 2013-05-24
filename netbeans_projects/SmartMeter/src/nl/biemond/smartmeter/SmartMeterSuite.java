/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.biemond.smartmeter;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import nl.biemond.smartmeter.entities.Device;
import nl.biemond.smartmeter.entities.EnergyMeasurement;
import nl.biemond.smartmeter.entities.GasMeasurement;

/**
 *
 * @author edwin
 */
@Path("/")
public class SmartMeterSuite {
    
    public SmartMeterSuite(){
      System.out.println("SmartMeterSuite");  
    }
     
    public static final String MESSAGE = "This is the SmartMeter application";
    MeasurementsDbStore store = MeasurementsDbStore.getInstance();
    
    @GET
    @Produces({MediaType.TEXT_HTML})
    public String getText() {
        return MESSAGE;
    }

    @GET
    @Path("listDevices")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Device> getList() {
        return store.listDevices();
    }

    @GET
    @Path("listEnergy")
    @Produces({MediaType.APPLICATION_JSON})
    public List<EnergyMeasurement> getEnergyList() {
        return store.listEnergyMeasurement();
    }    

    @GET
    @Path("listGas")
    @Produces({MediaType.APPLICATION_JSON})
    public List<GasMeasurement> getGasList() {
        return store.listGasMeasurement();
    }    
    
    
    
    @GET
    @Path("deleteDatabase")
    @Produces({MediaType.TEXT_HTML})
    public String deleteDatabase() {
        store.deleteEnergy();
        store.deleteGas();
        store.deleteDevices();
        return "deleted all records";
    }
    
}
