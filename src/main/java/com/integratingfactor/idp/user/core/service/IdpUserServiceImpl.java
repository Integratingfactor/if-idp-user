package com.integratingfactor.idp.user.core.service;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import com.integratingfactor.idp.user.api.model.IdpUser;
import com.integratingfactor.idp.user.api.model.IdpUserSecret;
import com.integratingfactor.idp.user.api.model.IdpUserProfile;
import com.integratingfactor.idp.user.db.service.DaoUserService;

public class IdpUserServiceImpl implements IdpUserService {
    private static Logger LOG = Logger.getLogger(IdpUserServiceImpl.class.getName());

    @Autowired
    DaoUserService dbUserService;

    @Override
    public IdpUser addIdpUser(IdpUser user) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void removeIdpUser(String accountId) {
        // TODO Auto-generated method stub

    }

    @Override
    public IdpUserProfile getIdpUserProfile(String accountId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateIdpUserProfile(IdpUserProfile profile) {
        // TODO Auto-generated method stub

    }

    @Override
    public IdpUserSecret getIdpUserSecret(String accountId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateIdpUserSecret(IdpUserSecret credentials) {
        // TODO Auto-generated method stub

    }
}
