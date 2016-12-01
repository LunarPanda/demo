package com.example.cloud;

import org.springframework.cloud.Cloud;
import org.springframework.cloud.app.ApplicationInstanceInfo;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.cloud.config.java.ServiceScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ServiceScan
//@Profile("cloud")
public class CloudConfig extends AbstractCloudConfig {
    @Bean("myCloud")
    public Cloud getCloud() {
        return cloud();
    }
}
