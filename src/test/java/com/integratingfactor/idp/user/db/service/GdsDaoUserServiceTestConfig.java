package com.integratingfactor.idp.user.db.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.integratingfactor.idp.common.db.util.GdsDaoService;

@Configuration
@PropertySource("classpath:app-test.properties")
public class GdsDaoUserServiceTestConfig {

    @Bean
    public GdsDaoUserService daoUserService() {
        return new GdsDaoUserService();
    }

    @Bean
    public GdsDaoService gdsDaoService() {
        return new GdsDaoService();
    }
}
