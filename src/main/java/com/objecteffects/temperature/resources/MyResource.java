package com.objecteffects.temperature.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import java.util.HashMap;
import java.util.Map;

import org.glassfish.jersey.server.mvc.Viewable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresource")
public class MyResource {
    final static Logger log = LoggerFactory.getLogger(MyResource.class);

    /**
     * Method handling HTTP GET requests. The returned object will be sent to
     * the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
    //@Template(name = "/getIt.mustache")
     */
    @GET
    public Viewable getIt() {
        log.info("getIt");

        Map<String, String> map = new HashMap<>();
        
        map.put("value", "Got it!");
        
        return new Viewable("/getIt.mustache", this);
    }
    
    public String value() {
        return("got it");
    }
}
