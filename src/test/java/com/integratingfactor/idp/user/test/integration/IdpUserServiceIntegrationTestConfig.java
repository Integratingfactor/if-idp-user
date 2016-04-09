package com.integratingfactor.idp.user.test.integration;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.integratingfactor.idp.user.api.service.IdpUserServiceApi;
import com.integratingfactor.idp.user.core.service.IdpUserServiceImpl;
import com.integratingfactor.idp.user.db.service.DaoUserService;

@Configuration
@EnableWebMvc
public class IdpUserServiceIntegrationTestConfig {

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
        return Mockito.mock(DaoUserService.class);
    }

}
