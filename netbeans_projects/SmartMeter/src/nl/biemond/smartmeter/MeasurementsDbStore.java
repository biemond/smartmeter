/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.biemond.smartmeter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import nl.biemond.smartmeter.entities.Device;
import nl.biemond.smartmeter.entities.EnergyMeasurement;
import nl.biemond.smartmeter.entities.EnergyOverview;
import nl.biemond.smartmeter.entities.GasMeasurement;
import nl.biemond.smartmeter.entities.GasOverview;

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
                    + "( id          INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) CONSTRAINT GAS_PK PRIMARY KEY"
                    + ", device      INTEGER NOT NULL CONSTRAINT gas_device_fk REFERENCES devices"
                    + ", date        DATE NOT NULL"
                    + ", time        TIMESTAMP NOT NULL"
                    + ", measurement NUMERIC(8,3)"
                    + ", enabled     INTEGER"
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
            String select = "SELECT id FROM devices where type = ? and device = ?";
            PreparedStatement pstmt = connection.prepareStatement(select);
            pstmt.setString(1, type);
            pstmt.setString(2, device);
            resultset = pstmt.executeQuery();

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

        int deviceId = findDevice(type, device);

        if (deviceId == 0) {
            try {

                String insert = "INSERT INTO devices(type,device) VALUES (?,?)";
                PreparedStatement pstmt = connection.prepareStatement(insert);
                pstmt.setString(1, type);
                pstmt.setString(2, device);
                pstmt.executeUpdate();


                return findDevice(type, device);

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

    public void addGasMeasurement(int device, Date time, float measurement, int enabled) {
        int found = 0;
        try {
            String select = " select 1 from gas_measurements where "
                    + " device = ? and date = ? and time = ?";
            PreparedStatement pstmt = connection.prepareStatement(select);
            pstmt.setInt(1, device);
            pstmt.setDate(2, new java.sql.Date(time.getTime()));
            pstmt.setTimestamp(3, new java.sql.Timestamp(time.getTime()));
            ResultSet resultset = pstmt.executeQuery();
            while (resultset.next()) {
                found = resultset.getInt(1);
            }

        } catch (SQLException se) {
            se.printStackTrace();
        }
      if ( found == 0) {
        try {
            String insert = " INSERT INTO gas_measurements"
                    + " (device, date,time,measurement,enabled )"
                    + " VALUES (?,?,?,?,? )";
            PreparedStatement pstmt = connection.prepareStatement(insert);
            pstmt.setInt(1, device);
            pstmt.setDate(2, new java.sql.Date(time.getTime()));
            pstmt.setTimestamp(3, new java.sql.Timestamp(time.getTime()));
            pstmt.setFloat(4, measurement);
            pstmt.setInt(5, enabled);

            pstmt.executeUpdate();

        } catch (SQLException se) {
            se.printStackTrace();
        }
      }  
    }

    public List<GasMeasurement> listGasMeasurement(Date day) {
        System.out.println("GasMeasurement for day: "+day);
        if ( day == null ) {
          return null;
        }          
        ResultSet resultset = null;
        ArrayList<GasMeasurement> list = new ArrayList<>();
        try {
            String select =
                    " SELECT e.id, date,time,measurement"
                    + " ,enabled, d.id, d.type, d.device"
                    + " FROM gas_measurements e "
                    + " ,    devices d "
                    + " WHERE e.device = d.id "
                    + " AND e.date = ? "
                    + " ORDER BY e.id desc";
            PreparedStatement pstmt = connection.prepareStatement(select);
            pstmt.setDate(1, new java.sql.Date(day.getTime()));
            resultset = pstmt.executeQuery();           
            while (resultset.next()) {
                list.add(new GasMeasurement(
                        resultset.getInt(1),
                        resultset.getDate(2),
                        resultset.getTimestamp(3),
                        resultset.getFloat(4),
                        resultset.getInt(5),
                        new Device(
                        resultset.getInt(6),
                        resultset.getString(7),
                        resultset.getString(8))));
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

     public List<GasOverview> listGasOverview(int deviceId) {
        System.out.println("GasOverview for deviceId: "+deviceId);
        ResultSet resultset = null;
        ArrayList<GasOverview> list = new ArrayList<>();
        try {
            String select  = 
                      " SELECT date, CAST(max(measurement) AS NUMERIC(8,2)), d.id, d.type, d.device"
                    + " FROM gas_measurements e "
                    + " ,    devices d "
                    + " WHERE e.device = d.id "
                    + " AND   d.id = ? "
                    + " GROUP BY date, d.id, d.type, d.device "
                    + " ORDER BY date ";
            PreparedStatement pstmt = connection.prepareStatement(select);
            pstmt.setInt(1, deviceId);
            resultset = pstmt.executeQuery();
            
            float lastEntry = 0;
            float difference = 0;
            while (resultset.next()) {
                
                if ( lastEntry != 0 ){
                    difference =  resultset.getFloat(2) - lastEntry;
                }
                lastEntry = resultset.getFloat(2);
                list.add(new GasOverview(
                        resultset.getDate(1),
                        resultset.getFloat(2),
                        roundTwoDecimals(difference),
                        new Device(
                        resultset.getInt(3),
                        resultset.getString(4),
                        resultset.getString(5))));
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
   
    
    public void addEnergyMeasurement(int device, float meter181, float meter182, float meter281, float meter282, String tarif, float currentConsumption, float currentProduction, int enabled) {
        try {

            String insert = "INSERT INTO energy_measurements (device, date,time,meter181,meter182"
                    + ",meter281,meter282,tarif,currentConsumption,currentProduction,enabled )"
                    + " VALUES (?,CURRENT_DATE,CURRENT_TIMESTAMP,?,?,?,?,?,?,?,? )";
            PreparedStatement pstmt = connection.prepareStatement(insert);
            pstmt.setInt(1, device);
            pstmt.setFloat(2, meter181);
            pstmt.setFloat(3, meter182);
            pstmt.setFloat(4, meter281);
            pstmt.setFloat(5, meter282);
            pstmt.setString(6, tarif);
            pstmt.setFloat(7, currentConsumption);
            pstmt.setFloat(8, currentProduction);
            pstmt.setInt(9, enabled);
            pstmt.executeUpdate();

        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    
    
    public List<EnergyMeasurement> listEnergyMeasurement(Date day) {
        System.out.println("EnergyMeasurement for day: "+day);
        if ( day == null ) {
          return null;
        }  
        ResultSet resultset = null;
        ArrayList<EnergyMeasurement> list = new ArrayList<>();
        try {
            String select =
                    " SELECT e.id, date,time,meter181,meter182"
                    + " ,meter281,meter282,tarif,currentConsumption"
                    + " ,currentProduction"
                    + " ,enabled, d.id, d.type, d.device"
                    + " FROM energy_measurements e "
                    + " ,    devices d "
                    + " WHERE e.device = d.id "
                    + " and   e.date = ? "
                    + " ORDER BY e.id desc";
            PreparedStatement pstmt = connection.prepareStatement(select);
            pstmt.setDate(1, new java.sql.Date(day.getTime()));
            resultset = pstmt.executeQuery();
            
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

    public List<EnergyOverview> listEnergyOverview(int deviceId) {
        System.out.println("EnergyOverview for deviceId: "+deviceId);
        ResultSet resultset = null;
        ArrayList<EnergyOverview> list = new ArrayList<>();
        try {
            String select  = 
                      " SELECT date, "
                    + " CAST(max(meter181+meter182) AS NUMERIC(8,2)), "
                    + " CAST(max(meter281+meter282) AS NUMERIC(8,2)), "
                    + " d.id, d.type, d.device"
                    + " FROM energy_measurements e "
                    + " ,    devices d "
                    + " WHERE e.device = d.id "
                    + " AND   d.id = ? "
                    + " GROUP BY date, d.id, d.type, d.device "
                    + " ORDER BY date ";
            PreparedStatement pstmt = connection.prepareStatement(select);
            pstmt.setInt(1, deviceId);
            resultset = pstmt.executeQuery();
            
            float lastEntryConsumption = 0;
            float differenceConsumption = 0;
            float lastEntryProduction = 0;
            float differenceProduction = 0;
            while (resultset.next()) {
                
                if ( lastEntryConsumption != 0 ){
                    differenceConsumption = resultset.getFloat(2) -lastEntryConsumption;
                }
                lastEntryConsumption = resultset.getFloat(2);
                if ( lastEntryProduction != 0 ){
                    differenceProduction = resultset.getFloat(3) -lastEntryProduction;
                }
                lastEntryProduction = resultset.getFloat(3);
                list.add(new EnergyOverview(
                        resultset.getDate(1),
                        resultset.getFloat(2),
                        roundTwoDecimals(differenceConsumption),
                        resultset.getFloat(3),
                        roundTwoDecimals(differenceProduction),
                        new Device(
                        resultset.getInt(4),
                        resultset.getString(5),
                        resultset.getString(6))));
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
    private double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }      

}
