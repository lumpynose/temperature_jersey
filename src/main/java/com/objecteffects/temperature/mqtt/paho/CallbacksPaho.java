package com.objecteffects.temperature.mqtt.paho;

import java.io.IOException;

import com.objecteffects.temperature.sensors.ProcessMessage;

import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CallbacksPaho implements MqttCallback {
    private final static Logger log =
            LoggerFactory.getLogger(CallbacksPaho.class);

    private final MqttClient client;
    private final ProcessMessage process;

    public CallbacksPaho(final MqttClient _client, final ProcessMessage _process) {
        this.client = _client;
        this.process = _process;
    }

    @Override
    public void disconnected(final MqttDisconnectResponse disconnectResponse) {
        log.warn("disconnected: {}", disconnectResponse);

        if (disconnectResponse.getException() == null) {
            log.warn("no exception");

            return;
        }

        try {
            this.client.reconnect();
        }
        catch (final MqttException e) {
            for (final StackTraceElement ste : e.getStackTrace()) {
                log.warn(ste.toString());
            }

            throw new RuntimeException(e);
        }
    }

    @Override
    public void mqttErrorOccurred(final MqttException exception) {
        log.warn("error occurred: {}", exception);

        try {
            this.client.reconnect();
        }
        catch (final MqttException e) {
            for (final StackTraceElement ste : e.getStackTrace()) {
                log.warn(ste.toString());
            }

            throw new RuntimeException(e);
        }
    }

    @Override
    public void messageArrived(final String topic,
            final MqttMessage mqttMessage) throws Exception {
        final String messageTxt = new String(mqttMessage.getPayload());
        log.debug("topic: {}, message: {}", topic, messageTxt);

        this.process.processData(topic, messageTxt);

        MqttProperties props = mqttMessage.getProperties();
        final String responseTopic = props.getResponseTopic();

        if (responseTopic != null) {
            log.debug("response topic: {}", responseTopic);
            final String corrData = new String(props.getCorrelationData());

            final MqttMessage response = new MqttMessage();

            props = new MqttProperties();

            props.setCorrelationData(corrData.getBytes());
            final String content = "Got message with correlation data "
                    + corrData;
            response.setPayload(content.getBytes());

            response.setProperties(props);
            this.client.publish(responseTopic, response);
        }
    }

    @Override
    public void deliveryComplete(final IMqttToken token) {
        log.debug("delivery complete: {}", token);
    }

    @Override
    public void connectComplete(final boolean reconnect,
            final String serverURI) {
        final AppProperties props = new AppProperties();

        try {
            props.loadProperties();
        }
        catch (final IOException e) {
            for (final StackTraceElement ste : e.getStackTrace()) {
                log.warn(ste.toString());
            }

            throw new RuntimeException(e);
        }

        final ConnectPaho listener = new ConnectPaho();

        for (final String topic : props.getTopics()) {
            try {
                listener.subscribe(topic);
            }
            catch (final Exception e) {
                for (final StackTraceElement ste : e.getStackTrace()) {
                    log.warn(ste.toString());
                }

                throw new RuntimeException(e);
            }
        }

        log.warn("connect complete: {}", Boolean.toString(reconnect));
    }

    @Override
    public void authPacketArrived(final int reasonCode,
            final MqttProperties properties) {
        log.debug("auth packet arrived: {}", Integer.toString(reasonCode));
    }
}
