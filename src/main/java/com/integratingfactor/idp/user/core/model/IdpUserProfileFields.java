package com.integratingfactor.idp.user.core.model;

public enum IdpUserProfileFields {
    first_name("first_name"), last_name("last_name"), business_name("business_name");

    private String value;

    IdpUserProfileFields(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public String getValue() {
        return value;
    }

    static public IdpUserProfileFields as(String value) {
        for (IdpUserProfileFields field : IdpUserProfileFields.values()) {
            if (field.value.equalsIgnoreCase(value)) {
                return field;
            }
        }
        return null;
    }

}
