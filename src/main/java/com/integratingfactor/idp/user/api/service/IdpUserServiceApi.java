package com.integratingfactor.idp.user.api.service;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.integratingfactor.idp.common.api.model.IdpApiError;
import com.integratingfactor.idp.common.exceptions.service.IdpNotFoundException;
import com.integratingfactor.idp.common.exceptions.service.IdpValidationException;
import com.integratingfactor.idp.user.api.model.IdpUser;
import com.integratingfactor.idp.user.api.model.IdpUserSecret;
import com.integratingfactor.idp.user.api.model.IdpUserProfile;
import com.integratingfactor.idp.user.core.service.IdpUserService;

@RestController
@RequestMapping(value = { "/api/internal/users" })
public class IdpUserServiceApi {
    private static Logger LOG = Logger.getLogger(IdpUserServiceApi.class.getName());

    @Autowired
    HttpServletRequest request;

    @Autowired
    IdpUserService userService;

    @RequestMapping(value = { "", "/" }, method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public IdpUser createUser(@RequestBody IdpUser user) {
        LOG.info("User creation request from " + request.getRemoteAddr());
        return userService.addIdpUser(user);
    }

    @RequestMapping(value = { "/{accountId}", "/{accountId}/" }, method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("accountId") String accountId) {
        LOG.info("User deletion request for " + accountId + " from " + request.getRemoteAddr());
        userService.removeIdpUser(accountId);
    }

    @RequestMapping(value = { "/{accountId}/secret" }, method = RequestMethod.GET)
    public IdpUserSecret getUserCredentials(@PathVariable("accountId") String accountId) {
        LOG.info("User credentials request for " + accountId + " from " + request.getRemoteAddr());
        return userService.getIdpUserSecret(accountId);
    }

    @RequestMapping(value = { "/{accountId}/secret" }, method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updateUserCredentials(@PathVariable("accountId") String accountId,
            @RequestBody IdpUserSecret secret) {
        LOG.info("User credentials update for " + accountId + " from " + request.getRemoteAddr());
        secret.setAccountId(accountId);
        userService.updateIdpUserSecret(secret);
    }

    @RequestMapping(value = { "/{accountId}/profile" }, method = RequestMethod.GET)
    public IdpUserProfile getUserProfile(@PathVariable("accountId") String accountId) {
        LOG.info("User profile request for " + accountId + " from " + request.getRemoteAddr());
        return userService.getIdpUserProfile(accountId);
    }

    @RequestMapping(value = { "/{accountId}/profile" }, method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updateUserCredentials(@PathVariable("accountId") String accountId,
            @RequestBody IdpUserProfile profile) {
        LOG.info("User profile update for " + accountId + " from " + request.getRemoteAddr());
        profile.setAccountId(accountId);
        userService.updateIdpUserProfile(profile);
    }

    @ExceptionHandler
    public ResponseEntity<IdpApiError> handleIdpException(Exception e) {
        IdpApiError error = new IdpApiError();
        error.setDescription(e.getMessage());
        if (e instanceof IdpValidationException) {
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
