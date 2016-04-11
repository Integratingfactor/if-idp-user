package com.integratingfactor.idp.user.db.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.integratingfactor.idp.common.db.gds.GdsDaoService;

@Configuration
@PropertySource("classpath:app-test.properties")
public class GdsDaoUserServiceTestConfig {
    @Autowired
    private Environment env;

    @Bean
    public GdsDaoUserService daoUserService() {
        return new GdsDaoUserService();
    }

    @Bean
    public GdsDaoService gdsDaoService() {
        return new GdsDaoService(env);
    }
}
