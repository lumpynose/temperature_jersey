package com.objecteffects.temperature.mqtt.paho.ebruno;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ebruno
 */
public class ListenerMain {
    final static Logger log = LoggerFactory.getLogger(ListenerMain.class);

    public static void main(String[] args) {
        String server = "192.168.50.5";
        String port = "1883";
        String[] topics = { "rtl_433/temperature/+", "zigbee/temperature/+" };
        int qos = 1;
        String broker;

        broker = "tcp://" + server + ":" + port;

        ConnectAndListen listener = new ConnectAndListen();

        try {
            listener.connectAndListen(broker, topics, qos);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
