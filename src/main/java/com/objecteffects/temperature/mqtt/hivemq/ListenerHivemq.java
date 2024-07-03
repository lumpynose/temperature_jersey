package com.objecteffects.temperature.mqtt.hivemq;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.message.connect.Mqtt5Connect;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck;
import com.objecteffects.temperature.gui.ISensors;
import com.objecteffects.temperature.mqtt.paho.ListenerPaho;

public class ListenerHivemq {
    private final static Logger log = LogManager.getLogger(ListenerPaho.class);

    @SuppressWarnings("unused")
    private final int qos = 1;

    private static Mqtt5BlockingClient client;

    @SuppressWarnings("unused")
    public ListenerHivemq(final ISensors _guiLayout) {
        // empty
    }

    public void connect(final String broker) {
        final String brokerIP = StringUtils.removeStart(broker, "tcp://");

        try {
            client = Mqtt5Client.builder().serverHost(brokerIP)
                    .automaticReconnectWithDefaultConfig().buildBlocking();
        }
        catch (final Exception e) {
            log.error("failed to build");

            for (final StackTraceElement ste : e.getStackTrace()) {
                log.warn(ste.toString());
            }

            throw e;
        }

        final Mqtt5Connect connectMessage = Mqtt5Connect.builder().build();

        final Mqtt5ConnAck connAckMessage = client.connect(connectMessage);

        log.info("connAckMessage: {}", connAckMessage);
    }

    public void listen(final String topic) {
        log.info("listening: {}", topic);

        client.subscribeWith().topicFilter(topic);

//        .callback(publish -> {
//            // Process the received message
//        }).send().whenComplete((subAck, throwable) -> {
//            if (throwable != null) {
//                // Handle failure to subscribe
//            }
//            else {
//                // Handle successful subscription, e.g. logging or incrementing a metric
//            }
//        });
    }
}
