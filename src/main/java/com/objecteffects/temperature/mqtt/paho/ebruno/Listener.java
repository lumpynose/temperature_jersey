package com.objecteffects.temperature.mqtt.paho.ebruno;

import org.eclipse.paho.mqttv5.client.IMqttMessageListener;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Listener implements IMqttMessageListener {
    final static Logger log = LoggerFactory.getLogger(Listener.class);
    
    private MqttClient client;
    
    public Listener(MqttClient _client) {
        this.client = _client;
    }
    
    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage)
            throws Exception {
        String messageTxt = new String(mqttMessage.getPayload());
        log.info("Message on " + topic + ": '" + messageTxt + "'");

        MqttProperties props = mqttMessage.getProperties();
        String responseTopic = props.getResponseTopic();

        if (responseTopic != null) {
            log.info("--Response topic: " + responseTopic);
            String corrData = new String(props.getCorrelationData());

            MqttMessage response = new MqttMessage();
            props = new MqttProperties();
            props.setCorrelationData(corrData.getBytes());
            String content = "Got message with correlation data " + corrData;
            response.setPayload(content.getBytes());
            response.setProperties(props);
            
            this.client.publish(responseTopic, response);
        }
    }

}
