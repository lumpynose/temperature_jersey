package com.objecteffects.temperature.mqtt.paho.ebruno;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.eclipse.paho.mqttv5.client.IMqttMessageListener;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttSubscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectAndListen {
    final static Logger log = LoggerFactory.getLogger(ConnectAndListen.class);
    
    private static MemoryPersistence persistence = new MemoryPersistence();
    private MqttClient client;

    public MqttClient connectAndListen(String broker, String[] topics, int qos)
            throws MqttException {
        this.client = connect(broker);
        listen(this.client, topics, qos, new Listener(this.client));

        return this.client;
    }

    public MqttClient connect(String broker) throws MqttException {
        MqttClient client;
        String clientId = UUID.randomUUID().toString();

        try {
            log.info("Connecting to MQTT broker: " + broker);

            MqttConnectionOptions connOpts = new MqttConnectionOptions();
            connOpts.setCleanStart(false);

            client = new MqttClient(broker, clientId, persistence);
            client.connect(connOpts);

            log.info("Connected");
        }
        catch (MqttException me) {
            log.info("reason " + me.getReasonCode());
            log.info("msg " + me.getMessage());
            log.info("loc " + me.getLocalizedMessage());
            log.info("cause " + me.getCause());
            log.info("excep " + me);
            me.printStackTrace();

            throw me;
        }

        return client;
    }

    public void listen(MqttClient client, String[] topics, int qos,
            IMqttMessageListener listener) {

        List<MqttSubscription> subs = new ArrayList<>();

        for (String topic : topics) {
            log.info("Subscribing to topic " + topic);

            subs.add(new MqttSubscription(topic, qos));
        }

        IMqttMessageListener[] listeners = new Listener[subs.size()];
        Arrays.fill(listeners, listener);

        try {
            client.subscribe(subs.toArray(new MqttSubscription[0]), listeners);

//            for (String topic : topics) {
//                log.info("Subscribing to topic " + topic);
//                client.subscribe(topic, qos, listener);
//            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            this.client.disconnect();
            log.info("Disconnected");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
