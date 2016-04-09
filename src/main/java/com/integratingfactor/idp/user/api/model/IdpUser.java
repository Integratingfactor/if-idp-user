package com.integratingfactor.idp.user.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class IdpUser {

    @JsonProperty("account_id")
    private String accountId;

    @JsonProperty("password")
    private String pasword;

    @JsonProperty("profile")
    private IdpUserProfile profile;

    public IdpUser() {
        profile = new IdpUserProfile();
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
    }

    public String getPasword() {
        return pasword;
    }

    public void setPasword(String pasword) {
        this.pasword = pasword;
    }
}
