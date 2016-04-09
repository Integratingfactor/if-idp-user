package com.integratingfactor.idp.user.db.service;

import java.util.Date;
import java.util.logging.Logger;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.integratingfactor.idp.common.db.util.GdsDaoService;
import com.integratingfactor.idp.common.exceptions.db.DbException;
import com.integratingfactor.idp.user.api.model.IdpUser;
import com.integratingfactor.idp.user.api.model.IdpUserProfile;
import com.integratingfactor.idp.user.api.model.IdpUserSecret;
import com.integratingfactor.idp.user.db.entity.UserDaoUserSecretByAccountIdUtil;

public class GdsDaoUserService implements InitializingBean, DaoUserService {
    private static Logger LOG = Logger.getLogger(GdsDaoUserService.class.getName());

    String serviceNameSpace = null;

    @Autowired
    GdsDaoService dao;

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            dao.registerDaoEntity(UserDaoUserSecretByAccountIdUtil.UserDaoUserSecretByAccountId.class);
        } catch (DbException e) {
            LOG.warning("Failed to register entity : " + e.getError());
            throw new RuntimeException(e.getError());
        }
    }

    @Override
    public void createUser(IdpUser user) throws DbException {
        // denormalize and save secret to be read as a whole later
        IdpUserSecret secret = new IdpUserSecret();
        secret.setAccountId(user.getAccountId());
        secret.setPassword(user.getPasword());
        secret.setDate(new Date().getTime());
        LOG.info("DAO writing secret for: " + user.getAccountId());
        dao.save(UserDaoUserSecretByAccountIdUtil.toEntity(secret));

        // TODO save profile
        LOG.info("SKIPPING writing profile for: " + user.getAccountId());
    }

    @Override
    public IdpUser readUserDetails(String accountId) throws DbException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void removeUser(String accountId) throws DbException {
        // remove secret
        LOG.info("DAO deleting secret for: " + accountId);
        dao.delete(UserDaoUserSecretByAccountIdUtil.toPk(accountId));

        // TODO remove profile
        LOG.info("SKIPPING deleting profile for: " + accountId);
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
        return UserDaoUserSecretByAccountIdUtil.toModel(dao.read(UserDaoUserSecretByAccountIdUtil.toPk(accountId)));
    }

    @Override
    public void updateUserSecret(IdpUserSecret password) throws DbException {
        // we just overwrite, but make sure it exists before
        dao.read(UserDaoUserSecretByAccountIdUtil.toPk(password.getAccountId()));
        LOG.info("DAO updating secret for: " + password.getAccountId());
        dao.save(UserDaoUserSecretByAccountIdUtil.toEntity(password));
    }

}
