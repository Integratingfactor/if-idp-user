package com.integratingfactor.idp.user.core.service;

import com.integratingfactor.idp.user.api.model.IdpUser;
import com.integratingfactor.idp.user.api.model.IdpUserSecret;
import com.integratingfactor.idp.user.api.model.IdpUserProfile;

/**
 * Service interface for IDP User account related services
 * 
 * @author gnulib
 *
 */
public interface IdpUserService {
    /**
     * add a new IDP user account in the system
     * 
     * @param request
     *            details about the IDP user
     * @return a valid authentication object for the user
     */
    IdpUser addIdpUser(IdpUser user);

    IdpUser getIdpUserDetails(String accountId);

    /**
     * remove an IDP user account in the system
     * 
     * @param accountId
     *            user's account ID
     */
    void removeIdpUser(String accountId);

    /**
     * get details of an IDP user account
     * 
     * @param accountId
     *            account id for the user
     * @return details of the IDP user account
     */
    IdpUserProfile getIdpUserProfile(String accountId);

    /**
     * update profile details of an IDP user account
     * 
     * @param profile
     *            updated profile for the user
     */
    void updateIdpUserProfile(IdpUser profile);

    /**
     * get user credentials based on account id
     * 
     * @param accountId
     *            account id of the user
     * @return credentials for the user, if exists, null otherwise
     */
    IdpUserSecret getIdpUserSecret(String accountId);

    /**
     * update user credentials
     * 
     * @param credentials
     *            changed credentials of the user
     */
    void updateIdpUserSecret(IdpUserSecret credentials);
}
