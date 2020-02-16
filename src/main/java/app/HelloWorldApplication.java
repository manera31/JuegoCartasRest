package main.java.app;

import org.glassfish.jersey.server.ResourceConfig;

public class HelloWorldApplication extends ResourceConfig {
    public HelloWorldApplication(){
        packages("service");
    }
}
