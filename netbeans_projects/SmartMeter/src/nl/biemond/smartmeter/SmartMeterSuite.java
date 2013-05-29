/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.biemond.smartmeter;

import com.sun.jersey.api.json.JSONWithPadding;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
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
    @Produces({ "application/x-javascript",MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public JSONWithPadding getList(@QueryParam("callback") String callback) {
        System.out.println("callback id: " + callback);
        if (null == callback) {
            return new JSONWithPadding(new GenericEntity<List<Device>>(store.listDevices()) {
            });
        } else {
            return new JSONWithPadding(new GenericEntity<List<Device>>(store.listDevices()) {
            }, callback);
        }
    }

    @GET
    @Path("listEnergy/{date}")
    @Produces({ "application/x-javascript",MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public JSONWithPadding getEnergyList(@PathParam("date") DateParam day,
                                                 @QueryParam("callback") String callback) {
        System.out.println("callback id: " + callback);
        if (null == callback) {
            return new JSONWithPadding(new GenericEntity<List<EnergyMeasurement>>(store.listEnergyMeasurement(day.getDate())) {
            });
        } else {
            return new JSONWithPadding(new GenericEntity<List<EnergyMeasurement>>(store.listEnergyMeasurement(day.getDate())) {
            }, callback);
        }
    }

    @GET
    @Path("listEnergyOverview/{deviceId}")
    @Produces({ "application/x-javascript",MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public JSONWithPadding getEnergyOverviewList(@PathParam("deviceId") int deviceId, 
                                                 @QueryParam("callback") String callback) {
        if (null == callback) {
            return new JSONWithPadding(new GenericEntity<List<EnergyOverview>>(store.listEnergyOverview(deviceId)) {
            });
        } else {
            return new JSONWithPadding(new GenericEntity<List<EnergyOverview>>(store.listEnergyOverview(deviceId)) {
            }, callback);
        }
    }

    @GET
    @Path("listGas/{date}")
    @Produces({ "application/x-javascript",MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public JSONWithPadding getGasList(@PathParam("date") DateParam day, 
                                      @QueryParam("callback") String callback) {
        if (null == callback) {
            return new JSONWithPadding(new GenericEntity<List<GasMeasurement>>(store.listGasMeasurement(day.getDate())) {
            });
        } else {
            return new JSONWithPadding(new GenericEntity<List<GasMeasurement>>(store.listGasMeasurement(day.getDate())) {
            }, callback);
        }
    }

    @GET
    @Path("listGasOverview/{deviceId}")
    @Produces({ "application/x-javascript",MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public JSONWithPadding getGasOverviewList(@PathParam("deviceId") int deviceId, 
                                              @QueryParam("callback") String callback) {
        if (null == callback) {
            return new JSONWithPadding(new GenericEntity<List<GasOverview>>(store.listGasOverview(deviceId)){});
        } else {
            return new JSONWithPadding(new GenericEntity<List<GasOverview>>(store.listGasOverview(deviceId)){}, callback);
        }
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
