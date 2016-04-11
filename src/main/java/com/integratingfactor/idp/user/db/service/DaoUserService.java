package com.integratingfactor.idp.user.db.service;

import com.integratingfactor.idp.common.db.exceptions.DbException;
import com.integratingfactor.idp.user.api.model.IdpUser;
import com.integratingfactor.idp.user.api.model.IdpUserProfile;
import com.integratingfactor.idp.user.api.model.IdpUserSecret;

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
    public void createUser(IdpUser user) throws DbException;

    /**
     * get all details of the user
     * 
     * @param accountId
     *            account ID of the user
     * @return details of user, including password and profile
     * @throws DbException
     */
    public IdpUser readUserDetails(String accountId) throws DbException;

    /**
     * remove IDP user from system
     * 
     * @param accountId
     *            user's account ID
     * @throws DbException
     */
    public void removeUser(String accountId) throws DbException;

    /**
     * read profile information about a user
     * 
     * @param accountId
     *            account ID of the user
     * @return user's profile information
     * @throws DbException
     */
    public IdpUserProfile readUserProfile(String accountId) throws DbException;

    /**
     * update profile information for an IDP user
     * 
     * @param accountId
     *            user's account ID
     * @param profile
     *            profile information to update (does not remove/change
     *            information not specified)
     * @throws DbException
     */
    public void updateUserProfile(String accountId, IdpUserProfile profile) throws DbException;

    /**
     * read password for a user
     * 
     * @param accountId
     *            user's account ID
     * @return user's password
     * @throws DbException
     */
    public IdpUserSecret readUserSecret(String accountId) throws DbException;

    /**
     * update password for an existing user
     * 
     * @param password
     *            new value to update
     * @throws DbException
     */
    public void updateUserSecret(IdpUserSecret password) throws DbException;
}
