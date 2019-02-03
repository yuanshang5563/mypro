package org.ys;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 *
 */
@EnableEurekaClient
@SpringBootApplication
@MapperScan({"org.ys.core.dao"})
public class CoreAdminApp {
    public static void main( String[] args ){
        SpringApplication.run(CoreAdminApp.class,args);
    }
}
