package com.learning.java;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Mandvee Vatsa
 * @date 22-May-2026 2:32:36 pm
 */

public enum ConnectionType {

    RS232("RS232"), SERVER_SOCKET("Server Socket"), CLIENT_SOCKET("Client Socket"),
    WEBSOCKET_CLIENT("Websocket Client"), WEBSOCKET_SERVER("Websocket Server"), MQTT_CLIENT("MQTT Client"),
    MQTT_BROKER("MQTT Broker"), NONE("");

    private String connectionType;

    ConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    public static ConnectionType fromString(String connectionType) {
        try {
            for (ConnectionType connectiontype : ConnectionType.values()) {
                if (StringUtils.containsIgnoreCase(connectiontype.getConnectionType(), connectionType)) {
                    return connectiontype;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return NONE;
    }

    public String getConnectionType() {
        return connectionType;
    }

}
