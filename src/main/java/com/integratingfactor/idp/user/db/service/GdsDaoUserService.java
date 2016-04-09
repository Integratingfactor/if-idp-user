package com.integratingfactor.idp.user.db.service;

import java.util.logging.Logger;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.integratingfactor.idp.common.db.service.GdsDaoBase;
import com.integratingfactor.idp.common.db.util.GdsDaoService;
import com.integratingfactor.idp.common.exceptions.db.DbException;
import com.integratingfactor.idp.user.api.model.IdpUser;
import com.integratingfactor.idp.user.api.model.IdpUserProfile;
import com.integratingfactor.idp.user.api.model.IdpUserSecret;

public class GdsDaoUserService implements GdsDaoBase, InitializingBean, DaoUserService {
    private static Logger LOG = Logger.getLogger(GdsDaoUserService.class.getName());

    String serviceNameSpace = null;

    @Autowired
    GdsDaoService dao;

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            dao.registerDaoEntity(String.class);
        } catch (DbException e) {
            LOG.warning("Failed to register entity : " + e.getError());
            throw new RuntimeException(e.getError());
        }
    }

    @Override
    public String nameSpace() {
        return serviceNameSpace;
    }

    @Override
    public void cleanupExpired() {
        // NO OP
    }

    @Override
    public void createUser(IdpUser user) throws DbException {
        // TODO Auto-generated method stub

    }

    @Override
    public IdpUser readUserDetails(String accountId) throws DbException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void removeUser(String accountId) throws DbException {
        // TODO Auto-generated method stub

    }

    @Override
    public IdpUserProfile readUserProfile(String accountId) throws DbException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateUserProfile(String accountId, IdpUserProfile profile) throws DbException {
        // TODO Auto-generated method stub

    }

    @Override
    public IdpUserSecret readUserSecret(String accountId) throws DbException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateUserSecret(IdpUserSecret password) throws DbException {
        // TODO Auto-generated method stub

    }

}
