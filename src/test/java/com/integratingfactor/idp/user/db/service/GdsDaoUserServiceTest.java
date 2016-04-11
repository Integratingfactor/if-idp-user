package com.integratingfactor.idp.user.db.service;

import java.util.UUID;

import org.hamcrest.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.integratingfactor.idp.common.db.exceptions.DbException;
import com.integratingfactor.idp.common.db.exceptions.NotFoundDbException;
import com.integratingfactor.idp.common.db.gds.GdsDaoService;
import com.integratingfactor.idp.user.api.model.IdpUser;
import com.integratingfactor.idp.user.api.model.IdpUserProfile;
import com.integratingfactor.idp.user.api.model.IdpUserSecret;
import com.integratingfactor.idp.user.core.model.IdpUserProfileFields;

@ContextConfiguration(classes = { GdsDaoUserServiceTestConfig.class })
public class GdsDaoUserServiceTest extends AbstractTestNGSpringContextTests {

    @Autowired
    GdsDaoService dao;

    @Autowired
    GdsDaoUserService userDao;

    @BeforeMethod
    public void reset() {
        Mockito.reset(dao);
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

    // @Test
    public void testCreateUserSuccess() throws DbException {
        userDao.createUser(testIdpUser());

        IdpUserSecret secret = userDao.readUserSecret(TestUserAccountId);

        // read password back and compare
        Assert.assertEquals(secret.getPassword(), TestUserSecret);

        // read profile back and compare
        IdpUserProfile profile = userDao.readUserProfile(TestUserAccountId);
        Assert.assertEquals(profile.get(IdpUserProfileFields.first_name), TestUserFirstName);
        Assert.assertEquals(profile.get(IdpUserProfileFields.last_name), TestUserLastName);

        // delete user
        userDao.removeUser(TestUserAccountId);
        try {
            userDao.readUserSecret(TestUserAccountId);
            Assert.fail("user still exists after calling delete");
        } catch (NotFoundDbException e) {
            System.out.println("Success: " + e.getError());
        }
    }

    // @Test
    // public void testCreateUser() throws Exception, DbException {
    // IdpUser user = new IdpUser();
    // user.setAccountId(TestUserAccountId);
    // MvcResult response = this.mockMvc
    // .perform(MockMvcRequestBuilders.post("/api/v1/users").contentType(MediaType.APPLICATION_JSON)
    // .content(mapper.writeValueAsBytes(testIdpUser())))
    // .andExpect(MockMvcResultMatchers.status().is(201))
    // .andExpect(MockMvcResultMatchers.jsonPath("$.account_id",
    // Matchers.notNullValue())).andReturn();
    // System.out.println(("Response Status: " +
    // response.getResponse().getStatus()));
    // System.out.println("Request response: " +
    // response.getResponse().getContentAsString());
    // // validate that user details were saved with user DAO
    // Mockito.verify(userDao).createUser(Mockito.any(IdpUser.class));
    // }
    //
    // @Test
    // public void testGetUserDetails() throws Exception, DbException {
    // Mockito.when(userDao.readUserDetails(Mockito.anyString())).thenReturn(testIdpUser());
    // MvcResult response = this.mockMvc
    // .perform(MockMvcRequestBuilders.get("/api/v1/users/" + TestUserAccountId)
    // .contentType(MediaType.APPLICATION_JSON))
    // .andExpect(MockMvcResultMatchers.status().is(200))
    // .andExpect(MockMvcResultMatchers.jsonPath("$.account_id",
    // Matchers.is(TestUserAccountId)))
    // .andExpect(MockMvcResultMatchers.jsonPath("$.password",
    // Matchers.is(TestUserSecret)))
    // .andExpect(MockMvcResultMatchers.jsonPath("$.profile.first_name",
    // Matchers.is(TestUserFirstName)))
    // .andExpect(MockMvcResultMatchers.jsonPath("$.profile.last_name",
    // Matchers.is(TestUserLastName)))
    // .andReturn();
    // Mockito.verify(userDao).readUserDetails(TestUserAccountId);
    // System.out.println(("Response Status: " +
    // response.getResponse().getStatus()));
    // System.out.println("Request response: " +
    // response.getResponse().getContentAsString());
    // }
    //
    // @Test
    // public void testDeleteUser() throws Exception, DbException {
    // this.mockMvc
    // .perform(MockMvcRequestBuilders.delete("/api/v1/users/" +
    // TestUserAccountId)
    // .contentType(MediaType.APPLICATION_JSON))
    // .andExpect(MockMvcResultMatchers.status().is(204)).andReturn();
    // Mockito.verify(userDao).removeUser(TestUserAccountId);
    // }
    //
    // @Test
    // public void testGetUserSecret() throws Exception, DbException {
    // Mockito.when(userDao.readUserSecret(Mockito.anyString())).thenReturn(testIdpUserSecret());
    // MvcResult response = this.mockMvc
    // .perform(MockMvcRequestBuilders.get("/api/v1/users/" + TestUserAccountId
    // + "/secret")
    // .contentType(MediaType.APPLICATION_JSON))
    // .andExpect(MockMvcResultMatchers.status().is(200))
    // .andExpect(MockMvcResultMatchers.jsonPath("$.account_id",
    // Matchers.is(TestUserAccountId)))
    // .andExpect(MockMvcResultMatchers.jsonPath("$.password",
    // Matchers.is(TestUserSecret))).andReturn();
    // Mockito.verify(userDao).readUserSecret(TestUserAccountId);
    // System.out.println(("Response Status: " +
    // response.getResponse().getStatus()));
    // System.out.println("Request response: " +
    // response.getResponse().getContentAsString());
    // }
    //
    // @Test
    // public void testUpdateUserSecret() throws Exception, DbException {
    // this.mockMvc
    // .perform(MockMvcRequestBuilders.put("/api/v1/users/" + TestUserAccountId
    // + "/secret")
    // .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(testIdpUserSecret())))
    // .andExpect(MockMvcResultMatchers.status().is(204)).andReturn();
    // Mockito.verify(userDao).updateUserSecret(Mockito.any(IdpUserSecret.class));
    // }
    //
    // @Test
    // public void testGetUserProfile() throws Exception, DbException {
    // Mockito.when(userDao.readUserProfile(Mockito.anyString())).thenReturn(testIdpUserProfile());
    // MvcResult response = this.mockMvc
    // .perform(MockMvcRequestBuilders.get("/api/v1/users/" + TestUserAccountId
    // + "/profile")
    // .contentType(MediaType.APPLICATION_JSON))
    // .andExpect(MockMvcResultMatchers.status().is(200))
    // .andExpect(MockMvcResultMatchers.jsonPath("$.first_name",
    // Matchers.is(TestUserFirstName)))
    // .andExpect(MockMvcResultMatchers.jsonPath("$.last_name",
    // Matchers.is(TestUserLastName))).andReturn();
    // Mockito.verify(userDao).readUserProfile(TestUserAccountId);
    // System.out.println(("Response Status: " +
    // response.getResponse().getStatus()));
    // System.out.println("Request response: " +
    // response.getResponse().getContentAsString());
    // }
    //
    // @Test
    // public void testUpdateUserProfile() throws Exception, DbException {
    // this.mockMvc
    // .perform(MockMvcRequestBuilders.put("/api/v1/users/" + TestUserAccountId
    // + "/profile")
    // .contentType(MediaType.APPLICATION_JSON)
    // .content(mapper.writeValueAsBytes(testIdpUserProfile())))
    // .andExpect(MockMvcResultMatchers.status().is(204)).andReturn();
    // Mockito.verify(userDao).updateUserProfile(Mockito.matches(TestUserAccountId),
    // Mockito.any(IdpUserProfile.class));
    // }

}
