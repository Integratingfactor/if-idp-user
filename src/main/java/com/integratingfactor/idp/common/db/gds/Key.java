package com.integratingfactor.idp.common.db.gds;

import java.lang.reflect.Field;

import com.integratingfactor.idp.common.exceptions.db.DbException;

public class Key<T> {
    String type;

    String key;

    T entity;

    protected Key() {

    }

    // public static <T> Key<T> create(String key, Class<T> type) {
    // Key<T> daoKey = new Key<T>();
    // daoKey.key = key;
    // daoKey.type = type.getSimpleName();
    // return daoKey;
    // }

    public static <T> Key<T> create(T entity, Class<T> type) {
        Key<T> daoKey = new Key<T>();
        daoKey.key = findKey(entity);
        daoKey.entity = entity;
        daoKey.type = type.getSimpleName();
        return daoKey;
    }

    private static <T> String findKey(T entity) {
        try {
            for (Field field : entity.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    return (String) GdsDaoService.findMethod(entity.getClass(), field, "get").invoke(entity);
                }
            }
        } catch (Exception | DbException e) {
        }
        return null;
    }
}
