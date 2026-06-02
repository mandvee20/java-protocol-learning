package com.learning.java.connection.mqtt;

import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

/**
 * @author Mandvee Vatsa
 * @date 22-May-2026 2:32:36 pm
 */
public class MqttCallbackImpl implements MqttCallback {


    @Override
    public void disconnected(MqttDisconnectResponse disconnectResponse) {
        System.out.println("Disconnection Response received from Mqtt Client ");
        MqttConnection.getInstance().setConnected(false);

    }

    @Override
    public void mqttErrorOccurred(MqttException exception) {
        System.err.println("Exception: occured for Mqtt Client with " + exception);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println("Message: " + new String(message.getPayload()) + " Received from Mqtt Client on topic: "
                + topic);

    }

    @Override
    public void deliveryComplete(IMqttToken token) {

        System.out.println("Message delivered to Mqtt Client ");


    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        System.out.println("Successfully connected to Mqtt Client");
        MqttConnection.getInstance().setConnected(true);


    }

    @Override
    public void authPacketArrived(int reasonCode, MqttProperties properties) {
        System.out.println(
                "Auth Packet with properties: " + properties + ", reasonCode: " + reasonCode + " arrived"
        );
    }


}
