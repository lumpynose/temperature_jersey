package com.objecteffects.temperature.sensors;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/*
 * the field temperature_F is set for rs433 devices while the field
 * temperature (Celsius) is set for zigbee devices. I'm assuming that a
 * sensor has one of the two temperature values.
 */
public enum TUnit {
    Celsius("C") {
        @Override
        public double convert(final SensorData sensor) {
            if (Float.isFinite(sensor.getTemperature())) {
                return sensor.getTemperature();
            }

            if (Float.isFinite(sensor.getTemperature_F())) {
                final double celsius = (sensor.getTemperature_F() - 32.0)
                        * (5.0 / 9.0);

                return celsius;
            }

            return Double.NaN;
        }
    },
    Fahrenheit("F") {
        @Override
        public double convert(final SensorData sensor) {
            if (Float.isFinite(sensor.getTemperature_F())) {
                return sensor.getTemperature_F();
            }

            if (Float.isFinite(sensor.getTemperature())) {
                final double fahr = sensor.getTemperature() * (9.0 / 5.0)
                        + 32.0;

                return fahr;
            }

            return Double.NaN;
        }
    },
    Kelvin("K") {
        @Override
        public double convert(final SensorData sensor) {
            if (Float.isFinite(sensor.getTemperature_F())) {
                final double kelvin = (sensor.getTemperature_F() - 32)
                        * (5.0 / 9.0) + 273.15;

                return kelvin;
            }

            if (Float.isFinite(sensor.getTemperature())) {
                final double kelvin = sensor.getTemperature() + 273.15;

                return kelvin;
            }

            return Double.NaN;
        }
    };

    private final String letter;

    private static final Map<String, TUnit> ENUM_MAP;

    TUnit(final String _letter) {
        this.letter = _letter;
    }

    @Override
    public String toString() {
        return this.letter;
    }

    static {
        final Map<String, TUnit> map = new HashMap<>();

        for (final TUnit tunit : TUnit.values()) {
            map.put(tunit.toString().toLowerCase(), tunit);
        }

        ENUM_MAP = Collections.unmodifiableMap(map);

        // Stream.of(TUnit.values()).collect(toMap(Enum::name, identity()));
    }

    public static TUnit get(final String name) {
        return ENUM_MAP.get(name.toLowerCase());
    }

    public abstract double convert(final SensorData sensor);
}
