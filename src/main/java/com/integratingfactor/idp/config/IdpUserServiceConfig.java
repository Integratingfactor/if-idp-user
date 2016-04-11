package com.integratingfactor.idp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.integratingfactor.idp.common.db.gds.GdsDaoService;
import com.integratingfactor.idp.user.api.service.IdpUserServiceApi;
import com.integratingfactor.idp.user.core.service.IdpUserServiceImpl;
import com.integratingfactor.idp.user.db.service.DaoUserService;
import com.integratingfactor.idp.user.db.service.GdsDaoUserService;

@Configuration
@PropertySource("classpath:app.properties")
public class IdpUserServiceConfig {
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
        // return new GdsDaoService(env);
        return new GdsDaoService();
    }
}
