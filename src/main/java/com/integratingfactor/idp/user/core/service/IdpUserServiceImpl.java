package com.integratingfactor.idp.user.core.service;

import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.integratingfactor.idp.common.db.exceptions.DbException;
import com.integratingfactor.idp.common.db.exceptions.NotFoundDbException;
import com.integratingfactor.idp.common.service.exceptions.IdpNotFoundException;
import com.integratingfactor.idp.common.service.exceptions.IdpServiceException;
import com.integratingfactor.idp.common.service.exceptions.IdpValidationException;
import com.integratingfactor.idp.user.api.model.IdpUser;
import com.integratingfactor.idp.user.api.model.IdpUserProfile;
import com.integratingfactor.idp.user.api.model.IdpUserSecret;
import com.integratingfactor.idp.user.core.model.IdpUserProfileFields;
import com.integratingfactor.idp.user.db.service.DaoUserService;

public class IdpUserServiceImpl implements IdpUserService {
    private static Logger LOG = Logger.getLogger(IdpUserServiceImpl.class.getName());

    @Autowired
    DaoUserService userDao;

    void validateInputNotNull(String value, String error) {
        if (StringUtils.isEmpty(value)) {
            throw new IdpValidationException(error);
        }
    }

    @Override
    public IdpUser addIdpUser(IdpUser user) {
        // validate user input
        validateInputNotNull(user.getPasword(), "password cannot be empty");

        // generate a new user account ID for the user
        LOG.info("generating new account ID for " + user.getProfile().get(IdpUserProfileFields.first_name));
        user.setAccountId(UUID.randomUUID().toString());

        // save user with user DAO
        try {
            userDao.createUser(user);
        } catch (DbException e) {
            e.printStackTrace();
            LOG.warning("Failed to save user with DAO");
            throw new IdpServiceException(e.getError());
        }
        // sanitize return value to only keep account ID
        user.setPasword(null);
        user.setProfile(null);
        return user;
    }

    @Override
    public IdpUser getIdpUserDetails(String accountId) {
        try {
            return userDao.readUserDetails(accountId);
        } catch (NotFoundDbException e) {
            LOG.info(e.getError());
            throw new IdpNotFoundException(e.getError());
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            LOG.warning("Failed to read user from DAO");
            throw new IdpServiceException(e.getError());
        }
    }

    @Override
    public void removeIdpUser(String accountId) {
        try {
            userDao.removeUser(accountId);
            LOG.info("removed user : " + accountId);
        } catch (NotFoundDbException e) {
            LOG.info(e.getError());
            throw new IdpNotFoundException(e.getError());
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            LOG.warning("Failed to remove user from DAO");
            throw new IdpServiceException(e.getError());
        }
    }

    @Override
    public IdpUserProfile getIdpUserProfile(String accountId) {
        try {
            return userDao.readUserProfile(accountId);
        } catch (NotFoundDbException e) {
            LOG.info(e.getError());
            throw new IdpNotFoundException(e.getError());
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            LOG.warning("Failed to read user profile from DAO");
            throw new IdpServiceException(e.getError());
        }
    }

    @Override
    public void updateIdpUserProfile(IdpUser profile) {
        // update user profile with user DAO
        try {
            userDao.updateUserProfile(profile.getAccountId(), profile.getProfile());
        } catch (NotFoundDbException e) {
            LOG.info(e.getError());
            throw new IdpNotFoundException(e.getError());
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            LOG.warning("Failed to update user profile in DAO");
            throw new IdpServiceException(e.getError());
        }
    }

    @Override
    public IdpUserSecret getIdpUserSecret(String accountId) {
        try {
            return userDao.readUserSecret(accountId);
        } catch (NotFoundDbException e) {
            LOG.info(e.getError());
            throw new IdpNotFoundException(e.getError());
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            LOG.warning("Failed to read user password from DAO");
            throw new IdpServiceException(e.getError());
        }
    }

    @Override
    public void updateIdpUserSecret(IdpUserSecret credentials) {
        // validate user input
        validateInputNotNull(credentials.getPassword(), "password cannot be empty");

        // update user password with user DAO
        try {
            userDao.updateUserSecret(credentials);
        } catch (NotFoundDbException e) {
            LOG.info(e.getError());
            throw new IdpNotFoundException(e.getError());
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            LOG.warning("Failed to update user password in DAO");
            throw new IdpServiceException(e.getError());
        }
    }
}
