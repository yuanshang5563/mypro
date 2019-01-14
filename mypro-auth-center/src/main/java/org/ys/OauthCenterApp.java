package org.ys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

import javax.swing.*;

/**
 *
 */
@SpringBootApplication
@EnableResourceServer
@EnableEurekaClient
public class OauthCenterApp {
    public static void main(String[] args ){
        SpringApplication.run(OauthCenterApp.class,args);
    }
}
