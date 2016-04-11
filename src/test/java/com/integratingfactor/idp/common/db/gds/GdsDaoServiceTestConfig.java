package com.integratingfactor.idp.common.db.gds;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:gds-dao-test.properties")
// @PropertySource("classpath:app-test.properties")
public class GdsDaoServiceTestConfig {
    @Autowired
    private Environment env;

    @Bean
    public GdsDaoService gdsDaoService() {
        return new GdsDaoService(env);
    }
}
