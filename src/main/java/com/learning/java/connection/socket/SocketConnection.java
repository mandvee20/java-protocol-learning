package com.learning.java.connection.socket;

import com.learning.java.ConnectionType;

/**
 * @author Mandvee Vatsa
 * @date 22-May-2026 2:32:36 pm
 */
public class SocketConnection {
    private String ip;
    private String port;
    private ConnectionType connectionType;

    private SocketConnection(ConnectionType connectionType) {
        this.connectionType = connectionType;
        switch (connectionType) {
            case SERVER_SOCKET:
                initSocketServerConnectionDetails();
                break;
            case CLIENT_SOCKET:
                initSocketClientConnectionDetails();
            default:
                break;
        }
    }

    public void initSocketServerConnectionDetails() {

    }

    public void initSocketClientConnectionDetails() {

    }
}
