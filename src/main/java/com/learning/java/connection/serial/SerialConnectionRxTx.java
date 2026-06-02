package com.learning.java.connection.serial;

import com.learning.java.Constants;
//import gnu.io.CommPortIdentifier;
//import gnu.io.RXTXCommDriver;
//import gnu.io.SerialPort;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author Mandvee Vatsa
 * @date 22-May-2026 2:32:36 pm
 */
public class SerialConnectionRxTx {

    private static SerialConnectionRxTx serialCon;
    private Integer baudrate = 9600;
    private String port = "/dev/ttyUSB1";
    private boolean isConnected;
    private InputStream inputStream;
    private OutputStream outputStream;
    private int connectionTimeoutInMS = 300000; //In MilliSeconds

    private SerialConnectionRxTx() {
        loadConnectionInfo();
    }

    public static SerialConnectionRxTx getInstance() {
        if (Objects.isNull(serialCon)) {
            synchronized (SerialConnectionRxTx.class) {
                if (Objects.isNull(serialCon)) {
                    serialCon = new SerialConnectionRxTx();
                }
            }
        }
        return serialCon;
    }

    public void makeConnection() {
        try {

            new Thread(() -> {
                Thread.currentThread().setName("Writer");
                writeData();
            }).start();

            new Thread(() -> {
                Thread.currentThread().setName("Reader");
                readResponse();
            }).start();


            new Thread(() -> {
                Thread.currentThread().setName("Connect");
                reconnect();
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connect() {
        try {
            System.out.println("Going to make Serial Connection !!");
//            File portFile = new File(port);
//            if (portFile.exists()) {
//                CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(port);
//
//                SerialPort serialport = (SerialPort) portIdentifier.open("Java Learning Portal", connectionTimeoutInMS);
//                serialport.setSerialPortParams(baudrate, serialport.DATABITS_8, serialport.STOPBITS_1,
//                        serialport.PARITY_NONE);
////				serialport.setRTS(true);
//                serialport.setFlowControlMode(serialport.FLOWCONTROL_NONE);
//                isConnected = Boolean.TRUE;
//                inputStream = serialport.getInputStream();
//                outputStream = serialport.getOutputStream();
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadConnectionInfo() {
        try {
            // Read file from src/main/resources
            URL filePath = SerialConnectionRxTx.class.getClassLoader()
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
            initRxTX();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initRxTX() {
        try {
            System.out.println(
                    "SerialPorts = " +
                            System.getProperty("gnu.io.rxtx.SerialPorts")
            );

            System.out.println(
                    "ParallelPorts = " +
                            System.getProperty("gnu.io.rxtx.ParallelPorts")
            );
            System.setProperty("gnu.io.rxtx.SerialPorts", port);
            System.setProperty("gnu.io.rxtx.ParallelPorts", "");

            System.out.println(System.getProperty("java.version"));
//            RXTXCommDriver rxtx = new RXTXCommDriver();
            System.out.println("java.ext.dirs Property: " + System.getProperty("java.ext.dirs"));
//            System.setProperty("java.ext.dirs", port);
            System.out.println("java.ext.dirs Property: " + System.getProperty("java.ext.dirs"));
            System.out.println("path.separator Property: " + System.getProperty("path.separator"));
            System.out.println("file.separator Property: " + System.getProperty("file.separator"));
            System.out.println("Port: " + port);
            System.out.println("Baudrate: " + baudrate);

            File lockF = new File("/var/lock/LCK.." + port);
            if (lockF.exists()) {
                System.out.println("Deleting Lock File " + lockF.getName());
                lockF.delete();
            }
            System.out.println(
                    "SerialPorts = " +
                            System.getProperty("gnu.io.rxtx.SerialPorts")
            );

            System.out.println(
                    "ParallelPorts = " +
                            System.getProperty("gnu.io.rxtx.ParallelPorts")
            );
//            rxtx.initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reconnect() {
        try {
            while (true) {
                if (!isConnected) {
                    connect();
                }
                Thread.sleep(5000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void writeData() {
        try {
            while (true) {
                if (isConnected) {
                    publishStringData("Hello World!!");
                    Thread.sleep(1000);
                    publishByteData("20 20 20 20 20 20 20 20 20");
                }
                Thread.sleep(30000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            isConnected = false;
        }
    }

    private void readResponse() {
        try {
            while (true) {
                if (isConnected) {
                    String response = StringUtils.EMPTY;
                    if (Objects.nonNull(inputStream)) {
                        byte[] data = new byte[20];
                        int availablebytes = inputStream.available();

                        if (availablebytes > 0) {
                            inputStream.read(data);
                            for (int i = 0; i < data.length; i++) {
                                String res = Integer.toHexString(data[i] & 0xff);
                                response = response.concat(res).concat(",");
                            }

                            System.out.println("Read Data : " + Arrays.asList(data));
                            System.out.println("Read Response: " + response);
                            System.out.println("Read String: " + new String(data));
                        } else
                            System.out.println("Available Bytes = 0");
                    } else {
                        isConnected = false;
                    }
                }
                Thread.sleep(500);
            }
        } catch (Exception e) {
            e.printStackTrace();
            isConnected = false;
        }
    }


    void publishStringData(String msg) {
        try {
            if (Objects.nonNull(outputStream)) {

                System.out.print(msg.getBytes());
                System.out.println();
                outputStream.write(msg.getBytes());
                outputStream.write("\n".getBytes());
                outputStream.flush();
            } else {
                System.out.println("Output Stream Is null");
                isConnected = false;
            }

        } catch (Exception e) {
            System.err.println(
                    "Exception: occurred while writing String message on Serial Client " + e);
            isConnected = false;
        }
    }

    void publishByteData(String msg) {
        try {
            byte[] comd = getByte(msg);
            if (Objects.nonNull(outputStream)) {
                for (byte b : comd) {
                    System.out.print(b + " ");
                }
                System.out.println();
                outputStream.write(comd);
                outputStream.write("\n".getBytes());
                outputStream.flush();
            } else {
                System.out.println("Output Stream Is null");
                isConnected = false;
            }
        } catch (Exception e) {
            System.err.println(
                    "Exception: occurred while writing Byte message on Serial Client " + e);
            isConnected = false;
        }
    }


    public byte[] getByte(String command) {
        String[] hexValues = command.split(" ");
        byte[] byteArray = new byte[hexValues.length];

        for (int i = 0; i < hexValues.length; i++) {

            System.out.print(hexValues[i]);
            System.out.print(" ");
            System.out.print(Integer.parseInt(hexValues[i], 16));
            byteArray[i] = (byte) Integer.parseInt(hexValues[i], 16);
            System.out.println();
        }
        return byteArray;
    }
}
