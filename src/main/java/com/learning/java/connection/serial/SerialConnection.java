package com.learning.java.connection.serial;

import com.learning.java.Constants;
import gnu.io.RXTXCommDriver;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * @author Mandvee Vatsa
 * @date 22-May-2026 2:32:36 pm
 */
public class SerialConnection {

    private SerialConnection serialCon ;
    private Integer baudrate = 9600;
    private String port = "/dev/ttyUSB1";

    private SerialConnection(){
        loadConnectionInfo();
    }

    private SerialConnection getInstance(){
        if(Objects.isNull(serialCon)){
            synchronized (SerialConnection.class){
                if(Objects.isNull(serialCon)){
                    serialCon = new SerialConnection();
                }
            }
        }
        return serialCon;
    }

    private void makeSerialConnection(){
        try{
            System.out.println("Going to make Serial Connection !!");
            System.setProperty("gnu.io.rxtx.SerialPorts", port);
            RXTXCommDriver rxtx = new RXTXCommDriver();

            File lockF = new File("/var/lock/LCK.." + port);
            if (lockF.exists()) {
                System.out.println("Deleting Lock File " + lockF.getName());
                lockF.delete();
            }

            rxtx.initialize();
            File portF = new File(port);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadConnectionInfo(){
        try {
            // Read file from src/main/resources
            URL filePath = SerialConnection.class.getClassLoader()
                    .getResource(Constants.CONFIG_FILE);

            // Convert file content into String
            String jsonContent = Files.readString(
                    Paths.get(filePath.toURI()));

            // Convert String → JSONObject
            JSONObject rootObject = new JSONObject(jsonContent);

            // Get rs232 object
            JSONObject rs232 = rootObject.getJSONObject(Constants.RS232);

            // Fetch fields
            port = rs232.getString(Constants.PORT);
            baudrate = rs232.getInt(Constants.BAUDRATE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
