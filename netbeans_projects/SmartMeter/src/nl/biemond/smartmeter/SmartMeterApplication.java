/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.biemond.smartmeter;

import java.util.Set;
import java.util.HashSet;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * This is a simple program that demonstrates JAX-RS web service & JavaDB
 */
@ApplicationPath("/")
public class SmartMeterApplication extends Application {

    
    @Override
    public Set<Class<?>> getClasses() {
        System.out.println("SmartMeterApplication getClasses");  
        
        final Set<Class<?>> classes = new HashSet<Class<?>>();
        // register root resource
        classes.add(SmartMeterSuite.class);
        return classes;
    }

    
    private static class MainProcess implements Runnable {

        public MainProcess() {
        }

        public void run() {
            System.out.println("MainProcess run");  

            try {
                (new ProcessData()).connect("/dev/ttyUSB0");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    static {
      System.out.println("static");
      Thread myThread = new Thread(new MainProcess());
      myThread.start();

    } 
    
}