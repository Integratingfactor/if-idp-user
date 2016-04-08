package com.integratingfactor.idp.user.db.service;

import com.integratingfactor.idp.common.exceptions.db.DbException;
import com.integratingfactor.idp.user.api.model.IdpUser;
import com.integratingfactor.idp.user.api.model.IdpUserSecret;
import com.integratingfactor.idp.user.api.model.IdpUserProfile;

/**
 * DAO interface for IDP User Data models
 * 
 * @author gnulib
 *
 */
public interface DaoUserService {

    /**
     * create IDP User entries
     * 
     * @param user
     *            details about the user
     * @throws DbException
     */
    public void createIdpUser(IdpUser user) throws DbException;

    /**
     * read profile information about a user
     * 
     * @param accountId
     *            account ID of the user
     * @return user's profile information
     * @throws DbException
     */
    public IdpUserProfile readIdpUserProfile(String accountId) throws DbException;

    public IdpUserSecret readIdpUserCredential(String accountId) throws DbException;

    /**
     * update credentials for an existing user
     * 
     * @param credential
     *            new value to update
     * @throws DbException
     */
    public void updateCredentials(IdpUserSecret credential) throws DbException;
}
