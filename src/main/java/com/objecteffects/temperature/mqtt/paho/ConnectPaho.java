package com.objecteffects.temperature.mqtt.paho;

import java.util.UUID;

import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttSubscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectPaho {
    private final static Logger log =
            LoggerFactory.getLogger(ConnectPaho.class);

    private MqttClient client;

    public ConnectPaho() {
    }

    public MqttClient connect(final String broker) throws MqttException {
        final MemoryPersistence persistence = new MemoryPersistence();

        final MqttConnectionOptions connOpts = new MqttConnectionOptions();
        connOpts.setCleanStart(true);
        connOpts.setAutomaticReconnect(true);

        final String clientId = UUID.randomUUID().toString();

        try {
            log.debug("Connecting to MQTT broker: {}", broker);

            this.client = new MqttClient(broker, clientId, persistence);

            this.client.connect(connOpts);

            log.debug("Connected");
        }
        catch (final MqttException e) {
            log.debug("reason: {}", Integer.valueOf(e.getReasonCode()));
            log.debug("msg: {}", e.getMessage());
            log.debug("loc: {}", e.getLocalizedMessage());
            log.debug("cause: {}", e.getCause());
            log.debug("excep: {}", e);

            for (final StackTraceElement ste : e.getStackTrace()) {
                log.warn(ste.toString());
            }

            throw e;
        }

        // this.client.setCallback(new CallbacksPaho(this.client));

        return this.client;
    }

    public void subscribe(final MqttClient client, final String topic,
            final int qos) throws Exception {
        try {
            log.debug("Subscribing to topic: {}", topic);

            final MqttSubscription sub = new MqttSubscription(topic, qos);

            final IMqttToken token = this.client
                    .subscribe(new MqttSubscription[] { sub });

            log.debug("token: {}", token.getResponse());
        }
        catch (final Exception e) {
            for (final StackTraceElement ste : e.getStackTrace()) {
                log.warn(ste.toString());
            }

            throw e;
        }
    }
}
