package com.learning.java;

/**
 * @author Mandvee Vatsa
 * @date 22-May-2026 2:32:36 pm
 */

public class UserInputProcessor {
	private static UserInputProcessor processor = new UserInputProcessor();
	// Eager Initialization (Simple & Thread-safe)

	public static UserInputProcessor getInstance() {
		return processor;
	}

	public void processRequest(String input) {
		System.out.println();
		switch (ConnectionType.fromString(input)) {
		case RS232:
			break;
		case SERVER_SOCKET:
			break;
		case CLIENT_SOCKET:
			break;
		case WEBSOCKET_CLIENT:
			break;
		case WEBSOCKET_SERVER:
			break;
		case MQTT_CLIENT:
			break;
		case MQTT_BROKER:
			break;

		default:
			break;
		}
	}
}
