package com.integratingfactor.idp.common.db.gds;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
// @PropertySource("classpath:gds-dao-test.properties")
@PropertySource("classpath:app-test.properties")
public class GdsDaoServiceTestConfig {
    @Bean
    public GdsDaoService gdsDaoService() {
        return new GdsDaoService();
    }
}
