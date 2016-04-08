package com.integratingfactor.idp.user.api.service;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.integratingfactor.idp.user.core.service.IdpUserService;

@Configuration
@EnableWebMvc
public class IdpUserServiceApiTestConfig {

    @Bean
    public IdpUserServiceApi idpUserServiceApi() {
        return new IdpUserServiceApi();
    }

    @Bean
    public IdpUserService idpUserService() {
        return Mockito.mock(IdpUserService.class);
    }
}
