package com.integratingfactor.idp.user.api.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.integratingfactor.idp.user.core.model.IdpUserProfileFields;

@JsonInclude(Include.NON_NULL)
public class IdpUserProfile {

    @JsonProperty("account_id")
    private String accountId;

    @JsonProperty("profile")
    private Map<IdpUserProfileFields, String> profile;

    public IdpUserProfile() {
        profile = new HashMap<IdpUserProfileFields, String>();
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Map<IdpUserProfileFields, String> getProfile() {
        return profile;
    }

    public void setProfile(Map<IdpUserProfileFields, String> profile) {
        this.profile = profile;
    }
}
