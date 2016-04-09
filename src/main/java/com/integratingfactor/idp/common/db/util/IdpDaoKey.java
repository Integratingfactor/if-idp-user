package com.integratingfactor.idp.common.db.util;

public class IdpDaoKey<T> {
    String type;

    String key;

    protected IdpDaoKey() {

    }

    public static <T> IdpDaoKey<T> create(String key, Class<T> type) {
        IdpDaoKey<T> daoKey = new IdpDaoKey<T>();
        daoKey.key = key;
        daoKey.type = type.getName();
        return daoKey;
    }
}
