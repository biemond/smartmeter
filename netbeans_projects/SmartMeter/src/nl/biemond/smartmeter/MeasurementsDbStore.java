/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.biemond.smartmeter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import nl.biemond.smartmeter.entities.Device;
import nl.biemond.smartmeter.entities.EnergyMeasurement;

/**
 *
 * @author edwin
 */
public class MeasurementsDbStore {

    private final static String DB_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    private final static String DB_NAME = "smartmeter";
    private final static String DB_URL = String.format("jdbc:derby:%s", DB_NAME);
    // Boot password must be at least 8 bytes long.
    private final static String DB_KEY = "jes12345";
    private static MeasurementsDbStore instance;
    private Connection connection;
    private Statement statement;

    private MeasurementsDbStore() {
        try {
            Class.forName(DB_DRIVER);
            connection = DriverManager.getConnection(DB_URL + ";create=true;dataEncryption=false;bootPassword=" + DB_KEY, null);
            statement = connection.createStatement();
            System.out.println("connection made");  
        } catch (ClassNotFoundException cnfe) {
            System.out.println("Please put derby.jar in the classpath");
            System.exit(1);
        } catch (SQLException sqe) {
            sqe.printStackTrace();
        }
        try {
            statement.execute(
                    "CREATE TABLE devices "
                    + "( id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) CONSTRAINT DEVICE_PK PRIMARY KEY"
                    + ", type 	 VARCHAR(100)  NOT NULL"
                    + ", device  VARCHAR(100)	NOT NULL"
                    + ")");
        } catch (SQLException ignored) {
            // ignore if already exist.
        }
        try {
            statement.execute(
                    "CREATE TABLE gas_measurements"
                    + "( id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) CONSTRAINT GAS_PK PRIMARY KEY"
                    + ", device INTEGER NOT NULL CONSTRAINT gas_device_fk REFERENCES devices"
                    + ", date DATE NOT NULL"
                    + ", time TIMESTAMP NOT NULL"
                    + ", measurement INTEGER"
                    + ", active INTEGER"
                    + ")");
        } catch (SQLException ignored) {
            // ignore if already exist.
        }


        try {
            statement.execute(
                    "CREATE TABLE energy_measurements"
                    + "( id       INTEGER   NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) CONSTRAINT ELEC_PK PRIMARY KEY"
                    + ", device   INTEGER   NOT NULL CONSTRAINT elec_device_fk REFERENCES devices"
                    + ", date     DATE      NOT NULL"
                    + ", time     TIMESTAMP NOT NULL"
                    + ", meter181 NUMERIC(8,3)"
                    + ", meter182 NUMERIC(8,3)"
                    + ", meter281 NUMERIC(8,3)"
                    + ", meter282 NUMERIC(8,3)"
                    + ", tarif    VARCHAR(100)"
                    + ", currentConsumption NUMERIC(5,0)"
                    + ", currentProduction  NUMERIC(5,0)"
                    + ", enabled            INTEGER"
                    + ")");
        } catch (SQLException ignored) {
            // ignore if already exist.
        }

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        // cleanup 
        System.out.println("connection finalize");  

        try {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
        // shutdown the database
        try {
            DriverManager.getConnection(DB_URL + ";shutdown=true");
        } catch (SQLException se) {
            if (!se.getSQLState().equals("08006")) {
                se.printStackTrace();
            }
            // otherwise ignore it : this is expected 
        }
    }

    public static synchronized MeasurementsDbStore getInstance() {
        if (instance == null) {
            instance = new MeasurementsDbStore();
        }
        System.out.println("return instance");  

        return instance;
    }

    public int findDevice(String type, String device) {

        ResultSet resultset = null;
        int deviceId = 0;
        try {
            resultset = statement.executeQuery("SELECT id FROM devices where type = '"
                    + type + "' and device = '" + device + "'");

            while (resultset.next()) {
                deviceId = resultset.getInt(1);
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            try {
                if (resultset != null) {
                    resultset.close();
                }
            } catch (SQLException ignored) {
            }
        }
        return deviceId;
        
    }

    
    public int addDevice(String type, String device) {

        int deviceId = findDevice(type,device);

        if (deviceId == 0) {
            try {
                statement.execute("INSERT INTO devices(type,device) VALUES ("
                        + "'" + type + "','" + device + "'"
                        + ")");
                return findDevice(type,device);
 
            } catch (SQLException se) {
                se.printStackTrace();
            }
 
        } 
        return deviceId; 
     }

    public List<Device> listDevices() {
        ResultSet resultset = null;
        ArrayList<Device> list = new ArrayList<>();
        try {
            resultset = statement.executeQuery("SELECT id, type, device FROM devices ORDER BY id");

            while (resultset.next()) {
                list.add(new Device(
                        resultset.getInt(1),
                        resultset.getString(2),
                        resultset.getString(3)));
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            try {
                if (resultset != null) {
                    resultset.close();
                }
            } catch (SQLException ignored) {
            }
        }
        return list;
    }

    public void addEnergyMeasurement( int device, float meter181, float meter182, float meter281, float meter282
                                    , String tarif, float currentConsumption, float currentProduction,int enabled) {
        try {
            statement.execute("INSERT INTO energy_measurements"
                    + "(device, date,time,meter181,meter182"
                    + ",meter281,meter282,tarif,currentConsumption,currentProduction,enabled "
                    + ") VALUES ("
                    + device + ","
                    + "CURRENT_DATE,"
                    + "CURRENT_TIMESTAMP,"
                    + meter181 + ","
                    + meter182 + ","
                    + meter281 + ","
                    + meter282 + ","
                    + "'"+tarif + "',"
                    + currentConsumption + ","
                    + currentProduction + ","
                    + enabled
                    + ")");
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public List<EnergyMeasurement> listEnergyMeasurement() {
        ResultSet resultset = null;
        ArrayList<EnergyMeasurement> list = new ArrayList<>();
        try {
            resultset = statement.executeQuery(
                    " SELECT e.id, date,time,meter181,meter182"
                    + " ,meter281,meter282,tarif,currentConsumption"
                    + " ,currentProduction"
                    + " ,enabled, d.id, d.type, d.device"
                    + " FROM energy_measurements e "
                    + " ,    devices d "
                    + " WHERE e.device = d.id "
                    + " ORDER BY e.id desc");
            while (resultset.next()) {
                list.add(new EnergyMeasurement(
                        resultset.getInt(1),
                        resultset.getDate(2),
                        resultset.getTimestamp(3),
                        resultset.getFloat(4),
                        resultset.getFloat(5),
                        resultset.getFloat(6),
                        resultset.getFloat(7),
                        resultset.getString(8),
                        resultset.getFloat(9),
                        resultset.getFloat(10),
                        resultset.getInt(11),
                        new Device(
                        resultset.getInt(12),
                        resultset.getString(13),
                        resultset.getString(14))));
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            try {
                if (resultset != null) {
                    resultset.close();
                }
            } catch (SQLException ignored) {
            }
        }
        return list;
    }

    public void deleteEnergy() {
        try {
            statement.execute("DELETE FROM energy_measurements");
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void deleteGas() {
        try {
            statement.execute("DELETE FROM gas_measurements");
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void deleteDevices() {
        try {
            statement.execute("DELETE FROM devices");
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }
}
