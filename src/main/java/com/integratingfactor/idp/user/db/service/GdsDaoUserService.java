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
import com.integratingfactor.idp.user.db.entity.UserDaoUserProfileByAccountIdUtil;
import com.integratingfactor.idp.user.db.entity.UserDaoUserSecretByAccountIdUtil;

public class GdsDaoUserService implements InitializingBean, DaoUserService {
    private static Logger LOG = Logger.getLogger(GdsDaoUserService.class.getName());

    @Autowired
    GdsDaoService dao;

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            dao.registerDaoEntity(UserDaoUserSecretByAccountIdUtil.UserDaoUserSecretByAccountId.class);
            dao.registerDaoEntity(UserDaoUserProfileByAccountIdUtil.UserDaoUserProfileByAccountIdPk.class);
            dao.registerDaoEntity(UserDaoUserProfileByAccountIdUtil.UserDaoUserProfileByAccountId.class);
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

        LOG.info("DAO writing profile for: " + user.getAccountId());
        dao.save(UserDaoUserProfileByAccountIdUtil.toEntities(user.getAccountId(), user.getProfile()));
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
        dao.delete(UserDaoUserSecretByAccountIdUtil.toKey(accountId));

        // TODO remove profile
        LOG.info("DAO deleting profile for: " + accountId);
        dao.deletePk(UserDaoUserProfileByAccountIdUtil.toPk(accountId));
    }

    @Override
    public IdpUserProfile readUserProfile(String accountId) throws DbException {
        return UserDaoUserProfileByAccountIdUtil
                .toModel(dao.readByAncestorKey(UserDaoUserProfileByAccountIdUtil.toPk(accountId),
                        UserDaoUserProfileByAccountIdUtil.UserDaoUserProfileByAccountId.class));
    }

    @Override
    public void updateUserProfile(String accountId, IdpUserProfile profile) throws DbException {
        // TODO Auto-generated method stub

    }

    @Override
    public IdpUserSecret readUserSecret(String accountId) throws DbException {
        return UserDaoUserSecretByAccountIdUtil.toModel(dao.readByEntityKey(UserDaoUserSecretByAccountIdUtil.toKey(accountId)));
    }

    @Override
    public void updateUserSecret(IdpUserSecret password) throws DbException {
        // we just overwrite, but make sure it exists before
        dao.readByEntityKey(UserDaoUserSecretByAccountIdUtil.toKey(password.getAccountId()));
        LOG.info("DAO updating secret for: " + password.getAccountId());
        dao.save(UserDaoUserSecretByAccountIdUtil.toEntity(password));
    }

}
