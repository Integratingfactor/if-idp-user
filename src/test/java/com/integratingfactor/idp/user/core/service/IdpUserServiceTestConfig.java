package com.integratingfactor.idp.user.core.service;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.integratingfactor.idp.user.db.service.DaoUserService;

@Configuration
public class IdpUserServiceTestConfig {

    @Bean
    public IdpUserServiceImpl idpUserService() {
        return new IdpUserServiceImpl();
    }

    @Bean
    public DaoUserService daoUserService() {
        return Mockito.mock(DaoUserService.class);
    }
}
