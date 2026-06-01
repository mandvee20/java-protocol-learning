package com.learning.java.connection.mqtt;

import com.learning.java.Constants;
import com.learning.java.connection.serial.SerialConnection;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptionsBuilder;
import org.eclipse.paho.mqttv5.client.persist.MqttDefaultFilePersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.json.JSONObject;

import javax.net.ssl.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Mandvee Vatsa
 * @date 22-May-2026 2:32:36 pm
 */
public class MqttConnection {
    private static MqttConnection mqttConnection;
    private String ip = "127.0.0.1";
    private String fqdn = "mqtt.mandvee.in";
    private String port = "1883";
    private String user = "mandvee";
    private String pwd = "abcd";
    @Setter
    private boolean isConnected = false;
    public static final String CLIENTID = UUID.randomUUID().toString();

    private String broker;
    private Constants.ConnectionProtocols connectionProtocol;
    private Constants.CertificateValidationType certValidationType =
            Constants.CertificateValidationType.IGNORE_CERTIFICATE;
    private String certificatePath = "/etc/ssl/project_casadigi_com/mqtt.crt";
    public int publishQOS = 1;
    @Getter
    private MqttClient client;
    private MqttConnectionOptions options;
    private MqttCallbackImpl callBackImpl = new MqttCallbackImpl();
    MqttDefaultFilePersistence persistence;
    int[] subscribeQOS = new int[] {1};

    String[] topicsToSubscribe =
            new String[] {"#"};
    public String disconnectionTopic = "mqtt/broker/disconnect";
    public String disconnectionMessage = "Mqtt Broker Disconnected";
    private long deviceReconnectDelay = 20000;
    private int connectionTimeout = 10; // In Seconds

    private MqttConnection(){
        loadConnectionInfo();
    }

    public static  MqttConnection getInstance(){
        if(Objects.isNull(mqttConnection)){
            synchronized (MqttConnection.class){
                if(Objects.isNull(mqttConnection)){
                    mqttConnection = new MqttConnection();
                }
            }
        }
        return mqttConnection;
    }


