package com.integratingfactor.idp.user.core.service;

import java.util.UUID;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.integratingfactor.idp.common.exceptions.db.DbException;
import com.integratingfactor.idp.common.exceptions.db.GenericDbException;
import com.integratingfactor.idp.common.exceptions.db.NotFoundDbException;
import com.integratingfactor.idp.common.exceptions.service.IdpNotFoundException;
import com.integratingfactor.idp.common.exceptions.service.IdpServiceException;
import com.integratingfactor.idp.common.exceptions.service.IdpValidationException;
import com.integratingfactor.idp.user.api.model.IdpUser;
import com.integratingfactor.idp.user.api.model.IdpUserProfile;
import com.integratingfactor.idp.user.api.model.IdpUserSecret;
import com.integratingfactor.idp.user.core.model.IdpUserProfileFields;
import com.integratingfactor.idp.user.db.service.DaoUserService;

@ContextConfiguration(classes = { IdpUserServiceTestConfig.class })
public class IdpUserServiceTest extends AbstractTestNGSpringContextTests {

    @Autowired
    IdpUserServiceImpl userService;

    @Autowired
    DaoUserService userDao;

    @BeforeMethod
    public void reset() {
        Mockito.reset(userDao);
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
    public void testAddIdpUserSuccess() throws DbException {
        IdpUser user = userService.addIdpUser(testIdpUser());

        // validate that a new account ID was created for user
        Assert.assertNotNull(user.getAccountId());
        Assert.assertNotEquals(user.getAccountId(), TestUserAccountId);

        // validate that return value is sanitized
        Assert.assertNull(user.getPasword());
        Assert.assertNull(user.getProfile());

        // validate that user details were saved with user DAO
        Mockito.verify(userDao).createUser(Mockito.any(IdpUser.class));
    }

    @Test
    public void testAddIdpUserValidatesMissingPassword() throws DbException {

        // simulate empty password in input
        IdpUser user = testIdpUser();
        user.setPasword("");

        // attempt creating user with invalid password
        try {
            userService.addIdpUser(user);
            Assert.fail("Failed to validate missing password");
        } catch (IdpValidationException e) {
            System.out.println("detected: " + e.getError());
        }

        // validate that no user details were saved with user DAO
        Mockito.verify(userDao, Mockito.times(0)).createUser(Mockito.any(IdpUser.class));
    }

    @Test
    public void testAddIdpUserDaoException() throws DbException {
        // simulate a DAO exception when trying to save user details
        Mockito.doThrow(new GenericDbException("induced failure")).when(userDao)
                .createUser(Mockito.any(IdpUser.class));

        // attempt creating user and catching DAO exception
        try {
            userService.addIdpUser(testIdpUser());
            Assert.fail("Failed to handle DB exception");
        } catch (IdpServiceException e) {
            System.out.println("handled: " + e.getError());
        } catch (Exception e) {
            Assert.fail("Failed to handle exception: " + e.getMessage());
        }

        // validate that attempt was made to save user details with user DAO
        Mockito.verify(userDao).createUser(Mockito.any(IdpUser.class));
    }

    @Test
    public void testGetIdpUserDetailsSuccess() throws DbException {
        Mockito.when(userDao.readUserDetails(Mockito.anyString())).thenReturn(testIdpUser());
        IdpUser user = userService.getIdpUserDetails(TestUserAccountId);

        // validate that user details were read from user DAO
        Mockito.verify(userDao).readUserDetails(TestUserAccountId);

        // validate that correct user details are provided
        Assert.assertEquals(user.getAccountId(), TestUserAccountId);
        Assert.assertEquals(user.getPasword(), TestUserSecret);
        Assert.assertEquals(user.getProfile().get(IdpUserProfileFields.first_name), TestUserFirstName);
        Assert.assertEquals(user.getProfile().get(IdpUserProfileFields.last_name), TestUserLastName);
    }

    @Test
    public void testGetIdpUserDetailsNotFound() throws DbException {
        Mockito.when(userDao.readUserDetails(Mockito.anyString()))
                .thenThrow(new NotFoundDbException("simulated not found exception"));
        try {
            userService.getIdpUserDetails(TestUserAccountId);
            Assert.fail("Failed to handle DB exception");
        } catch (IdpNotFoundException e) {
            System.out.println("handled: " + e.getError());
        } catch (Exception e) {
            Assert.fail("Failed to handle exception: " + e.getMessage());
        }

        // validate that user details were read from user DAO
        Mockito.verify(userDao).readUserDetails(TestUserAccountId);
    }

}
