package com.objecteffects.temperature.resources;

import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.mustache.MustacheMvcFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationPath("/")
public class MyApplication extends ResourceConfig {
    final static Logger log = LoggerFactory.getLogger(MyApplication.class);

    public MyApplication() {
        log.info("constructor");

        register(MustacheMvcFeature.class);

        property(MustacheMvcFeature.TEMPLATE_BASE_PATH, "mustache");

        packages("com.objecteffects.temperature");
    }
}