    private void loadConnectionInfo(){
        try {
            // Read file from src/main/resources
            URL filePath = MqttConnection.class.getClassLoader()
                    .getResource(Constants.CONFIG_FILE);

            // Convert file content into String
            String jsonContent = Files.readString(
                    Paths.get(filePath.toURI()));

            // Convert String → JSONObject
            JSONObject rootObject = new JSONObject(jsonContent);

            // Get mqtt object
            JSONObject mqtt = rootObject.getJSONObject(Constants.MQTT_CLIENT);

            // Fetch fields
            port = mqtt.getString(Constants.PORT);
            ip = mqtt.getString(Constants.IP);
            fqdn = mqtt.getString(Constants.FQDN);
            user = mqtt.getString(Constants.USERNAME);
            pwd = mqtt.getString(Constants.PASSWORD);
            String protocol = mqtt.getString(Constants.CONNECTION_PROTOCOL);
            String certificateValidation = mqtt.getString(Constants.CERTIFICATE_VALIDATION_TYPE);

            certValidationType = Constants.CertificateValidationType.fromString(
                    certificateValidation);
            connectionProtocol = StringUtils.isBlank(protocol)
                    || Objects.isNull(Constants.ConnectionProtocols.fromString(protocol))
                    ? Constants.ConnectionProtocols.TCP
                    : Constants.ConnectionProtocols.fromString(protocol);

            String url = StringUtils.isNotBlank(fqdn) ? fqdn
                    : (StringUtils.isNotEmpty(ip) ? ip : StringUtils.EMPTY);

            if (StringUtils.isNotEmpty(url) && StringUtils.isNotEmpty(port))
                broker = connectionProtocol.name().toLowerCase()
                        .concat(Constants.URL_SEPARATOR).concat(url)
                        .concat(Constants.COLON).concat(port);

            System.out.println("Broker URL : "+ broker);

            /** BROKER FQDN URL: "ssl://mqtt.mandvee.in:8883" **/
            /** BROKER IP URL: "tcp://10.11.12.13:1883" **/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void connect() throws MqttException
    {
        switch (connectionProtocol)
        {
            case SSL:
                sslConnect();
                break;
            case TCP:
            default:
                tcpConnect();
        }

    }

    public void makeConnection(){
        try{

            new Thread(() -> {
                Thread.currentThread().setName("Writer");
                writeData();
            }).start();

            new Thread(() -> {
                Thread.currentThread().setName("Connect");
                reconnect();
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reconnect(){
        try{
            while(true){
                if(!isConnected){
                    connect();
                }
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void writeData(){
        try{
            while(true){
                if(isConnected){
//                    publishData("writing/heartbeat", "Hello!");
                }
                Thread.sleep(60000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tcpConnect() throws MqttException
    {

        client = new MqttClient(broker, CLIENTID, persistence);
        options = new MqttConnectionOptionsBuilder().username(user)
                .password(pwd.getBytes())
                .connectionTimeout(connectionTimeout)
                .will(disconnectionTopic,
                        new MqttMessage(disconnectionMessage.getBytes()))
                .build();
        client.setCallback(callBackImpl);
        client.connect(options);
        client.subscribe(topicsToSubscribe, subscribeQOS);
        System.out.println(
                "TCP Connection successfully made for Mqtt Client");

    }

    private void sslConnect() throws MqttException
    {

        client = new MqttClient(broker, CLIENTID, persistence);
        options = new MqttConnectionOptionsBuilder().username(user)
                .password(pwd.getBytes())
                .connectionTimeout(connectionTimeout).cleanStart(true)
                .will(disconnectionTopic,
                        new MqttMessage(disconnectionMessage.getBytes()))
                .build();
        client.setCallback(callBackImpl);

        SSLSocketFactory sslSocketFactory = getSocketFactory();
        if (Objects.nonNull(sslSocketFactory))
        {
            options.setSocketFactory(sslSocketFactory);
            if (Constants.CertificateValidationType.IGNORE_CERTIFICATE
                    .equals(certValidationType))
                options.setSSLHostnameVerifier((hostname, session) -> true);

        }
        client.connect(options);
        client.subscribe(topicsToSubscribe, subscribeQOS);
        System.out.println(
                "SSL Connection successfully made for Mqtt Client");

    }

    private SSLSocketFactory getSocketFactory()
    {
        switch (certValidationType)
        {
            case VALIDATE_CERTIFICATE:
                return getSocketFactoryWithCertValidation(certificatePath);

            case IGNORE_CERTIFICATE:
            default:
                return getSocketFactoryWithoutCert();
        }
    }

    private SSLSocketFactory getSocketFactoryWithCertValidation(String certPath)
    {


        try
        {
            // Load CA certificate
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate caCert;

            try (BufferedInputStream bis =
                         new BufferedInputStream(new FileInputStream(certPath)))
            {
                caCert = (X509Certificate) cf.generateCertificate(bis);
            }

            // Create TrustStore in memory
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            trustStore.setCertificateEntry("mqtt-ca", caCert);

            // Create TrustManager
            TrustManagerFactory tmf = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);

            // Create SSL context
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, tmf.getTrustManagers(), new SecureRandom());

            return sslContext.getSocketFactory();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;

    }

    private SSLSocketFactory getSocketFactoryWithoutCert()
    {
        try
        {

            TrustManager[] trustAll = new TrustManager[] {new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers()
                {
                    return new X509Certificate[0];
                }

                public void checkClientTrusted(X509Certificate[] certs,
                                               String authType)
                {}

                public void checkServerTrusted(X509Certificate[] certs,
                                               String authType)
                {}
            }};

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, trustAll, new SecureRandom());
            return context.getSocketFactory();


        }
        catch (Exception e)
        {
           e.printStackTrace();
        }
        return null;

    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    void publishData(String topic, String msg)
    {

        MqttMessage message = new MqttMessage(msg.getBytes());
        message.setQos(publishQOS);
        try
        {
            client.publish(topic,message);
            System.out.println("Message: "+msg+" published on topic: "+topic+" to mqtt Client: {} ");

        }
        catch (MqttException e)
        {
            System.err.println(
                    "Exception: occured while publishing message request for Mqtt Client "+ e);
            isConnected = false;
        }
    }
}
