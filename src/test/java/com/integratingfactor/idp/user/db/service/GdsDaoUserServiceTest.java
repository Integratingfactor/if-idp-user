package com.integratingfactor.idp.user.db.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
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
        // Mockito.reset(dao);
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

}
