package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;


@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, JacksonAutoConfiguration.class})
public class SOASECApp {

    /**
     * Manda in esecuzione il server Tomcat, che si occupa di eseguire la two-factor authentication.
     */

    public static void main(String[] args) {
        SpringApplication.run(SOASECApp.class, args);
    }

}
