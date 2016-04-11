package com.integratingfactor.idp.user.api.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class IdpUserSecret implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -5455627311099448935L;

    @JsonProperty("account_id")
    private String accountId;

    @JsonProperty("password")
    private String password;

    @JsonProperty("date")
    private Long date;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

}
