package com.integratingfactor.idp.user.api.service;

import java.util.UUID;

import org.hamcrest.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.integratingfactor.idp.user.api.model.IdpUser;
import com.integratingfactor.idp.user.api.model.IdpUserProfile;
import com.integratingfactor.idp.user.api.model.IdpUserSecret;
import com.integratingfactor.idp.user.core.model.IdpUserProfileFields;
import com.integratingfactor.idp.user.core.service.IdpUserService;

@ContextConfiguration(classes = { IdpUserServiceApiTestConfig.class })
@WebAppConfiguration
public class IdpUserServiceApiTest extends AbstractTestNGSpringContextTests {

    @Autowired
    IdpUserServiceApi userApi;

    @Autowired
    IdpUserService userService;

    private MockMvc mockMvc;

    @BeforeMethod
    public void reset() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(userApi).build();
        Mockito.reset(userService);
    }

    ObjectMapper mapper = new ObjectMapper();

    public static final String TestUserAccountId = UUID.randomUUID().toString();
    public static final String TestUserSecret = "!@#$%^&*";
    public static final String TestUserFirstName = "Mr. Test";
    public static final String TestUserLastName = "User";

    public static IdpUser testIdpUser() {
        IdpUser user = new IdpUser();
        user.setAccountId(TestUserAccountId);
        user.setPasword(TestUserSecret);
        user.setProfile(testIdpUserProfile());
        return user;
    }

    public static IdpUserSecret testIdpUserSecret() {
        IdpUserSecret secret = new IdpUserSecret();
        secret.setAccountId(TestUserAccountId);
        secret.setPassword(TestUserSecret);
        return secret;
    }

    public static IdpUserProfile testIdpUserProfile() {
        IdpUserProfile profile = new IdpUserProfile();
        profile.put(IdpUserProfileFields.first_name, TestUserFirstName);
        profile.put(IdpUserProfileFields.last_name, TestUserLastName);
        return profile;
    }

    @Test
    public void testCreateUserEndpoint() throws Exception {
        IdpUser user = new IdpUser();
        user.setAccountId(TestUserAccountId);
        Mockito.when(userService.addIdpUser(Mockito.any(IdpUser.class))).thenReturn(user);
        MvcResult response = this.mockMvc
                .perform(MockMvcRequestBuilders.post("/api/v1/users").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(testIdpUser())))
                .andExpect(MockMvcResultMatchers.status().is(201))
                .andExpect(MockMvcResultMatchers.jsonPath("$.account_id", Matchers.is(TestUserAccountId))).andReturn();
        Mockito.verify(userService).addIdpUser(Mockito.any(IdpUser.class));
        System.out.println(("Response Status: " + response.getResponse().getStatus()));
        System.out.println("Request response: " + response.getResponse().getContentAsString());
    }

    @Test
    public void testGetUserDetailsEndpoint() throws Exception {
        Mockito.when(userService.getIdpUserDetails(Mockito.anyString())).thenReturn(testIdpUser());
        MvcResult response = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/api/v1/users/" + TestUserAccountId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.account_id", Matchers.is(TestUserAccountId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password", Matchers.is(TestUserSecret)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.profile.first_name", Matchers.is(TestUserFirstName)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.profile.last_name", Matchers.is(TestUserLastName)))
                .andReturn();
        Mockito.verify(userService).getIdpUserDetails(TestUserAccountId);
        System.out.println(("Response Status: " + response.getResponse().getStatus()));
        System.out.println("Request response: " + response.getResponse().getContentAsString());
    }

    @Test
    public void testDeleteUserEndpoint() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.delete("/api/v1/users/" + TestUserAccountId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(204)).andReturn();
        Mockito.verify(userService).removeIdpUser(TestUserAccountId);
    }

    @Test
    public void testGetUserSecretEndpoint() throws Exception {
        Mockito.when(userService.getIdpUserSecret(Mockito.anyString())).thenReturn(testIdpUserSecret());
        MvcResult response = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/api/v1/users/" + TestUserAccountId + "/secret")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.account_id", Matchers.is(TestUserAccountId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password", Matchers.is(TestUserSecret))).andReturn();
        Mockito.verify(userService).getIdpUserSecret(TestUserAccountId);
        System.out.println(("Response Status: " + response.getResponse().getStatus()));
        System.out.println("Request response: " + response.getResponse().getContentAsString());
    }

    @Test
    public void testUpdateUserSecretEndpoint() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/api/v1/users/" + TestUserAccountId + "/secret")
                        .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(testIdpUserSecret())))
                .andExpect(MockMvcResultMatchers.status().is(204)).andReturn();
        Mockito.verify(userService).updateIdpUserSecret(Mockito.any(IdpUserSecret.class));
    }

    @Test
    public void testGetUserProfileEndpoint() throws Exception {
        Mockito.when(userService.getIdpUserProfile(Mockito.anyString())).thenReturn(testIdpUserProfile());
        MvcResult response = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/api/v1/users/" + TestUserAccountId + "/profile")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.first_name", Matchers.is(TestUserFirstName)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.last_name", Matchers.is(TestUserLastName)))
                .andReturn();
        Mockito.verify(userService).getIdpUserProfile(TestUserAccountId);
        System.out.println(("Response Status: " + response.getResponse().getStatus()));
        System.out.println("Request response: " + response.getResponse().getContentAsString());
    }

    @Test
    public void testUpdateUserProfileEndpoint() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/api/v1/users/" + TestUserAccountId + "/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(testIdpUserProfile())))
                .andExpect(MockMvcResultMatchers.status().is(204)).andReturn();
        Mockito.verify(userService).updateIdpUserProfile(Mockito.any(IdpUser.class));
    }
}
