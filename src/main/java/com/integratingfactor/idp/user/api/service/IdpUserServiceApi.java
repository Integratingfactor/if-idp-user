package com.integratingfactor.idp.user.api.service;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.integratingfactor.idp.common.api.model.IdpApiError;
import com.integratingfactor.idp.common.service.exceptions.IdpNotFoundException;
import com.integratingfactor.idp.common.service.exceptions.IdpValidationException;
import com.integratingfactor.idp.user.api.model.IdpUser;
import com.integratingfactor.idp.user.api.model.IdpUserProfile;
import com.integratingfactor.idp.user.api.model.IdpUserSecret;
import com.integratingfactor.idp.user.core.service.IdpUserService;

@RestController
@RequestMapping(value = { "/api/v1/users", "/api/internal/users" })
public class IdpUserServiceApi {
    private static Logger LOG = Logger.getLogger(IdpUserServiceApi.class.getName());

    @Autowired
    HttpServletRequest request;

    @Autowired
    IdpUserService userService;

    // TODO add RBAC to this method to IDP service accounts only
    @RequestMapping(value = { "", "/" }, method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public IdpUser createUser(@RequestBody IdpUser user) {
        LOG.info("User creation request from " + request.getRemoteAddr());
        return userService.addIdpUser(user);
    }

    // TODO add RBAC to this method to IDP service accounts only
    @RequestMapping(value = { "/{accountId}", "/{accountId}/" }, method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public IdpUser getUserDetails(@PathVariable("accountId") String accountId) {
        LOG.info("User details request for " + accountId + " from " + request.getRemoteAddr());
        return userService.getIdpUserDetails(accountId);
    }

    // TODO add RBAC to this method to IDP service accounts only
    @RequestMapping(value = { "/{accountId}", "/{accountId}/" }, method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("accountId") String accountId) {
        LOG.info("User deletion request for " + accountId + " from " + request.getRemoteAddr());
        userService.removeIdpUser(accountId);
    }

    // TODO add RBAC to this method to IDP service accounts only
    @RequestMapping(value = { "/{accountId}/secret" }, method = RequestMethod.GET)
    public IdpUserSecret getUserSecret(@PathVariable("accountId") String accountId) {
        LOG.info("User credentials request for " + accountId + " from " + request.getRemoteAddr());
        return userService.getIdpUserSecret(accountId);
    }

    // TODO add RBAC to this method to IDP service accounts only
    @RequestMapping(value = { "/{accountId}/secret" }, method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updateUserSecret(@PathVariable("accountId") String accountId,
            @RequestBody IdpUserSecret secret) {
        LOG.info("User credentials update for " + accountId + " from " + request.getRemoteAddr());
        secret.setAccountId(accountId);
        userService.updateIdpUserSecret(secret);
    }

    // TODO add RBAC to this method to IDP service accounts only
    @RequestMapping(value = { "/{accountId}/profile" }, method = RequestMethod.GET)
    public IdpUserProfile getUserProfile(@PathVariable("accountId") String accountId) {
        LOG.info("User profile request for " + accountId + " from " + request.getRemoteAddr());
        return userService.getIdpUserProfile(accountId);
    }

    // TODO add RBAC to this method to IDP service accounts only
    @RequestMapping(value = { "/{accountId}/profile" }, method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updateUserCredentials(@PathVariable("accountId") String accountId,
            @RequestBody IdpUserProfile profile) {
        LOG.info("User profile update for " + accountId + " from " + request.getRemoteAddr());
        IdpUser user = new IdpUser();
        user.setAccountId(accountId);
        user.setProfile(profile);
        userService.updateIdpUserProfile(user);
    }

    @ExceptionHandler
    public ResponseEntity<IdpApiError> handleIdpException(Exception e) {
        IdpApiError error = new IdpApiError();
        error.setDescription(e.getMessage());
        LOG.info("Handling exception: " + e.getMessage());
        if (e instanceof IdpValidationException || e instanceof HttpMessageNotReadableException
                || e instanceof HttpMediaTypeNotSupportedException) {
            error.setError("invalid request");
            error.setCode(HttpStatus.BAD_REQUEST);
        } else if (e instanceof IdpNotFoundException) {
            error.setError("not found");
            error.setCode(HttpStatus.NOT_FOUND);
        } else {
            error.setError("unknown exception");
            error.setCode(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<IdpApiError>(error, error.getCode());
    }
}
