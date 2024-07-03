package com.objecteffects.temperature.sensors;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessMessage {
    private final static Logger log = LoggerFactory
            .getLogger(ProcessMessage.class);

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
    private static Map<String, String> propSensors = MainPaho.getPropSensors();
    private static TUnit tunit = MainPaho.getTunit();

    public ProcessMessage() {
    }

    public SensorData processData(final String topic, final String data) {
        final Gson gson = new Gson();

        final String topic_trimmed = StringUtils.substringAfterLast(topic, "/");

        log.debug("topic: {}", topic_trimmed);

        final SensorData target = gson.fromJson(data, SensorData.class);

        if (!propSensors.containsKey(topic_trimmed)) {
            return null;
        }

        target.setSensorName(propSensors.get(topic_trimmed));

        target.setTemperatureShow((float) tunit.convert(target));
        target.setTemperatureLetter(tunit.toString());

        final LocalDateTime dateTime = LocalDateTime.now();

        target.setTimestamp(dtf.format(dateTime));

        log.debug("decoded data: {}", target.toString());

        return target;
    }
}
