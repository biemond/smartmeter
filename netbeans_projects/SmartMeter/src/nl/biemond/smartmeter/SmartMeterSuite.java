/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.biemond.smartmeter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import nl.biemond.smartmeter.entities.Device;
import nl.biemond.smartmeter.entities.EnergyMeasurement;
import nl.biemond.smartmeter.entities.EnergyOverview;
import nl.biemond.smartmeter.entities.GasMeasurement;
import nl.biemond.smartmeter.entities.GasOverview;

/**
 *
 * @author edwin
 */
@Path("/")
public class SmartMeterSuite {

    public SmartMeterSuite() {
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
    @Path("listEnergy/{date}")
    @Produces({MediaType.APPLICATION_JSON})
    public List<EnergyMeasurement> getEnergyList(@PathParam("date") DateParam day) {
        return store.listEnergyMeasurement(day.getDate());
    }

    @GET
    @Path("listEnergyOverview/{deviceId}")
    @Produces({MediaType.APPLICATION_JSON})
    public List<EnergyOverview> getEnergyOverviewList(@PathParam("deviceId") int deviceId) {
        return store.listEnergyOverview(deviceId);
    }

    @GET
    @Path("listGas/{date}")
    @Produces({MediaType.APPLICATION_JSON})
    public List<GasMeasurement> getGasList(@PathParam("date") DateParam day) {
        return store.listGasMeasurement(day.getDate());
    }

    @GET
    @Path("listGasOverview/{deviceId}")
    @Produces({MediaType.APPLICATION_JSON})
    public List<GasOverview> getGasOverviewList(@PathParam("deviceId") int deviceId) {
        return store.listGasOverview(deviceId);
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
