package com.integratingfactor.idp.user.test.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.integratingfactor.idp.common.db.gds.GdsDaoService;
import com.integratingfactor.idp.user.api.service.IdpUserServiceApi;
import com.integratingfactor.idp.user.core.service.IdpUserServiceImpl;
import com.integratingfactor.idp.user.db.service.DaoUserService;
import com.integratingfactor.idp.user.db.service.GdsDaoUserService;

@Configuration
@EnableWebMvc
@PropertySource("classpath:app-test.properties")
public class IdpUserServiceIntegrationTestConfig {
    @Autowired
    private Environment env;

    @Bean
    public IdpUserServiceApi idpUserServiceApi() {
        return new IdpUserServiceApi();
    }

    @Bean
    public IdpUserServiceImpl idpUserService() {
        return new IdpUserServiceImpl();
    }

    @Bean
    public DaoUserService daoUserService() {
        return new GdsDaoUserService();
    }

    @Bean
    public GdsDaoService gdsDaoService() {
        return new GdsDaoService(env);
    }
}
