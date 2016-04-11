package com.integratingfactor.idp.user.test.integration;

import org.hamcrest.Matchers;
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
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.integratingfactor.idp.common.db.exceptions.DbException;
import com.integratingfactor.idp.user.api.model.IdpUser;
import com.integratingfactor.idp.user.api.model.IdpUserProfile;
import com.integratingfactor.idp.user.api.model.IdpUserSecret;
import com.integratingfactor.idp.user.api.service.IdpUserServiceApi;
import com.integratingfactor.idp.user.core.model.IdpUserProfileFields;
import com.integratingfactor.idp.user.core.service.IdpUserService;
import com.integratingfactor.idp.user.db.service.DaoUserService;

@ContextConfiguration(classes = { IdpUserServiceIntegrationTestConfig.class })
@WebAppConfiguration
public class IdpUserServiceIntegrationTest extends AbstractTestNGSpringContextTests {

    @Autowired
    IdpUserServiceApi userApi;

    @Autowired
    IdpUserService userService;

    @Autowired
    DaoUserService userDao;

    private MockMvc mockMvc;

    @BeforeClass
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(userApi).build();
    }

    @BeforeMethod
    public void init() {
        createTestUser();
    }

    @AfterMethod
    public void cleanup() throws Exception {
        removeUser(TestUserAccountId);
    }

    ObjectMapper mapper = new ObjectMapper();

    // public static final String TestUserAccountId =
    // UUID.randomUUID().toString();
    public static final String TestUserAccountId = "a-test-user-id";
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

    private void removeUser(String accountId) throws Exception {
        System.out.println("Removing user: " + accountId);
        this.mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/users/" + accountId).contentType(MediaType.APPLICATION_JSON));
    }

    private void createTestUser() {
        try {
            userDao.createUser(testIdpUser());
        } catch (DbException e) {
            Assert.fail(e.getError());
        }
    }

    @Test
    public void testCreateUser() throws Exception {
        IdpUser user = new IdpUser();
        user.setAccountId(TestUserAccountId);
        MvcResult response = this.mockMvc
                .perform(MockMvcRequestBuilders.post("/api/v1/users").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(testIdpUser())))
                .andExpect(MockMvcResultMatchers.status().is(201))
                .andExpect(MockMvcResultMatchers.jsonPath("$.account_id", Matchers.notNullValue())).andReturn();
        System.out.println("Response Status: " + response.getResponse().getStatus());
        System.out.println("Request response: " + response.getResponse().getContentAsString());

        // make sure to clean up user
        removeUser(mapper.readValue(response.getResponse().getContentAsString(), IdpUser.class).getAccountId());
    }

    @Test
    public void testGetUserDetails() throws Exception {
        MvcResult response = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/api/v1/users/" + TestUserAccountId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.account_id", Matchers.is(TestUserAccountId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password", Matchers.is(TestUserSecret)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.profile.first_name", Matchers.is(TestUserFirstName)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.profile.last_name", Matchers.is(TestUserLastName)))
                .andReturn();
        System.out.println(("Response Status: " + response.getResponse().getStatus()));
        System.out.println("Request response: " + response.getResponse().getContentAsString());
    }

    @Test
    public void testDeleteUser() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.delete("/api/v1/users/" + TestUserAccountId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(204)).andReturn();
    }

    @Test
    public void testGetUserSecret() throws Exception {
        MvcResult response = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/api/v1/users/" + TestUserAccountId + "/secret")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.account_id", Matchers.is(TestUserAccountId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password", Matchers.is(TestUserSecret))).andReturn();
        System.out.println(("Response Status: " + response.getResponse().getStatus()));
        System.out.println("Request response: " + response.getResponse().getContentAsString());
    }

    @Test
    public void testUpdateUserSecret() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/api/v1/users/" + TestUserAccountId + "/secret")
                        .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(testIdpUserSecret())))
                .andExpect(MockMvcResultMatchers.status().is(204)).andReturn();
    }

    @Test
    public void testGetUserProfile() throws Exception {
        MvcResult response = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/api/v1/users/" + TestUserAccountId + "/profile")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.first_name", Matchers.is(TestUserFirstName)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.last_name", Matchers.is(TestUserLastName))).andReturn();
        System.out.println(("Response Status: " + response.getResponse().getStatus()));
        System.out.println("Request response: " + response.getResponse().getContentAsString());
    }

    @Test
    public void testUpdateUserProfile() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/api/v1/users/" + TestUserAccountId + "/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(testIdpUserProfile())))
                .andExpect(MockMvcResultMatchers.status().is(204)).andReturn();
    }

}
