package com.hmrc.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;


@SpringBootApplication
public class HmrcCamelApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(HmrcCamelApiApplication.class, args);
    }

}