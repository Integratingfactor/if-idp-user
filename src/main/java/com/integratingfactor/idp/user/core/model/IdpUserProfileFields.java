package com.integratingfactor.idp.user.core.model;

public enum IdpUserProfileFields {
    firstName("first_name"), lastName("last_name"), businessName("business_name");

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
