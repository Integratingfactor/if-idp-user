package com.integratingfactor.idp.user.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class IdpUser {

    @JsonProperty("account_id")
    private String accountId;

    @JsonProperty("credentials")
    private IdpUserSecret credential;

    @JsonProperty("profile")
    private IdpUserProfile profile;

    public IdpUser() {
        credential = new IdpUserSecret();
        profile = new IdpUserProfile();
    }

    public IdpUserSecret getCredential() {
        return credential;
    }

    public void setCredential(IdpUserSecret credential) {
        this.credential = credential;
    }


    public IdpUserProfile getProfile() {
        return profile;
    }

    public void setProfile(IdpUserProfile profile) {
        this.profile = profile;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
        credential.setAccountId(accountId);
        profile.setAccountId(accountId);
    }
}
