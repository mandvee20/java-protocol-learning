package com.learning.java;

import org.apache.commons.lang3.StringUtils;

public class Constants {

    public static final String CONFIG_FILE = "configuration.json";
    public static final String RS232 = "rs232";
    public static final String MQTT_CLIENT = "mqttClient";
    public static final String PORT = "port";
    public static final String BAUDRATE = "baudrate";
    public static final String IP = "ip";
    public static final String FQDN = "fqdn";
    public static final String USERNAME = "user";
    public static final String PASSWORD = "pwd";
    public static final String CERTIFICATE_VALIDATION_TYPE = "certificateValidationType";
    public static final String CONNECTION_PROTOCOL = "connectionProtocol";

    public static final String URL_SEPARATOR = "://";
    public static final String SLASH = "/";
    public static final String COLON = ":";

    /**
     * Common Protocols For Connection
     **/
    public enum ConnectionProtocols
    {

        TCP,
        MQTT,
        TLS,
        SSL;


        public static ConnectionProtocols fromString(String request)
        {
            try
            {
                for (ConnectionProtocols protocol : ConnectionProtocols.values())
                {
                    if (StringUtils.equalsIgnoreCase(protocol.name(), request))
                        return protocol;
                }
                return TCP;
            }
            catch (Exception e)
            {
                return TCP;
            }
        }
    }
    /**
     * Certificate Validation Type for SSL Connection
     **/
    public enum CertificateValidationType
    {

        VALIDATE_CERTIFICATE,
        IGNORE_CERTIFICATE;

        public static CertificateValidationType fromString(String request)
        {
            try
            {
                for (CertificateValidationType type : CertificateValidationType
                        .values())
                {
                    if (StringUtils.equalsIgnoreCase(type.name(), request))
                        return type;
                }
                return IGNORE_CERTIFICATE;
            }
            catch (Exception e)
            {
                return IGNORE_CERTIFICATE;
            }
        }
    }
}
