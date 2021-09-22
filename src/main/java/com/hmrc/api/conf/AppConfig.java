package com.hmrc.api.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Value("${get.login}")
    public String loginPath;

    @Value("${get.client}")
    public String clientPath;

}
